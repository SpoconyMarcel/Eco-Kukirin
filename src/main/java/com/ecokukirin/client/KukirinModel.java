package com.ecokukirin.client;

import com.ecokukirin.KukirinEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

/** Model 3D hulajnogi zbudowany z szescianow (1 block = 16 px). Przod = -Z. */
public class KukirinModel extends EntityModel<KukirinEntity> {
    private final ModelPart root;

    public KukirinModel(ModelPart root) { this.root = root; }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData r = data.getRoot();
        ModelTransform ground = ModelTransform.pivot(0f, 24f, 0f);

        // gorna polowa tekstury (v 0-31) = kolor wariantu, dolna (v 32-63) = czern/guma
        r.addChild("deck",   ModelPartBuilder.create().uv(0, 0).cuboid(-3f, -5f, -9f, 6f, 1.5f, 18f), ground);
        r.addChild("battery",ModelPartBuilder.create().uv(0, 0).cuboid(-2f, -3.5f, -6f, 4f, 1.5f, 12f), ground);
        r.addChild("fender_f",ModelPartBuilder.create().uv(0, 0).cuboid(-2f, -9.5f, -15f, 4f, 0.5f, 8f), ground);
        r.addChild("fender_r",ModelPartBuilder.create().uv(0, 0).cuboid(-2f, -9.5f, 7f, 4f, 0.5f, 8f), ground);

        r.addChild("wheel_f",ModelPartBuilder.create().uv(0, 32).cuboid(-1.5f, -9f, -15f, 3f, 9f, 9f), ground);
        r.addChild("wheel_r",ModelPartBuilder.create().uv(0, 32).cuboid(-1.5f, -9f, 6f, 3f, 9f, 9f), ground);

        r.addChild("stem",   ModelPartBuilder.create().uv(0, 20).cuboid(-0.75f, -30f, -11f, 1.5f, 26f, 1.5f), ground);
        r.addChild("fork",   ModelPartBuilder.create().uv(0, 20).cuboid(-2f, -9f, -11f, 4f, 1f, 1.5f), ground);
        r.addChild("handle", ModelPartBuilder.create().uv(0, 0).cuboid(-5.5f, -31f, -11f, 11f, 1.2f, 1.5f), ground);
        r.addChild("grip_l", ModelPartBuilder.create().uv(0, 32).cuboid(-6.5f, -31.3f, -11.3f, 2f, 1.8f, 2f), ground);
        r.addChild("grip_r", ModelPartBuilder.create().uv(0, 32).cuboid(4.5f, -31.3f, -11.3f, 2f, 1.8f, 2f), ground);
        r.addChild("screen", ModelPartBuilder.create().uv(0, 32).cuboid(-1.5f, -33f, -11f, 3f, 2f, 1.5f), ground);
        r.addChild("light",  ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -20f, -12.5f, 3f, 2f, 1.5f), ground);

        return TexturedModelData.of(data, 64, 64);
    }

    @Override
    public void setAngles(KukirinEntity e, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float spin = limbAngle * 1.2f;
        root.getChild("wheel_f").pitch = spin;
        root.getChild("wheel_r").pitch = spin;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
