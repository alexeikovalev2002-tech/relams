package com.mogayt;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.IntConsumer;

public class KeyBindHelper {

    public static TextFieldWidget create(TextRenderer tr, int x, int y, int w, int h, int current, IntConsumer setter) {
        TextFieldWidget f = new TextFieldWidget(tr, x, y, w, h, Text.literal("Кл."));
        f.setMaxLength(1);
        f.setText(KeyBinds.keyToLetter(current));
        f.setChangedListener(t -> setter.accept(KeyBinds.letterToKey(t)));
        return f;
    }
}
