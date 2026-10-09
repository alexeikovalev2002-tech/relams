package com.mogayt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockEspMod implements ClientModInitializer {

    public static int oreType = 0;
    public static boolean espEnabled = true;
    public static int oreDistance = 24;
    public static int chestType = 0;
    public static boolean chestEspEnabled = false;
    public static int chestDistance = 24;
    public static boolean playerEspEnabled = false;
    public static int playerDistance = 64;
    public static boolean itemEspEnabled = false;
    public static int itemDistance = 32;

    public static final Set<Block> DIAMOND_BLOCKS = new HashSet<>();
    public static final Set<Block> IRON_BLOCKS = new HashSet<>();
    public static final Set<Block> COAL_BLOCKS = new HashSet<>();
    public static final Set<Block> EMERALD_BLOCKS = new HashSet<>();
    public static final Set<Block> NETHERITE_BLOCKS = new HashSet<>();
    public static final Set<Block> ALL_ORES = new HashSet<>();
    public static final Set<Block> NORMAL_CHESTS = new HashSet<>();
    public static final Set<Block> SHULKER_CHESTS = new HashSet<>();
    public static final Set<Block> BARREL_BLOCKS = new HashSet<>();
    public static final Set<Block> ENDER_CHESTS = new HashSet<>();
    public static final Set<Block> BED_BLOCKS = new HashSet<>();
    public static final Set<Block> ALL_CHESTS = new HashSet<>();

    private static KeyBinding openMenuKey;
    public static final List<PlayerEntity> foundPlayers = new ArrayList<>();
    public static final List<ItemEntity> foundItems = new ArrayList<>();
    public static final List<BlockPos> foundBlocks = new ArrayList<>();
    public static final List<BlockPos> foundChests = new ArrayList<>();
    public static final List<BlockPos> foundShulkers = new ArrayList<>();
    public static final List<BlockPos> foundBarrels = new ArrayList<>();
    public static final List<BlockPos> foundEnderChests = new ArrayList<>();
    public static final List<BlockPos> foundBeds = new ArrayList<>();
    private static int tickCounter = 0;
    private static BlockPos lastScanPos = null;

    public static final RenderLayer THROUGH_WALLS = RenderLayer.of(
            "mog-mod-through-walls", VertexFormats.LINES, VertexFormat.DrawMode.LINES,
            1536, false, true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.LINES_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false));

    static {
        DIAMOND_BLOCKS.add(Blocks.DIAMOND_ORE);
        DIAMOND_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        DIAMOND_BLOCKS.add(Blocks.DIAMOND_BLOCK);
        IRON_BLOCKS.add(Blocks.IRON_ORE);
        IRON_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        IRON_BLOCKS.add(Blocks.IRON_BLOCK);
        IRON_BLOCKS.add(Blocks.RAW_IRON_BLOCK);
        COAL_BLOCKS.add(Blocks.COAL_ORE);
        COAL_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        COAL_BLOCKS.add(Blocks.COAL_BLOCK);
        EMERALD_BLOCKS.add(Blocks.EMERALD_ORE);
        EMERALD_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        EMERALD_BLOCKS.add(Blocks.EMERALD_BLOCK);
        NETHERITE_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
        NETHERITE_BLOCKS.add(Blocks.NETHERITE_BLOCK);
        ALL_ORES.addAll(DIAMOND_BLOCKS);
        ALL_ORES.addAll(IRON_BLOCKS);
        ALL_ORES.addAll(COAL_BLOCKS);
        ALL_ORES.addAll(EMERALD_BLOCKS);
        ALL_ORES.addAll(NETHERITE_BLOCKS);
        NORMAL_CHESTS.add(Blocks.CHEST);
        NORMAL_CHESTS.add(Blocks.TRAPPED_CHEST);
        BARREL_BLOCKS.add(Blocks.BARREL);
        ENDER_CHESTS.add(Blocks.ENDER_CHEST);
        SHULKER_CHESTS.add(Blocks.SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.WHITE_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.ORANGE_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.MAGENTA_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.LIGHT_BLUE_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.YELLOW_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.LIME_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.PINK_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.GRAY_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.LIGHT_GRAY_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.CYAN_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.PURPLE_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.BLUE_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.BROWN_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.GREEN_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.RED_SHULKER_BOX);
        SHULKER_CHESTS.add(Blocks.BLACK_SHULKER_BOX);
        BED_BLOCKS.add(Blocks.WHITE_BED);
        BED_BLOCKS.add(Blocks.ORANGE_BED);
        BED_BLOCKS.add(Blocks.MAGENTA_BED);
        BED_BLOCKS.add(Blocks.LIGHT_BLUE_BED);
        BED_BLOCKS.add(Blocks.YELLOW_BED);
        BED_BLOCKS.add(Blocks.LIME_BED);
        BED_BLOCKS.add(Blocks.PINK_BED);
        BED_BLOCKS.add(Blocks.GRAY_BED);
        BED_BLOCKS.add(Blocks.LIGHT_GRAY_BED);
        BED_BLOCKS.add(Blocks.CYAN_BED);
        BED_BLOCKS.add(Blocks.PURPLE_BED);
        BED_BLOCKS.add(Blocks.BLUE_BED);
        BED_BLOCKS.add(Blocks.BROWN_BED);
        BED_BLOCKS.add(Blocks.GREEN_BED);
        BED_BLOCKS.add(Blocks.RED_BED);
        BED_BLOCKS.add(Blocks.BLACK_BED);
        ALL_CHESTS.addAll(NORMAL_CHESTS);
        ALL_CHESTS.addAll(SHULKER_CHESTS);
        ALL_CHESTS.addAll(BARREL_BLOCKS);
        ALL_CHESTS.addAll(ENDER_CHESTS);
        ALL_CHESTS.addAll(BED_BLOCKS);
    }

    public static Set<Block> getTargetBlocks() {
        switch (oreType) {
            case 1: return IRON_BLOCKS;
            case 2: return COAL_BLOCKS;
            case 3: return EMERALD_BLOCKS;
            case 4: return NETHERITE_BLOCKS;
            case 5: return ALL_ORES;
            default: return DIAMOND_BLOCKS;
        }
    }

    public static String getOreName() {
        switch (oreType) {
            case 1: return "Железо";
            case 2: return "Уголь";
            case 3: return "Изумруды";
            case 4: return "Незерит";
            case 5: return "Всё";
            default: return "Алмазы";
        }
    }

    public static String getChestName() {
        switch (chestType) {
            case 1: return "Сундуки";
            case 2: return "Шалкеры";
            case 3: return "Бочки";
            case 4: return "Эндер";
            case 5: return "Кровати";
            default: return "Все";
        }
    }

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H,
                "category.mog-mod.keys"));

        Config.init();
        ChatBind.register();
        EspRenderer.register();
        ClientTickEvents.START_CLIENT_TICK.register(AimMobs::tick);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new EspMenuScreen());
            }
            Fullbright.tick();
            Optimizer.tick();
            Freecam.tick(client);
            if (client.player != null && client.player.age % 200 == 0) Config.save();
            if (client.world == null || client.player == null) return;

            updatePlayers(client);
            if (tickCounter % 5 == 0) updateItems(client);

            tickCounter++;
            if (tickCounter < 10) return;
            tickCounter = 0;

            BlockPos pp = client.player.getBlockPos();
            if (lastScanPos != null && lastScanPos.getSquaredDistance(pp) < 16) return;
            lastScanPos = pp;
            updateBlocks(client, pp);
        });
    }

    private static void updatePlayers(MinecraftClient client) {
        foundPlayers.clear();
        if (!playerEspEnabled) return;
        double maxSq = (double) playerDistance * playerDistance;
        for (PlayerEntity p : client.world.getPlayers()) {
            if (p == client.player) continue;
            if (p.squaredDistanceTo(client.player) <= maxSq) foundPlayers.add(p);
        }
    }

    private static void updateItems(MinecraftClient client) {
        foundItems.clear();
        if (!itemEspEnabled) return;
        double maxSq = (double) itemDistance * itemDistance;
        for (ItemEntity it : client.world.getEntitiesByClass(ItemEntity.class,
                client.player.getBoundingBox().expand(itemDistance), e -> true)) {
            if (it.squaredDistanceTo(client.player) <= maxSq) foundItems.add(it);
        }
    }

    private static void updateBlocks(MinecraftClient client, BlockPos pp) {
        foundBlocks.clear();
        foundChests.clear();
        foundShulkers.clear();
        foundBarrels.clear();
        foundEnderChests.clear();
        foundBeds.clear();

        if (!espEnabled && !chestEspEnabled) return;

        int blockRadius = 0;
        if (espEnabled) blockRadius = Math.max(blockRadius, oreDistance);
        if (chestEspEnabled) blockRadius = Math.max(blockRadius, chestDistance);
        if (blockRadius > 32) blockRadius = 32;
        if (blockRadius <= 0) return;

        double oreSq = (double) oreDistance * oreDistance;
        double chestSq = (double) chestDistance * chestDistance;
        Set<Block> oreTargets = espEnabled ? getTargetBlocks() : null;

        for (BlockPos pos : BlockPos.iterate(
                pp.add(-blockRadius, -blockRadius, -blockRadius),
                pp.add(blockRadius, blockRadius, blockRadius))) {

            double distSq = pos.getSquaredDistance(pp);
            BlockState state = client.world.getBlockState(pos);
            Block b = state.getBlock();

            if (espEnabled && oreTargets.contains(b)) {
                if (distSq <= oreSq) foundBlocks.add(pos.toImmutable());
                continue;
            }
            if (chestEspEnabled && ALL_CHESTS.contains(b)) {
                if (distSq > chestSq) continue;
                if (chestType == 1 && !NORMAL_CHESTS.contains(b)) continue;
                if (chestType == 2 && !SHULKER_CHESTS.contains(b)) continue;
                if (chestType == 3 && !BARREL_BLOCKS.contains(b)) continue;
                if (chestType == 4 && !ENDER_CHESTS.contains(b)) continue;
                if (chestType == 5 && !BED_BLOCKS.contains(b)) continue;
                BlockPos im = pos.toImmutable();
                if (NORMAL_CHESTS.contains(b)) foundChests.add(im);
                else if (SHULKER_CHESTS.contains(b)) foundShulkers.add(im);
                else if (BARREL_BLOCKS.contains(b)) foundBarrels.add(im);
                else if (ENDER_CHESTS.contains(b)) foundEnderChests.add(im);
                else if (BED_BLOCKS.contains(b)) foundBeds.add(im);
            }
        }
    }
}
