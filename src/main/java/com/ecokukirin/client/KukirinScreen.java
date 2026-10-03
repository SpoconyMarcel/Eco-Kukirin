package com.ecokukirin.client;

import com.ecokukirin.EcoKukirinMod;
import com.ecokukirin.KukirinVariant;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Menu otwierane klawiszem V. */
public class KukirinScreen extends Screen {
    public KukirinScreen() { super(Text.translatable("gui.eco_kukirin.title")); }

    @Override
    protected void init() {
        int w = 200, h = 20, gap = 24;
        int x = (this.width - w) / 2;
        KukirinVariant[] vs = KukirinVariant.values();
        int y = this.height / 2 - (vs.length * gap) / 2;

        for (KukirinVariant v : vs) {
            String label = v.displayName + "  (x" + v.speedMultiplier + ")";
            this.addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> send(v.ordinal()))
                    .dimensions(x, y, w, h).build());
            y += gap;
        }
        y += 6;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.eco_kukirin.remove"), b -> send(-1))
                .dimensions(x, y, w, h).build());
        y += gap;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.eco_kukirin.close"), b -> close())
                .dimensions(x, y, w, h).build());
    }

    private void send(int id) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        ClientPlayNetworking.send(EcoKukirinMod.SPAWN_PACKET, buf);
        close();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2,
                this.height / 2 - (KukirinVariant.values().length * 24) / 2 - 20, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_V) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}
