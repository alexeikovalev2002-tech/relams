package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ItemEspScreen extends Screen {

    public ItemEspScreen() {
        super(Text.literal("ItemESP"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.itemEspEnabled ? "ItemESP: ВКЛ" : "ItemESP: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.itemEspEnabled = !BlockEspMod.itemEspEnabled;
                    button.setMessage(Text.literal(BlockEspMod.itemEspEnabled ? "ItemESP: ВКЛ" : "ItemESP: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 40, 200, 20).build());

        TextFieldWidget distField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy + 10, 100, 20, Text.literal("Дистанция"));
        distField.setText(String.valueOf(BlockEspMod.itemDistance));
        distField.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text.trim());
                if (v > 0 && v <= 128) BlockEspMod.itemDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(distField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) this.client.setScreen(new EspMenuScreen());
                }
        ).dimensions(this.width / 2 - 100, cy + 50, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция ItemESP (1-128):"),
                this.width / 2, this.height / 2 + 10 - 12, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
