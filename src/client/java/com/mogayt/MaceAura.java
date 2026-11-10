package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MaceAura {

    public static boolean enabled = false;
    
    // Настройки целей (сохранены из предыдущих обновлений)
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;
    
    private static long lastAttack = 0;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null || client.getNetworkHandler() == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity me = client.player;

        // 1. Проверяем, падаем ли мы (работает только в падении)
        if (me.fallDistance <= 0 && me.getVelocity().y >= -0.1) return;

        // 2. Ищем ближайшую цель
        LivingEntity target = null;
        double bestSq = Double.MAX_VALUE;
        Box searchBox = me.getBoundingBox().expand(6.0); // Радиус поиска
        
        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, searchBox, e -> e != me)) {
            if (!entity.isAlive()) continue;

            // Фильтрация: Игроки / Мобы
            if (entity instanceof PlayerEntity && !targetPlayers) continue;
            if (entity instanceof MobEntity && !targetMobs) continue;
            if (!(entity instanceof PlayerEntity) && !(entity instanceof MobEntity)) continue;

            // ГЛАВНОЕ УСЛОВИЕ: Цель должна быть НИЖЕ игрока (по Y)
            // Если ноги цели выше или на уровне ног игрока — пропускаем
            if (entity.getY() >= me.getY()) continue;

            double dSq = me.squaredDistanceTo(entity);
            if (dSq < bestSq) { 
                bestSq = dSq; 
                target = entity; 
            }
        }

        if (target == null) return;

        double dist = Math.sqrt(bestSq);
        if (dist > 5.0) return; // Максимальная дистанция удара

        // 3. Проверяем булаву в руке
        ItemStack mainHand = me.getMainHandStack();
        if (mainHand.getItem() != Items.MACE) {
            // Если булавы нет, ищем её в инвентаре и свапаем
            int maceSlot = findMace(me);
            if (maceSlot != -1) {
                int currentSlot = me.getInventory().selectedSlot;
                client.interactionManager.clickSlot(me.playerScreenHandler.syncId, maceSlot, currentSlot, SlotActionType.SWAP, me);
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
            }
            return; // Ждём следующего тика, чтобы ударить
        }

        // 4. Мгновенное наведение и удар
        if (dist <= 4.5) {
            Vec3d targetEye = target.getEyePos();
            Vec3d eye = me.getEyePos();
            double dx = targetEye.x - eye.x;
            double dy = targetEye.y - eye.y;
            double dz = targetEye.z - eye.z;
            double horiz = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));

            // МГНОВЕННЫЙ ПОВОРОТ КАМЕРЫ (телепортация без плавности)
            me.setYaw(targetYaw);
            me.setPitch(targetPitch);
            me.prevYaw = targetYaw;
            me.prevPitch = targetPitch;

            // МГНОВЕННЫЙ УДАР
            long now = System.currentTimeMillis();
            // Задержка 500 мс, чтобы не спамить пакетами каждый кадр
            if (now - lastAttack >= 500) {
                client.interactionManager.attackEntity(me, target);
                me.swingHand(Hand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }

    private static int findMace(PlayerEntity player) {
        // Ищем в хотбаре (0-8)
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.MACE) return i;
        }
        // Ищем в инвентаре (9-35)
        for (int i = 9; i < 36; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.MACE) return i;
        }
        return -1;
    }
}
