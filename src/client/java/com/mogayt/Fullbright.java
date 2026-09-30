package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

import java.lang.reflect.Field;

public class Fullbright {

    public static boolean enabled = false;
    private static final double NORMAL_GAMMA = 0.5;
    private static final double MAX_GAMMA = 15.0;

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        SimpleOption<Double> gammaOption = client.options.getGamma();
        try {
            Field valueField = SimpleOption.class.getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(gammaOption, enabled ? MAX_GAMMA : NORMAL_GAMMA);
        } catch (Exception e) {
            // Запасной вариант, если рефлексия не сработала
            gammaOption.setValue(enabled ? 1.0 : NORMAL_GAMMA);
        }
    }
}
