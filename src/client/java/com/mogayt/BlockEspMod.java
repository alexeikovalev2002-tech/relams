package com.mogayt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;

public class BlockEspMod implements ClientModInitializer {

    private static final Set<Block> TARGET_BLOCKS = new HashSet<>();

    static {
        TARGET_BLOCKS.add(Blocks.DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        TARGET_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
    }

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return true;

            MatrixStack matrices = worldRenderContext.matrixStack();
            if (matrices == null) return true;

            Vec3d cameraPos = worldRenderContext.camera().getPos();
            VertexConsumerProvider consumers = worldRenderContext.consumers();
            if (consumers == null) return true;

            VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());

            int radius = 32;
            BlockPos playerPos = client.player.getBlockPos();

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius))) {

                BlockState state = client.world.getBlockState(pos);
                if (TARGET_BLOCKS.contains(state.getBlock())) {
                    double x = pos.getX() - cameraPos.x;
                    double y = pos.getY() - cameraPos.y;
                    double z = pos.getZ() - cameraPos.z;

                    // ИСПРАВЛЕННАЯ СТРОЧКА:
                    VertexRendering.drawBox(
                            matrices, buffer,
                            x, y, z,
                            x + 1, y + 1, z + 1,
                            1.0f, 0.0f, 0.0f, 1.0f
                    );
                }
            }
            return true;
        });
    }
}
