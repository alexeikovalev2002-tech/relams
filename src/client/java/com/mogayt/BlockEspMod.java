package com.mogayt;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
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

    // Маркеры на экране: {screenX, screenY, distance, blockName}
    private static final List<Marker> markers = new ArrayList<>();

    private static class Marker {
        int x, y;
        double distance;
        String name;
        Marker(int x, int y, double distance, String name) {
            this.x = x; this.y = y; this.distance = distance; this.name = name;
        }
    }

    static {
        TARGET_BLOCKS.add(Blocks.DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
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
            int radius = 32;

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius))) {

                BlockState state = client.world.getBlockState(pos);
                if (TARGET_BLOCKS.contains(state.getBlock())) {
                    foundBlocks.add(pos.toImmutable());
                }
            }
        });

        // Преобразуем координаты блоков в экранные (во время рендера мира)
        WorldRenderEvents.LAST.register((worldRenderContext) -> {
            markers.clear();
            if (!espEnabled || foundBlocks.isEmpty()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;

            MatrixStack matrices = worldRenderContext.matrixStack();
            if (matrices == null) return;

            Vec3d cameraPos = worldRenderContext.camera().getPos();
            Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
            Matrix4f posMatrix = matrices.peek().getPositionMatrix();

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            for (BlockPos pos : foundBlocks) {
                double dx = pos.getX() + 0.5 - cameraPos.x;
                double dy = pos.getY() + 0.5 - cameraPos.y;
                double dz = pos.getZ() + 0.5 - cameraPos.z;

                Vector4f vec = new Vector4f((float) dx, (float) dy, (float) dz, 1.0f);
                vec.mul(posMatrix);
                vec.mul(projMatrix);

                if (vec.w <= 0.0f) continue; // Позади камеры

                float ndcX = vec.x / vec.w;
                float ndcY = vec.y / vec.w;
                float ndcZ = vec.z / vec.w;

                if (ndcZ < -1.0f || ndcZ > 1.0f) continue;

                int screenX = (int) ((ndcX * 0.5f + 0.5f) * screenWidth);
                int screenY = (int) ((0.5f - ndcY * 0.5f) * screenHeight);

                if (screenX < 0 || screenX > screenWidth || screenY < 0 || screenY > screenHeight) continue;

                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                String name = pos.getY() < 0 ? "АЛМАЗ" : "РУДА";

                markers.add(new Marker(screenX, screenY, distance, name));
            }
        });

        // Рисуем маркеры поверх экрана
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!espEnabled || markers.isEmpty()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.textRenderer == null) return;

            for (Marker m : markers) {
                // Красный крестик
                drawContext.fill(m.x - 6, m.y - 1, m.x + 6, m.y + 1, 0xFFFF0000);
                drawContext.fill(m.x - 1, m.y - 6, m.x + 1, m.y + 6, 0xFFFF0000);

                // Текст с расстоянием
                String label = m.name + " " + (int) m.distance + "м";
                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        Text.literal(label),
                        m.x + 8, m.y - 4,
                        0xFFFFFF00
                );
            }
        });
    }
}
