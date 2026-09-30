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

    private static final Set<Block> TARGET_BLOCKS = new HashSet<>();
    public static boolean espEnabled = true;

    private static KeyBinding openMenuKey;

    private static final List<BlockPos> foundBlocks = new ArrayList<>();
    private static int tickCounter = 0;

    // Кастомный слой рендера: рисует СКВОЗЬ СТЕНЫ
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
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)   // всегда поверх
                    .writeMaskState(RenderPhase.COLOR_MASK)     // не пишем в глубину
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false)
    );

    static {
        TARGET_BLOCKS.add(Blocks.DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
        // Золото убрано
    }

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.mog-mod.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new EspMenuScreen());
                }
            }

            if (!espEnabled || client.world == null || client.player == null) return;

            tickCounter++;
            if (tickCounter < 10) return;
            tickCounter = 0;

            foundBlocks.clear();
            BlockPos playerPos = client.player.getBlockPos();
            int radius = 16;

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius))) {

                BlockState state = client.world.getBlockState(pos);
                if (TARGET_BLOCKS.contains(state.getBlock())) {
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

            // Используем наш слой со сквозным рендером
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
