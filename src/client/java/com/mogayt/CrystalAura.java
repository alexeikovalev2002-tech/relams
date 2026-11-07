package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class CrystalAura {

    public static boolean enabled = false;
    public static double range = 4.5;
    public static double aimSpeed = 15.0;
    
    private static long lastAttack = 0;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        PlayerEntity me = client.player;
        EndCrystalEntity target = null;
        double bestDistSq = Double.MAX_VALUE;

        // Ищем кристаллы в радиусе
        Box searchBox = me.getBoundingBox().expand(range);
        for (EndCrystalEntity crystal : client.world.getEntitiesByClass(EndCrystalEntity.class, searchBox, e -> true)) {
            if (!crystal.isAlive()) continue;
            
            // Проверка высоты: если игрок выше кристалла (или на его уровне), не бьем, чтобы не взорваться
            if (me.getY() >= crystal.getY() - 0.2) continue; 

            double distSq = me.squaredDistanceTo(crystal);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                target = crystal;
            }
        }

        if (target == null) return;

        Vec3d eyePos = me.getEyePos();
        // Берем центр хитбокса кристалла
        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
        
        double dx = targetPos.x - eyePos.x;
        double dy = targetPos.y - eyePos.y;
        double dz = targetPos.z - eyePos.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));

        // Плавное наведение
        float yawDiff = targetYaw - me.getYaw();
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float pitchDiff = targetPitch - me.getPitch();
        float factor = (float) (aimSpeed / 100.0);
        if (factor > 1.0f) factor = 1.0f;
        if (factor < 0.01f) factor = 0.01f;

        me.prevYaw = me.getYaw();
        me.prevPitch = me.getPitch();
        me.setYaw(me.getYaw() + yawDiff * factor);
        me.setPitch(me.getPitch() + pitchDiff * factor);

        // Проверяем, попал ли прицел в кристалл
        Vec3d look = me.getRotationVector();
        Vec3d toTarget = targetPos.subtract(eyePos).normalize();
        double dot = look.dotProduct(toTarget);

        // 0.99 означает, что прицел смотрит почти точно в центр (около 8 градусов погрешности)
        if (dot > 0.99) {
            long now = System.currentTimeMillis();
            if (now - lastAttack >= 100) { // Бьем не чаще раза в 100 мс
                client.interactionManager.attackEntity(me, target);
                me.swingHand(Hand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }
}
