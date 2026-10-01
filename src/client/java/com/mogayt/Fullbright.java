package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright {

    public static boolean enabled = false;
    private static boolean wasEnabled = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        if (enabled) {
            StatusEffectInstance current = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < 400) {
                client.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        1_000_000,
                        0,
                        false,
                        false,
                        false
                ));
            }
        } else if (wasEnabled) {
            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }

        wasEnabled = enabled;
    }
}
