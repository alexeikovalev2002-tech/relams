package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspMenuScreen extends Screen {

    private int espX, espY, espW, espH;
    private int chestX, chestY, chestW, chestH;

    public EspMenuScreen() {
        super(Text.literal("Mog Mod Menu"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        espW = 200;
        espH = 20;
        espX = this.width / 2 - 100;
        espY = centerY - 110;

        chestW = 200;
        chestH = 20;
        chestX = this.width / 2 - 100;
        chestY = centerY - 80;

        // ESP руды
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getEspLabel()),
                (button) -> {
                    BlockEspMod.espEnabled = !BlockEspMod.espEnabled;
                    button.setMessage(Text.literal(getEspLabel()));
                }
        ).dimensions(espX, espY, espW, espH).build());

        // ESP сундуки
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getChestLabel()),
                (button) -> {
                    BlockEspMod.chestEspEnabled = !BlockEspMod.chestEspEnabled;
                    button.setMessage(Text.literal(getChestLabel()));
                }
        ).dimensions(chestX, chestY, chestW, chestH).build());

        // Player ESP
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled;
                    button.setMessage(Text.literal(BlockEspMod.playerEspEnabled ? "PlayerESP: ВКЛ" : "PlayerESP: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 50, 200, 20).build());

        // Fullbright
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"),
                (button) -> {
                    Fullbright.enabled = !Fullbright.enabled;
                    button.setMessage(Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 20, 200, 20).build());

        // Freecam
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"),
                (button) -> {
                    Freecam.enabled = !Freecam.enabled;
                    if (Freecam.enabled) Freecam.onEnable();
                    button.setMessage(Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY + 10, 200, 20).build());

        // Distant Horizons
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.distantEnabled ? "Distant: ВКЛ" : "Distant: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.distantEnabled = !BlockEspMod.distantEnabled;
                    button.setMessage(Text.literal(BlockEspMod.distantEnabled ? "Distant: ВКЛ" : "Distant: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY + 40, 200, 20).build());

        // Fix Lag
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ"),
                (button) -> {
                    Optimizer.enabled = !Optimizer.enabled;
                    button.setMessage(Text.literal(Optimizer.enabled ? "Fix Lag: ВКЛ" : "Fix Lag: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY + 70, 200, 20).build());

        // Закрыть
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (button) -> this.close()
        ).dimensions(this.width / 2 - 100, centerY + 110, 200, 20).build());
    }

    private String getEspLabel() {
        String state = BlockEspMod.espEnabled ? "ВКЛ" : "ВЫКЛ";
        return "ESP [" + BlockEspMod.getOreName() + "]: " + state;
    }

    private String getChestLabel() {
        String state = BlockEspMod.chestEspEnabled ? "ВКЛ" : "ВЫКЛ";
        return "ChestESP [" + BlockEspMod.getChestName() + "]: " + state;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1
                && mouseX >= espX && mouseX <= espX + espW
                && mouseY >= espY && mouseY <= espY + espH) {
            if (this.client != null) {
                this.client.setScreen(new OreSelectScreen());
            }
            return true;
        }
        if (button == 1
                && mouseX >= chestX && mouseX <= chestX + chestW
                && mouseY >= chestY && mouseY <= chestY + chestH) {
            if (this.client != null) {
                this.client.setScreen(new ChestSelectScreen());
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("ПКМ по ESP или ChestESP — выбор"),
                this.width / 2, 40, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
