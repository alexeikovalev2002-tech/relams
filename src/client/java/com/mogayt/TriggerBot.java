package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public class TriggerBot {

    public static boolean enabled = false;
    
    // Настройки целей
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;
    
    // Настройки атаки
    public static boolean onlyCrits = false;
    public static double delayMin = 0.1;
    public static double delayMax = 0.3;
    
    private static long lastAttackTime = 0;
    private static long nextAttackDelay = 0;
    
    // Флаг для предотвращения закликивания во время крита
    private static boolean hasCritThisJump = false;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        // Сбрасываем флаг крита, когда мы на земле
        if (client.player.isOnGround()) {
            hasCritThisJump = false;
        }

        // === 1. ПРОВЕРКА КРИТОВ ===
        if (onlyCrits) {
            // Если мы уже ударили критом в этом прыжке — ждем приземления
            if (hasCritThisJump) return; 
            
            // Проверка на падение
            if (client.player.getVelocity().y >= -0.05) return; 
            
            // Проверка на воду, лестницы и т.д.
            if (client.player.isTouchingWater() || client.player.isClimbing() || client.player.hasVehicle()) return;
        } else {
            // Для обычного режима проверяем кулдаун атаки
            if (client.player.getAttackCooldownProgress(0.0f) < 0.9f) return;
        }

        // === 2. ПОИСК ЦЕЛИ (Только то, на что смотрит прицел) ===
        if (!(client.crosshairTarget instanceof EntityHitResult hitResult)) return;
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;
        if (target == client.player || !target.isAlive()) return;

        boolean isPlayer = target instanceof PlayerEntity;
        boolean isMob = target instanceof MobEntity;

        if (isPlayer && !targetPlayers) return;
        if (isMob && !targetMobs) return;
        if (!isPlayer && !isMob) return;

        // === 3. ЗАДЕРЖКА И УДАР ===
        long now = System.currentTimeMillis();
        
        // Для критов задержка не нужна, для обычного режима — рандомная
        long currentDelay = onlyCrits ? 0 : nextAttackDelay;

        if (now - lastAttackTime >= currentDelay) {
            
            // Временно отключаем спринт (он отменяет крит)
            boolean wasSprinting = client.player.isSprinting();
            if (wasSprinting) client.player.setSprinting(false);
            
            // Наносим удар
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            
            // Возвращаем спринт
            if (wasSprinting) client.player.setSprinting(true);
            
            lastAttackTime = now;

            // Если это был крит — ставим флаг, чтобы не кликать дальше в воздухе
            if (onlyCrits) {
                hasCritThisJump = true;
            } else {
                // Если обычный режим — генерируем следующую задержку
                double min = Math.min(delayMin, delayMax);
                double max = Math.max(delayMin, delayMax);
                double randomSec = min + (Math.random() * (max - min));
                nextAttackDelay = (long) (randomSec * 1000);
            }
        }
    }
}
