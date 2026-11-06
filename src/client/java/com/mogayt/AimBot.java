package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AimBot {

    public static boolean enabled = false;
    public static double distance = 4.5;
    
    // НОВЫЕ НАСТРОЙКИ
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        PlayerEntity me = client.player;
        double maxSq = distance * distance;
        LivingEntity target = null;
        double bestSq = Double.MAX_VALUE;
        
        Box searchBox = me.getBoundingBox().expand(distance);
        
        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, searchBox, e -> e != me)) {
            if (!entity.isAlive()) continue;
            
            // ФИЛЬТРАЦИЯ ЦЕЛЕЙ
            if (entity instanceof PlayerEntity && !targetPlayers) continue;
            if (entity instanceof MobEntity && !targetMobs) continue;
            if (!(entity instanceof PlayerEntity) && !(entity instanceof MobEntity)) continue;

            double dSq = me.squaredDistanceTo(entity);
            if (dSq > maxSq) continue;
            if (dSq < bestSq) { bestSq = dSq; target = entity; }
        }
        if (target == null) return;

        Vec3d eye = me.getEyePos();
        Vec3d targetEye = target.getEyePos();
        double dx = targetEye.x - eye.x;
        double dz = targetEye.z - eye.z;

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

        me.setYaw(targetYaw);
    }
}
