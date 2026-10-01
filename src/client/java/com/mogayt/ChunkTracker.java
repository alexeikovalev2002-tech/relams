package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

public class ChunkTracker {

    // Сохраняем все чанки, которые когда-либо видели
    private static final Map<ChunkPos, Map<BlockPos, BlockState>> savedChunks = new HashMap<>();

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            // Запоминаем только те блоки, которые нам интересны
            ChunkPos pos = chunk.getPos();
            Map<BlockPos, BlockState> blocks = new HashMap<>();

            // Проходим по всем блокам в чанке
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = world.getBottomY(); y < world.getTopY(); y++) {
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

    // Очистка старых чанков (чтобы не жрало память)
    public static void cleanup() {
        if (savedChunks.size() > 500) {
            // Удаляем самые старые (по расстоянию до игрока)
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            BlockPos playerPos = client.player.getBlockPos();
            savedChunks.entrySet().removeIf(entry -> {
                ChunkPos chunkPos = entry.getKey();
                double dist = Math.sqrt(
                        Math.pow(chunkPos.getStartX() - playerPos.getX(), 2) +
                        Math.pow(chunkPos.getStartZ() - playerPos.getZ(), 2)
                );
                return dist > 200; // удаляем всё, что дальше 200 блоков
            });
        }
    }
}
