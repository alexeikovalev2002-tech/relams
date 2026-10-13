package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
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

    // 0 = idle, 1 = открыть инвентарь, 2 = свап, 3 = закрыть
    private static int state = 0;
    private static int timer = 0;
    private static int savedSlot = -1;
    private static int totemSlot = -1;
    private static long cooldown = 0;

    private static final int STEP_DELAY = 3; // 3 тика ≈ 150 мс
    private static final long COOLDOWN_MS = 1000;
    private static final int OFFHAND_BUTTON = 40;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) { reset(); return; }
        if (client.interactionManager == null) { reset(); return; }
        if (client.getNetworkHandler() == null) { reset(); return; }

        long now = System.currentTimeMillis();

        // ================== ОТКРЫТИЕ ИНВЕНТАРЯ ==================
        if (state == 1) {
            client.setScreen(new InventoryScreen(client.player));
            state = 2;
            timer = 0;
            return;
        }

        // ================== СВАП ЧЕРЕЗ ИНВЕНТАРЬ ==================
        if (state == 2) {
            timer++;
            if (timer < STEP_DELAY) return;
            timer = 0;

            // Проверяем что InventoryScreen действительно открыт
            if (!(client.currentScreen instanceof InventoryScreen)) {
                // Не открылся — откатываемся
                reset();
                return;
            }

            int syncId = client.player.playerScreenHandler.syncId;

            // Ищем тотем в инвентаре (9-35 = рюкзак)
            for (int i = 9; i < 36; i++) {
                if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    client.interactionManager.clickSlot(syncId, i, OFFHAND_BUTTON, SlotActionType.SWAP, client.player);
                    state = 3;
                    timer = 0;
                    return;
                }
            }

            // Не нашли — закрываем и откат
            state = 3;
            timer = 0;
            return;
        }

        // ================== ЗАКРЫТИЕ ==================
        if (state == 3) {
            timer++;
            if (timer < STEP_DELAY) return;
            timer = 0;

            client.setScreen(null);
            client.player.closeHandledScreen();
            cooldown = now + COOLDOWN_MS;
            reset();
            return;
        }

        // ================== ХОТБАР ==================
        // Пока открыт любой экран — ждём
        if (client.currentScreen != null) return;
        if (now < cooldown) return;

        PlayerEntity p = client.player;
        if (p.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) { reset(); return; }

        PlayerInventory inv = p.getInventory();

        if (state == 0) {
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
                savedSlot = inv.selectedSlot;
                state = 10; // хотбар-флоу
                timer = 0;
                return;
            }

            // Тотем в рюкзаке — открываем инвентарь
            if (useFromInventory) {
                for (int i = 9; i < 36; i++) {
                    if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        state = 1; // открыть инвентарь
                        timer = 0;
                        return;
                    }
                }
            }
            return;
        }

        // ================== ХОТБАР-ФЛОУ (state = 10, 11, 12) ==================
        if (state >= 10) {
            timer++;
            if (timer < STEP_DELAY) return;
            timer = 0;

            if (state == 10) {
                inv.selectedSlot = totemSlot;
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(totemSlot));
                state = 11;
            } else if (state == 11) {
                client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                        BlockPos.ORIGIN, Direction.DOWN));
                state = 12;
            } else if (state == 12) {
                inv.selectedSlot = savedSlot;
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(savedSlot));
                cooldown = now + COOLDOWN_MS;
                reset();
            }
        }
    }

    private static void reset() {
        state = 0;
        timer = 0;
        savedSlot = -1;
        totemSlot = -1;
    }
}
