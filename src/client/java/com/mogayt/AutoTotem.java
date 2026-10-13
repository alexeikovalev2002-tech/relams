package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem {

    public static boolean enabled = false;
    public static float heartsThreshold = 5.0f; // половина сердец: 1 сердце = 2 hp
    public static boolean useFromInventory = false;

    private static long lastAction = 0;
    private static final long COOLDOWN = 1000; // 1 сек

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (client.interactionManager == null) return;

        long now = System.currentTimeMillis();
        if (now - lastAction < COOLDOWN) return;

        PlayerEntity p = client.player;
        if (p.getHealth() > heartsThreshold * 2.0f) return;

        // Если в офф-хенде уже тотем — выходим
        if (p.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        PlayerInventory inv = p.getInventory();

        // 1. Ищем тотем в хотбаре (0-8)
        int hotbarSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                hotbarSlot = i;
                break;
            }
        }

        // 2. Если не нашли — ищем в основном инвентаре (9-35)
        int mainSlot = -1;
        if (hotbarSlot == -1 && useFromInventory) {
            for (int i = 9; i < 36; i++) {
                if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    mainSlot = i;
                    break;
                }
            }
        }

        if (hotbarSlot == -1 && mainSlot == -1) return;

        int syncId = p.playerScreenHandler.syncId;

        // Если тотем в основном инвентаре — сначала перекидываем в хотбар слот 0
        if (mainSlot != -1) {
            client.interactionManager.clickSlot(syncId, mainSlot, 0, SlotActionType.SWAP, p);
            hotbarSlot = 0;
        }

        // Свапаем хотбар-слот с офф-хендом
        // В PlayerScreenHandler: хотбар 0-8 = слоты 36-44, офф-хенд = слот 45
        client.interactionManager.clickSlot(syncId, 36 + hotbarSlot, 40, SlotActionType.SWAP, p);

        lastAction = now;
    }
}
