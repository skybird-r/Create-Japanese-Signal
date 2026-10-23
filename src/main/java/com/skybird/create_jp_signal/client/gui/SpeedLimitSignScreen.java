package com.skybird.create_jp_signal.client.gui;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.block.sign.RailwaySignSettings.Variant;
import com.skybird.create_jp_signal.menu.SpeedLimitSignMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetSpeedLimitSignPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SpeedLimitSignScreen extends AbstractContainerScreen<SpeedLimitSignMenu> {
    private ModularGuiLine speedLine;
    private Variant variant;
    private Checkbox letterO;
    public SpeedLimitSignScreen(SpeedLimitSignMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = 210; imageHeight = 145;
    }
    @Override protected void init() {
        super.init();
        CompoundTag data = new CompoundTag();
        if (speedLine == null) data.putInt("Speed", menu.speed); else speedLine.saveValues(data);
        if (variant == null) variant = menu.variant;
        boolean useLetterO = letterO == null ? menu.useLetterO : letterO.selected();
        speedLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, speedLine, leftPos + 20, topPos + 35)
            .addScrollInput(0, 170, (input, label) -> input.withRange(5, 501).withShiftStep(1)
                .withStepFunction(context -> context.shift ? 25 : 5)
                .titled(Component.translatable("create_jp_signal.gui.speed_limit_sign.speed")), "Speed");
        speedLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        addRenderableWidget(Button.builder(variantLabel(), button -> {
            variant = Variant.values()[(variant.ordinal() + 1) % Variant.values().length];
            button.setMessage(variantLabel());
        }).bounds(leftPos + 20, topPos + 58, 170, 20).build());
        letterO = addRenderableWidget(new Checkbox(leftPos + 20, topPos + 85, 170, 20,
            Component.translatable("create_jp_signal.gui.sign.use_letter_o"), useLetterO));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(leftPos + 55, topPos + 117, 100, 20).build());
    }
    private Component variantLabel() {
        return Component.translatable("create_jp_signal.gui.speed_limit_sign.variant." + variant.name().toLowerCase(java.util.Locale.ROOT));
    }
    @Override public void onClose() {
        if (speedLine != null) {
            CompoundTag data = new CompoundTag(); speedLine.saveValues(data);
            PacketHandler.sendToServer(new SetSpeedLimitSignPacket(menu.pos, data.getInt("Speed"), variant, letterO.selected()));
        }
        super.onClose();
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3F3F3F);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, title, imageWidth / 2, 10, 0xFFFFFF);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics); super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY);
    }
}
