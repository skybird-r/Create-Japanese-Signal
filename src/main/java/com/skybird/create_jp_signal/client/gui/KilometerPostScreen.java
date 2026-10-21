package com.skybird.create_jp_signal.client.gui;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.menu.KilometerPostMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetKilometerPostPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class KilometerPostScreen extends AbstractContainerScreen<KilometerPostMenu> {
    private ModularGuiLine distanceLine;
    private Checkbox letterO;

    public KilometerPostScreen(KilometerPostMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 200;
        imageHeight = 125;
    }

    @Override protected void init() {
        super.init();
        CompoundTag data = new CompoundTag();
        if (distanceLine != null) distanceLine.saveValues(data);
        else data.putInt("Distance", menu.getDistance());
        boolean useLetterO = letterO == null ? menu.useLetterO : letterO.selected();
        distanceLine = new ModularGuiLine();
        new ModularGuiLineBuilder(font, distanceLine, leftPos + 20, topPos + 35)
            .addScrollInput(0, 160, (input, label) -> input.withRange(0, 1000)
                .withStepFunction(context -> context.shift ? 10 : 1)
                .titled(Component.translatable("create_jp_signal.gui.kilometer_post.distance")), "Distance");
        distanceLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        letterO = addRenderableWidget(new Checkbox(leftPos + 20, topPos + 57, 160, 20,
            Component.translatable("create_jp_signal.gui.sign.use_letter_o"), useLetterO));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(leftPos + 50, topPos + 95, 100, 20).build());
    }

    @Override public void onClose() {
        if (distanceLine != null) {
            CompoundTag data = new CompoundTag();
            distanceLine.saveValues(data);
            PacketHandler.sendToServer(new SetKilometerPostPacket(menu.pos, data.getInt("Distance"), letterO.selected()));
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
