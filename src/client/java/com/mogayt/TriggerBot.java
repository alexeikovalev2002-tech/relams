package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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

        // 1. ПРОВЕРКА КРИТА (Точная ванильная копия)
        if (onlyCrits) {
            boolean canCrit = client.player.fallDistance > 0.0F 
                    && !client.player.isOnGround() 
                    && !client.player.isClimbing() 
                    && !client.player.isTouchingWater() 
                    && !client.player.hasStatusEffect(StatusEffects.BLINDNESS) 
                    && !client.player.hasVehicle()
                    && !client.player.isSprinting(); // Спринт отменяет крит!
            
            if (!canCrit) return; 
        }

        // 2. ПРОВЕРКА КУЛДАУНА АТАКИ
        // Если полоска атаки не заполнена хотя бы на 90%, удар не пройдет
        if (client.player.getAttackCooldownProgress(0.5f) < 0.9f) return;

        // 3. ПОИСК ЦЕЛИ
        LivingEntity target = getTarget(client);
        if (target == null) return;

        boolean isPlayer = target instanceof PlayerEntity;
        boolean isMob = target instanceof MobEntity;

        if (isPlayer && !targetPlayers) return;
        if (isMob && !targetMobs) return;
        if (!isPlayer && !isMob) return;

        // 4. ЗАДЕРЖКА
        long now = System.currentTimeMillis();
        
        // Если включены криты, мы бьем моментально, как только кулдаун готов.
        // Иначе используем рандомную задержку из настроек.
        long currentDelay = onlyCrits ? 0 : nextAttackDelay; 

        if (now - lastAttackTime >= currentDelay) {
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            lastAttackTime = now;
            
            double min = Math.min(delayMin, delayMax);
            double max = Math.max(delayMin, delayMax);
            double randomSec = min + (Math.random() * (max - min));
            nextAttackDelay = (long) (randomSec * 1000);
        }
    }

    private static LivingEntity getTarget(MinecraftClient client) {
        // Сначала проверяем стандартный прицел
        if (client.crosshairTarget instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() instanceof LivingEntity living && living != client.player && living.isAlive()) {
                return living;
            }
        }

        // Если прицел промахнулся (например, из-за покачивания камеры при прыжке),
        // проверяем хитбоксы сущностей напрямую лучом из глаз
        Vec3d eyePos = client.player.getEyePos();
        Vec3d reachVec = client.player.getRotationVector().multiply(3.2); // Ванильная дистанция ~3 блока
        Vec3d endPos = eyePos.add(reachVec);

        for (Entity entity : client.world.getOtherEntities(client.player, client.player.getBoundingBox().expand(3.5))) {
            if (entity instanceof LivingEntity living && living.isAlive()) {
                // Проверяем, пересекает ли луч хитбокс моба
                if (living.getBoundingBox().raycast(eyePos, endPos).isPresent()) {
                    return living;
                }
            }
        }
        return null;
    }
}
