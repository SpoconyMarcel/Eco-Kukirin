package com.ecokukirin.client;

import com.ecokukirin.KukirinEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class KukirinRenderer extends MobEntityRenderer<KukirinEntity, KukirinModel> {
    public KukirinRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new KukirinModel(ctx.getPart(EcoKukirinClient.MODEL_LAYER)), 0.4f);
    }

    /** Wheelie: obrot calej hulajnogi wokol tylnej osi (przod w gore). */
    @Override
    protected void setupTransforms(KukirinEntity entity, MatrixStack matrices,
                                   float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        float angle = MathHelper.lerp(tickDelta, entity.prevWheelieAngle, entity.wheelieAngle);
        if (angle != 0f) {
            // tylna os: ~0.28 m nad ziemia, ~0.66 m za srodkiem (przod = -Z po obrocie yaw)
            matrices.translate(0.0, 0.28, 0.656);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(angle));
            matrices.translate(0.0, -0.28, -0.656);
        }
    }

    @Override
    public Identifier getTexture(KukirinEntity entity) {
        return entity.getVariant().texture;
    }
}
