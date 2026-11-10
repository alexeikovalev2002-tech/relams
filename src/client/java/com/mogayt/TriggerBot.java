package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
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

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        // 1. Проверка на криты (Точная ванильная проверка)
        if (onlyCrits) {
            boolean canCrit = client.player.fallDistance > 0.0F 
                    && !client.player.isOnGround() 
                    && !client.player.isClimbing() 
                    && !client.player.isTouchingWater() 
                    && !client.player.hasVehicle() 
                    && !client.player.hasStatusEffect(StatusEffects.BLINDNESS);
            
            if (!canCrit) return; // Если не падаем — не бьем
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
        if (!isPlayer && !isMob) return;

        // 4. Проверка задержки
        long now = System.currentTimeMillis();
        
        // Если это первый удар, генерируем задержку сразу
        if (nextAttackDelay == 0) {
            nextAttackDelay = (long) ((delayMin + Math.random() * (delayMax - delayMin)) * 1000);
        }

        if (now - lastAttackTime >= nextAttackDelay) {
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            
            lastAttackTime = now;
            
            // Генерируем новую задержку на следующий удар
            double min = Math.min(delayMin, delayMax);
            double max = Math.max(delayMin, delayMax);
            double randomSec = min + (Math.random() * (max - min));
            nextAttackDelay = (long) (randomSec * 1000);
        }
    }
}
