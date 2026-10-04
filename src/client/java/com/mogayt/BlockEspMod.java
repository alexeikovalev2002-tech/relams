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

    private static final float[] C_CHEST = {1f, 0.5f, 0f};
    private static final float[] C_SHULKER = {0.2f, 0.6f, 1f};
    private static final float[] C_BARREL = {0.55f, 0.27f, 0.07f};
    private static final float[] C_ENDER = {0f, 0.2f, 1f};
    private static final float[] C_BED = {1f, 0f, 0f};

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
            AimMobs.tick(client);
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
            BlockPos pp = client.player.getBlockPos();
            int scan = Math.max(Math.max(oreDistance, chestDistance), playerDistance);

            if (espEnabled) {
                Set<Block> targets = getTargetBlocks();
                for (BlockPos pos : BlockPos.iterate(pp.add(-scan, -scan, -scan), pp.add(scan, scan, scan))) {
                    if (Math.sqrt(pos.getSquaredDistance(pp)) > oreDistance) continue;
                    if (targets.contains(client.world.getBlockState(pos).getBlock())) foundBlocks.add(pos.toImmutable());
                }
                if (distantEnabled) {
                    for (Map.Entry<ChunkPos, Map<BlockPos, BlockState>> e : ChunkTracker.getSavedChunks().entrySet()) {
                        ChunkPos cp = e.getKey();
                        double d = Math.sqrt(Math.pow(cp.getStartX() - pp.getX(), 2) + Math.pow(cp.getStartZ() - pp.getZ(), 2));
                        if (d > oreDistance * 16) distantBlocks.addAll(e.getValue().keySet());
                    }
                }
            }

            if (chestEspEnabled) {
                for (BlockPos pos : BlockPos.iterate(pp.add(-scan, -scan, -scan), pp.add(scan, scan, scan))) {
                    if (Math.sqrt(pos.getSquaredDistance(pp)) > chestDistance) continue;
                    Block b = client.world.getBlockState(pos).getBlock();
                    if (!ALL_CHESTS.contains(b)) continue;
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

            if (playerEspEnabled) {
                for (PlayerEntity p : client.world.getPlayers()) {
                    if (p == client.player) continue;
                    if (p.distanceTo(client.player) <= playerDistance) foundPlayers.add(p);
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
            Matrix4f mat = matrices.peek().getPositionMatrix();

            if (espEnabled) {
                for (BlockPos pos : foundBlocks) RenderHelper.drawBox(buf, mat, pos, cam, 1f, 0f, 0f);
                for (BlockPos pos : distantBlocks) RenderHelper.drawBox(buf, mat, pos, cam, 0.2f, 0.5f, 1f);
            }
            if (chestEspEnabled) {
                for (BlockPos pos : foundChests) RenderHelper.drawBox(buf, mat, pos, cam, C_CHEST[0], C_CHEST[1], C_CHEST[2]);
                for (BlockPos pos : foundShulkers) RenderHelper.drawBox(buf, mat, pos, cam, C_SHULKER[0], C_SHULKER[1], C_SHULKER[2]);
                for (BlockPos pos : foundBarrels) RenderHelper.drawBox(buf, mat, pos, cam, C_BARREL[0], C_BARREL[1], C_BARREL[2]);
                for (BlockPos pos : foundEnderChests) RenderHelper.drawBox(buf, mat, pos, cam, C_ENDER[0], C_ENDER[1], C_ENDER[2]);
                for (BlockPos pos : foundBeds) RenderHelper.drawBox(buf, mat, pos, cam, C_BED[0], C_BED[1], C_BED[2]);
            }
            if (playerEspEnabled) {
                for (PlayerEntity p : foundPlayers) {
                    Box box = p.getBoundingBox();
                    RenderHelper.drawBoxRaw(buf, mat,
                            (float)(box.minX - cam.x), (float)(box.minY - cam.y), (float)(box.minZ - cam.z),
                            (float)(box.maxX - cam.x), (float)(box.maxY - cam.y), (float)(box.maxZ - cam.z),
                            1f, 1f, 0f);
                }
            }

            TrajectoryPredictor.update(client);
            if (TrajectoryPredictor.enabled && TrajectoryPredictor.impactPoint != null) {
                RenderHelper.drawDiamond(buf, mat, TrajectoryPredictor.impactPoint,
                        TrajectoryPredictor.impactNormal, cam, 0.4f);
            }

            playerMarkers.clear();
            if (playerEspEnabled && !foundPlayers.isEmpty()) {
                MatrixStack vs = new MatrixStack();
                vs.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ctx.camera().getPitch()));
                vs.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ctx.camera().getYaw() + 180f));
                Matrix4f vm = new Matrix4f(vs.peek().getPositionMatrix());
                Matrix4f pm = new Matrix4f(ctx.projectionMatrix());
                int sw = client.getWindow().getScaledWidth();
                int sh = client.getWindow().getScaledHeight();
                for (PlayerEntity p : foundPlayers) {
                    Box box = p.getBoundingBox();
                    float px = (float) ((box.minX + box.maxX) / 2 - cam.x);
                    float py = (float) (box.maxY - cam.y + 0.7);
                    float pz = (float) ((box.minZ + box.maxZ) / 2 - cam.z);
                    Vector4f v = new Vector4f(px, py, pz, 1f);
                    v.mul(vm);
                    v.mul(pm);
                    if (v.w <= 0.01f) continue;
                    int sx = (int) ((v.x / v.w * 0.5f + 0.5f) * sw);
                    int sy = (int) ((0.5f - v.y / v.w * 0.5f) * sh);
                    PlayerMarker marker = new PlayerMarker();
                    marker.x = sx;
                    marker.y = sy;
                    marker.name = p.getName().getString();
                    marker.hp = p.getHealth();
                    marker.maxHp = p.getMaxHealth();
                    playerMarkers.add(marker);
                }
            }
        });

        HudRenderCallback.EVENT.register((dc, td) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() == null) return;
            int sw = client.getWindow().getScaledWidth();
            int sh = client.getWindow().getScaledHeight();

            if (AimMobs.enabled) {
                int cx = sw / 2;
                int cy = sh / 2;
                double fovRad = Math.toRadians(AimMobs.fovAngle);
                int radius = (int) (sh * 0.5 * Math.tan(fovRad) / Math.tan(Math.toRadians(70.0)));
                if (radius < 10) radius = 10;
                if (radius > sh / 2) radius = sh / 2;
                int segments = 180;
                for (int i = 0; i < segments; i++) {
                    double a1 = i * Math.PI * 2.0 / segments;
                    double a2 = (i + 1) * Math.PI * 2.0 / segments;
                    int x1 = cx + (int)(Math.cos(a1) * radius);
                    int y1 = cy + (int)(Math.sin(a1) * radius);
                    int x2 = cx + (int)(Math.cos(a2) * radius);
                    int y2 = cy + (int)(Math.sin(a2) * radius);
                    dc.fill(Math.min(x1,x2)-2, Math.min(y1,y2)-2, Math.max(x1,x2)+2, Math.max(y1,y2)+2, 0xFFFFFFFF);
                }
            }

            if (!playerEspEnabled || playerMarkers.isEmpty()) return;
            if (client.textRenderer == null) return;
            for (PlayerMarker m : playerMarkers) {
                int nw = client.textRenderer.getWidth(m.name);
                dc.drawTextWithShadow(client.textRenderer, Text.literal(m.name), m.x - nw / 2, m.y, 0xFFFFFFFF);
                int hpP = (int) ((m.hp / m.maxHp) * 100);
                int hc = 0xFF00FF00;
                if (hpP < 60) hc = 0xFFFFFF00;
                if (hpP < 30) hc = 0xFFFF0000;
                String ht = String.format("%.0f", m.hp);
                int hw = client.textRenderer.getWidth(ht);
                dc.drawTextWithShadow(client.textRenderer, Text.literal(ht), m.x - hw / 2, m.y + 10, hc);
            }
        });
    }
                                                                      }
