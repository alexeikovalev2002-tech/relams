package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ChestSelectScreen extends Screen {

    private TextFieldWidget distanceField;

    public ChestSelectScreen() {
        super(Text.literal("Выбор сундуков"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2 - 40;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 0 ? "> " : "") + "Все"),
                (b) -> { BlockEspMod.chestType = 0; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 80, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 1 ? "> " : "") + "Обычные сундуки"),
                (b) -> { BlockEspMod.chestType = 1; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 55, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 2 ? "> " : "") + "Шалкеры"),
                (b) -> { BlockEspMod.chestType = 2; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 3 ? "> " : "") + "Бочки"),
                (b) -> { BlockEspMod.chestType = 3; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 5, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 4 ? "> " : "") + "Эндер-сундуки"),
                (b) -> { BlockEspMod.chestType = 4; this.close(); }
        ).dimensions(this.width / 2 - 100, cy + 20, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 5 ? "> " : "") + "Кровати"),
                (b) -> { BlockEspMod.chestType = 5; this.close(); }
        ).dimensions(this.width / 2 - 100, cy + 45, 200, 20).build());

        distanceField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 75, 100, 20, Text.literal("Дистанция"));
        distanceField.setText(String.valueOf(BlockEspMod.chestDistance));
        distanceField.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text.trim());
                if (v > 0 && v <= 128) BlockEspMod.chestDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distanceField);

        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 105, 100, 20,
                KeyBinds.chestKey, k -> KeyBinds.chestKey = k));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 140, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        int cy = this.height / 2 - 40;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Дистанция (1-128)"),
                this.width / 2, cy + 62, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Клавиша"),
                this.width / 2, cy + 92, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
