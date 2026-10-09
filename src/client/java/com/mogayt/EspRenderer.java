package com.mogayt;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class EspRenderer {

    private static final List<PM> pms = new ArrayList<>();
    private static final List<IM> ims = new ArrayList<>();

    private static class PM { int x, y; String name; float hp, maxHp; }
    private static class IM { int x, y; String name; }

    public static void register() {
        WorldRenderEvents.LAST.register(EspRenderer::renderWorld);
        HudRenderCallback.EVENT.register(EspRenderer::renderHud);
    }

    private static void renderWorld(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;
        Vec3d cam = ctx.camera().getPos();
        VertexConsumerProvider consumers = ctx.consumers();
        if (consumers == null) return;
        VertexConsumer buf = consumers.getBuffer(BlockEspMod.THROUGH_WALLS);
        Matrix4f mat = matrices.peek().getPositionMatrix();

        if (BlockEspMod.espEnabled) {
            for (var p : BlockEspMod.foundBlocks) RenderHelper.drawBox(buf, mat, p, cam, 1f, 0f, 0f);
        }
        if (BlockEspMod.chestEspEnabled) {
            for (var p : BlockEspMod.foundChests) RenderHelper.drawBox(buf, mat, p, cam, 1f, 0.5f, 0f);
            for (var p : BlockEspMod.foundShulkers) RenderHelper.drawBox(buf, mat, p, cam, 0.2f, 0.6f, 1f);
            for (var p : BlockEspMod.foundBarrels) RenderHelper.drawBox(buf, mat, p, cam, 0.55f, 0.27f, 0.07f);
            for (var p : BlockEspMod.foundEnderChests) RenderHelper.drawBox(buf, mat, p, cam, 0f, 0.2f, 1f);
            for (var p : BlockEspMod.foundBeds) RenderHelper.drawBox(buf, mat, p, cam, 1f, 0f, 0f);
        }
        if (BlockEspMod.playerEspEnabled) {
            for (PlayerEntity p : BlockEspMod.foundPlayers) {
                Box b = p.getBoundingBox();
                RenderHelper.drawBoxRaw(buf, mat,
                        (float)(b.minX - cam.x), (float)(b.minY - cam.y), (float)(b.minZ - cam.z),
                        (float)(b.maxX - cam.x), (float)(b.maxY - cam.y), (float)(b.maxZ - cam.z),
                        1f, 1f, 0f);
            }
        }
        if (BlockEspMod.itemEspEnabled) {
            for (ItemEntity it : BlockEspMod.foundItems) {
                Box b = it.getBoundingBox();
                RenderHelper.drawBoxRaw(buf, mat,
                        (float)(b.minX - cam.x), (float)(b.minY - cam.y), (float)(b.minZ - cam.z),
                        (float)(b.maxX - cam.x), (float)(b.maxY - cam.y), (float)(b.maxZ - cam.z),
                        0.6f, 1f, 0.6f);
            }
        }

        TrajectoryPredictor.update(client);
        if (TrajectoryPredictor.enabled && TrajectoryPredictor.impactPoint != null) {
            RenderHelper.drawDiamond(buf, mat, TrajectoryPredictor.impactPoint,
                    TrajectoryPredictor.impactNormal, cam, 0.4f);
        }

        pms.clear();
        ims.clear();
        boolean need = BlockEspMod.playerEspEnabled || BlockEspMod.itemEspEnabled;
        if (!need) return;

        MatrixStack vs = new MatrixStack();
        vs.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ctx.camera().getPitch()));
        vs.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ctx.camera().getYaw() + 180f));
        Matrix4f vm = new Matrix4f(vs.peek().getPositionMatrix());
        Matrix4f pm = new Matrix4f(ctx.projectionMatrix());
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        if (BlockEspMod.playerEspEnabled) {
            for (PlayerEntity p : BlockEspMod.foundPlayers) {
                Box b = p.getBoundingBox();
                float px = (float) ((b.minX + b.maxX) / 2 - cam.x);
                float py = (float) (b.maxY - cam.y + 0.7);
                float pz = (float) ((b.minZ + b.maxZ) / 2 - cam.z);
                Vector4f v = new Vector4f(px, py, pz, 1f);
                v.mul(vm); v.mul(pm);
                if (v.w <= 0.01f) continue;
                PM m = new PM();
                m.x = (int) ((v.x / v.w * 0.5f + 0.5f) * sw);
                m.y = (int) ((0.5f - v.y / v.w * 0.5f) * sh);
                m.name = p.getName().getString();
                m.hp = p.getHealth();
                m.maxHp = p.getMaxHealth();
                pms.add(m);
            }
        }
        if (BlockEspMod.itemEspEnabled) {
            for (ItemEntity it : BlockEspMod.foundItems) {
                Box b = it.getBoundingBox();
                float px = (float) ((b.minX + b.maxX) / 2 - cam.x);
                float py = (float) (b.maxY - cam.y + 0.5);
                float pz = (float) ((b.minZ + b.maxZ) / 2 - cam.z);
                Vector4f v = new Vector4f(px, py, pz, 1f);
                v.mul(vm); v.mul(pm);
                if (v.w <= 0.01f) continue;
                IM m = new IM();
                m.x = (int) ((v.x / v.w * 0.5f + 0.5f) * sw);
                m.y = (int) ((0.5f - v.y / v.w * 0.5f) * sh);
                m.name = it.getStack().getName().getString();
                ims.add(m);
            }
        }
    }

    private static void renderHud(DrawContext dc, RenderTickCounter td) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getWindow() == null) return;
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        if (AimMobs.enabled) {
            int cx = sw / 2, cy = sh / 2;
            int radius = (int) (sh * 0.5 * Math.tan(Math.toRadians(AimMobs.fovAngle)) / Math.tan(Math.toRadians(70.0)));
            if (radius < 10) radius = 10;
            if (radius > sh / 2) radius = sh / 2;
            for (int i = 0; i < 180; i++) {
                double a = i * Math.PI * 2.0 / 180;
                int x = cx + (int) Math.round(Math.cos(a) * radius);
                int y = cy + (int) Math.round(Math.sin(a) * radius);
                dc.fill(x, y, x + 1, y + 1, 0xFFFFFFFF);
            }
        }

        if (client.textRenderer == null) return;

        for (PM m : pms) {
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
        for (IM m : ims) {
            int nw = client.textRenderer.getWidth(m.name);
            dc.drawTextWithShadow(client.textRenderer, Text.literal(m.name), m.x - nw / 2, m.y, 0xFFAAFFAA);
        }
    }
                    }
