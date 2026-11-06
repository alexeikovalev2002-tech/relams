package com.mogayt;

import net.minecraft.client.MinecraftClient;
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
    
    // НОВЫЕ НАСТРОЙКИ
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;
    
    private static long lastAttack = 0;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null || client.getNetworkHandler() == null) return;
        if (client.currentScreen != null) return;

        PlayerEntity me = client.player;

        // 1. Проверяем, падаем ли мы
        if (me.fallDistance <= 0 && me.getVelocity().y >= -0.1) return;

        // 2. Ищем ближайшую цель (игрок или моб)
        LivingEntity target = null;
        double bestSq = Double.MAX_VALUE;
        Box searchBox = me.getBoundingBox().expand(6.0); // Ищем в радиусе 6 блоков
        
        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, searchBox, e -> e != me)) {
            if (!entity.isAlive()) continue;

            // ФИЛЬТРАЦИЯ ЦЕЛЕЙ
            if (entity instanceof PlayerEntity && !targetPlayers) continue;
            if (entity instanceof MobEntity && !targetMobs) continue;
            if (!(entity instanceof PlayerEntity) && !(entity instanceof MobEntity)) continue;

            double dSq = me.squaredDistanceTo(entity);
            if (dSq < bestSq) { bestSq = dSq; target = entity; }
        }

        if (target == null) return;

        double dist = Math.sqrt(bestSq);
        if (dist > 5.0) return; // Игрок/моб должен быть в радиусе 5 блоков

        // 3. Проверяем булаву в руке
        ItemStack mainHand = me.getMainHandStack();
        if (mainHand.getItem() != Items.MACE) {
            // Ищем булаву
            int maceSlot = findMace(me);
            if (maceSlot != -1) {
                int currentSlot = me.getInventory().selectedSlot;
                // Свапаем булаву в текущий слот
                client.interactionManager.clickSlot(me.playerScreenHandler.syncId, maceSlot, currentSlot, SlotActionType.SWAP, me);
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
            }
            return; // Ждём следующего тика, чтобы ударить
        }

        // 4. Если булава в руке и мы близко — бьём
        if (dist <= 4.0) {
            // Наводим камеру на цель
            Vec3d targetEye = target.getEyePos();
            Vec3d eye = me.getEyePos();
            double dx = targetEye.x - eye.x;
            double dy = targetEye.y - eye.y;
            double dz = targetEye.z - eye.z;
            double horiz = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));

            me.prevYaw = me.getYaw();
            me.prevPitch = me.getPitch();
            me.setYaw(targetYaw);
            me.setPitch(targetPitch);

            // Бьём раз в секунду
            long now = System.currentTimeMillis();
            if (now - lastAttack >= 1000) {
                client.interactionManager.attackEntity(me, target);
                me.swingHand(Hand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }

    private static int findMace(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.MACE) return i;
        }
        for (int i = 9; i < 36; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.MACE) return i;
        }
        return -1;
    }
}
