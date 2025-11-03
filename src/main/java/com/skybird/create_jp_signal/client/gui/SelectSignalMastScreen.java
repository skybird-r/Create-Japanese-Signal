package com.skybird.create_jp_signal.client.gui;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.AllBlocks;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SelectSignalMastPacket;
import com.skybird.create_jp_signal.util.Lang;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class SelectSignalMastScreen extends Screen {
    private static final List<Block> SELECTABLE_SIGNALS = List.of(
        AllBlocks.COLOR_SINGLE_ROUND_SIGNAL_MAST.get(),
        AllBlocks.COLOR_SINGLE_SQUARE_SIGNAL_MAST.get(),
        AllBlocks.COLOR_SINGLE_TUNNEL_SIGNAL_MAST.get(),
        AllBlocks.REPEATER_SINGLE_SIGNAL_MAST.get(),
        AllBlocks.REPEATER_SINGLE_TUNNEL_SIGNAL_MAST.get(),
        AllBlocks.SHUNT_SINGLE_SIGNAL_MAST.get()
    );

    public SelectSignalMastScreen() {
        super(Lang.translatable("gui.select_signal_mast.title"));
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 240;
        int buttonHeight = 20;
        int startX = (this.width - buttonWidth) / 2;
        int startY = (this.height - (SELECTABLE_SIGNALS.size() * (buttonHeight + 5))) / 2;

        for (int i = 0; i < SELECTABLE_SIGNALS.size(); i++) {
            Block block = SELECTABLE_SIGNALS.get(i);
            ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
            
            this.addRenderableWidget(Button.builder(
                Component.translatable(block.getDescriptionId()),
                (button) -> {
                    PacketHandler.sendToServer(new SelectSignalMastPacket(blockId.toString()));
                    this.onClose();
                })
                .bounds(startX, startY + i * (buttonHeight + 5), buttonWidth, buttonHeight)
                .build());
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // 背景を描画
        this.renderBackground(pGuiGraphics);
        
        // 中央にタイトルを描画
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // init()で追加したボタンなどのウィジェットを描画
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
