package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TriggerBotScreen extends Screen {

    public TriggerBotScreen() {
        super(Text.literal("Trigger Bot"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        // Вкл/Выкл
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(TriggerBot.enabled ? "TriggerBot: ВКЛ" : "TriggerBot: ВЫКЛ"),
                (b) -> {
                    TriggerBot.enabled = !TriggerBot.enabled;
                    Notification.show("TriggerBot", TriggerBot.enabled);
                    b.setMessage(Text.literal(TriggerBot.enabled ? "TriggerBot: ВКЛ" : "TriggerBot: ВЫКЛ"));
                    Config.save();
                }
        ).dimensions(this.width / 2 - 100, cy - 80, 200, 20).build());

        // Только криты
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Только криты: " + (TriggerBot.onlyCrits ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    TriggerBot.onlyCrits = !TriggerBot.onlyCrits;
                    b.setMessage(Text.literal("Только криты: " + (TriggerBot.onlyCrits ? "ВКЛ" : "ВЫКЛ")));
                    Config.save();
                }
        ).dimensions(this.width / 2 - 100, cy - 50, 200, 20).build());

        // Игроки
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Игроки: " + (TriggerBot.targetPlayers ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    TriggerBot.targetPlayers = !TriggerBot.targetPlayers;
                    b.setMessage(Text.literal("Игроки: " + (TriggerBot.targetPlayers ? "ВКЛ" : "ВЫКЛ")));
                    Config.save();
                }
        ).dimensions(this.width / 2 - 100, cy - 20, 95, 20).build());

        // Мобы
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Мобы: " + (TriggerBot.targetMobs ? "ВКЛ" : "ВЫКЛ")),
                (b) -> {
                    TriggerBot.targetMobs = !TriggerBot.targetMobs;
                    b.setMessage(Text.literal("Мобы: " + (TriggerBot.targetMobs ? "ВКЛ" : "ВЫКЛ")));
                    Config.save();
                }
        ).dimensions(this.width / 2 + 5, cy - 20, 95, 20).build());

        // Задержка От
        TextFieldWidget delayMinField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 100, cy + 15, 95, 20, Text.literal("От"));
        delayMinField.setText(String.valueOf(TriggerBot.delayMin));
        delayMinField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 0 && v <= 5) {
                    TriggerBot.delayMin = v;
                    Config.save();
                }
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(delayMinField);

        // Задержка До
        TextFieldWidget delayMaxField = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 5, cy + 15, 95, 20, Text.literal("До"));
        delayMaxField.setText(String.valueOf(TriggerBot.delayMax));
        delayMaxField.setChangedListener(t -> {
            try {
                double v = Double.parseDouble(t.trim());
                if (v >= 0 && v <= 5) {
                    TriggerBot.delayMax = v;
                    Config.save();
                }
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(delayMaxField);

        // Назад
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
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Задержка в секундах (0 - 5)"), this.width / 2, cy + 3, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
