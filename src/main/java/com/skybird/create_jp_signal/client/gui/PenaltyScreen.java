package com.skybird.create_jp_signal.client.gui;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.create.train.track.PenaltyBoundary;
import com.skybird.create_jp_signal.menu.PenaltyMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetPenaltyPacket;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PenaltyScreen extends AbstractContainerScreen<PenaltyMenu> {

    private ModularGuiLine penaltyLine;

    public PenaltyScreen(PenaltyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 200;
        imageHeight = 80;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;
        titleLabelX = -9999;

        CompoundTag data = new CompoundTag();
        data.putInt("Penalty", menu.blockEntity == null ? 100 : menu.blockEntity.getPenalty());

        penaltyLine = new ModularGuiLine();
        ModularGuiLineBuilder builder = new ModularGuiLineBuilder(font, penaltyLine, x + 20, y + 30);
        builder.addScrollInput(0, 160, (input, label) -> {
            input.withRange(0, PenaltyBoundary.MAX_PENALTY + 1)
                .withStepFunction(context -> context.shift ? 100 : 10)
                .titled(Component.translatable("create_jp_signal.gui.penalty.value"));
            label.withSuffix(" m");
        }, "Penalty");

        penaltyLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(x + 50, y + 50, 100, 20)
            .build());
    }

    @Override
    public void removed() {
        super.removed();
        if (penaltyLine == null || menu.blockEntity == null)
            return;
        CompoundTag data = new CompoundTag();
        penaltyLine.saveValues(data);
        PacketHandler.sendToServer(new SetPenaltyPacket(menu.blockEntity.getBlockPos(), data.getInt("Penalty")));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3F3F3F);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, title, imageWidth / 2, 10, 0xFFFFFF);
    }
}
