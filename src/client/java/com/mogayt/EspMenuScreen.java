package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class EspMenuScreen extends Screen {
    private static final int BG = 0xFF1A1A1A;
    private static final int CARD = 0xFF222222;
    private static final int TEXT = 0xFFF0F0F0;
    private static final int MUTED = 0xFF9A9A9A;
    private static final int PURPLE = 0xFF8B23D2;
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

    // ID модулей — что делать при клике
    private static final int MOD_ESP = 0;
    private static final int MOD_CHEST = 1;
    private static final int MOD_PLAYER = 2;
    private static final int MOD_ITEM = 3;
    private static final int MOD_TRAJ = 4;
    private static final int MOD_CNC = 5;
    private static final int MOD_FB = 6;
    private static final int MOD_FC = 7;
    private static final int MOD_FIX = 8;
    private static final int MOD_AIM_MOBS = 9;
    private static final int MOD_AIM_BOT = 10;
    private static final int MOD_TOTEM = 11;
    private static final int MOD_CHAT = 12;

    private static class Module {
        final String name;
        final String description;
        final int id;
        Module(String name, String description, int id) {
            this.name = name; this.description = description; this.id = id;
        }
    }

    private final List<Module> renderLeft = new ArrayList<>();
    private final List<Module> renderRight = new ArrayList<>();
    private final List<Module> hudLeft = new ArrayList<>();
    private final List<Module> hudRight = new ArrayList<>();
    private final List<Module> utilityLeft = new ArrayList<>();
    private final List<Module> utilityRight = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;
    private int scrollMax = 0;

    public EspMenuScreen() {
        super(Text.literal("Mog Menu"));

        renderLeft.add(new Module("ESP", "Подсветка руды", MOD_ESP));
        renderLeft.add(new Module("ChestESP", "Сундуки, шалкеры, бочки", MOD_CHEST));
        renderLeft.add(new Module("PlayerESP", "Подсветка игроков", MOD_PLAYER));
        renderLeft.add(new Module("ItemESP", "Подсветка предметов", MOD_ITEM));

        renderRight.add(new Module("Trajectory", "Траектория полёта", MOD_TRAJ));
        renderRight.add(new Module("CameraNoClip", "Камера сквозь стены", MOD_CNC));
        renderRight.add(new Module("Fullbright", "Ночное зрение", MOD_FB));
        renderRight.add(new Module("Freecam", "Свободная камера", MOD_FC));

        hudLeft.add(new Module("AimMobs", "Помощь в прицеле", MOD_AIM_MOBS));
        hudLeft.add(new Module("AimBot", "Авто-наведение", MOD_AIM_BOT));

        hudRight.add(new Module("AutoTotem", "Авто-тотем", MOD_TOTEM));
        hudRight.add(new Module("Fix Lag", "Оптимизация", MOD_FIX));

        utilityLeft.add(new Module("ChatBind", "Бинды команд", MOD_CHAT));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ==== СОСТОЯНИЕ ====
    private boolean isOn(int id) {
        switch (id) {
            case MOD_ESP: return BlockEspMod.espEnabled;
            case MOD_CHEST: return BlockEspMod.chestEspEnabled;
            case MOD_PLAYER: return BlockEspMod.playerEspEnabled;
            case MOD_ITEM: return BlockEspMod.itemEspEnabled;
            case MOD_TRAJ: return TrajectoryPredictor.enabled;
            case MOD_CNC: return CameraNoClip.enabled;
            case MOD_FB: return Fullbright.enabled;
            case MOD_FC: return Freecam.enabled;
            case MOD_FIX: return Optimizer.enabled;
            case MOD_AIM_MOBS: return AimMobs.enabled;
            case MOD_AIM_BOT: return AimBot.enabled;
            case MOD_TOTEM: return AutoTotem.enabled;
        }
        return false;
    }

    private void toggle(int id) {
        switch (id) {
            case MOD_ESP: BlockEspMod.espEnabled = !BlockEspMod.espEnabled; break;
            case MOD_CHEST: BlockEspMod.chestEspEnabled = !BlockEspMod.chestEspEnabled; break;
            case MOD_PLAYER: BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled; break;
            case MOD_ITEM: BlockEspMod.itemEspEnabled = !BlockEspMod.itemEspEnabled; break;
            case MOD_TRAJ: TrajectoryPredictor.enabled = !TrajectoryPredictor.enabled; break;
            case MOD_CNC: CameraNoClip.enabled = !CameraNoClip.enabled; break;
            case MOD_FB: Fullbright.enabled = !Fullbright.enabled; break;
            case MOD_FC:
                Freecam.enabled = !Freecam.enabled;
                if (Freecam.enabled) Freecam.onEnable();
                break;
            case MOD_FIX: Optimizer.enabled = !Optimizer.enabled; break;
            case MOD_AIM_MOBS: AimMobs.enabled = !AimMobs.enabled; break;
            case MOD_AIM_BOT: AimBot.enabled = !AimBot.enabled; break;
            case MOD_TOTEM: AutoTotem.enabled = !AutoTotem.enabled; break;
        }
    }

    private void openSettings(int id) {
        if (this.client == null) return;
        switch (id) {
            case MOD_ESP: this.client.setScreen(new OreSelectScreen()); break;
            case MOD_CHEST: this.client.setScreen(new ChestSelectScreen()); break;
            case MOD_PLAYER: this.client.setScreen(new PlayerEspScreen()); break;
            case MOD_ITEM: this.client.setScreen(new ItemEspScreen()); break;
            case MOD_TRAJ: this.client.setScreen(new TrajectoryScreen()); break;
            case MOD_CNC: this.client.setScreen(new CameraNoClipScreen()); break;
            case MOD_FB: this.client.setScreen(new FullbrightScreen()); break;
            case MOD_FC: this.client.setScreen(new FreecamScreen()); break;
            case MOD_FIX: this.client.setScreen(new OptimizerScreen()); break;
            case MOD_AIM_MOBS: this.client.setScreen(new AimMobsScreen()); break;
            case MOD_AIM_BOT: this.client.setScreen(new AimBotScreen()); break;
            case MOD_TOTEM: this.client.setScreen(new AutoTotemScreen()); break;
            case MOD_CHAT: this.client.setScreen(new ChatBindScreen()); break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, OUTSIDE);

        int clockW = 92;
        int clockX = (width - clockW) / 2;
        rounded(context, clockX, 3, clockW, 24, 0xFF080808);
        centeredText(context, "Mog Menu", clockX, 10, clockW, TEXT);

        int paletteX = Math.max(8, (width - Math.min(width - 16, 690)) / 2);
        int paletteW = Math.min(width - 16, 690);
        rounded(context, paletteX, 34, paletteW, 42, 0xFF101010);

        text(context, "☼", paletteX + 12, 47, TEXT);
        int dotsStart = paletteX + 48;
        int usable = Math.max(20, paletteW - 115);
        int step = Math.max(24, Math.min(34, usable / palette.length));

        for (int i = 0; i < palette.length; i++) {
            int cx = dotsStart + i * step;
            if (cx + 18 < paletteX + paletteW - 45) {
                rounded(context, cx - 1, 44, 20, 20, 0xFF080808);
                rounded(context, cx, 45, 18, 18, palette[i]);
                if (selectedColor == palette[i]) {
                    context.fill(cx - 1, 43, cx + 19, 44, TEXT);
                }
            }
        }
        text(context, "<  >", paletteX + paletteW - 36, 48, TEXT);

        panelW = Math.min(690, width - 28);
        panelH = Math.min(330, height - 105);
        panelX = (width - panelW) / 2;
        panelY = 86;
        if (panelH < 190) panelH = Math.max(160, height - panelY - 24);

        rounded(context, panelX - 5, panelY - 5, panelW + 10, panelH + 10, 0x33303030);
        rounded(context, panelX, panelY, panelW, panelH, BG);

        int tabY = panelY + 12;
        int tabW = 74;
        int gap = 14;
        int totalTabsW = tabW * 3 + gap * 2;
        int tabX = panelX + (panelW - totalTabsW) / 2;

        for (int i = 0; i < tabs.length; i++) {
            int x = tabX + i * (tabW + gap);
            if (i == selectedTab) {
                rounded(context, x, tabY, tabW, 30, 0xFF666666);
            }
            centeredText(context, tabs[i], x, tabY + 8, tabW, TEXT);
        }

        int listTop = panelY + 53;
        int listBottom = panelY + panelH - 10;
        int listHeight = listBottom - listTop;
        int colGap = 10;
        int colW = (panelW - 48 - colGap) / 2;
        int leftX = panelX + 24;
        int rightX = leftX + colW + colGap;

        List<Module> left = currentLeft();
        List<Module> right = currentRight();
        int rows = Math.max(left.size(), right.size());
        int cardH = 40;
        int rowGap = 8;
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

        String footer = "ЛКМ - вкл/выкл     ПКМ - настройки";
        centeredText(context, footer, 0, Math.min(height - 12, panelY + panelH + 8), width, TEXT);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawModule(DrawContext context, Module module, int x, int y, int w, int h) {
        rounded(context, x, y, w, h, CARD);

        int labelY = module.description.isEmpty() ? y + 14 : y + 7;
        text(context, module.name, x + 12, labelY, TEXT);

        if (!module.description.isEmpty()) {
            text(context, module.description, x + 12, y + 24, MUTED);
        }

        int sw = 36;
        int sh = 17;
        int sx = x + w - sw - 12;
        int sy = y + (h - sh) / 2;
        rounded(context, sx, sy, sw, sh, isOn(module.id) ? selectedColor : 0xFF191919);

        int knob = 11;
        int kx = isOn(module.id) ? sx + sw - knob - 3 : sx + 3;
        rounded(context, kx, sy + 3, knob, knob, 0xFFFFFFFF);
    }

    private List<Module> currentLeft() {
        return selectedTab == 0 ? renderLeft : selectedTab == 1 ? hudLeft : utilityLeft;
    }

    private List<Module> currentRight() {
        return selectedTab == 0 ? renderRight : selectedTab == 1 ? hudRight : utilityRight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int paletteX = Math.max(8, (width - Math.min(width - 16, 690)) / 2);
            int paletteW = Math.min(width - 16, 690);
            int dotsStart = paletteX + 48;
            int usable = Math.max(20, paletteW - 115);
            int step = Math.max(24, Math.min(34, usable / palette.length));

            for (int i = 0; i < palette.length; i++) {
                int x = dotsStart + i * step;
                if (x + 18 < paletteX + paletteW - 45 &&
                        mouseX >= x - 2 && mouseX <= x + 20 &&
                        mouseY >= 43 && mouseY <= 65) {
                    selectedColor = palette[i];
                    return true;
                }
            }

            int tabW = 74;
            int gap = 14;
            int totalTabsW = tabW * 3 + gap * 2;
            int tabX = panelX + (panelW - totalTabsW) / 2;
            int tabY = panelY + 12;

            for (int i = 0; i < tabs.length; i++) {
                int x = tabX + i * (tabW + gap);
                if (mouseX >= x && mouseX <= x + tabW &&
                        mouseY >= tabY && mouseY <= tabY + 30) {
                    selectedTab = i;
                    scroll = 0;
                    return true;
                }
            }

            int listTop = panelY + 53;
            int listBottom = panelY + panelH - 10;
            int colGap = 10;
            int colW = (panelW - 48 - colGap) / 2;
            int leftX = panelX + 24;
            int rightX = leftX + colW + colGap;
            int cardH = 40;
            int rowGap = 8;

            if (mouseY >= listTop && mouseY < listBottom) {
                Module clicked = findClicked(currentLeft(), mouseX, mouseY, leftX, colW, listTop, cardH, rowGap);
                if (clicked == null) {
                    clicked = findClicked(currentRight(), mouseX, mouseY, rightX, colW, listTop, cardH, rowGap);
                }
                if (clicked != null) {
                    toggle(clicked.id);
                    return true;
                }
            }
        }

        if (button == 1) {
            int listTop = panelY + 53;
            int listBottom = panelY + panelH - 10;
            int colGap = 10;
            int colW = (panelW - 48 - colGap) / 2;
            int leftX = panelX + 24;
            int rightX = leftX + colW + colGap;
            int cardH = 40;
            int rowGap = 8;

            if (mouseY >= listTop && mouseY < listBottom) {
                Module clicked = findClicked(currentLeft(), mouseX, mouseY, leftX, colW, listTop, cardH, rowGap);
                if (clicked == null) {
                    clicked = findClicked(currentRight(), mouseX, mouseY, rightX, colW, listTop, cardH, rowGap);
                }
                if (clicked != null) {
                    openSettings(clicked.id);
                    return true;
                }
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Module findClicked(List<Module> modules, double mouseX, double mouseY,
                               int x, int w, int listTop, int cardH, int rowGap) {
        if (mouseX < x || mouseX > x + w) return null;
        for (int i = 0; i < modules.size(); i++) {
            int y = listTop + i * (cardH + rowGap) - scroll;
            if (mouseY >= y && mouseY <= y + cardH) {
                return modules.get(i);
            }
        }
        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        scroll -= (int) (verticalAmount * 28);
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
            int inset = dy == 0 ? 0 : Math.max(0, r - (int) Math.floor(Math.sqrt(Math.max(0, r * r - dy * dy))));
            context.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
        }
    }
                                                    }
