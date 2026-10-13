package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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

    private static int state = 0;
    private static int timer = 0;
    private static int savedSlot = -1;
    private static int totemSlot = -1;
    private static long cooldown = 0;

    private static final int STEP_DELAY = 2;
    private static final long COOLDOWN_MS = 1000;
    private static final int OFFHAND_BUTTON = 40; // кнопка "swap with offhand"

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) { reset(); return; }
        if (client.interactionManager == null) { reset(); return; }
        if (client.getNetworkHandler() == null) { reset(); return; }

        // Если открыт любой экран — не трогаем, иначе конфликт с инвентарём
        if (client.currentScreen != null) { return; }

        long now = System.currentTimeMillis();
        if (now < cooldown) return;

        PlayerEntity p = client.player;

        // В офф-хенде уже тотем — ничего не делаем
        if (p.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) { reset(); return; }

        PlayerInventory inv = p.getInventory();

        // ===== НАЧАЛО ЛОГИКИ =====
        if (state == 0) {
            if (p.getHealth() > heartsThreshold * 2.0f) return;

            // 1) Ищем тотем в хотбаре (0-8)
            totemSlot = -1;
            for (int i = 0; i < 9; i++) {
                if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    totemSlot = i;
                    break;
                }
            }

            if (totemSlot >= 0) {
                savedSlot = inv.selectedSlot;
                state = 1;
                timer = 0;
                return;
            }

            // 2) Если тотема в хотбаре нет, а "из инвентаря" разрешено — берём из рюкзака
            if (useFromInventory) {
                for (int i = 9; i < 36; i++) {
                    if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        int syncId = p.playerScreenHandler.syncId;
                        // SWAP с кнопкой 40 = свап с офф-хендом
                        client.interactionManager.clickSlot(syncId, i, OFFHAND_BUTTON, SlotActionType.SWAP, p);
                        cooldown = now + COOLDOWN_MS;
                        reset();
                        return;
                    }
                }
            }
            return;
        }

        // ===== F-СВАП ЧЕРЕЗ ХОТБАР =====
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
                // Шаг 2: нажать F
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
