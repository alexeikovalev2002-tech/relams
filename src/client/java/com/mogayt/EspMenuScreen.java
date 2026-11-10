package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspMenuScreen extends Screen {

    private int espX, espY, espW, espH;
    private int chestX, chestY, chestW, chestH;
    private int playerX, playerY, playerW, playerH;
    private int itemX, itemY, itemW, itemH;
    private int chatX, chatY, chatW, chatH;
    private int aimX, aimY, aimW, aimH;
    private int botX, botY, botW, botH;
    private int totemX, totemY, totemW, totemH;
    private int maceX, maceY, maceW, maceH;
    private int crystalX, crystalY, crystalW, crystalH;
    private int anchorX, anchorY, anchorW, anchorH;
    private int fbX, fbY, fbW, fbH;
    private int fcX, fcY, fcW, fcH;
    private int optX, optY, optW, optH;
    private int trjX, trjY, trjW, trjH;
    private int cncX, cncY, cncW, cncH;

    // Настройки скролла и ползунка
    private double scrollOffset = 0;
    private double maxScroll = 0;
    private int scrollbarX, scrollbarY, scrollbarW, scrollbarH;
    private boolean isDragging = false;

    // Общая высота контента (от первой кнопки до низа последней)
    private static final int TOTAL_CONTENT_HEIGHT = 304;
    private static final int VIEW_HEIGHT_MARGIN = 50; // Отступы сверху и снизу экрана

    public EspMenuScreen() {
        super(Text.literal("Mog Mod Menu"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;
        int w = 200, h = 16;
        int x = this.width / 2 - 100;

        // Вычисляем область просмотра и максимальный скролл
        int viewHeight = this.height - VIEW_HEIGHT_MARGIN;
        maxScroll = Math.max(0, TOTAL_CONTENT_HEIGHT - viewHeight);

        // Ограничиваем скролл
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        int startY = (cy - 140) - (int) scrollOffset;
        
        // Настройки ползунка
        scrollbarX = this.width / 2 + 105;
        scrollbarY = cy - 140;
        scrollbarW = 6;
        scrollbarH = viewHeight;

        espX = x; espY = startY; espW = w; espH = h;
        chestX = x; chestY = startY + 18; chestW = w; chestH = h;
        playerX = x; playerY = startY + 36; playerW = w; playerH = h;
        itemX = x; itemY = startY + 54; itemW = w; itemH = h;
        chatX = x; chatY = startY + 72; chatW = w; chatH = h;
        aimX = x; aimY = startY + 90; aimW = w; aimH = h;
        botX = x; botY = startY + 108; botW = w; botH = h;
        totemX = x; totemY = startY + 126; totemW = w; totemH = h;
        maceX = x; maceY = startY + 144; maceW = w; maceH = h;
        crystalX = x; crystalY = startY + 162; crystalW = w; crystalH = h;
        anchorX = x; anchorY = startY + 180; anchorW = w; anchorH = h;
        fbX = x; fbY = startY + 198; fbW = w; fbH = h;
        fcX = x; fcY = startY + 216; fcW = w; fcH = h;
        optX = x; optY = startY + 234; optW = w; optH = h;
        trjX = x; trjY = startY + 252; trjW = w; trjH = h;
        cncX = x; cncY = startY + 270; cncW = w; cncH = h;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getEspLabel()),
                (b) -> { BlockEspMod.espEnabled = !BlockEspMod.espEnabled; Notification.show("ESP", BlockEspMod.espEnabled); b.setMessage(Text.literal(getEspLabel())); }
        ).dimensions(espX, espY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getChestLabel()),
                (b) -> { BlockEspMod.chestEspEnabled = !BlockEspMod.chestEspEnabled; Notification.show("ChestESP", BlockEspMod.chestEspEnabled); b.setMessage(Text.literal(getChestLabel())); }
        ).dimensions(chestX, chestY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"),
                (b) -> { BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled; Notification.show("PlayerESP", BlockEspMod.playerEspEnabled); b.setMessage(Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ")); }
        ).dimensions(playerX, playerY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.itemEspEnabled ? "ItemESP: ВКЛ" : "ItemESP: ВЫКЛ"),
                (b) -> { BlockEspMod.itemEspEnabled = !BlockEspMod.itemEspEnabled; Notification.show("ItemESP", BlockEspMod.itemEspEnabled); b.setMessage(Text.literal(BlockEspMod.itemEspEnabled ? "ItemESP: ВКЛ" : "ItemESP: ВЫКЛ")); }
        ).dimensions(itemX, itemY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("ChatBind [" + ChatBind.binds.size() + " шт]"),
                (b) -> {}
        ).dimensions(chatX, chatY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"),
                (b) -> { AimMobs.enabled = !AimMobs.enabled; Notification.show("AimMobs", AimMobs.enabled); b.setMessage(Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ")); }
        ).dimensions(aimX, aimY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ"),
                (b) -> { AimBot.enabled = !AimBot.enabled; Notification.show("AimBot", AimBot.enabled); b.setMessage(Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ")); }
        ).dimensions(botX, botY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AutoTotem.enabled ? "AutoTotem: ВКЛ" : "AutoTotem: ВЫКЛ"),
                (b) -> { AutoTotem.enabled = !AutoTotem.enabled; Notification.show("AutoTotem", AutoTotem.enabled); b.setMessage(Text.literal(AutoTotem.enabled ? "AutoTotem: ВКЛ" : "AutoTotem: ВЫКЛ")); }
        ).dimensions(totemX, totemY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(MaceAura.enabled ? "MaceAura: ВКЛ" : "MaceAura: ВЫКЛ"),
                (b) -> { MaceAura.enabled = !MaceAura.enabled; Notification.show("MaceAura", MaceAura.enabled); b.setMessage(Text.literal(MaceAura.enabled ? "MaceAura: ВКЛ" : "MaceAura: ВЫКЛ")); }
        ).dimensions(maceX, maceY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(CrystalAura.enabled ? "CrystalAura: ВКЛ" : "CrystalAura: ВЫКЛ"),
                (b) -> { CrystalAura.enabled = !CrystalAura.enabled; Notification.show("CrystalAura", CrystalAura.enabled); b.setMessage(Text.literal(CrystalAura.enabled ? "CrystalAura: ВКЛ" : "CrystalAura: ВЫКЛ")); }
        ).dimensions(crystalX, crystalY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AnchorAura.enabled ? "AnchorAura: ВКЛ" : "AnchorAura: ВЫКЛ"),
                (b) -> { AnchorAura.enabled = !AnchorAura.enabled; Notification.show("AnchorAura", AnchorAura.enabled); b.setMessage(Text.literal(AnchorAura.enabled ? "AnchorAura: ВКЛ" : "AnchorAura: ВЫКЛ")); }
        ).dimensions(anchorX, anchorY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"),
                (b) -> { Fullbright.enabled = !Fullbright.enabled; Notification.show("Fullbright", Fullbright.enabled); b.setMessage(Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ")); }
        ).dimensions(fbX, fbY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"),
                (b) -> { Freecam.enabled = !Freecam.enabled; if (Freecam.enabled) Freecam.onEnable(); Notification.show("Freecam", Freecam.enabled); b.setMessage(Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ")); }
        ).dimensions(fcX, fcY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ"),
                (b) -> { Optimizer.enabled = !Optimizer.enabled; Notification.show("Fix Lag", Optimizer.enabled); b.setMessage(Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ")); }
        ).dimensions(optX, optY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ"),
                (b) -> { TrajectoryPredictor.enabled = !TrajectoryPredictor.enabled; Notification.show("Trajectory", TrajectoryPredictor.enabled); b.setMessage(Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ")); }
        ).dimensions(trjX, trjY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(CameraNoClip.enabled ? "CameraNoClip: ВКЛ" : "CameraNoClip: ВЫКЛ"),
                (b) -> { CameraNoClip.enabled = !CameraNoClip.enabled; Notification.show("CameraNoClip", CameraNoClip.enabled); b.setMessage(Text.literal(CameraNoClip.enabled ? "CameraNoClip: ВКЛ" : "CameraNoClip: ВЫКЛ")); }
        ).dimensions(cncX, cncY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (b) -> this.close()
        ).dimensions(x, startY + 288, w, h).build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= verticalAmount * 15; // Крутим колесико
        
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        
        this.clearAndInit();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Проверяем клик по ползунку
        if (button == 0 && maxScroll > 0) {
            int viewHeight = this.height - VIEW_HEIGHT_MARGIN;
            int thumbHeight = Math.max(20, (int) ((float) viewHeight * viewHeight / TOTAL_CONTENT_HEIGHT));
            int thumbY = scrollbarY + (int) ((scrollOffset / maxScroll) * (scrollbarH - thumbHeight));

            if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarW && mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                isDragging = true;
                return true;
            }
        }

        if (button == 1) {
            if (inside(mouseX, mouseY, espX, espY, espW, espH)) { if (this.client != null) this.client.setScreen(new OreSelectScreen()); return true; }
            if (inside(mouseX, mouseY, chestX, chestY, chestW, chestH)) { if (this.client != null) this.client.setScreen(new ChestSelectScreen()); return true; }
            if (inside(mouseX, mouseY, playerX, playerY, playerW, playerH)) { if (this.client != null) this.client.setScreen(new PlayerEspScreen()); return true; }
            if (inside(mouseX, mouseY, itemX, itemY, itemW, itemH)) { if (this.client != null) this.client.setScreen(new ItemEspScreen()); return true; }
            if (inside(mouseX, mouseY, chatX, chatY, chatW, chatH)) { if (this.client != null) this.client.setScreen(new ChatBindScreen()); return true; }
            if (inside(mouseX, mouseY, aimX, aimY, aimW, aimH)) { if (this.client != null) this.client.setScreen(new AimMobsScreen()); return true; }
            if (inside(mouseX, mouseY, botX, botY, botW, botH)) { if (this.client != null) this.client.setScreen(new AimBotScreen()); return true; }
            if (inside(mouseX, mouseY, totemX, totemY, totemW, totemH)) { if (this.client != null) this.client.setScreen(new AutoTotemScreen()); return true; }
            if (inside(mouseX, mouseY, maceX, maceY, maceW, maceH)) { if (this.client != null) this.client.setScreen(new MaceAuraScreen()); return true; }
            if (inside(mouseX, mouseY, crystalX, crystalY, crystalW, crystalH)) { if (this.client != null) this.client.setScreen(new CrystalAuraScreen()); return true; }
            if (inside(mouseX, mouseY, anchorX, anchorY, anchorW, anchorH)) { if (this.client != null) this.client.setScreen(new AnchorAuraScreen()); return true; }
            if (inside(mouseX, mouseY, fbX, fbY, fbW, fbH)) { if (this.client != null) this.client.setScreen(new FullbrightScreen()); return true; }
            if (inside(mouseX, mouseY, fcX, fcY, fcW, fcH)) { if (this.client != null) this.client.setScreen(new FreecamScreen()); return true; }
            if (inside(mouseX, mouseY, optX, optY, optW, optH)) { if (this.client != null) this.client.setScreen(new OptimizerScreen()); return true; }
            if (inside(mouseX, mouseY, trjX, trjY, trjW, trjH)) { if (this.client != null) this.client.setScreen(new TrajectoryScreen()); return true; }
            if (inside(mouseX, mouseY, cncX, cncY, cncW, cncH)) { if (this.client != null) this.client.setScreen(new CameraNoClipScreen()); return true; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging && button == 0) {
            int viewHeight = this.height - VIEW_HEIGHT_MARGIN;
            int thumbHeight = Math.max(20, (int) ((float) viewHeight * viewHeight / TOTAL_CONTENT_HEIGHT));
            double scrollableTrack = scrollbarH - thumbHeight;

            // Центрируем ползунок относительно курсора мыши
            double ratio = (mouseY - scrollbarY - (thumbHeight / 2.0)) / scrollableTrack;
            if (ratio < 0) ratio = 0;
            if (ratio > 1) ratio = 1;

            scrollOffset = ratio * maxScroll;
            this.clearAndInit();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Рисуем ползунок (только если есть что скроллить)
        if (maxScroll > 0) {
            // Фон ползунка
            context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarW, scrollbarY + scrollbarH, 0x55000000);
            
            // Сам ползунок
            int thumbHeight = Math.max(20, (int) ((float) scrollbarH * scrollbarH / TOTAL_CONTENT_HEIGHT));
            int thumbY = scrollbarY + (int) ((scrollOffset / maxScroll) * (scrollbarH - thumbHeight));
            context.fill(scrollbarX, thumbY, scrollbarX + scrollbarW, thumbY + thumbHeight, 0xFFAAAAAA);
        }

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("ПКМ по кнопкам — настройки"),
                this.width / 2, 18, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    private String getEspLabel() {
        return "ESP [" + BlockEspMod.getOreName() + "]: " + (BlockEspMod.espEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    private String getChestLabel() {
        return "ChestESP [" + BlockEspMod.getChestName() + "]: " + (BlockEspMod.chestEspEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
