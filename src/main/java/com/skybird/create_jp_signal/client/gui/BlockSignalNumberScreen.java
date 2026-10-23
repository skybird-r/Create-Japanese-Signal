package com.skybird.create_jp_signal.client.gui;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.menu.BlockSignalNumberMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetBlockSignalNumberPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BlockSignalNumberScreen extends AbstractContainerScreen<BlockSignalNumberMenu> {
    private ModularGuiLine numberLine;
    private Checkbox letterO;

    public BlockSignalNumberScreen(BlockSignalNumberMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 200;
        imageHeight = 125;
    }

    @Override protected void init() {
        super.init();
        CompoundTag data = new CompoundTag();
        if (numberLine != null) numberLine.saveValues(data);
        else data.putInt("Number", menu.number);
        boolean useLetterO = letterO == null ? menu.useLetterO : letterO.selected();
        numberLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, numberLine, leftPos + 20, topPos + 35)
            .addScrollInput(0, 160, (input, label) -> input.withRange(0, 100)
                .withShiftStep(1).withStepFunction(context -> context.shift ? 5 : 1)
                .titled(Component.translatable("create_jp_signal.gui.block_signal_number.number")), "Number");
        numberLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        letterO = addRenderableWidget(new Checkbox(leftPos + 20, topPos + 57, 160, 20,
            Component.translatable("create_jp_signal.gui.sign.use_letter_o"), useLetterO));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(leftPos + 50, topPos + 95, 100, 20).build());
    }

    @Override public void onClose() {
        if (numberLine != null) {
            CompoundTag data = new CompoundTag();
            numberLine.saveValues(data);
            PacketHandler.sendToServer(new SetBlockSignalNumberPacket(menu.pos, data.getInt("Number"), letterO.selected()));
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
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
