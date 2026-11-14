package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EspMenuScreen extends Screen {

    // Статические переменные, чтобы меню и тема запоминались
    public static int menuX = -1;
    public static int menuY = -1;
    public static int themeColor = 0xFF9632FF; // Фиолетовый по умолчанию (ARGB)

    private int panelX, panelY, panelW, panelH;
    private Category currentCategory = Category.COMBAT;

    private final List<ModuleEntry> currentModules = new ArrayList<>();

    private boolean draggingWindow = false;
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

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
        Category(String name) { this.name = name; }
    }

    @Override
    protected void init() {
        panelW = 400;
        panelH = 260;

        // Если позиция не задана, ставим по центру экрана
        if (menuX == -1 || menuY == -1) {
            menuX = (this.width - panelW) / 2;
            menuY = (this.height - panelH) / 2;
        }

        panelX = menuX;
        panelY = menuY;

        updateModuleList();
    }

    private void updateModuleList() {
        currentModules.clear();
        
        switch (currentCategory) {
            case COMBAT:
                currentModules.add(new ModuleEntry("AimBot", () -> AimBot.enabled, v -> AimBot.enabled = v, () -> client.setScreen(new AimBotScreen())));
                currentModules.add(new ModuleEntry("AimMobs", () -> AimMobs.enabled, v -> AimMobs.enabled = v, () -> client.setScreen(new AimMobsScreen())));
                currentModules.add(new ModuleEntry("MaceAura", () -> MaceAura.enabled, v -> MaceAura.enabled = v, () -> client.setScreen(new MaceAuraScreen())));
                currentModules.add(new ModuleEntry("CrystalAura", () -> CrystalAura.enabled, v -> CrystalAura.enabled = v, () -> client.setScreen(new CrystalAuraScreen())));
                currentModules.add(new ModuleEntry("AnchorAura", () -> AnchorAura.enabled, v -> AnchorAura.enabled = v, () -> client.setScreen(new AnchorAuraScreen())));
                currentModules.add(new ModuleEntry("TriggerBot", () -> TriggerBot.enabled, v -> TriggerBot.enabled = v, () -> client.setScreen(new TriggerBotScreen())));
                break;
            case RENDER:
                currentModules.add(new ModuleEntry("BlockESP", () -> BlockEspMod.espEnabled, v -> BlockEspMod.espEnabled = v, () -> client.setScreen(new OreSelectScreen())));
                currentModules.add(new ModuleEntry("ChestESP", () -> BlockEspMod.chestEspEnabled, v -> BlockEspMod.chestEspEnabled = v, () -> client.setScreen(new ChestSelectScreen())));
                currentModules.add(new ModuleEntry("PlayerESP", () -> BlockEspMod.playerEspEnabled, v -> BlockEspMod.playerEspEnabled = v, () -> client.setScreen(new PlayerEspScreen())));
                currentModules.add(new ModuleEntry("ItemESP", () -> BlockEspMod.itemEspEnabled, v -> BlockEspMod.itemEspEnabled = v, () -> client.setScreen(new ItemEspScreen())));
                currentModules.add(new ModuleEntry("Fullbright", () -> Fullbright.enabled, v -> Fullbright.enabled = v, () -> client.setScreen(new FullbrightScreen())));
                currentModules.add(new ModuleEntry("Trajectory", () -> TrajectoryPredictor.enabled, v -> TrajectoryPredictor.enabled = v, () -> client.setScreen(new TrajectoryScreen())));
                currentModules.add(new ModuleEntry("CameraNoClip", () -> CameraNoClip.enabled, v -> CameraNoClip.enabled = v, () -> client.setScreen(new CameraNoClipScreen())));
                currentModules.add(new ModuleEntry("Freecam", () -> Freecam.enabled, v -> { Freecam.enabled = v; if (v) Freecam.onEnable(); }, () -> client.setScreen(new FreecamScreen())));
                break;
            case PLAYER:
                currentModules.add(new ModuleEntry("AutoTotem", () -> AutoTotem.enabled, v -> AutoTotem.enabled = v, () -> client.setScreen(new AutoTotemScreen())));
                currentModules.add(new ModuleEntry("Fix Lag", () -> Optimizer.enabled, v -> Optimizer.enabled = v, () -> client.setScreen(new OptimizerScreen())));
                break;
            case MISC:
                currentModules.add(new ModuleEntry("ChatBind (" + ChatBind.binds.size() + ")", () -> false, v -> {}, () -> client.setScreen(new ChatBindScreen())));
                break;
            case SETTINGS:
                break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Затемняем фон
        this.renderBackground(context, mouseX, mouseY, delta);

        // 1. Тень окна
        context.fill(panelX - 3, panelY - 3, panelX + panelW + 3, panelY + panelH + 3, 0x60000000);

        // 2. Основное окно
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF00F0F14);
        
        // 3. Шапка окна (за нее таскаем)
        context.fill(panelX, panelY, panelX + panelW, panelY + 24, 0xFF19191E);
        context.drawTextWithShadow(this.textRenderer, "Mog Client v1.0", panelX + 10, panelY + 8, 0xFFFFFFFF);
        
        // Кнопка закрытия (крестик)
        boolean closeHovered = isHovered(mouseX, mouseY, panelX + panelW - 20, panelY + 4, 16, 16);
        context.drawTextWithShadow(this.textRenderer, "x", panelX + panelW - 15, panelY + 8, closeHovered ? 0xFFFF5555 : 0xFFAAAAAA);

        // Рамка окна
        context.drawBorder(panelX, panelY, panelW, panelH, themeColor);

        // 4. Левая панель (Sidebar)
        int sidebarW = 100;
        context.fill(panelX, panelY + 24, panelX + sidebarW, panelY + panelH, 0xFF141419);
        context.drawBorder(panelX, panelY + 24, sidebarW, panelH - 24, 0xFF282832);

        // Отрисовка вкладок
        int catY = panelY + 40;
        for (Category cat : Category.values()) {
            boolean isHovered = isHovered(mouseX, mouseY, panelX, catY, sidebarW, 20);
            boolean isSelected = (currentCategory == cat);

            if (isSelected) {
                context.fill(panelX, catY, panelX + 3, catY + 20, themeColor); // Полоска слева
                context.fill(panelX, catY, panelX + sidebarW, catY + 20, 0x20FFFFFF); // Подсветка фона
            } else if (isHovered) {
                context.fill(panelX, catY, panelX + sidebarW, catY + 20, 0x10FFFFFF);
            }

            int color = isSelected ? themeColor : 0xFFB4B4B4;
            context.drawTextWithShadow(this.textRenderer, cat.name, panelX + 15, catY + 6, color);
            catY += 22;
        }

        // 5. Правая панель (Контент)
        int contentX = panelX + sidebarW + 15;
        int contentY = panelY + 35;
        int contentW = panelW - sidebarW - 30;

        if (currentCategory == Category.SETTINGS) {
            renderSettings(context, mouseX, mouseY, contentX, contentY, contentW);
        } else {
            for (ModuleEntry mod : currentModules) {
                boolean enabled = mod.state.get();
                boolean isHovered = isHovered(mouseX, mouseY, contentX, contentY, contentW, 20);

                // Фон модуля
                if (enabled) {
                    context.fill(contentX, contentY, contentX + contentW, contentY + 20, (themeColor & 0x00FFFFFF) | 0x40000000);
                } else if (isHovered) {
                    context.fill(contentX, contentY, contentX + contentW, contentY + 20, 0x10FFFFFF);
                }

                // Текст
                int color = enabled ? 0xFFFFFFFF : 0xFF969696;
                context.drawTextWithShadow(this.textRenderer, mod.name, contentX + 5, contentY + 6, color);

                // Переключатель (Toggle Switch)
                int toggleX = contentX + contentW - 30;
                int toggleY = contentY + 4;
                int toggleW = 24;
                int toggleH = 12;
                
                context.fill(toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, enabled ? themeColor : 0xFF3C3C3C);
                
                int circleX = enabled ? toggleX + toggleW - 10 : toggleX + 2;
                context.fill(circleX, toggleY + 2, circleX + 8, toggleY + 10, 0xFFFFFFFF);

                contentY += 24;
            }
        }

        // Подсказка внизу
        context.drawTextWithShadow(this.textRenderer, "LMB - Toggle | RMB - Settings", panelX + 10, panelY + panelH - 12, 0xFF646464);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderSettings(DrawContext context, int mouseX, int mouseY, int x, int y, int w) {
        context.drawTextWithShadow(this.textRenderer, "Theme Color", x, y, 0xFFFFFFFF);
        y += 15;
        
        // Кнопки выбора цвета
        int[] colors = {0xFF9632FF, 0xFF00AAFF, 0xFFFF5555, 0xFF00FF00};
        for (int i = 0; i < colors.length; i++) {
            int bx = x + i * 25;
            boolean hover = isHovered(mouseX, mouseY, bx, y, 20, 20);
            context.fill(bx, y, bx + 20, y + 20, colors[i]);
            if (hover) context.drawBorder(bx - 1, y - 1, 22, 22, 0xFFFFFFFF);
            if (themeColor == colors[i]) context.drawBorder(bx - 2, y - 2, 24, 24, 0xFFFFFFFF);
        }
        
        y += 35;
        
        // Кнопка сброса позиции меню
        boolean resetHover = isHovered(mouseX, mouseY, x, y, w, 20);
        context.fill(x, y, x + w, y + 20, resetHover ? 0x20FFFFFF : 0xFF282828);
        context.drawCenteredTextWithShadow(this.textRenderer, "Reset Menu Position", x + w / 2, y + 6, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 1. Проверка клика по шапке (перетаскивание)
        if (isHovered(mouseX, mouseY, panelX, panelY, panelW, 24)) {
            if (button == 0) {
                // Закрытие
                if (isHovered(mouseX, mouseY, panelX + panelW - 20, panelY + 4, 16, 16)) {
                    this.close();
                    return true;
                }
                
                draggingWindow = true;
                dragOffsetX = mouseX - panelX;
                dragOffsetY = mouseY - panelY;
                return true;
            }
        }

        // 2. Клик по вкладкам
        int sidebarW = 100;
        int catY = panelY + 40;
        for (Category cat : Category.values()) {
            if (isHovered(mouseX, mouseY, panelX, catY, sidebarW, 20)) {
                currentCategory = cat;
                updateModuleList();
                return true;
            }
            catY += 22;
        }

        // 3. Клик по контенту
        int contentX = panelX + sidebarW + 15;
        int contentY = panelY + 35;
        int contentW = panelW - sidebarW - 30;

        if (currentCategory == Category.SETTINGS) {
            // Выбор цвета
            int[] colors = {0xFF9632FF, 0xFF00AAFF, 0xFFFF5555, 0xFF00FF00};
            int colorY = contentY + 15;
            for (int i = 0; i < colors.length; i++) {
                int bx = contentX + i * 25;
                if (isHovered(mouseX, mouseY, bx, colorY, 20, 20)) {
                    themeColor = colors[i];
                    return true;
                }
            }
            
            // Сброс позиции
            int resetY = colorY + 35;
            if (isHovered(mouseX, mouseY, contentX, resetY, contentW, 20)) {
                menuX = (this.width - panelW) / 2;
                menuY = (this.height - panelH) / 2;
                panelX = menuX;
                panelY = menuY;
                return true;
            }
        } else {
            // Клик по модулям
            for (ModuleEntry mod : currentModules) {
                if (isHovered(mouseX, mouseY, contentX, contentY, contentW, 20)) {
                    if (button == 0) {
                        boolean currentState = mod.state.get();
                        mod.toggle.accept(!currentState);
                        Notification.show(mod.name, !currentState);
                        Config.save();
                    } else if (button == 1) {
                        mod.openSettings.run();
                    }
                    return true;
                }
                contentY += 24;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingWindow && button == 0) {
            panelX = (int) (mouseX - dragOffsetX);
            panelY = (int) (mouseY - dragOffsetY);
            
            // Ограничиваем экраном
            if (panelX < 0) panelX = 0;
            if (panelY < 0) panelY = 0;
            if (panelX > this.width - panelW) panelX = this.width - panelW;
            if (panelY > this.height - panelH) panelY = this.height - panelH;

            // Запоминаем позицию
            menuX = panelX;
            menuY = panelY;
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

    private record ModuleEntry(String name, Supplier<Boolean> state, Consumer<Boolean> toggle, Runnable openSettings) {}
                                 }
