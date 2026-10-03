package com.ecokukirin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class KukirinEntity extends PathAwareEntity {
    private static final TrackedData<Integer> VARIANT =
            DataTracker.registerData(KukirinEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Boolean> WHEELIE =
            DataTracker.registerData(KukirinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final float MAX_WHEELIE_ANGLE = 38f;

    /** Aktualny i poprzedni kat podniesienia przodu (stopnie) - do plynnej animacji. */
    public float wheelieAngle, prevWheelieAngle;

    @Nullable
    private UUID ownerUuid;

    public KukirinEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setStepHeight(1.0f); // wjezdzanie na schody/bloki
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createKukirinAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(WHEELIE, false);
    }

    public boolean isWheelie() { return this.dataTracker.get(WHEELIE); }
    public void setWheelie(boolean w) { this.dataTracker.set(WHEELIE, w); }

    @Override
    public void tick() {
        super.tick();
        // serwer: bez kierowcy lub w powietrzu = koniec wheelie
        if (!this.getWorld().isClient && isWheelie() && (!this.hasPassengers() || !this.isOnGround())) {
            setWheelie(false);
        }
        // klient i serwer: plynna animacja kata
        prevWheelieAngle = wheelieAngle;
        float target = isWheelie() ? MAX_WHEELIE_ANGLE : 0f;
        wheelieAngle += (target - wheelieAngle) * 0.18f;
        if (Math.abs(target - wheelieAngle) < 0.05f) wheelieAngle = target;
    }

    public KukirinVariant getVariant() { return KukirinVariant.byId(this.dataTracker.get(VARIANT)); }
    public void setVariant(KukirinVariant v) { this.dataTracker.set(VARIANT, v.ordinal()); }
    @Nullable public UUID getOwnerUuid() { return ownerUuid; }
    public void setOwnerUuid(@Nullable UUID uuid) { this.ownerUuid = uuid; }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.dataTracker.get(VARIANT));
        if (ownerUuid != null) nbt.putUuid("Owner", ownerUuid);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(VARIANT, nbt.getInt("Variant"));
        if (nbt.containsUuid("Owner")) ownerUuid = nbt.getUuid("Owner");
    }

    // --- interakcja / jazda ---
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && !this.hasPassengers()) {
            player.startRiding(this);
        }
        return ActionResult.success(this.getWorld().isClient);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity le ? le : null;
    }

    @Override
    public double getMountedHeightOffset() { return 0.6; }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) { return false; }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) { return false; }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // odporna na wszystko poza pustka i graczami w trybie kreatywnym
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD)
                || source.isOf(net.minecraft.entity.damage.DamageTypes.GENERIC_KILL)
                || (source.getAttacker() instanceof PlayerEntity p && p.getAbilities().creativeMode)) {
            return super.damage(source, amount);
        }
        return false;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isAlive() && this.getControllingPassenger() instanceof PlayerEntity rider) {
            float yaw = rider.getYaw();
            this.setYaw(yaw);
            this.prevYaw = yaw;
            this.setPitch(0f);
            this.bodyYaw = yaw;
            this.headYaw = yaw;

            float side = rider.sidewaysSpeed * 0.4f;
            float forward = rider.forwardSpeed;
            if (forward < 0) forward *= 0.3f;

            this.setMovementSpeed(0.28f * getVariant().speedMultiplier * (isWheelie() ? 0.9f : 1f));
            super.travel(new Vec3d(side, movementInput.y, forward));
        } else {
            super.travel(movementInput);
        }
    }
}
