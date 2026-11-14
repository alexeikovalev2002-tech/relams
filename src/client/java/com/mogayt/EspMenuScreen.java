package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EspMenuScreen extends Screen {

    // Цветовая схема (Pulse Visuals style)
    private static final int BG_COLOR = new Color(10, 10, 15, 230).getRGB();
    private static final int SIDEBAR_COLOR = new Color(15, 15, 20, 255).getRGB();
    private static final int ACCENT_COLOR = new Color(150, 50, 255).getRGB(); // Фиолетовый
    private static final int TEXT_COLOR = new Color(220, 220, 220).getRGB();
    private static final int TEXT_DISABLED = new Color(120, 120, 120).getRGB();
    private static final int HOVER_COLOR = new Color(255, 255, 255, 30).getRGB();
    private static final int ENABLED_BG = new Color(150, 50, 255, 60).getRGB();

    private int panelX, panelY, panelW, panelH;
    private Category currentCategory = Category.COMBAT;

    // Список модулей для текущей вкладки
    private final List<ModuleEntry> currentModules = new ArrayList<>();

    public EspMenuScreen() {
        super(Text.literal("Mog Client"));
    }

    private enum Category {
        COMBAT("Combat"),
        RENDER("Render"),
        PLAYER("Player"),
        MISC("Misc");

        public final String name;
        Category(String name) { this.name = name; }
    }

    @Override
    protected void init() {
        // Размеры окна
        panelW = 360;
        panelH = 240;
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

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
                // Для ChatBind сделаем фейковый тумблер, так как это не модуль, а список
                currentModules.add(new ModuleEntry("ChatBind (" + ChatBind.binds.size() + ")", () -> false, v -> {}, () -> client.setScreen(new ChatBindScreen())));
                break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Затемняем фон игры
        this.renderBackground(context, mouseX, mouseY, delta);

        // 1. Основное окно
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, BG_COLOR);
        // Рамка
        context.drawBorder(panelX, panelY, panelW, panelH, ACCENT_COLOR);

        // 2. Левая панель (Sidebar)
        int sidebarW = 100;
        context.fill(panelX, panelY, panelX + sidebarW, panelY + panelH, SIDEBAR_COLOR);
        context.drawBorder(panelX, panelY, sidebarW, panelH, new Color(40, 40, 50).getRGB());

        // Заголовок в сайдбаре
        context.drawCenteredTextWithShadow(this.textRenderer, "MOG", panelX + sidebarW / 2, panelY + 15, ACCENT_COLOR);
        context.drawCenteredTextWithShadow(this.textRenderer, "CLIENT", panelX + sidebarW / 2, panelY + 25, 0xFFAAAAAA);

        // Отрисовка категорий
        int catY = panelY + 50;
        for (Category cat : Category.values()) {
            boolean isHovered = isHovered(mouseX, mouseY, panelX, catY, sidebarW, 20);
            boolean isSelected = (currentCategory == cat);

            if (isSelected) {
                context.fill(panelX, catY, panelX + 3, catY + 20, ACCENT_COLOR); // Полоска слева
                context.fill(panelX, catY, panelX + sidebarW, catY + 20, HOVER_COLOR); // Подсветка фона
            } else if (isHovered) {
                context.fill(panelX, catY, panelX + sidebarW, catY + 20, HOVER_COLOR);
            }

            int color = isSelected ? ACCENT_COLOR : TEXT_COLOR;
            context.drawTextWithShadow(this.textRenderer, cat.name, panelX + 15, catY + 6, color);
            catY += 22;
        }

        // 3. Правая панель (Модули)
        int contentX = panelX + sidebarW + 10;
        int contentY = panelY + 10;
        int contentW = panelW - sidebarW - 20;

        // Заголовок категории
        context.drawTextWithShadow(this.textRenderer, currentCategory.name + " Modules", contentX, contentY, TEXT_COLOR);
        context.fill(contentX, contentY + 12, contentX + contentW, contentY + 13, new Color(40, 40, 50).getRGB());
        contentY += 20;

        // Отрисовка модулей
        for (ModuleEntry mod : currentModules) {
            boolean enabled = mod.state.get();
            boolean isHovered = isHovered(mouseX, mouseY, contentX, contentY, contentW, 18);

            // Фон модуля
            if (enabled) {
                context.fill(contentX, contentY, contentX + contentW, contentY + 18, ENABLED_BG);
            } else if (isHovered) {
                context.fill(contentX, contentY, contentX + contentW, contentY + 18, HOVER_COLOR);
            }

            // Текст модуля
            int color = enabled ? 0xFFFFFFFF : TEXT_DISABLED;
            context.drawTextWithShadow(this.textRenderer, mod.name, contentX + 5, contentY + 5, color);

            // Индикатор включения (справа)
            if (enabled) {
                context.fill(contentX + contentW - 12, contentY + 5, contentX + contentW - 5, contentY + 12, ACCENT_COLOR);
            } else {
                context.drawBorder(contentX + contentW - 12, contentY + 5, 8, 8, new Color(80, 80, 80).getRGB());
            }

            contentY += 22;
        }

        // Подсказка внизу
        context.drawTextWithShadow(this.textRenderer, "ЛКМ - Вкл/Выкл | ПКМ - Настройки", panelX + 10, panelY + panelH - 12, new Color(100, 100, 100).getRGB());

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sidebarW = 100;
        
        // 1. Проверка клика по категориям
        int catY = panelY + 50;
        for (Category cat : Category.values()) {
            if (isHovered(mouseX, mouseY, panelX, catY, sidebarW, 20)) {
                currentCategory = cat;
                updateModuleList(); // Обновляем список модулей
                return true;
            }
            catY += 22;
        }

        // 2. Проверка клика по модулям
        int contentX = panelX + sidebarW + 10;
        int contentY = panelY + 10 + 20;
        int contentW = panelW - sidebarW - 20;

        for (ModuleEntry mod : currentModules) {
            if (isHovered(mouseX, mouseY, contentX, contentY, contentW, 18)) {
                if (button == 0) {
                    // ЛКМ - переключить
                    boolean currentState = mod.state.get();
                    mod.toggle.accept(!currentState);
                    Notification.show(mod.name, !currentState);
                    Config.save();
                } else if (button == 1) {
                    // ПКМ - открыть настройки
                    mod.openSettings.run();
                }
                return true;
            }
            contentY += 22;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isHovered(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Вспомогательный класс для хранения данных модуля
    private record ModuleEntry(String name, Supplier<Boolean> state, Consumer<Boolean> toggle, Runnable openSettings) {}
                                   }
