package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Mog Mod — главное ClickGUI-меню.
 * Открывается из BlockEspMod по кейбинду (RIGHT SHIFT).
 */
public class EspMenuScreen extends Screen {

    private static final int BG      = 0xFF1A1A1A;
    private static final int CARD    = 0xFF222222;
    private static final int TEXT    = 0xFFF0F0F0;
    private static final int MUTED   = 0xFF9A9A9A;
    private static final int PURPLE  = 0xFF8B23D2;
    private static final int OUTSIDE = 0xFF3B447D;

    private final String[] tabs = {"Render", "Hud", "Utility"};
    private int selectedTab = 0;
    private int selectedColor = PURPLE;
    private int scroll = 0;

    private final int[] palette = {
            0xFF65009B, 0xFFCD4100, 0xFFA00000, 0xFFA0A0A0,
            0xFF142D5A, 0xFFDCB4B9, 0xFF00919B, 0xFF555F96,
            0xFF8728AA, 0xFF00A023, 0xFF915A9B, 0xFFAA5F28
    };

    private final List<Module> renderLeft  = new ArrayList<>();
    private final List<Module> renderRight = new ArrayList<>();
    private final List<Module> hudLeft     = new ArrayList<>();
    private final List<Module> hudRight    = new ArrayList<>();
    private final List<Module> utilLeft    = new ArrayList<>();
    private final List<Module> utilRight   = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;
    private int scrollMax = 0;

