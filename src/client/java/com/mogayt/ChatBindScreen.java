package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ChatBindScreen extends Screen {

    public ChatBindScreen() {
        super(Text.literal("Chat Bind"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;
        int startY = centerY - 90;
        int rowH = 35;

        for (int i = 0; i < ChatBind.binds.size(); i++) {
            final int idx = i;
            ChatBind.Bind bind = ChatBind.binds.get(i);
            int y = startY + i * rowH;

            // Поле команды
            TextFieldWidget cmdField = new TextFieldWidget(this.textRenderer,
                    this.width / 2 - 110, y, 160, 20,
                    Text.literal("Команда"));
            cmdField.setText(bind.command);
            cmdField.setChangedListener(text -> ChatBind.binds.get(idx).command = text);
            this.addDrawableChild(cmdField);

            // Поле клавиши
            TextFieldWidget keyField = new TextFieldWidget(this.textRenderer,
                    this.width / 2 + 55, y, 55, 20,
                    Text.literal("Клавиша"));
            keyField.setMaxLength(1);
            keyField.setText(ChatBind.keyToLetter(bind.keyCode));
            keyField.setChangedListener(text -> {
                int key = ChatBind.letterToKey(text);
                if (key > 0) {
                    ChatBind.binds.get(idx).keyCode = key;
                }
            });
            this.addDrawableChild(keyField);
        }

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (button) -> {
                    if (this.client != null) {
                        this.client.setScreen(new EspMenuScreen());
                    }
                }
        ).dimensions(this.width / 2 - 100, startY + ChatBind.binds.size() * rowH + 20, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Команда"),
                this.width / 2 - 30, this.height / 2 - 105, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Клавиша"),
                this.width / 2 + 82, this.height / 2 - 105, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
