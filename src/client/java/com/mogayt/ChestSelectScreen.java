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
        int centerY = this.height / 2 - 30;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 0 ? "> " : "") + "Все"),
                (button) -> { BlockEspMod.chestType = 0; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 80, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 1 ? "> " : "") + "Обычные сундуки"),
                (button) -> { BlockEspMod.chestType = 1; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 55, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 2 ? "> " : "") + "Шалкеры"),
                (button) -> { BlockEspMod.chestType = 2; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 3 ? "> " : "") + "Бочки"),
                (button) -> { BlockEspMod.chestType = 3; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 5, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 4 ? "> " : "") + "Эндер-сундуки"),
                (button) -> { BlockEspMod.chestType = 4; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY + 20, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 5 ? "> " : "") + "Кровати"),
                (button) -> { BlockEspMod.chestType = 5; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY + 45, 200, 20).build());

        // ===== ТЕКСТОВОЕ ПОЛЕ ДИСТАНЦИИ =====
        distanceField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, centerY + 80, 100, 20,
                Text.literal("Дистанция"));
        distanceField.setText(String.valueOf(BlockEspMod.chestDistance));
        distanceField.setChangedListener(text -> {
            try {
                int val = Integer.parseInt(text.trim());
                if (val > 0 && val <= 256) {
                    BlockEspMod.chestDistance = val;
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(distanceField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) {
                        this.client.setScreen(new EspMenuScreen());
                    }
                }
        ).dimensions(this.width / 2 - 100, centerY + 115, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция ESP сундуков (1-256):"),
                this.width / 2, this.height / 2 + 60, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
