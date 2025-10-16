package com.skybird.create_jp_signal.client.gui;

import java.util.Map;

import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity.AttachmentSlot;
import com.skybird.create_jp_signal.menu.MastLinkMenu;
import com.skybird.create_jp_signal.network.FinalizeLinkPacket;
import com.skybird.create_jp_signal.network.PacketHandler;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MastLinkScreen extends AbstractContainerScreen<MastLinkMenu> {

    public MastLinkScreen(MastLinkMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width / 2) - 100;
        int y = (this.height / 2) - 40;
        int yOffset = 0;

        Map<AttachmentSlot, SignalHead> heads = this.menu.blockEntity.getSignalHeads();

        for (Map.Entry<AttachmentSlot, SignalHead> entry : heads.entrySet()) {
            SignalHead head = entry.getValue();
            
            Button button = Button.builder(
                Component.literal(entry.getKey().name() + " の信号機に接続" + (head.getControllerPos() == null ? "" : "(接続中)")),
                (btn) -> {
                    PacketHandler.sendToServer(new FinalizeLinkPacket(
                        this.menu.controlBoxPos,
                        this.menu.blockEntity.getBlockPos(),
                        head.getUniqueId()
                    ));
                    this.onClose();
                })
                .bounds(x, y + yOffset, 200, 20)
                .build();
            addRenderableWidget(button);
            yOffset += 24;
        }

        if (yOffset == 0) {
            // 空いているスロットが一つもない場合
            // (ボタンが一つも作られなかった場合)
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) { }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, (this.height / 2) - 60, 0xFFFFFF);
    }
}