package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EspMenuScreen extends Screen {

    public static int menuX = -1;
    public static int menuY = -1;
    public static int themeColor = 0xFF9632FF;

    private int panelX, panelY, panelW, panelH;
    private Category currentCategory = Category.COMBAT;

    private final List<ModuleEntry> currentModules = new ArrayList<>();
    private double scrollOffset = 0.0;

    private boolean draggingWindow = false;
    private double dragOffsetX = 0.0;
    private double dragOffsetY = 0.0;

    private static final int[] THEME_COLORS = {
            0xFF9632FF,
            0xFF00AAFF,
            0xFFFF5555,
            0xFF00FF00
    };

    public EspMenuScreen() {
        super(Text.literal("Mog Client"));
    }

    private enum Category {
        COMBAT("Combat"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc"),
        SETTINGS("Settings");

        public final String name;

        Category(String name) {
            this.name = name;
        }
    }

    @Override
    protected void init() {
        panelW = 420;
        panelH = 270;

        if (menuX == -1 || menuY == -1) {
            menuX = (this.width - panelW) / 2;
            menuY = (this.height - panelH) / 2;
        }

        clampPanelPosition();
        updateModuleList();
    }

    private void clampPanelPosition() {
        panelX = MathHelper.clamp(menuX, 0, Math.max(0, this.width - panelW));
        panelY = MathHelper.clamp(menuY, 0, Math.max(0, this.height - panelH));
        menuX = panelX;
        menuY = panelY;
    }

    private void setCategory(Category category) {
        this.currentCategory = category;
        this.scrollOffset = 0.0;
        updateModuleList();
    }

    private void openSubScreen(Supplier<Screen> screenSupplier) {
        if (this.client != null) {
            this.client.setScreen(screenSupplier.get());
        }
    }

    private void updateModuleList() {
        currentModules.clear();

        switch (currentCategory) {
            case COMBAT -> {
                currentModules.add(new ModuleEntry("AimBot", () -> AimBot.enabled, v -> AimBot.enabled = v, () -> openSubScreen(AimBotScreen::new)));
                currentModules.add(new ModuleEntry("AimMobs", () -> AimMobs.enabled, v -> AimMobs.enabled = v, () -> openSubScreen(AimMobsScreen::new)));
                currentModules.add(new ModuleEntry("MaceAura", () -> MaceAura.enabled, v -> MaceAura.enabled = v, () -> openSubScreen(MaceAuraScreen::new)));
                currentModules.add(new ModuleEntry("CrystalAura", () -> CrystalAura.enabled, v -> CrystalAura.enabled = v, () -> openSubScreen(CrystalAuraScreen::new)));
                currentModules.add(new ModuleEntry("AnchorAura", () -> AnchorAura.enabled, v -> AnchorAura.enabled = v, () -> openSubScreen(AnchorAuraScreen::new)));
                currentModules.add(new ModuleEntry("TriggerBot", () -> TriggerBot.enabled, v -> TriggerBot.enabled = v, () -> openSubScreen(TriggerBotScreen::new)));
            }
            case RENDER -> {
                currentModules.add(new ModuleEntry("BlockESP", () -> BlockEspMod.espEnabled, v -> BlockEspMod.espEnabled = v, () -> openSubScreen(OreSelectScreen::new)));
                currentModules.add(new ModuleEntry("ChestESP", () -> BlockEspMod.chestEspEnabled, v -> BlockEspMod.chestEspEnabled = v, () -> openSubScreen(ChestSelectScreen::new)));
                currentModules.add(new ModuleEntry("PlayerESP", () -> BlockEspMod.playerEspEnabled, v -> BlockEspMod.playerEspEnabled = v, () -> openSubScreen(PlayerEspScreen::new)));
                currentModules.add(new ModuleEntry("ItemESP", () -> BlockEspMod.itemEspEnabled, v -> BlockEspMod.itemEspEnabled = v, () -> openSubScreen(ItemEspScreen::new)));
                currentModules.add(new ModuleEntry("Fullbright", () -> Fullbright.enabled, v -> Fullbright.enabled = v, () -> openSubScreen(FullbrightScreen::new)));
                currentModules.add(new ModuleEntry("Trajectory", () -> TrajectoryPredictor.enabled, v -> TrajectoryPredictor.enabled = v, () -> openSubScreen(TrajectoryScreen::new)));
                currentModules.add(new ModuleEntry("CameraNoClip", () -> CameraNoClip.enabled, v -> CameraNoClip.enabled = v, () -> openSubScreen(CameraNoClipScreen::new)));
                currentModules.add(new ModuleEntry("Freecam", () -> Freecam.enabled, v -> {
                    Freecam.enabled = v;
                    if (v) {
                        Freecam.onEnable();
                    } else {
                        Freecam.onDisable();
                    }
                }, () -> openSubScreen(FreecamScreen::new)));
            }
            case PLAYER -> {
                currentModules.add(new ModuleEntry("AutoTotem", () -> AutoTotem.enabled, v -> AutoTotem.enabled = v, () -> openSubScreen(AutoTotemScreen::new)));
                currentModules.add(new ModuleEntry("Fix Lag", () -> Optimizer.enabled, v -> Optimizer.enabled = v, () -> openSubScreen(OptimizerScreen::new)));
            }
            case MISC -> currentModules.add(new ModuleEntry(
                    "ChatBind (" + ChatBind.binds.size() + ")",
                    () -> false,
                    v -> {},
                    () -> openSubScreen(ChatBindScreen::new)
            ));
            case SETTINGS -> {
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // Тень и тело окна
        context.fill(panelX - 3, panelY - 3, panelX + panelW + 3, panelY + panelH + 3, 0x60000000);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF00F0F14);

        // Шапка окна
        context.fill(panelX, panelY, panelX + panelW, panelY + 24, 0xFF19191E);
        context.drawTextWithShadow(this.textRenderer, "Mog Client v1.0", panelX + 10, panelY + 8, 0xFFFFFFFF);

        boolean closeHovered = isHovered(mouseX, mouseY, panelX + panelW - 20, panelY + 4, 16, 16);
        context.drawTextWithShadow(this.textRenderer, "x", panelX + panelW - 15, panelY + 8, closeHovered ? 0xFFFF5555 : 0xFFAAAAAA);

        context.drawBorder(panelX, panelY, panelW, panelH, themeColor);

        // Левый сайдбар
        int sidebarW = 105;
        context.fill(panelX, panelY + 24, panelX + sidebarW, panelY + panelH, 0xFF141419);
        context.drawBorder(panelX, panelY + 24, sidebarW, panelH - 24, 0xFF282832);

        int catY = panelY + 38;
        for (Category cat : Category.values()) {
            boolean hovered = isHovered(mouseX, mouseY, panelX, catY, sidebarW, 20);
            boolean selected = (currentCategory == cat);

            if (selected) {
                context.fill(panelX, catY, panelX + 3, catY + 20, themeColor);
                context.fill(panelX, catY, panelX + sidebarW, catY + 20, 0x20FFFFFF);
            } else if (hovered) {
                context.fill(panelX, catY, panelX + sidebarW, catY + 20, 0x10FFFFFF);
            }

            int textColor = selected ? themeColor : 0xFFB4B4B4;
            context.drawTextWithShadow(this.textRenderer, cat.name, panelX + 14, catY + 6, textColor);
            catY += 22;
        }

        // Область контента
        int contentX = panelX + sidebarW + 14;
        int contentY = panelY + 32;
        int contentW = panelW - sidebarW - 24;
        int contentH = panelH - 50;

        context.enableScissor(contentX, contentY, contentX + contentW, contentY + contentH);

        if (currentCategory == Category.SETTINGS) {
            renderSettings(context, mouseX, mouseY, contentX, contentY, contentW);
        } else {
            renderModuleList(context, mouseX, mouseY, contentX, contentY, contentW, contentH);
        }

        context.disableScissor();

        // Подсказка внизу
        context.drawTextWithShadow(
                this.textRenderer,
                "LMB: Toggle | RMB: Settings | Scroll: List",
                panelX + 10,
                panelY + panelH - 13,
                0xFF787882
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderModuleList(DrawContext context, int mouseX, int mouseY, int x, int y, int w, int h) {
        int rowHeight = 20;
        int rowStep = 24;
        int totalHeight = currentModules.size() * rowStep;
        int maxScroll = Math.max(0, totalHeight - h);
        scrollOffset = MathHelper.clamp(scrollOffset, 0.0, maxScroll);

        int drawY = y - (int) scrollOffset;
        for (ModuleEntry mod : currentModules) {
            if (drawY + rowHeight >= y && drawY <= y + h) {
                boolean enabled = Boolean.TRUE.equals(mod.state.get());
                boolean hovered = isHovered(mouseX, mouseY, x, drawY, w, rowHeight)
                        && isHovered(mouseX, mouseY, x, y, w, h);

                if (enabled) {
                    context.fill(x, drawY, x + w, drawY + rowHeight, (themeColor & 0x00FFFFFF) | 0x40000000);
                } else if (hovered) {
                    context.fill(x, drawY, x + w, drawY + rowHeight, 0x14FFFFFF);
                }

                context.drawTextWithShadow(this.textRenderer, mod.name, x + 6, drawY + 6, enabled ? 0xFFFFFFFF : 0xFF9E9EA8);

                int toggleW = 24;
                int toggleH = 12;
                int toggleX = x + w - toggleW - 6;
                int toggleY = drawY + 4;

                context.fill(toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, enabled ? themeColor : 0xFF3C3C46);
                int knobX = enabled ? (toggleX + toggleW - 10) : (toggleX + 2);
                context.fill(knobX, toggleY + 2, knobX + 8, toggleY + 10, 0xFFFFFFFF);
            }
            drawY += rowStep;
        }
    }

    private void renderSettings(DrawContext context, int mouseX, int mouseY, int x, int y, int w) {
        context.drawTextWithShadow(this.textRenderer, "Theme Color", x, y + 4, 0xFFFFFFFF);
        int colorY = y + 20;

        for (int i = 0; i < THEME_COLORS.length; i++) {
            int bx = x + i * 26;
            boolean hover = isHovered(mouseX, mouseY, bx, colorY, 20, 20);
            context.fill(bx, colorY, bx + 20, colorY + 20, THEME_COLORS[i]);
            if (hover) {
                context.drawBorder(bx - 1, colorY - 1, 22, 22, 0xFFFFFFFF);
            }
            if (themeColor == THEME_COLORS[i]) {
                context.drawBorder(bx - 2, colorY - 2, 24, 24, 0xFFFFFFFF);
            }
        }

        int resetY = colorY + 36;
        boolean resetHover = isHovered(mouseX, mouseY, x, resetY, w, 20);
        context.fill(x, resetY, x + w, resetY + 20, resetHover ? 0x26FFFFFF : 0xFF282832);
        context.drawCenteredTextWithShadow(this.textRenderer, "Reset Menu Position", x + w / 2, resetY + 6, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (currentCategory != Category.SETTINGS && !currentModules.isEmpty()) {
            int sidebarW = 105;
            int contentX = panelX + sidebarW + 14;
            int contentY = panelY + 32;
            int contentW = panelW - sidebarW - 24;
            int contentH = panelH - 50;

            if (isHovered(mouseX, mouseY, contentX, contentY, contentW, contentH)) {
                int totalHeight = currentModules.size() * 24;
                int maxScroll = Math.max(0, totalHeight - contentH);
                scrollOffset = MathHelper.clamp(scrollOffset - verticalAmount * 18.0, 0.0, maxScroll);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Шапка: закрытие или перетаскивание
        if (isHovered(mouseX, mouseY, panelX, panelY, panelW, 24) && button == 0) {
            if (isHovered(mouseX, mouseY, panelX + panelW - 20, panelY + 4, 16, 16)) {
                this.close();
                return true;
            }
            draggingWindow = true;
            dragOffsetX = mouseX - panelX;
            dragOffsetY = mouseY - panelY;
            return true;
        }

        // Категории
        int sidebarW = 105;
        int catY = panelY + 38;
        for (Category cat : Category.values()) {
            if (isHovered(mouseX, mouseY, panelX, catY, sidebarW, 20)) {
                setCategory(cat);
                return true;
            }
            catY += 22;
        }

        // Контент
        int contentX = panelX + sidebarW + 14;
        int contentY = panelY + 32;
        int contentW = panelW - sidebarW - 24;
        int contentH = panelH - 50;

        if (currentCategory == Category.SETTINGS) {
            int colorY = contentY + 20;
            for (int i = 0; i < THEME_COLORS.length; i++) {
                int bx = contentX + i * 26;
                if (isHovered(mouseX, mouseY, bx, colorY, 20, 20)) {
                    themeColor = THEME_COLORS[i];
                    return true;
                }
            }

            int resetY = colorY + 36;
            if (isHovered(mouseX, mouseY, contentX, resetY, contentW, 20)) {
                menuX = (this.width - panelW) / 2;
                menuY = (this.height - panelH) / 2;
                clampPanelPosition();
                return true;
            }
        } else if (isHovered(mouseX, mouseY, contentX, contentY, contentW, contentH)) {
            int rowHeight = 20;
            int rowStep = 24;
            int drawY = contentY - (int) scrollOffset;

            for (ModuleEntry mod : currentModules) {
                if (drawY + rowHeight >= contentY && drawY <= contentY + contentH) {
                    if (isHovered(mouseX, mouseY, contentX, drawY, contentW, rowHeight)) {
                        if (button == 0) {
                            boolean nextState = !Boolean.TRUE.equals(mod.state.get());
                            mod.toggle.accept(nextState);
                            Notification.show(mod.name, nextState);
                            Config.save();
                        } else if (button == 1) {
                            mod.openSettings.run();
                        }
                        return true;
                    }
                }
                drawY += rowStep;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingWindow && button == 0) {
            menuX = (int) (mouseX - dragOffsetX);
            menuY = (int) (mouseY - dragOffsetY);
            clampPanelPosition();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingWindow = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isHovered(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private record ModuleEntry(String name, Supplier<Boolean> state, Consumer<Boolean> toggle, Runnable openSettings) {
    }
}
