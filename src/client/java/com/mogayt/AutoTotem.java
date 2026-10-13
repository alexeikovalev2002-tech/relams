package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTotem {

    public static boolean enabled = false;
    public static float heartsThreshold = 5.0f;
    public static boolean useFromInventory = false;

    // Состояние конечного автомата
    private static int state = 0;
    private static int timer = 0;
    private static int savedSlot = -1;
    private static int totemSlot = -1;
    private static long cooldown = 0;

    // Задержка между действиями — 2 тика (~100 мс), быстрее уже нельзя
    private static final int STEP_DELAY = 2;
    private static final long COOLDOWN_MS = 1000;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) { reset(); return; }
        if (client.interactionManager == null) { reset(); return; }
        if (client.getNetworkHandler() == null) { reset(); return; }

        long now = System.currentTimeMillis();
        if (now < cooldown) return;

        PlayerEntity p = client.player;

        // Если в офф-хенде уже тотем — ничего не делаем
        if (p.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) { reset(); return; }

        PlayerInventory inv = p.getInventory();

        // === НАЧАЛО ===
        if (state == 0) {
            // Порог хп не достигнут
            if (p.getHealth() > heartsThreshold * 2.0f) return;

            // Ищем тотем в хотбаре (0-8)
            totemSlot = -1;
            for (int i = 0; i < 9; i++) {
                if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    totemSlot = i;
                    break;
                }
            }

            if (totemSlot >= 0) {
                // Случай 1: тотем в хотбаре — F-свап с запоминанием слота
                savedSlot = inv.selectedSlot;
                state = 1;
                timer = 0;
                return;
            }

            if (useFromInventory) {
                // Случай 2: тотем в основном инвентаре (9-35)
                for (int i = 9; i < 36; i++) {
                    if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        // Свапаем напрямую в офф-хенд (слот 45 в PlayerScreenHandler)
                        int syncId = p.playerScreenHandler.syncId;
                        client.interactionManager.clickSlot(syncId, i, 45, SlotActionType.SWAP, p);
                        cooldown = now + COOLDOWN_MS;
                        reset();
                        return;
                    }
                }
            }
            return;
        }

        // === F-СВАП ЧЕРЕЗ ХОТБАР ===
        timer++;
        if (timer < STEP_DELAY) return;
        timer = 0;

        switch (state) {
            case 1:
                // Шаг 1: переключиться на слот с тотемом
                inv.selectedSlot = totemSlot;
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(totemSlot));
                state = 2;
                break;

            case 2:
                // Шаг 2: нажать F (свап с офф-хендом)
                client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                        BlockPos.ORIGIN, Direction.DOWN));
                state = 3;
                break;

            case 3:
                // Шаг 3: вернуть прежний слот
                inv.selectedSlot = savedSlot;
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(savedSlot));
                cooldown = now + COOLDOWN_MS;
                reset();
                break;
        }
    }

    private static void reset() {
        state = 0;
        timer = 0;
        savedSlot = -1;
        totemSlot = -1;
    }
}
