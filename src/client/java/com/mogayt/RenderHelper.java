package com.mogayt;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class RenderHelper {

    public static void drawBox(VertexConsumer b, Matrix4f m, BlockPos p, Vec3d cam, float r, float g, float bl) {
        float x1 = (float)(p.getX() - cam.x);
        float y1 = (float)(p.getY() - cam.y);
        float z1 = (float)(p.getZ() - cam.z);
        float x2 = x1 + 1, y2 = y1 + 1, z2 = z1 + 1;
        line(b, m, x1, y1, z1, x2, y1, z1, r, g, bl);
        line(b, m, x1, y1, z1, x1, y2, z1, r, g, bl);
        line(b, m, x1, y1, z1, x1, y1, z2, r, g, bl);
        line(b, m, x2, y1, z1, x2, y2, z1, r, g, bl);
        line(b, m, x2, y1, z1, x2, y1, z2, r, g, bl);
        line(b, m, x1, y2, z1, x2, y2, z1, r, g, bl);
        line(b, m, x1, y2, z1, x1, y2, z2, r, g, bl);
        line(b, m, x1, y1, z2, x2, y1, z2, r, g, bl);
        line(b, m, x1, y1, z2, x1, y2, z2, r, g, bl);
        line(b, m, x2, y2, z1, x2, y2, z2, r, g, bl);
        line(b, m, x2, y1, z2, x2, y2, z2, r, g, bl);
        line(b, m, x1, y2, z2, x2, y2, z2, r, g, bl);
    }

    public static void drawBoxRaw(VertexConsumer b, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float bl) {
        line(b, m, x1, y1, z1, x2, y1, z1, r, g, bl);
        line(b, m, x1, y1, z1, x1, y2, z1, r, g, bl);
        line(b, m, x1, y1, z1, x1, y1, z2, r, g, bl);
        line(b, m, x2, y1, z1, x2, y2, z1, r, g, bl);
        line(b, m, x2, y1, z1, x2, y1, z2, r, g, bl);
        line(b, m, x1, y2, z1, x2, y2, z1, r, g, bl);
        line(b, m, x1, y2, z1, x1, y2, z2, r, g, bl);
        line(b, m, x1, y1, z2, x2, y1, z2, r, g, bl);
        line(b, m, x1, y1, z2, x1, y2, z2, r, g, bl);
        line(b, m, x2, y2, z1, x2, y2, z2, r, g, bl);
        line(b, m, x2, y1, z2, x2, y2, z2, r, g, bl);
        line(b, m, x1, y2, z2, x2, y2, z2, r, g, bl);
    }

    public static void drawDiamond(VertexConsumer bufLines, VertexConsumer bufFilled,
                                   Matrix4f m, Vec3d c, Vec3d n, Vec3d cam,
                                   float lineWidth, boolean filled) {
        if (filled) {
            Vec3d vp = c.add(n.multiply(0.03));
            float cx = (float)(vp.x - cam.x);
            float cy = (float)(vp.y - cam.y);
            float cz = (float)(vp.z - cam.z);
            float s = 0.4f;

            float[] a, b, d, e;
            if (Math.abs(n.y) > 0.5) {
                a = new float[]{cx, cy, cz + s};
                b = new float[]{cx + s, cy, cz};
                d = new float[]{cx, cy, cz - s};
                e = new float[]{cx - s, cy, cz};
            } else if (Math.abs(n.x) > 0.5) {
                a = new float[]{cx, cy + s, cz};
                b = new float[]{cx, cy, cz + s};
                d = new float[]{cx, cy - s, cz};
                e = new float[]{cx, cy, cz - s};
            } else {
                a = new float[]{cx, cy + s, cz};
                b = new float[]{cx + s, cy, cz};
                d = new float[]{cx, cy - s, cz};
                e = new float[]{cx - s, cy, cz};
            }
            // Квад из 4 вершин (POSITION_COLOR + QUADS)
            bufFilled.vertex(m, a[0], a[1], a[2]).color(1f, 0.2f, 0.2f, 0.5f);
            bufFilled.vertex(m, b[0], b[1], b[2]).color(1f, 0.2f, 0.2f, 0.5f);
            bufFilled.vertex(m, d[0], d[1], d[2]).color(1f, 0.2f, 0.2f, 0.5f);
            bufFilled.vertex(m, e[0], e[1], e[2]).color(1f, 0.2f, 0.2f, 0.5f);
        } else {
            // Контур: рисуем ромб несколько раз с разным отступом по нормали → эффект толщины
            int layers = (int) Math.max(1, Math.round(lineWidth * 2));
            for (int i = 0; i < layers; i++) {
                float off = 0.02f + i * 0.02f;
                Vec3d vp = c.add(n.multiply(off));
                float cx = (float)(vp.x - cam.x);
                float cy = (float)(vp.y - cam.y);
                float cz = (float)(vp.z - cam.z);
                float s = 0.4f;

                float[] a, b, d, e;
                if (Math.abs(n.y) > 0.5) {
                    a = new float[]{cx, cy, cz + s};
                    b = new float[]{cx + s, cy, cz};
                    d = new float[]{cx, cy, cz - s};
                    e = new float[]{cx - s, cy, cz};
                } else if (Math.abs(n.x) > 0.5) {
                    a = new float[]{cx, cy + s, cz};
                    b = new float[]{cx, cy, cz + s};
                    d = new float[]{cx, cy - s, cz};
                    e = new float[]{cx, cy, cz - s};
                } else {
                    a = new float[]{cx, cy + s, cz};
                    b = new float[]{cx + s, cy, cz};
                    d = new float[]{cx, cy - s, cz};
                    e = new float[]{cx - s, cy, cz};
                }
                line(bufLines, m, a[0], a[1], a[2], b[0], b[1], b[2], 1f, 0.2f, 0.2f);
                line(bufLines, m, b[0], b[1], b[2], d[0], d[1], d[2], 1f, 0.2f, 0.2f);
                line(bufLines, m, d[0], d[1], d[2], e[0], e[1], e[2], 1f, 0.2f, 0.2f);
                line(bufLines, m, e[0], e[1], e[2], a[0], a[1], a[2], 1f, 0.2f, 0.2f);
            }
        }
    }

    private static void line(VertexConsumer b, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float bl) {
        b.vertex(m, x1, y1, z1).color(r, g, bl, 1f).normal(0f, 1f, 0f);
        b.vertex(m, x2, y2, z2).color(r, g, bl, 1f).normal(0f, 1f, 0f);
    }
                                    }
