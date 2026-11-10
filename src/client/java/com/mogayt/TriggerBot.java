package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
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
    public static double delayMin = 0.1; // Минимальная задержка в секундах
    public static double delayMax = 0.3; // Максимальная задержка в секундах
    
    private static long lastAttackTime = 0;
    private static long nextAttackDelay = 0; // Рандомная задержка для следующего удара

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        // 1. Проверка на криты (если включено)
        if (onlyCrits) {
            // Бьем только если падаем (fallDistance > 0) и не находимся на земле
            if (client.player.fallDistance <= 0 || client.player.isOnGround()) {
                return;
            }
        }

        // 2. Проверяем, на что смотрит прицел
        if (!(client.crosshairTarget instanceof EntityHitResult hitResult)) return;
        
        Entity targetEntity = hitResult.getEntity();
        if (!(targetEntity instanceof LivingEntity target)) return;
        if (target == client.player || !target.isAlive()) return;

        // 3. Проверка фильтров (Игрок / Моб)
        boolean isPlayer = target instanceof PlayerEntity;
        boolean isMob = target instanceof MobEntity;

        if (isPlayer && !targetPlayers) return;
        if (isMob && !targetMobs) return;
        if (!isPlayer && !isMob) return; // Игнорируем всё остальное (например, лодки)

        // 4. Проверка задержки (Рандом)
        long now = System.currentTimeMillis();
        if (now - lastAttackTime >= nextAttackDelay) {
            // Наносим удар
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            
            // Генерируем новую задержку на следующий удар
            lastAttackTime = now;
            
            // Защита от дурака: если min > max, меняем их местами
            double min = Math.min(delayMin, delayMax);
            double max = Math.max(delayMin, delayMax);
            
            // Рандомное число от min до max (переводим в миллисекунды)
            double randomSec = min + (Math.random() * (max - min));
            nextAttackDelay = (long) (randomSec * 1000);
        }
    }
}
