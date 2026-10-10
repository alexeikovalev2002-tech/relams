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
    private int fbX, fbY, fbW, fbH;
    private int fcX, fcY, fcW, fcH;
    private int optX, optY, optW, optH;
    private int trjX, trjY, trjW, trjH;
    private int cncX, cncY, cncW, cncH;

    public EspMenuScreen() {
        super(Text.literal("Mog Mod Menu"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;
        int w = 200, h = 18;
        int x = this.width / 2 - 100;

        espX = x; espY = cy - 120; espW = w; espH = h;
        chestX = x; chestY = cy - 100; chestW = w; chestH = h;
        playerX = x; playerY = cy - 80; playerW = w; playerH = h;
        itemX = x; itemY = cy - 60; itemW = w; itemH = h;
        chatX = x; chatY = cy - 40; chatW = w; chatH = h;
        aimX = x; aimY = cy - 20; aimW = w; aimH = h;
        botX = x; botY = cy; botW = w; botH = h;
        fbX = x; fbY = cy + 20; fbW = w; fbH = h;
        fcX = x; fcY = cy + 40; fcW = w; fcH = h;
        optX = x; optY = cy + 60; optW = w; optH = h;
        trjX = x; trjY = cy + 80; trjW = w; trjH = h;
        cncX = x; cncY = cy + 100; cncW = w; cncH = h;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getEspLabel()),
                (b) -> { BlockEspMod.espEnabled = !BlockEspMod.espEnabled; b.setMessage(Text.literal(getEspLabel())); }
        ).dimensions(espX, espY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getChestLabel()),
                (b) -> { BlockEspMod.chestEspEnabled = !BlockEspMod.chestEspEnabled; b.setMessage(Text.literal(getChestLabel())); }
        ).dimensions(chestX, chestY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"),
                (b) -> { BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled; b.setMessage(Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ")); }
        ).dimensions(playerX, playerY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.itemEspEnabled ? "ItemESP: ВКЛ" : "ItemESP: ВЫКЛ"),
                (b) -> { BlockEspMod.itemEspEnabled = !BlockEspMod.itemEspEnabled; b.setMessage(Text.literal(BlockEspMod.itemEspEnabled ? "ItemESP: ВКЛ" : "ItemESP: ВЫКЛ")); }
        ).dimensions(itemX, itemY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("ChatBind [" + ChatBind.binds.size() + " шт]"),
                (b) -> {}
        ).dimensions(chatX, chatY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"),
                (b) -> { AimMobs.enabled = !AimMobs.enabled; b.setMessage(Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ")); }
        ).dimensions(aimX, aimY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ"),
                (b) -> { AimBot.enabled = !AimBot.enabled; b.setMessage(Text.literal(AimBot.enabled ? "AimBot: ВКЛ" : "AimBot: ВЫКЛ")); }
        ).dimensions(botX, botY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"),
                (b) -> { Fullbright.enabled = !Fullbright.enabled; b.setMessage(Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ")); }
        ).dimensions(fbX, fbY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"),
                (b) -> { Freecam.enabled = !Freecam.enabled; if (Freecam.enabled) Freecam.onEnable(); b.setMessage(Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ")); }
        ).dimensions(fcX, fcY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ"),
                (b) -> { Optimizer.enabled = !Optimizer.enabled; b.setMessage(Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ")); }
        ).dimensions(optX, optY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ"),
                (b) -> { TrajectoryPredictor.enabled = !TrajectoryPredictor.enabled; b.setMessage(Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ")); }
        ).dimensions(trjX, trjY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(CameraNoClip.enabled ? "CameraNoClip: ВКЛ" : "CameraNoClip: ВЫКЛ"),
                (b) -> { CameraNoClip.enabled = !CameraNoClip.enabled; b.setMessage(Text.literal(CameraNoClip.enabled ? "CameraNoClip: ВКЛ" : "CameraNoClip: ВЫКЛ")); }
        ).dimensions(cncX, cncY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (b) -> this.close()
        ).dimensions(x, cy + 125, w, h).build());
    }

    private String getEspLabel() {
        return "ESP [" + BlockEspMod.getOreName() + "]: " + (BlockEspMod.espEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    private String getChestLabel() {
        return "ChestESP [" + BlockEspMod.getChestName() + "]: " + (BlockEspMod.chestEspEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 1) {
            if (inside(mx, my, espX, espY, espW, espH)) { if (this.client != null) this.client.setScreen(new OreSelectScreen()); return true; }
            if (inside(mx, my, chestX, chestY, chestW, chestH)) { if (this.client != null) this.client.setScreen(new ChestSelectScreen()); return true; }
            if (inside(mx, my, playerX, playerY, playerW, playerH)) { if (this.client != null) this.client.setScreen(new PlayerEspScreen()); return true; }
            if (inside(mx, my, itemX, itemY, itemW, itemH)) { if (this.client != null) this.client.setScreen(new ItemEspScreen()); return true; }
            if (inside(mx, my, chatX, chatY, chatW, chatH)) { if (this.client != null) this.client.setScreen(new ChatBindScreen()); return true; }
            if (inside(mx, my, aimX, aimY, aimW, aimH)) { if (this.client != null) this.client.setScreen(new AimMobsScreen()); return true; }
            if (inside(mx, my, botX, botY, botW, botH)) { if (this.client != null) this.client.setScreen(new AimBotScreen()); return true; }
            if (inside(mx, my, fbX, fbY, fbW, fbH)) { if (this.client != null) this.client.setScreen(new FullbrightScreen()); return true; }
            if (inside(mx, my, fcX, fcY, fcW, fcH)) { if (this.client != null) this.client.setScreen(new FreecamScreen()); return true; }
            if (inside(mx, my, optX, optY, optW, optH)) { if (this.client != null) this.client.setScreen(new OptimizerScreen()); return true; }
            if (inside(mx, my, trjX, trjY, trjW, trjH)) { if (this.client != null) this.client.setScreen(new TrajectoryScreen()); return true; }
            if (inside(mx, my, cncX, cncY, cncW, cncH)) { if (this.client != null) this.client.setScreen(new CameraNoClipScreen()); return true; }
        }
        return super.mouseClicked(mx, my, button);
    }

    private boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("ПКМ по кнопкам — настройки"),
                this.width / 2, 18, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
