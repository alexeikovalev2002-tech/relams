package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

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

        // 1. Проверка на криты
        if (onlyCrits) {
            // Если мы на земле, в воде, на лестнице или в транспорте — крита не будет
            if (client.player.isOnGround() || client.player.isClimbing() || 
                client.player.isTouchingWater() || client.player.hasVehicle() || 
                client.player.fallDistance <= 0.0f) {
                return;
            }
        }

        // 2. Ищем цель через Raycast (надежнее, чем crosshairTarget)
        LivingEntity target = getTarget(client);
        if (target == null) return;

        // 3. Проверка фильтров (Игрок / Моб)
        boolean isPlayer = target instanceof PlayerEntity;
        boolean isMob = target instanceof MobEntity;

        if (isPlayer && !targetPlayers) return;
        if (isMob && !targetMobs) return;
        if (!isPlayer && !isMob) return;

        // 4. Проверка задержки
        long now = System.currentTimeMillis();
        
        // Если включены криты, мы НЕ ждем задержку, а бьем сразу (окно крита очень маленькое)
        // В остальных случаях работает рандомная задержка
        long currentDelay = onlyCrits ? 50 : nextAttackDelay; 

        if (now - lastAttackTime >= currentDelay) {
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            
            lastAttackTime = now;
            
            // Генерируем новую задержку на следующий удар (для обычного режима)
            double min = Math.min(delayMin, delayMax);
            double max = Math.max(delayMin, delayMax);
            double randomSec = min + (Math.random() * (max - min));
            nextAttackDelay = (long) (randomSec * 1000);
        }
    }

    // Метод для поиска цели через Raycast
    private static LivingEntity getTarget(MinecraftClient client) {
        // Сначала проверяем стандартный crosshairTarget (если попали мышкой)
        if (client.crosshairTarget instanceof EntityHitResult hitResult) {
            if (hitResult.getEntity() instanceof LivingEntity living && living != client.player && living.isAlive()) {
                return living;
            }
        }

        // Если crosshairTarget не сработал (например, смотрим на блок), 
        // пускаем луч из глаз и ищем ближайшую сущность
        Vec3d eyePos = client.player.getEyePos();
        Vec3d lookVec = client.player.getRotationVector();
        Vec3d endPos = eyePos.add(lookVec.multiply(3.5)); // 3.5 блока дистанция удара

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        // Ищем сущности в радиусе 4 блоков
        for (Entity entity : client.world.getEntitiesByClass(LivingEntity.class, client.player.getBoundingBox().expand(4.0), e -> e != client.player && e.isAlive())) {
            LivingEntity living = (LivingEntity) entity;
            
            // Проверяем, пересекает ли луч хитбокс сущности (с небольшим запасом 0.1)
            if (living.getBoundingBox().expand(0.1).raycast(eyePos, endPos).isPresent()) {
                double dist = eyePos.squaredDistanceTo(living.getPos());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = living;
                }
            }
        }

        return closest;
    }
}
