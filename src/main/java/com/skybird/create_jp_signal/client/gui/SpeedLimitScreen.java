package com.skybird.create_jp_signal.client.gui;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Components;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.train.track.SpeedLimitBoundary;
import com.skybird.create_jp_signal.menu.SpeedLimitMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetSpeedLimitPacket;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SpeedLimitScreen extends AbstractContainerScreen<SpeedLimitMenu> {

    public SpeedLimitScreen(SpeedLimitMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        
        this.imageWidth = 200;
        this.imageHeight = 100;
    }

    private ModularGuiLine speedLine;
    private ModularGuiLine distanceLine;

    @Override
    protected void init() {
        super.init();

        int x = this.leftPos;
        int y = this.topPos;

        this.titleLabelX = -9999;

        double initialSpeed = 50.0;
        double initialDistance = 50.0;
        if (this.menu.blockEntity != null) {
            initialSpeed = this.menu.blockEntity.getSpeedLimit();
            initialDistance = this.menu.blockEntity.getLimitDistance();
        }

        // JpSignals.LOGGER.info("Screen init: " + Double.toString(initialSpeed) + " " + Double.toString(initialDistance));

        // GUIのデータコンテナを初期化
        CompoundTag data = new CompoundTag();
        data.putInt("Speed", (int) initialSpeed);
        data.putInt("Distance", (int) initialDistance);

        speedLine = new ModularGuiLine();
        ModularGuiLineBuilder speedBuilder = new ModularGuiLineBuilder(this.font, speedLine, x + 20, y + 30);
        
        speedBuilder.addScrollInput(0, 160, (si, l) -> {
            si.withRange(15, 501)
                .withStepFunction(c -> c.shift ? 25 : 5)
                .titled(Component.literal("制限速度"));
            l.withSuffix(" km/h");
        }, "Speed");

        distanceLine = new ModularGuiLine();
        ModularGuiLineBuilder distanceBuilder = new ModularGuiLineBuilder(this.font, distanceLine, x + 20, y + 50);

        distanceBuilder.addScrollInput(0, 160, (si, l) -> {
            si.withRange(0, 501)
                .withStepFunction(c -> c.shift ? 50 : 5)
                .titled(Component.literal("制限距離"));
            l.withSuffix(" m");
        }, "Distance");

        speedLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);
        distanceLine.loadValues(data, this::addRenderableWidget, this::addRenderableOnly);

        addRenderableWidget(Button.builder(Components.translatable("gui.done"), btn -> this.onClose())
            .bounds(x + 50, y + 70, 100, 20).build());
    }

    @Override
    public void removed() {
        super.removed();
        CompoundTag data = new CompoundTag();
        speedLine.saveValues(data);
        distanceLine.saveValues(data);
        
        PacketHandler.sendToServer(new SetSpeedLimitPacket(
            this.menu.blockEntity.getBlockPos(),
            data.getInt("Speed"),
            data.getInt("Distance")
        ));
    }

    @Override
    protected void renderBg(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
        // background.render(gg, this.leftPos, this.topPos);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        gg.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF3F3F3F);
    }
    
    @Override
    protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
        gg.drawCenteredString(this.font, this.title, this.width, this.topPos + 10, 0xFFFFFF);
    }
}
