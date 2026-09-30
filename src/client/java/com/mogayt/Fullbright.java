package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class Fullbright {

    public static boolean enabled = false;
    private static final double NORMAL_GAMMA = 0.5;
    private static final double MAX_GAMMA = 10.0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.options == null) return;
            if (enabled) {
                client.options.getGamma().setValue(MAX_GAMMA);
            } else {
                client.options.getGamma().setValue(NORMAL_GAMMA);
            }
        });
    }
}
