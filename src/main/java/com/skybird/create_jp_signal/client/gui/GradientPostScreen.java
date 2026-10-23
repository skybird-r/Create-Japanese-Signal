package com.skybird.create_jp_signal.client.gui;

import java.util.Locale;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.menu.GradientPostMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetGradientPostPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GradientPostScreen extends AbstractContainerScreen<GradientPostMenu> {
    private ModularGuiLine frontLine;
    private ModularGuiLine backLine;
    private Checkbox decimals;
    private Checkbox letterO;
    private Checkbox tunnel;

    public GradientPostScreen(GradientPostMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 220;
        imageHeight = 200;
    }

    @Override protected void init() {
        super.init();
        CompoundTag data = new CompoundTag();
        if (frontLine != null) {
            frontLine.saveValues(data);
            backLine.saveValues(data);
        } else {
            data.putInt("Front", menu.frontGradient);
            data.putInt("Back", menu.backGradient);
        }
        boolean tunnelMode = tunnel == null ? menu.tunnelMode : tunnel.selected();
        boolean useLetterO = letterO == null ? menu.useLetterO : letterO.selected();
        boolean showDecimals = decimals == null ? menu.showDecimals : decimals.selected();
        frontLine = createLine(40, "Front", "front");
        backLine = createLine(73, "Back", "back");
        frontLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        backLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        decimals = addRenderableWidget(new Checkbox(leftPos + 20, topPos + 94, 180, 20,
            Component.translatable("create_jp_signal.gui.gradient_post.decimals"), showDecimals));
        letterO = addRenderableWidget(new Checkbox(leftPos + 20, topPos + 118, 180, 20,
            Component.translatable("create_jp_signal.gui.sign.use_letter_o"), useLetterO));
        tunnel = addRenderableWidget(new Checkbox(leftPos + 20, topPos + 142, 180, 20,
            Component.translatable("create_jp_signal.gui.gradient_post.tunnel_mode"), tunnelMode));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(leftPos + 60, topPos + 173, 100, 20).build());
    }

    private ModularGuiLine createLine(int y, String key, String title) {
        ModularGuiLine line = new ModularGuiLine();
        new ModularGuiLineBuilder(font, line, leftPos + 20, topPos + y)
            .addScrollInput(0, 180, (input, label) -> input.withRange(-9999, 10000)
                // Disable ScrollInput's extra Shift rounding: each fast step is exactly 5.0.
                .withShiftStep(1).withStepFunction(context -> context.shift ? 50 : 1)
                .format(value -> Component.literal(String.format(Locale.ROOT, "%+.1f ‰", value / 10.0)))
                .titled(Component.translatable("create_jp_signal.gui.gradient_post." + title)), key);
        return line;
    }

    @Override public void onClose() {
        if (frontLine != null) {
            CompoundTag data = new CompoundTag();
            frontLine.saveValues(data);
            backLine.saveValues(data);
            PacketHandler.sendToServer(new SetGradientPostPacket(menu.pos,
                data.getInt("Front"), data.getInt("Back"), decimals.selected(), letterO.selected(), tunnel.selected()));
        }
        super.onClose();
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3F3F3F);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, title, imageWidth / 2, 8, 0xFFFFFF);
        graphics.drawString(font, Component.translatable("create_jp_signal.gui.gradient_post." + (tunnel.selected() ? "left" : "front")), 20, 24, 0xFFFFFF);
        graphics.drawString(font, Component.translatable("create_jp_signal.gui.gradient_post." + (tunnel.selected() ? "right" : "back")), 20, 57, 0xFFFFFF);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
