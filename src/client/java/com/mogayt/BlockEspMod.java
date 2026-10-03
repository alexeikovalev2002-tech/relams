package com.mogayt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockEspMod implements ClientModInitializer {

    // ===== РУДЫ =====
    public static int oreType = 0; // 0=Алмазы, 1=Железо, 2=Уголь
    public static boolean espEnabled = true;

    private static final Set<Block> DIAMOND_BLOCKS = new HashSet<>();
    private static final Set<Block> IRON_BLOCKS = new HashSet<>();
    private static final Set<Block> COAL_BLOCKS = new HashSet<>();

    // ===== СУНДУКИ =====
    public static int chestType = 0; // 0=Все, 1=Обычные, 2=Шалкеры, 3=Эндер
    public static boolean chestEspEnabled = false;

    private static final Set<Block> ALL_CHESTS = new HashSet<>();
    private static final Set<Block> NORMAL_CHESTS = new HashSet<>();
    private static final Set<Block> SHULKER_CHESTS = new HashSet<>();
    private static final Set<Block> ENDER_CHESTS = new HashSet<>();

    // ===== DAЛЁКАЯ ПРОРИСОВКА =====
    public static boolean distantEnabled = false;

    private static KeyBinding openMenuKey;

    private static final List<BlockPos> foundBlocks = new ArrayList<>();
    private static final List<BlockPos> distantBlocks = new ArrayList<>();
    private static final List<BlockPos> chestBlocks = new ArrayList<>();
    private static int tickCounter = 0;

    private static final RenderLayer THROUGH_WALLS = RenderLayer.of(
            "mog-mod-through-walls",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            1536,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.LINES_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false)
    );

    static {
        // Руды
        DIAMOND_BLOCKS.add(Blocks.DIAMOND_ORE);
        DIAMOND_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);

        IRON_BLOCKS.add(Blocks.IRON_ORE);
        IRON_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);

        COAL_BLOCKS.add(Blocks.COAL_ORE);
        COAL_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);

        // Сундуки
        NORMAL_CHESTS.add(Blocks.CHEST);
        NORMAL_CHESTS.add(Blocks.TRAPPED_CHEST);

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

        ENDER_CHESTS.add(Blocks.ENDER_CHEST);

        ALL_CHESTS.addAll(NORMAL_CHESTS);
        ALL_CHESTS.addAll(SHULKER_CHESTS);
        ALL_CHESTS.addAll(ENDER_CHESTS);
    }

    public static Set<Block> getTargetBlocks() {
        switch (oreType) {
            case 1: return IRON_BLOCKS;
            case 2: return COAL_BLOCKS;
            default: return DIAMOND_BLOCKS;
        }
    }

    public static String getOreName() {
        switch (oreType) {
            case 1: return "Железо";
            case 2: return "Уголь";
            default: return "Алмазы";
        }
    }

    public static Set<Block> getChestBlocks() {
        switch (chestType) {
            case 1: return NORMAL_CHESTS;
            case 2: return SHULKER_CHESTS;
            case 3: return ENDER_CHESTS;
            default: return ALL_CHESTS;
        }
    }

    public static String getChestName() {
        switch (chestType) {
            case 1: return "Сундуки";
            case 2: return "Шалкеры";
            case 3: return "Эндер";
            default: return "Все";
        }
    }

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.mog-mod.keys"
        ));

        ChunkTracker.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new EspMenuScreen());
                }
            }

            Fullbright.tick();
            Optimizer.tick();
            Freecam.tick(client);

            if (client.player != null && client.player.age % 100 == 0) {
                ChunkTracker.cleanup();
            }

            if (client.world == null || client.player == null) return;

            tickCounter++;
            if (tickCounter < 10) return;
            tickCounter = 0;

            foundBlocks.clear();
            distantBlocks.clear();
            chestBlocks.clear();

            BlockPos playerPos = client.player.getBlockPos();
            int radius = 24;

            // ===== РУДЫ =====
            if (espEnabled) {
                Set<Block> targets = getTargetBlocks();
                for (BlockPos pos : BlockPos.iterate(
                        playerPos.add(-radius, -radius, -radius),
                        playerPos.add(radius, radius, radius))) {
                    BlockState state = client.world.getBlockState(pos);
                    if (targets.contains(state.getBlock())) {
                        foundBlocks.add(pos.toImmutable());
                    }
                }

                if (distantEnabled) {
                    for (Map.Entry<ChunkPos, Map<BlockPos, BlockState>> entry : ChunkTracker.getSavedChunks().entrySet()) {
                        ChunkPos chunkPos = entry.getKey();
                        double dist = Math.sqrt(
                                Math.pow(chunkPos.getStartX() - playerPos.getX(), 2) +
                                Math.pow(chunkPos.getStartZ() - playerPos.getZ(), 2)
                        );

                        if (dist > radius * 16) {
                            for (BlockPos pos : entry.getValue().keySet()) {
                                distantBlocks.add(pos);
                            }
                        }
                    }
                }
            }

            // ===== СУНДУКИ =====
            if (chestEspEnabled) {
                Set<Block> chests = getChestBlocks();
                for (BlockPos pos : BlockPos.iterate(
                        playerPos.add(-radius, -radius, -radius),
                        playerPos.add(radius, radius, radius))) {
                    BlockState state = client.world.getBlockState(pos);
                    if (chests.contains(state.getBlock())) {
                        chestBlocks.add(pos.toImmutable());
                    }
                }
            }
        });

        WorldRenderEvents.LAST.register((worldRenderContext) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;

            MatrixStack matrices = worldRenderContext.matrixStack();
            if (matrices == null) return;

            Vec3d cameraPos = worldRenderContext.camera().getPos();
            VertexConsumerProvider consumers = worldRenderContext.consumers();
            if (consumers == null) return;

            VertexConsumer buffer = consumers.getBuffer(THROUGH_WALLS);

            // Руды — красные
            if (espEnabled) {
                for (BlockPos pos : foundBlocks) {
                    double x = pos.getX() - cameraPos.x;
                    double y = pos.getY() - cameraPos.y;
                    double z = pos.getZ() - cameraPos.z;

                    VertexRendering.drawBox(
                            matrices, buffer,
                            x, y, z,
                            x + 1, y + 1, z + 1,
                            1.0f, 0.0f, 0.0f, 1.0f
                    );
                }

                // Далёкие — синие
                for (BlockPos pos : distantBlocks) {
                    double x = pos.getX() - cameraPos.x;
                    double y = pos.getY() - cameraPos.y;
                    double z = pos.getZ() - cameraPos.z;

                    VertexRendering.drawBox(
                            matrices, buffer,
                            x, y, z,
                            x + 1, y + 1, z + 1,
                            0.2f, 0.5f, 1.0f, 1.0f
                    );
                }
            }

            // Сундуки — зелёные
            if (chestEspEnabled) {
                for (BlockPos pos : chestBlocks) {
                    double x = pos.getX() - cameraPos.x;
                    double y = pos.getY() - cameraPos.y;
                    double z = pos.getZ() - cameraPos.z;

                    VertexRendering.drawBox(
                            matrices, buffer,
                            x, y, z,
                            x + 1, y + 1, z + 1,
                            0.0f, 1.0f, 0.0f, 1.0f
                    );
                }
            }
        });
    }
}
