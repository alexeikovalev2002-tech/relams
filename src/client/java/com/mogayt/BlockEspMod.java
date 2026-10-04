package com.mogayt;

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
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public static boolean distantEnabled = false;

    private static final Set<Block> DIAMOND_BLOCKS = new HashSet<>();
    private static final Set<Block> IRON_BLOCKS = new HashSet<>();
    private static final Set<Block> COAL_BLOCKS = new HashSet<>();
    private static final Set<Block> EMERALD_BLOCKS = new HashSet<>();
    private static final Set<Block> NETHERITE_BLOCKS = new HashSet<>();
    private static final Set<Block> ALL_ORES = new HashSet<>();
    private static final Set<Block> NORMAL_CHESTS = new HashSet<>();
    private static final Set<Block> SHULKER_CHESTS = new HashSet<>();
    private static final Set<Block> BARREL_BLOCKS = new HashSet<>();
    private static final Set<Block> ENDER_CHESTS = new HashSet<>();
    private static final Set<Block> BED_BLOCKS = new HashSet<>();
    private static final Set<Block> ALL_CHESTS = new HashSet<>();

    private static final float[] COLOR_CHEST = {1f, 0.5f, 0f, 1f};
    private static final float[] COLOR_SHULKER = {0.2f, 0.6f, 1f, 1f};
    private static final float[] COLOR_BARREL = {0.55f, 0.27f, 0.07f, 1f};
    private static final float[] COLOR_ENDER = {0f, 0.2f, 1f, 1f};
    private static final float[] COLOR_BED = {1f, 0f, 0f, 1f};

    private static KeyBinding openMenuKey;
    private static final List<PlayerEntity> foundPlayers = new ArrayList<>();
    private static final List<PlayerMarker> playerMarkers = new ArrayList<>();
    private static final List<BlockPos> foundBlocks = new ArrayList<>();
    private static final List<BlockPos> distantBlocks = new ArrayList<>();
    private static final List<BlockPos> foundChests = new ArrayList<>();
    private static final List<BlockPos> foundShulkers = new ArrayList<>();
    private static final List<BlockPos> foundBarrels = new ArrayList<>();
    private static final List<BlockPos> foundEnderChests = new ArrayList<>();
    private static final List<BlockPos> foundBeds = new ArrayList<>();
    private static int tickCounter = 0;

    private static class PlayerMarker {
        int x, y;
        String name;
        float hp, maxHp;
    }

    private static final RenderLayer THROUGH_WALLS = RenderLayer.of(
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

        ChunkTracker.register();
        ChatBind.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new EspMenuScreen());
            }
            Fullbright.tick();
            Optimizer.tick();
            Freecam.tick(client);
            if (client.player != null && client.player.age % 100 == 0) ChunkTracker.cleanup();
            if (client.world == null || client.player == null) return;
            tickCounter++;
            if (tickCounter < 10) return;
            tickCounter = 0;
            foundBlocks.clear();
            distantBlocks.clear();
            foundChests.clear();
            foundShulkers.clear();
            foundBarrels.clear();
            foundEnderChests.clear();
            foundBeds.clear();
            foundPlayers.clear();
            BlockPos playerPos = client.player.getBlockPos();
            int scanRadius = Math.max(Math.max(oreDistance, chestDistance), playerDistance);

            if (espEnabled) {
                Set<Block> targets = getTargetBlocks();
                for (BlockPos pos : BlockPos.iterate(
                        playerPos.add(-scanRadius, -scanRadius, -scanRadius),
                        playerPos.add(scanRadius, scanRadius, scanRadius))) {
                    if (Math.sqrt(pos.getSquaredDistance(playerPos)) > oreDistance) continue;
                    if (targets.contains(client.world.getBlockState(pos).getBlock()))
                        foundBlocks.add(pos.toImmutable());
                }
                if (distantEnabled) {
                    for (Map.Entry<ChunkPos, Map<BlockPos, BlockState>> entry : ChunkTracker.getSavedChunks().entrySet()) {
                        ChunkPos cp = entry.getKey();
                        double dist = Math.sqrt(Math.pow(cp.getStartX() - playerPos.getX(), 2) + Math.pow(cp.getStartZ() - playerPos.getZ(), 2));
                        if (dist > oreDistance * 16) distantBlocks.addAll(entry.getValue().keySet());
                    }
                }
            }

            if (chestEspEnabled) {
                for (BlockPos pos : BlockPos.iterate(
                        playerPos.add(-scanRadius, -scanRadius, -scanRadius),
                        playerPos.add(scanRadius, scanRadius, scanRadius))) {
                    if (Math.sqrt(pos.getSquaredDistance(playerPos)) > chestDistance) continue;
                    Block block = client.world.getBlockState(pos).getBlock();
                    if (!ALL_CHESTS.contains(block)) continue;
                    if (chestType == 1 && !NORMAL_CHESTS.contains(block)) continue;
                    if (chestType == 2 && !SHULKER_CHESTS.contains(block)) continue;
                    if (chestType == 3 && !BARREL_BLOCKS.contains(block)) continue;
                    if (chestType == 4 && !ENDER_CHESTS.contains(block)) continue;
                    if (chestType == 5 && !BED_BLOCKS.contains(block)) continue;
                    BlockPos im = pos.toImmutable();
                    if (NORMAL_CHESTS.contains(block)) foundChests.add(im);
                    else if (SHULKER_CHESTS.contains(block)) foundShulkers.add(im);
                    else if (BARREL_BLOCKS.contains(block)) foundBarrels.add(im);
                    else if (ENDER_CHESTS.contains(block)) foundEnderChests.add(im);
                    else if (BED_BLOCKS.contains(block)) foundBeds.add(im);
                }
            }

            if (playerEspEnabled) {
                for (PlayerEntity player : client.world.getPlayers()) {
                    if (player == client.player) continue;
                    if (player.distanceTo(client.player) <= playerDistance) foundPlayers.add(player);
                }
            }
        });

        WorldRenderEvents.LAST.register((ctx) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;
            MatrixStack matrices = ctx.matrixStack();
            if (matrices == null) return;
            Vec3d cam = ctx.camera().getPos();
            VertexConsumerProvider consumers = ctx.consumers();
            if (consumers == null) return;
            VertexConsumer buf = consumers.getBuffer(THROUGH_WALLS);

            if (espEnabled) {
                for (BlockPos pos : foundBlocks) drawBox(matrices, buf, pos, cam, 1f, 0f, 0f, 1f);
                for (BlockPos pos : distantBlocks) drawBox(matrices, buf, pos, cam, 0.2f, 0.5f, 1f, 1f);
            }
            if (chestEspEnabled) {
                for (BlockPos pos : foundChests) drawBox(matrices, buf, pos, cam, COLOR_CHEST[0], COLOR_CHEST[1], COLOR_CHEST[2], 1f);
                for (BlockPos pos : foundShulkers) drawBox(matrices, buf, pos, cam, COLOR_SHULKER[0], COLOR_SHULKER[1], COLOR_SHULKER[2], 1f);
                for (BlockPos pos : foundBarrels) drawBox(matrices, buf, pos, cam, COLOR_BARREL[0], COLOR_BARREL[1], COLOR_BARREL[2], 1f);
                for (BlockPos pos : foundEnderChests) drawBox(matrices, buf, pos, cam, COLOR_ENDER[0], COLOR_ENDER[1], COLOR_ENDER[2], 1f);
                for (BlockPos pos : foundBeds) drawBox(matrices, buf, pos, cam, COLOR_BED[0], COLOR_BED[1], COLOR_BED[2], 1f);
            }
            if (playerEspEnabled) {
                for (PlayerEntity player : foundPlayers) {
                    Box box = player.getBoundingBox();
                    VertexRendering.drawBox(matrices, buf,
                            box.minX - cam.x, box.minY - cam.y, box.minZ - cam.z,
                            box.maxX - cam.x, box.maxY - cam.y, box.maxZ - cam.z,
                            1f, 1f, 0f, 1f);
                }
            }

            TrajectoryPredictor.update(client);
            if (TrajectoryPredictor.enabled) {
                List<Vec3d> traj = TrajectoryPredictor.getTrajectory();
                if (!traj.isEmpty()) {
                    Matrix4f mat = matrices.peek().getPositionMatrix();
                    for (int i = 0; i < traj.size() - 1; i++) {
                        drawLine(buf, mat, traj.get(i), traj.get(i + 1), cam,
                                TrajectoryPredictor.COLOR[0], TrajectoryPredictor.COLOR[1], TrajectoryPredictor.COLOR[2]);
                    }
                    drawDiamond(buf, mat, traj.get(traj.size() - 1), cam, 0.4f);
                }
            }

            playerMarkers.clear();
            if (playerEspEnabled && !foundPlayers.isEmpty()) {
                MatrixStack viewStack = new MatrixStack();
                viewStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ctx.camera().getPitch()));
                viewStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ctx.camera().getYaw() + 180f));
                Matrix4f viewMatrix = new Matrix4f(viewStack.peek().getPositionMatrix());
                Matrix4f projMatrix = new Matrix4f(ctx.projectionMatrix());
                int screenW = client.getWindow().getScaledWidth();
                int screenH = client.getWindow().getScaledHeight();
                for (PlayerEntity player : foundPlayers) {
                    Box box = player.getBoundingBox();
                    float px = (float) ((box.minX + box.maxX) / 2 - cam.x);
                    float py = (float) (box.maxY - cam.y + 0.7);
                    float pz = (float) ((box.minZ + box.maxZ) / 2 - cam.z);
                    Vector4f vec = new Vector4f(px, py, pz, 1f);
                    vec.mul(viewMatrix);
                    vec.mul(projMatrix);
                    if (vec.w <= 0.01f) continue;
                    int sx = (int) ((vec.x / vec.w * 0.5f + 0.5f) * screenW);
                    int sy = (int) ((0.5f - vec.y / vec.w * 0.5f) * screenH);
                    PlayerMarker marker = new PlayerMarker();
                    marker.x = sx;
                    marker.y = sy;
                    marker.name = player.getName().getString();
                    marker.hp = player.getHealth();
                    marker.maxHp = player.getMaxHealth();
                    playerMarkers.add(marker);
                }
            }
        });

        HudRenderCallback.EVENT.register((dc, td) -> {
            if (!playerEspEnabled || playerMarkers.isEmpty()) return;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.textRenderer == null) return;
            for (PlayerMarker m : playerMarkers) {
                int nameW = client.textRenderer.getWidth(m.name);
                dc.drawTextWithShadow(client.textRenderer, Text.literal(m.name), m.x - nameW / 2, m.y, 0xFFFFFFFF);
                int hpPercent = (int) ((m.hp / m.maxHp) * 100);
                int hpColor = 0xFF00FF00;
                if (hpPercent < 60) hpColor = 0xFFFFFF00;
                if (hpPercent < 30) hpColor = 0xFFFF0000;
                String hpText = String.format("%.0f", m.hp);
                int hpW = client.textRenderer.getWidth(hpText);
                dc.drawTextWithShadow(client.textRenderer, Text.literal(hpText), m.x - hpW / 2, m.y + 10, hpColor);
            }
        });
    }

    private static void drawBox(MatrixStack m, VertexConsumer b, BlockPos p, Vec3d cam, float r, float g, float bl, float a) {
        VertexRendering.drawBox(m, b, p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z,
                p.getX() + 1 - cam.x, p.getY() + 1 - cam.y, p.getZ() + 1 - cam.z, r, g, bl, a);
    }

    private static void drawLine(VertexConsumer b, Matrix4f m, Vec3d p1, Vec3d p2, Vec3d cam, float r, float g, float bl) {
        b.vertex(m, (float)(p1.x - cam.x), (float)(p1.y - cam.y), (float)(p1.z - cam.z)).color(r, g, bl, 1f).normal(0f, 1f, 0f);
        b.vertex(m, (float)(p2.x - cam.x), (float)(p2.y - cam.y), (float)(p2.z - cam.z)).color(r, g, bl, 1f).normal(0f, 1f, 0f);
    }

    private static void drawDiamond(VertexConsumer b, Matrix4f m, Vec3d c, Vec3d cam, float s) {
        float cx = (float)(c.x - cam.x), cy = (float)(c.y - cam.y), cz = (float)(c.z - cam.z);
        float[][] pts = {{cx, cy + s, cz}, {cx + s, cy, cz}, {cx, cy - s, cz}, {cx - s, cy, cz}};
        for (int i = 0; i < 4; i++) {
            float[] a = pts[i], d = pts[(i + 1) % 4];
            b.vertex(m, a[0], a[1], a[2]).color(1f, 0.2f, 0.2f, 1f).normal(0f, 1f, 0f);
            b.vertex(m, d[0], d[1], d[2]).color(1f, 0.2f, 0.2f, 1f).normal(0f, 1f, 0f);
        }
    }
            }
