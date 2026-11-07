package com.mogayt;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AnchorAura {

    public static boolean enabled = false;
    public static double range = 5.0; // Радиус поиска игроков и якоря

    private static long lastClickTime = 0;
    private static int clicks = 0;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null || client.getNetworkHandler() == null) return;
        if (client.currentScreen != null) return;

        PlayerEntity me = client.player;

        // 1. Проверяем, есть ли игроки в радиусе
        boolean playerNearby = false;
        for (PlayerEntity p : client.world.getPlayers()) {
            if (p != me && p.isAlive() && me.squaredDistanceTo(p) <= (range * range)) {
                playerNearby = true;
                break;
            }
        }
        if (!playerNearby) {
            clicks = 0; // Сбрасываем, если никого нет
            return;
        }

        // 2. Ищем якорь в радиусе 4 блоков от нас
        BlockPos anchorPos = findNearbyAnchor(client, me);
        if (anchorPos == null) {
            clicks = 0; // Якоря нет — сбрасываем счетчик
            return;
        }

        // 3. Проверяем, есть ли светокамень в руке
        ItemStack mainHand = me.getMainHandStack();
        if (mainHand.getItem() != Items.GLOWSTONE) {
            int glowstoneSlot = findGlowstone(me);
            if (glowstoneSlot != -1) {
                int currentSlot = me.getInventory().selectedSlot;
                // Меняем слоты
                client.interactionManager.clickSlot(me.playerScreenHandler.syncId, glowstoneSlot, currentSlot, SlotActionType.SWAP, me);
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
            }
            return; // Ждем следующего тика, чтобы кликнуть
        }

        // 4. Кликаем по якорю (1 раз в 0.5 сек)
        long now = System.currentTimeMillis();
        if (now - lastClickTime >= 500) {
            if (clicks < 5) {
                Vec3d hitVec = Vec3d.ofCenter(anchorPos);
                BlockHitResult hit = new BlockHitResult(hitVec, Direction.UP, anchorPos, false);
                
                client.interactionManager.interactBlock(me, Hand.MAIN_HAND, hit);
                me.swingHand(Hand.MAIN_HAND);
                
                lastClickTime = now;
                clicks++;
            }
        }
    }

    private static BlockPos findNearbyAnchor(MinecraftClient client, PlayerEntity me) {
        BlockPos pp = me.getBlockPos();
        // Ищем якорь в радиусе 4 блоков по горизонтали и вертикали
        for (BlockPos pos : BlockPos.iterate(pp.add(-4, -2, -4), pp.add(4, 2, 4))) {
            if (client.world.getBlockState(pos).isOf(Blocks.RESPAWN_ANCHOR)) {
                return pos.toImmutable();
            }
        }
        return null;
    }

    private static int findGlowstone(PlayerEntity player) {
        // Ищем в хотбаре (0-8)
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.GLOWSTONE) return i;
        }
        // Ищем в инвентаре (9-35)
        for (int i = 9; i < 36; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.GLOWSTONE) return i;
        }
        return -1;
    }
}
