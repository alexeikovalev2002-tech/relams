package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspMenuScreen extends Screen {

    private int espX, espY, espW, espH;
    private int chestX, chestY, chestW, chestH;
    private int playerX, playerY, playerW, playerH;
    private int chatX, chatY, chatW, chatH;
    private int aimX, aimY, aimW, aimH;

    public EspMenuScreen() {
        super(Text.literal("Mog Mod Menu"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;
        int w = 200, h = 20;
        int x = this.width / 2 - 100;

        espX = x; espY = cy - 110; espW = w; espH = h;
        chestX = x; chestY = cy - 88; chestW = w; chestH = h;
        playerX = x; playerY = cy - 66; playerW = w; playerH = h;
        chatX = x; chatY = cy - 44; chatW = w; chatH = h;
        aimX = x; aimY = cy - 22; aimW = w; aimH = h;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getEspLabel()),
                (button) -> {
                    BlockEspMod.espEnabled = !BlockEspMod.espEnabled;
                    button.setMessage(Text.literal(getEspLabel()));
                }).dimensions(espX, espY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getChestLabel()),
                (button) -> {
                    BlockEspMod.chestEspEnabled = !BlockEspMod.chestEspEnabled;
                    button.setMessage(Text.literal(getChestLabel()));
                }).dimensions(chestX, chestY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled;
                    button.setMessage(Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"));
                }).dimensions(playerX, playerY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("ChatBind [" + ChatBind.binds.size() + " шт]"),
                (button) -> {}).dimensions(chatX, chatY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"),
                (button) -> {
                    AimMobs.enabled = !AimMobs.enabled;
                    button.setMessage(Text.literal(AimMobs.enabled ? "AimMobs: ВКЛ" : "AimMobs: ВЫКЛ"));
                }).dimensions(aimX, aimY, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"),
                (button) -> {
                    Fullbright.enabled = !Fullbright.enabled;
                    button.setMessage(Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"));
                }).dimensions(x, cy, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"),
                (button) -> {
                    Freecam.enabled = !Freecam.enabled;
                    if (Freecam.enabled) Freecam.onEnable();
                    button.setMessage(Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"));
                }).dimensions(x, cy + 22, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.distantEnabled ? "Distant: ВКЛ" : "Distant: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.distantEnabled = !BlockEspMod.distantEnabled;
                    button.setMessage(Text.literal(BlockEspMod.distantEnabled ? "Distant: ВКЛ" : "Distant: ВЫКЛ"));
                }).dimensions(x, cy + 44, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ"),
                (button) -> {
                    Optimizer.enabled = !Optimizer.enabled;
                    button.setMessage(Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ"));
                }).dimensions(x, cy + 66, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ"),
                (button) -> {
                    TrajectoryPredictor.enabled = !TrajectoryPredictor.enabled;
                    button.setMessage(Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ"));
                }).dimensions(x, cy + 88, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (button) -> this.close()).dimensions(x, cy + 118, w, h).build());
    }

    private String getEspLabel() {
        return "ESP [" + BlockEspMod.getOreName() + "]: " + (BlockEspMod.espEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    private String getChestLabel() {
        return "ChestESP [" + BlockEspMod.getChestName() + "]: " + (BlockEspMod.chestEspEnabled ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            if (inside(mouseX, mouseY, espX, espY, espW, espH) && this.client != null) {
                this.client.setScreen(new OreSelectScreen());
                return true;
            }
            if (inside(mouseX, mouseY, chestX, chestY, chestW, chestH) && this.client != null) {
                this.client.setScreen(new ChestSelectScreen());
                return true;
            }
            if (inside(mouseX, mouseY, playerX, playerY, playerW, playerH) && this.client != null) {
                this.client.setScreen(new PlayerEspScreen());
                return true;
            }
            if (inside(mouseX, mouseY, chatX, chatY, chatW, chatH) && this.client != null) {
                this.client.setScreen(new ChatBindScreen());
                return true;
            }
            if (inside(mouseX, mouseY, aimX, aimY, aimW, aimH) && this.client != null) {
                this.client.setScreen(new AimMobsScreen());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("ПКМ по кнопкам — настройки"),
                this.width / 2, 24, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
