package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class TriggerBot {

    public static boolean enabled = false;
    
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;
    
    public static boolean onlyCrits = false;
    public static double delayMin = 0.1;
    public static double delayMax = 0.3;
    
    private static long lastAttackTime = 0;
    private static long nextAttackDelay = 0;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        // === ПРОВЕРКА КРИТОВ ===
        if (onlyCrits) {
            // Если стоим на земле — крита не будет
            if (client.player.isOnGround()) return;
            // Если не падаем (скорость по Y не отрицательная) — крита не будет
            if (client.player.getVelocity().y >= -0.05) return;
            // В воде, на лестнице, в лодке — крита не будет
            if (client.player.isTouchingWater() || client.player.isClimbing() || client.player.hasVehicle()) return;
        } else {
            // Для обычного режима проверяем кулдаун атаки
            if (client.player.getAttackCooldownProgress(0.0f) < 0.9f) return;
        }

        // === ПОИСК ЦЕЛИ (без привязки к камере) ===
        LivingEntity target = getClosestTarget(client);
        if (target == null) return;

        boolean isPlayer = target instanceof PlayerEntity;
        boolean isMob = target instanceof MobEntity;

        if (isPlayer && !targetPlayers) return;
        if (isMob && !targetMobs) return;
        if (!isPlayer && !isMob) return;

        // === УДАР ===
        long now = System.currentTimeMillis();
        long currentDelay = onlyCrits ? 50 : nextAttackDelay;

        if (now - lastAttackTime >= currentDelay) {
            // Временно отключаем спринт, чтобы крит прошел
            boolean wasSprinting = client.player.isSprinting();
            if (wasSprinting) client.player.setSprinting(false);
            
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            
            if (wasSprinting) client.player.setSprinting(true);
            
            lastAttackTime = now;
            
            double min = Math.min(delayMin, delayMax);
            double max = Math.max(delayMin, delayMax);
            double randomSec = min + (Math.random() * (max - min));
            nextAttackDelay = (long) (randomSec * 1000);
        }
    }

    // Поиск ближайшего моба/игрока в радиусе 4 блоков
    private static LivingEntity getClosestTarget(MinecraftClient client) {
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        
        for (Entity entity : client.world.getOtherEntities(client.player, client.player.getBoundingBox().expand(4.0))) {
            if (entity instanceof LivingEntity living && living != client.player && living.isAlive()) {
                double dist = client.player.squaredDistanceTo(living);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = living;
                }
            }
        }
        return closest;
    }
}
