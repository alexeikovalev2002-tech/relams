package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class OreSelectScreen extends Screen {

    private TextFieldWidget distanceField;

    public OreSelectScreen() {
        super(Text.literal("Выбор руды"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2 - 20;

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

        // Текстовое поле дистанции
        distanceField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, centerY + 75, 100, 20,
                Text.literal("Дистанция"));
        distanceField.setText(String.valueOf(BlockEspMod.oreDistance));
        distanceField.setChangedListener(text -> {
            try {
                int val = Integer.parseInt(text.trim());
                if (val > 0 && val <= 256) {
                    BlockEspMod.oreDistance = val;
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
        ).dimensions(this.width / 2 - 100, centerY + 110, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция ESP руды (1-256):"),
                this.width / 2, this.height / 2 - 15 + 75 - 12, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
