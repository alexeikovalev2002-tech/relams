package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AnchorAuraScreen extends Screen {

    public AnchorAuraScreen() {
        super(Text.literal("Anchor Aura"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AnchorAura.enabled ? "AnchorAura: ВКЛ" : "AnchorAura: ВЫКЛ"),
                (b) -> {
                    AnchorAura.enabled = !AnchorAura.enabled;
                    Notification.show("AnchorAura", AnchorAura.enabled);
                    b.setMessage(Text.literal(AnchorAura.enabled ? "AnchorAura: ВКЛ" : "AnchorAura: ВЫКЛ"));
                    Config.save();
                }
        ).dimensions(this.width / 2 - 100, cy - 40, 200, 20).build());

        TextFieldWidget rangeField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 10, 100, 20, Text.literal("Радиус"));
        rangeField.setText(String.valueOf(AnchorAura.range));
        rangeField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 1 && v <= 10) {
                    AnchorAura.range = v;
                    Config.save();
                }
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(rangeField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> {
                    Config.save();
                    if (this.client != null) this.client.setScreen(new EspMenuScreen());
                }
        ).dimensions(this.width / 2 - 100, cy + 60, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        int cy = this.height / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Радиус поиска (1-10)"), this.width / 2, cy - 2, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
