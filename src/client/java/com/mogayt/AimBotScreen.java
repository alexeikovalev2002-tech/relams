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
                (button) -> {
                    AimBot.enabled = !AimBot.enabled;
                    button.setMessage(Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 80, 200, 20).build());

        TextFieldWidget delayField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 35, 100, 20, Text.literal("Задержка"));
        delayField.setText(String.valueOf(AimBot.delay));
        delayField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v >= 0.1 && v <= 5.0) AimBot.delay = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(delayField);

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 10, 100, 20, Text.literal("Дистанция"));
        distField.setText(String.valueOf(AimBot.distance));
        distField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v >= 1 && v <= 8) AimBot.distance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        TextFieldWidget speedField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 55, 100, 20, Text.literal("Скорость"));
        speedField.setText(String.valueOf(AimBot.rotateSpeed));
        speedField.setChangedListener(text -> {
            try {
                double v = Double.parseDouble(text.trim());
                if (v >= 1 && v <= 180) AimBot.rotateSpeed = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(speedField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) this.client.setScreen(new EspMenuScreen());
                }
        ).dimensions(this.width / 2 - 100, cy + 95, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Задержка удара (0.1-5 сек)"), this.width / 2, this.height / 2 - 47, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция (1-8 блоков)"), this.width / 2, this.height / 2 - 2, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Скорость поворота (1-180)"), this.width / 2, this.height / 2 + 43, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
