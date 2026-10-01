package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class ChunkTracker {

    private static final Map<ChunkPos, Map<BlockPos, BlockState>> savedChunks = new HashMap<>();

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            ChunkPos pos = chunk.getPos();
            Map<BlockPos, BlockState> blocks = new HashMap<>();

            int bottom = world.getBottomY();
            int top = bottom + world.getHeight();

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = bottom; y < top; y++) {
                        BlockPos bp = new BlockPos(pos.getStartX() + x, y, pos.getStartZ() + z);
                        BlockState state = chunk.getBlockState(bp);
                        if (BlockEspMod.getTargetBlocks().contains(state.getBlock())) {
                            blocks.put(bp.toImmutable(), state);
                        }
                    }
                }
            }

            if (!blocks.isEmpty()) {
                savedChunks.put(pos, blocks);
            }
        });
    }

    public static Map<ChunkPos, Map<BlockPos, BlockState>> getSavedChunks() {
        return savedChunks;
    }

    public static void cleanup() {
        if (savedChunks.size() > 500) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            BlockPos playerPos = client.player.getBlockPos();
            savedChunks.entrySet().removeIf(entry -> {
                ChunkPos chunkPos = entry.getKey();
                double dist = Math.sqrt(
                        Math.pow(chunkPos.getStartX() - playerPos.getX(), 2) +
                        Math.pow(chunkPos.getStartZ() - playerPos.getZ(), 2)
                );
                return dist > 200;
            });
        }
    }
}
