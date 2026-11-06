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
                (b) -> {
                    AimMobs.enabled = !AimMobs.enabled;
                    b.setMessage(Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 120, 200, 20).build());

        // Кнопки выбора цели
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Игроки: " + (AimMobs.targetPlayers ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    AimMobs.targetPlayers = !AimMobs.targetPlayers;
                    b.setMessage(Text.literal("Игроки: " + (AimMobs.targetPlayers ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(this.width / 2 - 100, cy - 90, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Мобы: " + (AimMobs.targetMobs ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    AimMobs.targetMobs = !AimMobs.targetMobs;
                    b.setMessage(Text.literal("Мобы: " + (AimMobs.targetMobs ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(this.width / 2 + 5, cy - 90, 95, 20).build());

        TextFieldWidget speedField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 55, 100, 20, Text.literal("Скорость"));
        speedField.setText(String.valueOf(AimMobs.aimSpeed));
        speedField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v > 0 && v <= 50) AimMobs.aimSpeed = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(speedField);

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 15, 100, 20, Text.literal("Дистанция"));
        distField.setText(String.valueOf(AimMobs.aimDistance));
        distField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v > 0 && v <= 128) AimMobs.aimDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        TextFieldWidget fovField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 25, 100, 20, Text.literal("FOV"));
        fovField.setText(String.valueOf(AimMobs.fovAngle));
        fovField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v > 1 && v <= 90) AimMobs.fovAngle = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(fovField);

        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 65, 100, 20,
                KeyBinds.aimMobsKey, k -> KeyBinds.aimMobsKey = k));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 105, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        int cy = this.height / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Скорость (1-50)"),
                this.width / 2, cy - 67, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Дистанция (1-128)"),
                this.width / 2, cy - 27, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("FOV (1-90°)"),
                this.width / 2, cy + 13, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Клавиша"),
                this.width / 2, cy + 53, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
