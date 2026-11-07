package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class CrystalAuraScreen extends Screen {

    public CrystalAuraScreen() {
        super(Text.literal("Crystal Aura"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(CrystalAura.enabled ? "CrystalAura: ВКЛ" : "CrystalAura: ВЫКЛ"),
                (b) -> {
                    CrystalAura.enabled = !CrystalAura.enabled;
                    Notification.show("CrystalAura", CrystalAura.enabled);
                    b.setMessage(Text.literal(CrystalAura.enabled ? "CrystalAura: ВКЛ" : "CrystalAura: ВЫКЛ"));
                    Config.save();
                }
        ).dimensions(this.width / 2 - 100, cy - 60, 200, 20).build());

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 15, 100, 20, Text.literal("Радиус"));
        distField.setText(String.valueOf(CrystalAura.range));
        distField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 1 && v <= 6) {
                    CrystalAura.range = v;
                    Config.save();
                }
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        TextFieldWidget speedField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 30, 100, 20, Text.literal("Скорость"));
        speedField.setText(String.valueOf(CrystalAura.aimSpeed));
        speedField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v > 0 && v <= 100) {
                    CrystalAura.aimSpeed = v;
                    Config.save();
                }
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(speedField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> {
                    Config.save();
                    if (this.client != null) this.client.setScreen(new EspMenuScreen());
                }
        ).dimensions(this.width / 2 - 100, cy + 80, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        int cy = this.height / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Радиус (1-6)"), this.width / 2, cy - 27, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Скорость наведения (1-100)"), this.width / 2, cy + 18, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
