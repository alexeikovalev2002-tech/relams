package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MaceAuraScreen extends Screen {

    public MaceAuraScreen() {
        super(Text.literal("MaceAura"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(MaceAura.enabled ? "MaceAura: ВКЛ" : "MaceAura: ВЫКЛ"),
                (b) -> {
                    MaceAura.enabled = !MaceAura.enabled;
                    Notification.show("MaceAura", MaceAura.enabled);
                    b.setMessage(Text.literal(MaceAura.enabled ? "MaceAura: ВКЛ" : "MaceAura: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 40, 200, 20).build());

        // Кнопки выбора цели
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Игроки: " + (MaceAura.targetPlayers ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    MaceAura.targetPlayers = !MaceAura.targetPlayers;
                    b.setMessage(Text.literal("Игроки: " + (MaceAura.targetPlayers ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(this.width / 2 - 100, cy - 10, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Мобы: " + (MaceAura.targetMobs ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    MaceAura.targetMobs = !MaceAura.targetMobs;
                    b.setMessage(Text.literal("Мобы: " + (MaceAura.targetMobs ? "ВКЛ" : "ВЫКЛ")));
                }
        ).dimensions(this.width / 2 + 5, cy - 10, 95, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 30, 200, 20).build());
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
