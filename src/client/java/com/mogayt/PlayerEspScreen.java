package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class PlayerEspScreen extends Screen {

    public PlayerEspScreen() {
        super(Text.literal("Player ESP"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"),
                (b) -> {
                    BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled;
                    b.setMessage(Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 80, 200, 20).build());

        TextFieldWidget dist = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 30, 100, 20, Text.literal("Дистанция"));
        dist.setText(String.valueOf(BlockEspMod.playerDistance));
        dist.setChangedListener(text -> {
            try {
                int v = Integer.parseInt(text.trim());
                if (v > 0 && v <= 256) BlockEspMod.playerDistance = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(dist);

        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 20, 100, 20,
                KeyBinds.playerKey, k -> KeyBinds.playerKey = k));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 70, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Дистанция (1-256)"),
                this.width / 2, this.height / 2 - 42, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Клавиша"),
                this.width / 2, this.height / 2 + 8, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
