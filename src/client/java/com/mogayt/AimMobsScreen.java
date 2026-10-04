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
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"),
                (button) -> {
                    AimMobs.enabled = !AimMobs.enabled;
                    button.setMessage(Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 90, 200, 20).build());

        TextFieldWidget speedField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 45, 100, 20, Text.literal("Скорость"));
        speedField.setText(String.valueOf(AimMobs.aimSpeed));
        speedField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v > 0 && v <= 30) AimMobs.aimSpeed = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(speedField);

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy, 100, 20, Text.literal("Дистанция"));
        distField.setText(String.valueOf(AimMobs.aimDistance));
        distField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v > 0 && v <= 128) AimMobs.aimDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        TextFieldWidget fovField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 45, 100, 20, Text.literal("FOV"));
        fovField.setText(String.valueOf(AimMobs.fovAngle));
        fovField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v > 1 && v <= 90) AimMobs.fovAngle = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(fovField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) this.client.setScreen(new EspMenuScreen());
                }
        ).dimensions(this.width / 2 - 100, cy + 90, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Скорость (1-30)"), this.width / 2, this.height / 2 - 57, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция (1-128)"), this.width / 2, this.height / 2 - 12, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("FOV-круг (1-90°)"), this.width / 2, this.height / 2 + 33, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
