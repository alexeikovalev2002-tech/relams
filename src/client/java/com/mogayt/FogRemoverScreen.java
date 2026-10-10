package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class FogRemoverScreen extends Screen {

    public FogRemoverScreen() {
        super(Text.literal("Fog Remover"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(FogRemover.enabled ? "Fog: ВКЛ" : "Fog: ВЫКЛ"),
                (b) -> {
                    FogRemover.enabled = !FogRemover.enabled;
                    b.setMessage(Text.literal(FogRemover.enabled ? "Fog: ВКЛ" : "Fog: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 40, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 10, 200, 20).build());
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
