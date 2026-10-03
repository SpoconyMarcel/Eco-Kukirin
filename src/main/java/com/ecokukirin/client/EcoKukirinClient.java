package com.ecokukirin.client;

import com.ecokukirin.EcoKukirinMod;
import com.ecokukirin.KukirinEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EcoKukirinClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_LAYER =
            new EntityModelLayer(new Identifier(EcoKukirinMod.MOD_ID, "kukirin"), "main");

    private static KeyBinding menuKey;
    private static boolean lastWheelieState = false;

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(MODEL_LAYER, KukirinModel::getTexturedModelData);
        EntityRendererRegistry.register(EcoKukirinMod.KUKIRIN, KukirinRenderer::new);

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.eco_kukirin.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.eco_kukirin"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // wheelie: spacja podczas jazdy na Kukirinie (wysylamy tylko przy zmianie stanu)
            boolean wheelie = client.player != null
                    && client.currentScreen == null
                    && client.player.getVehicle() instanceof KukirinEntity
                    && client.options.jumpKey.isPressed();
            if (wheelie != lastWheelieState) {
                lastWheelieState = wheelie;
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(wheelie);
                ClientPlayNetworking.send(EcoKukirinMod.WHEELIE_PACKET, buf);
            }

            while (menuKey.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new KukirinScreen());
                }
            }
        });
    }
}
