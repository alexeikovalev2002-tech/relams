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
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockEspMod implements ClientModInitializer {

    // 0 = Алмазы, 1 = Железо, 2 = Уголь
    public static int oreType = 0;

    private static final Set<Block> DIAMOND_BLOCKS = new HashSet<>();
    private static final Set<Block> IRON_BLOCKS = new HashSet<>();
    private static final Set<Block> COAL_BLOCKS = new HashSet<>();

    public static boolean espEnabled = true;

    private static KeyBinding openMenuKey;

    private static final List<BlockPos> foundBlocks = new ArrayList<>();
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
        DIAMOND_BLOCKS.add(Blocks.DIAMOND_ORE);
        DIAMOND_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);

        IRON_BLOCKS.add(Blocks.IRON_ORE);
        IRON_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);

        COAL_BLOCKS.add(Blocks.COAL_ORE);
        COAL_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
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

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.mog-mod.keys"
        ));

        Fullbright.register();
        Freecam.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new EspMenuScreen());
                }
            }

            Fullbright.tick();
            Freecam.tick(client);

            if (!espEnabled || client.world == null || client.player == null) return;

            tickCounter++;
            if (tickCounter < 10) return;
            tickCounter = 0;

            foundBlocks.clear();
            BlockPos playerPos = client.player.getBlockPos();
            int radius = 24;

            Set<Block> targets = getTargetBlocks();

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius))) {

                BlockState state = client.world.getBlockState(pos);
                if (targets.contains(state.getBlock())) {
                    foundBlocks.add(pos.toImmutable());
                }
            }
        });

        WorldRenderEvents.LAST.register((worldRenderContext) -> {
            if (!espEnabled || foundBlocks.isEmpty()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;

            MatrixStack matrices = worldRenderContext.matrixStack();
            if (matrices == null) return;

            Vec3d cameraPos = worldRenderContext.camera().getPos();
            VertexConsumerProvider consumers = worldRenderContext.consumers();
            if (consumers == null) return;

            VertexConsumer buffer = consumers.getBuffer(THROUGH_WALLS);

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
        });
    }
}
