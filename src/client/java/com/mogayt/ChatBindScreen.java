package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ChatBindScreen extends Screen {

    private TextFieldWidget commandField;

    private static final String[] PRESETS = {
            "/warp pvp",
            "/warp mine",
            "/rtp"
    };

    public ChatBindScreen() {
        super(Text.literal("Chat Bind"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        // Текстовое поле
        commandField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 100, centerY - 70, 200, 20,
                Text.literal("Команда"));
        commandField.setText(ChatBind.command);
        commandField.setChangedListener(text -> {
            ChatBind.command = text;
        });
        this.addDrawableChild(commandField);

        // Пресеты
        int y = centerY - 30;
        for (String preset : PRESETS) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(preset),
                    (button) -> {
                        ChatBind.command = preset;
                        commandField.setText(preset);
                    }
            ).dimensions(this.width / 2 - 100, y, 200, 20).build());
            y += 25;
        }

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) {
                        this.client.setScreen(new EspMenuScreen());
                    }
                }
        ).dimensions(this.width / 2 - 100, y + 10, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Клавиша по умолчанию: R (изменяется в настройках управления)"),
                this.width / 2, this.height / 2 - 90, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
