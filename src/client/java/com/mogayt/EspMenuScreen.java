package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class PlayerEspScreen extends Screen {

    private TextFieldWidget distanceField;

    public PlayerEspScreen() {
        super(Text.literal("Player ESP"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2 - 30;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled;
                    button.setMessage(Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 40, 200, 20).build());

        distanceField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, centerY + 10, 100, 20,
                Text.literal("Дистанция"));
        distanceField.setText(String.valueOf(BlockEspMod.playerDistance));
        distanceField.setChangedListener(text -> {
            try {
                int val = Integer.parseInt(text.trim());
                if (val > 0 && val <= 256) {
                    BlockEspMod.playerDistance = val;
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
        ).dimensions(this.width / 2 - 100, centerY + 50, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Дистанция PlayerESP (1-256):"),
                this.width / 2, this.height / 2 - 30 + 10 - 12, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