    public EspMenuScreen() {
        super(Text.literal("Mog Mod"));

        renderLeft.add(new Module("NoRender"));
        renderLeft.add(new Module("Crosshair"));
        renderLeft.add(new Module("TargetEsp"));
        renderLeft.add(new Module("Aspect Ratio"));
        renderLeft.add(new Module("ChinaHat"));

        renderRight.add(new Module("Fullbright"));
        renderRight.add(new Module("ViewModel"));
        renderRight.add(new Module("UI", "RIGHT SHIFT"));
        renderRight.add(new Module("CustomHitBox"));
        renderRight.add(new Module("JumpCircle"));

        hudLeft.add(new Module("ArrayList"));
        hudLeft.add(new Module("Watermark"));
        hudLeft.add(new Module("Notifications"));
        hudLeft.add(new Module("Coordinates"));

        hudRight.add(new Module("ArmorHud"));
        hudRight.add(new Module("TargetHud"));
        hudRight.add(new Module("Keystrokes"));
        hudRight.add(new Module("PotionHud"));

        utilLeft.add(new Module("Profiles"));
        utilLeft.add(new Module("Config"));
        utilLeft.add(new Module("Theme"));

        utilRight.add(new Module("Friends"));
        utilRight.add(new Module("Waypoints"));
        utilRight.add(new Module("Sounds"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, OUTSIDE);

        // Часы
        int clockW = 78;
        int clockX = (width - clockW) / 2;
        rounded(context, clockX, 3, clockW, 20, 0xFF080808);
        centeredText(context, "18:24:31", clockX, 8, clockW, TEXT);

        // Палитра
        int paletteX = Math.max(8, (width - Math.min(width - 16, 520)) / 2);
        int paletteW = Math.min(width - 16, 520);
        rounded(context, paletteX, 30, paletteW, 34, 0xFF101010);

        text(context, "☼", paletteX + 10, 41, TEXT);
        int dotsStart = paletteX + 38;
        int usable = Math.max(20, paletteW - 100);
        int step = Math.max(20, Math.min(27, usable / palette.length));

        for (int i = 0; i < palette.length; i++) {
            int cx = dotsStart + i * step;
            if (cx + 15 < paletteX + paletteW - 38) {
                rounded(context, cx - 1, 38, 16, 16, 0xFF080808);
                rounded(context, cx, 39, 14, 14, palette[i]);
                if (selectedColor == palette[i]) {
                    context.fill(cx - 1, 37, cx + 15, 38, TEXT);
                }
            }
        }
        text(context, "< >", paletteX + paletteW - 31, 41, TEXT);

        // Панель
        panelW = Math.min(520, width - 28);
        panelH = Math.min(255, height - 95);
        panelX = (width - panelW) / 2;
        panelY = 72;
        if (panelH < 190) panelH = Math.max(145, height - panelY - 24);

        rounded(context, panelX - 5, panelY - 5, panelW + 10, panelH + 10, 0x33303030);
        rounded(context, panelX, panelY, panelW, panelH, BG);

        // Табы
        int tabY = panelY + 9;
        int tabW = 66;
        int gap = 10;
        int totalTabsW = tabW * 3 + gap * 2;
        int tabX = panelX + (panelW - totalTabsW) / 2;

        for (int i = 0; i < tabs.length; i++) {
            int x = tabX + i * (tabW + gap);
            if (i == selectedTab) {
                rounded(context, x, tabY, tabW, 25, 0xFF666666);
            }
            centeredText(context, tabs[i], x, tabY + 7, tabW, TEXT);
        }

        int listTop = panelY + 43;
        int listBottom = panelY + panelH - 8;
        int listHeight = listBottom - listTop;
        int colGap = 8;
        int colW = (panelW - 40 - colGap) / 2;
        int leftX = panelX + 20;
        int rightX = leftX + colW + colGap;

        List<Module> left = currentLeft();
        List<Module> right = currentRight();
        int rows = Math.max(left.size(), right.size());
        int cardH = 31;
        int rowGap = 6;
        scrollMax = Math.max(0, rows * (cardH + rowGap) - rowGap - listHeight);

        context.enableScissor(panelX + 12, listTop, panelX + panelW - 12, listBottom);
        for (int i = 0; i < left.size(); i++) {
            int y = listTop + i * (cardH + rowGap) - scroll;
            drawModule(context, left.get(i), leftX, y, colW, cardH);
        }
        for (int i = 0; i < right.size(); i++) {
            int y = listTop + i * (cardH + rowGap) - scroll;
            drawModule(context, right.get(i), rightX, y, colW, cardH);
        }
        context.disableScissor();

        if (scrollMax > 0) {
            int barX = panelX + panelW - 11;
            int barY = listTop;
            int barH = listHeight;
            context.fill(barX, barY, barX + 3, barY + barH, 0xFF303030);
            int thumbH = Math.max(25, barH * barH / (barH + scrollMax));
            int thumbY = barY + (barH - thumbH) * scroll / scrollMax;
            rounded(context, barX, thumbY, 4, thumbH, 0xFFBDBDBD);
        }

        centeredText(context, "MMB - bind     RMB - settings",
                0, Math.min(height - 12, panelY + panelH + 8), width, TEXT);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawModule(DrawContext context, Module module, int x, int y, int w, int h) {
        rounded(context, x, y, w, h, CARD);

        int labelY = module.description.isEmpty() ? y + 11 : y + 4;
        text(context, module.name, x + 10, labelY, TEXT);

        if (!module.description.isEmpty()) {
            text(context, module.description, x + 12, y + 18, MUTED);
        }

        int sw = 31;
        int sh = 15;
        int sx = x + w - sw - 9;
        int sy = y + (h - sh) / 2;
        rounded(context, sx, sy, sw, sh, module.enabled ? selectedColor : 0xFF191919);

        int knob = 9;
        int kx = module.enabled ? sx + sw - knob - 3 : sx + 3;
        rounded(context, kx, sy + 3, knob, knob, 0xFFFFFFFF);
    }

    private List<Module> currentLeft() {
        return selectedTab == 0 ? renderLeft : selectedTab == 1 ? hudLeft : utilLeft;
    }

    private List<Module> currentRight() {
        return selectedTab == 0 ? renderRight : selectedTab == 1 ? hudRight : utilRight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int paletteX = Math.max(8, (width - Math.min(width - 16, 520)) / 2);
            int paletteW = Math.min(width - 16, 520);
            int dotsStart = paletteX + 38;
            int usable = Math.max(20, paletteW - 100);
            int step = Math.max(20, Math.min(27, usable / palette.length));

            for (int i = 0; i < palette.length; i++) {
                int x = dotsStart + i * step;
                if (x + 15 < paletteX + paletteW - 38 &&
                        mouseX >= x - 2 && mouseX <= x + 16 &&
                        mouseY >= 37 && mouseY <= 55) {
                    selectedColor = palette[i];
                    return true;
                }
            }

            int tabW = 66;
            int gap = 10;
            int totalTabsW = tabW * 3 + gap * 2;
            int tabX = panelX + (panelW - totalTabsW) / 2;
            int tabY = panelY + 9;

            for (int i = 0; i < tabs.length; i++) {
                int x = tabX + i * (tabW + gap);
                if (mouseX >= x && mouseX <= x + tabW &&
                        mouseY >= tabY && mouseY <= tabY + 25) {
                    selectedTab = i;
                    scroll = 0;
                    return true;
                }
            }

            int listTop = panelY + 43;
            int listBottom = panelY + panelH - 8;
            int colGap = 8;
            int colW = (panelW - 40 - colGap) / 2;
            int leftX = panelX + 20;
            int rightX = leftX + colW + colGap;
            int cardH = 31;
            int rowGap = 6;

            if (mouseY >= listTop && mouseY < listBottom) {
                Module clicked = findClicked(currentLeft(), mouseX, mouseY,
                        leftX, colW, listTop, cardH, rowGap);
                if (clicked == null) {
                    clicked = findClicked(currentRight(), mouseX, mouseY,
                            rightX, colW, listTop, cardH, rowGap);
                }
                if (clicked != null) {
                    clicked.enabled = !clicked.enabled; // пока визуально
                    return true;
                }
            }
        }

        if (button == 1) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Module findClicked(List<Module> modules, double mouseX, double mouseY,
                               int x, int w, int listTop, int cardH, int rowGap) {
        if (mouseX < x || mouseX > x + w) return null;
        for (int i = 0; i < modules.size(); i++) {
            int y = listTop + i * (cardH + rowGap) - scroll;
            if (mouseY >= y && mouseY <= y + cardH) return modules.get(i);
        }
        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        scroll -= (int) (verticalAmount * 22);
        scroll = Math.max(0, Math.min(scrollMax, scroll));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void text(DrawContext context, String value, int x, int y, int color) {
        context.drawTextWithShadow(textRenderer, Text.literal(value), x, y, color);
    }

    private void centeredText(DrawContext context, String value,
                              int x, int y, int w, int color) {
        int textW = textRenderer.getWidth(value);
        text(context, value, x + (w - textW) / 2, y, color);
    }

    private void rounded(DrawContext context, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        int r = Math.min(7, Math.min(w / 2, h / 2));
        for (int row = 0; row < h; row++) {
            double dy = row < r ? r - row - 0.5
                    : row >= h - r ? row - (h - r) + 0.5 : 0;
            int inset = dy == 0 ? 0
                    : Math.max(0, r - (int) Math.floor(Math.sqrt(Math.max(0, r * r - dy * dy))));
            context.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
        }
    }

    /** Внутренний класс модуля — визуальный тумблер. */
    public static class Module {
        public final String name;
        public final String description;
        public boolean enabled;

        public Module(String name) { this(name, ""); }
        public Module(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
