package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AimBotScreen extends Screen {

    public AimBotScreen() {
        super(Text.literal("AimBot"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ"),
                (b) -> {
                    AimBot.enabled = !AimBot.enabled;
                    b.setMessage(Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 90, 200, 20).build());

        TextFieldWidget delayMinField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 105, cy - 45, 100, 20, Text.literal("Мин"));
        delayMinField.setText(String.valueOf(AimBot.delayMin));
        delayMinField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 0.1 && v <= 5.0) AimBot.delayMin = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(delayMinField);

        TextFieldWidget delayMaxField = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 5, cy - 45, 100, 20, Text.literal("Макс"));
        delayMaxField.setText(String.valueOf(AimBot.delayMax));
        delayMaxField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 0.1 && v <= 5.0) AimBot.delayMax = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(delayMaxField);

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy, 100, 20, Text.literal("Дистанция"));
        distField.setText(String.valueOf(AimBot.distance));
        distField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 1 && v <= 8) AimBot.distance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 40, 100, 20,
                KeyBinds.aimBotKey, k -> KeyBinds.aimBotKey = k));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 80, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        int cy = this.height / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Задержка мин/макс (0.1-5 сек)"),
                this.width / 2, cy - 57, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Дистанция (1-8)"),
                this.width / 2, cy - 12, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Клавиша"),
                this.width / 2, cy + 28, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
