package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AimMobsScreen extends Screen {

    public AimMobsScreen() {
        super(Text.literal("AimMobs"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"),
                (button) -> {
                    AimMobs.enabled = !AimMobs.enabled;
                    button.setMessage(Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 70, 200, 20).build());

        TextFieldWidget speedField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, centerY - 25, 100, 20, Text.literal("Скорость"));
        speedField.setText(String.valueOf(AimMobs.aimSpeed));
        speedField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v > 0 && v <= 30) AimMobs.aimSpeed = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(speedField);

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, centerY + 25, 100, 20, Text.literal("Дистанция"));
        distField.setText(String.valueOf(AimMobs.aimDistance));
        distField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v > 0 && v <= 128) AimMobs.aimDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) this.client.setScreen(new EspMenuScreen());
                }
        ).dimensions(this.width / 2 - 100, centerY + 70, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Скорость (0.1-30 град/тик)"),
                this.width / 2, this.height / 2 - 37, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция (1-128 блоков)"),
                this.width / 2, this.height / 2 + 13, 0xAAAAAA);
        super.render(context, mouseX, mouseY, context.getScaledWindowHeight() == 0 ? 0 : 0);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
