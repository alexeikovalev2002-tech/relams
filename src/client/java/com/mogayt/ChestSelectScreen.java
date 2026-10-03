package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ChestSelectScreen extends Screen {

    public ChestSelectScreen() {
        super(Text.literal("Выбор сундуков"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 0 ? "> " : "") + "Все сундуки"),
                (button) -> {
                    BlockEspMod.chestType = 0;
                    this.close();
                }
        ).dimensions(this.width / 2 - 100, centerY - 50, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 1 ? "> " : "") + "Обычные сундуки"),
                (button) -> {
                    BlockEspMod.chestType = 1;
                    this.close();
                }
        ).dimensions(this.width / 2 - 100, centerY - 20, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 2 ? "> " : "") + "Шалкеры"),
                (button) -> {
                    BlockEspMod.chestType = 2;
                    this.close();
                }
        ).dimensions(this.width / 2 - 100, centerY + 10, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.chestType == 3 ? "> " : "") + "Эндер-сундуки"),
                (button) -> {
                    BlockEspMod.chestType = 3;
                    this.close();
                }
        ).dimensions(this.width / 2 - 100, centerY + 40, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) {
                        this.client.setScreen(new EspMenuScreen());
                    }
                }
        ).dimensions(this.width / 2 - 100, centerY + 80, 200, 20).build());
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
