package com.skybird.create_jp_signal.client.gui;

import java.util.Map;

import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity.AttachmentSlot;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.menu.SignalLinkMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SignalLinkPacket;
import com.skybird.create_jp_signal.util.Lang;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SignalLinkScreen extends AbstractContainerScreen<SignalLinkMenu> {

    public SignalLinkScreen(SignalLinkMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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
            Component alreadyLinked = head.getControllerPos() == null ? Component.literal("") : Lang.translatable("gui.signal_link.already_linked");
            
            Button button = Button.builder(
                Lang.translatable("gui.signal_link.link_button", entry.getKey().name()).append(alreadyLinked),
                (btn) -> {
                    PacketHandler.sendToServer(new SignalLinkPacket(
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