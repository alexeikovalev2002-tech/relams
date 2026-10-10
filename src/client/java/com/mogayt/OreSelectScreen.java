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
        int cy = this.height / 2 - 40;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 0 ? "> " : "") + "Алмазы"),
                (b) -> { BlockEspMod.oreType = 0; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 80, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 1 ? "> " : "") + "Железо"),
                (b) -> { BlockEspMod.oreType = 1; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 55, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 2 ? "> " : "") + "Уголь"),
                (b) -> { BlockEspMod.oreType = 2; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 30, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 3 ? "> " : "") + "Изумруды"),
                (b) -> { BlockEspMod.oreType = 3; this.close(); }
        ).dimensions(this.width / 2 - 100, cy - 5, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 4 ? "> " : "") + "Незерит"),
                (b) -> { BlockEspMod.oreType = 4; this.close(); }
        ).dimensions(this.width / 2 - 100, cy + 20, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((BlockEspMod.oreType == 5 ? "> " : "") + "Всё"),
                (b) -> { BlockEspMod.oreType = 5; this.close(); }
        ).dimensions(this.width / 2 - 100, cy + 45, 200, 20).build());

        distanceField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 75, 100, 20, Text.literal("Дистанция"));
        distanceField.setText(String.valueOf(BlockEspMod.oreDistance));
        distanceField.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text.trim());
                if (v > 0 && v <= 128) BlockEspMod.oreDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distanceField);

        // Клавиша
        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 105, 100, 20,
                KeyBinds.espKey, k -> KeyBinds.espKey = k));

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
