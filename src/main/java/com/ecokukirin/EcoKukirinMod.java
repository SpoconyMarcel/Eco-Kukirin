package com.ecokukirin;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class EcoKukirinMod implements ModInitializer {
    public static final String MOD_ID = "eco_kukirin";

    /** Pakiet klient -> serwer: int (ordinal wariantu, -1 = usun moja hulajnoge). */
    public static final Identifier SPAWN_PACKET = new Identifier(MOD_ID, "spawn_kukirin");

    /** Pakiet klient -> serwer: boolean (czy spacja wcisnieta = wheelie). */
    public static final Identifier WHEELIE_PACKET = new Identifier(MOD_ID, "wheelie");

    public static final EntityType<KukirinEntity> KUKIRIN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "kukirin"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, KukirinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.9f))
                    .trackRangeBlocks(80)
                    .build());

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(KUKIRIN, KukirinEntity.createKukirinAttributes());

        ServerPlayNetworking.registerGlobalReceiver(SPAWN_PACKET, (server, player, handler, buf, sender) -> {
            int id = buf.readInt();
            server.execute(() -> handleRequest(player, id));
        });

        ServerPlayNetworking.registerGlobalReceiver(WHEELIE_PACKET, (server, player, handler, buf, sender) -> {
            boolean on = buf.readBoolean();
            server.execute(() -> {
                if (player.getVehicle() instanceof KukirinEntity k) {
                    k.setWheelie(on && k.isOnGround());
                }
            });
        });
    }

    private static void handleRequest(ServerPlayerEntity player, int id) {
        ServerWorld world = player.getServerWorld();

        // usun poprzednie hulajnogi tego gracza (respawn)
        world.getEntitiesByType(KUKIRIN, e -> player.getUuid().equals(e.getOwnerUuid()))
                .forEach(e -> e.discard());

        if (id < 0) {
            player.sendMessage(Text.literal("Usunięto hulajnogę."), true);
            return;
        }

        KukirinVariant variant = KukirinVariant.byId(id);
        KukirinEntity k = KUKIRIN.create(world);
        if (k == null) return;

        Vec3d pos = player.getPos().add(player.getRotationVector().multiply(1.5, 0, 1.5));
        k.refreshPositionAndAngles(pos.x, player.getY(), pos.z, player.getYaw(), 0f);
        k.setVariant(variant);
        k.setOwnerUuid(player.getUuid());
        k.setCustomName(Text.literal(variant.displayName));
        world.spawnEntity(k);

        player.sendMessage(Text.literal(variant.displayName + " zrespawnowana! Kliknij PPM, aby wsiąść."), true);
    }
}
