package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright {

    public static boolean enabled = false;
    private static boolean wasEnabled = false;

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        if (enabled) {
            StatusEffectInstance current = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
            // Если эффекта нет или он скоро кончится — обновляем
            if (current == null || current.getDuration() < 400) {
                client.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        1_000_000,  // ~13.8 часов — фактически бесконечно
                        0,          // уровень I
                        false,      // не ambient
                        false,      // без частиц
                        false       // без иконки на HUD
                ));
            }
        } else if (wasEnabled) {
            // Выключаем — убираем эффект
            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }

        wasEnabled = enabled;
    }
}
