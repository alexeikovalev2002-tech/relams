package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OreSelectScreen extends Screen {

    public OreSelectScreen() {
        super(Text.literal("Выбор руды"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 0 ? "> " : "") + "Алмазы"),
                (button) -> { BlockEspMod.oreType = 0; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 80, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 1 ? "> " : "") + "Железо"),
                (button) -> { BlockEspMod.oreType = 1; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 55, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 2 ? "> " : "") + "Уголь"),
                (button) -> { BlockEspMod.oreType = 2; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 3 ? "> " : "") + "Изумруды"),
                (button) -> { BlockEspMod.oreType = 3; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY - 5, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 4 ? "> " : "") + "Незерит"),
                (button) -> { BlockEspMod.oreType = 4; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY + 20, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 5 ? "> " : "") + "Всё"),
                (button) -> { BlockEspMod.oreType = 5; this.close(); }
        ).dimensions(this.width / 2 - 100, centerY + 45, 200, 20).build());

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
