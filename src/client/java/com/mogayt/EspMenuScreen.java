package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspMenuScreen extends Screen {

    public EspMenuScreen() {
        super(Text.literal("Mog Mod Menu"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        // ESP
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.espEnabled ? "ESP: ВКЛ" : "ESP: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.espEnabled = !BlockEspMod.espEnabled;
                    button.setMessage(Text.literal(BlockEspMod.espEnabled ? "ESP: ВКЛ" : "ESP: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 50, 200, 20).build());

        // Fullbright
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"),
                (button) -> {
                    Fullbright.enabled = !Fullbright.enabled;
                    button.setMessage(Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 20, 200, 20).build());

        // Freecam
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"),
                (button) -> {
                    Freecam.enabled = !Freecam.enabled;
                    if (Freecam.enabled) Freecam.onEnable();
                    button.setMessage(Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY + 10, 200, 20).build());

        // Закрыть
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (button) -> this.close()
        ).dimensions(this.width / 2 - 100, centerY + 50, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
