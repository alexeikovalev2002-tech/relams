package com.mogayt;

import com.mojang.blaze3d.systems.RenderSystem; // <-- ДОБАВЛЕН ИМПОРТ
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
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class BlockEspMod implements ClientModInitializer {

    private static final Set<Block> TARGET_BLOCKS = new HashSet<>();
    public static boolean espEnabled = true;

    private static KeyBinding openMenuKey;

    static {
        TARGET_BLOCKS.add(Blocks.DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
        TARGET_BLOCKS.add(Blocks.GOLD_ORE);
        TARGET_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
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
        });

        WorldRenderEvents.LAST.register((worldRenderContext) -> {
            if (!espEnabled) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;

            MatrixStack matrices = worldRenderContext.matrixStack();
            if (matrices == null) return;

            Vec3d cameraPos = worldRenderContext.camera().getPos();
            VertexConsumerProvider consumers = worldRenderContext.consumers();
            if (consumers == null) return;

            // Отключаем тест глубины, чтобы рисовать сквозь стены
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            // ИСПРАВЛЕНО: Добавлен аргумент 1.0 (толщина линии)
            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();

            int radius = 24;
            BlockPos playerPos = client.player.getBlockPos();

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius))) {

                BlockState state = client.world.getBlockState(pos);
                if (TARGET_BLOCKS.contains(state.getBlock())) {
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
            }

            // Возвращаем тест глубины обратно
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        });
    }
}
