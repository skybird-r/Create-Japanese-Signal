package com.skybird.create_jp_signal.client.gui;

import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity.AttachmentSlot;
import com.skybird.create_jp_signal.block.signal.signal_type.AllSignalTypes;
import com.skybird.create_jp_signal.block.signal.signal_type.ColorLightSignalType;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.client.gui.widget.AspectRuleListWidget;
import com.skybird.create_jp_signal.client.gui.widget.DropdownWidget;
import com.skybird.create_jp_signal.menu.MastConfigMenu;
import com.skybird.create_jp_signal.network.ConfigureMastPacket;
import com.skybird.create_jp_signal.network.PacketHandler;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MastConfigScreen extends AbstractContainerScreen<MastConfigMenu> {

    private ISignalType selectedType;
    private int selectedHeadCount = 1;
    private ColorLightSignalAppearance.BackplateType selectedBackplate = ColorLightSignalAppearance.BackplateType.ROUND;

    public MastConfigScreen(MastConfigMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        // BEに保存されているタイプを初期値として読み込む
        this.selectedType = this.menu.blockEntity.getSignalType();
        if (this.selectedType == null) {
            this.selectedType = AllSignalTypes.COLOR_LIGHT_SIGNAL; // 未設定なら色灯式をデフォルト
        }

        this.selectedHeadCount = this.menu.blockEntity.getSignalHeads().size();
        if (this.selectedHeadCount == 0) this.selectedHeadCount = 1;


        SignalHead primaryHead = this.menu.blockEntity.getSignalHeads().get(AttachmentSlot.PRIMARY);
        if (primaryHead != null && primaryHead.getAppearance() instanceof ColorLightSignalAppearance appearance) {
            this.selectedBackplate = appearance.getBackplateType();
        }

        this.titleLabelY = -9999;
        this.inventoryLabelY = -9999;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        int x = (this.width / 2) - 100;
        int y = (this.height / 2) - 60;
        int yOffset = 0;



        //信号機タイプ選択
        DropdownWidget<ISignalType> typeDropdown = new DropdownWidget<>(x, y + yOffset + 10, 200, 20,
            AllSignalTypes.getValues().stream().toList(),
            (type) -> Component.literal(type.getDisplayName()),
            (type) -> {
                this.selectedType = type;
                this.init();
            }
        );
        typeDropdown.setCurrentOption(this.selectedType);
        addRenderableWidget(typeDropdown);
        yOffset += 24;

        // 色灯式
        if (this.selectedType instanceof ColorLightSignalType) {
            yOffset += 10;
            
            // 個数選択
            DropdownWidget<Integer> countDropdown = new DropdownWidget<>(x, y + yOffset + 10, 98, 20,
                List.of(1, 2),
                (count) -> Component.literal(count + "個"),
                (count) -> this.selectedHeadCount = count
            );
            countDropdown.setCurrentOption(this.selectedHeadCount);
            addRenderableWidget(countDropdown);

            // 背板タイプ
            DropdownWidget<ColorLightSignalAppearance.BackplateType> backplateDropdown = new DropdownWidget<>(x + 102, y + yOffset + 10, 98, 20,
                Arrays.asList(ColorLightSignalAppearance.BackplateType.values()),
                (bt) -> Component.literal(bt.getDisplayName()),
                (bt) -> this.selectedBackplate = bt
            );
            backplateDropdown.setCurrentOption(this.selectedBackplate);
            addRenderableWidget(backplateDropdown);
            yOffset += 24;
        }
        
        addRenderableWidget(Button.builder(Component.literal("決定"), (btn) -> {
            CompoundTag appearanceData = new CompoundTag();
            if (this.selectedType instanceof ColorLightSignalType) {
                appearanceData.putString("BackplateType", this.selectedBackplate.name());
            }
        
            PacketHandler.sendToServer(new ConfigureMastPacket(
                this.menu.blockEntity.getBlockPos(),
                this.selectedType.getId(),
                appearanceData,
                this.selectedHeadCount
            ));
            this.onClose();
        
        }).bounds(x, y + 100, 200, 20).build());
    }
    
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {}

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        int x = (this.width / 2) - 100;
        int y = (this.height / 2) - 60;
        int yOffset = 0;

        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, y - 10, 0xFFFFFF);
        
        pGuiGraphics.drawString(this.font, "信号機の種類:", x, y, 0xA0A0A0);
        yOffset += 24;
        
        if (this.selectedType instanceof ColorLightSignalType) {
            yOffset += 10;
            pGuiGraphics.drawString(this.font, "設置数:", x, y + yOffset, 0xA0A0A0);
            pGuiGraphics.drawString(this.font, "背板タイプ:", x + 102, y + yOffset, 0xA0A0A0);
        }

        for (var widget : this.children()) {
            if (widget instanceof DropdownWidget<?> dropdown && dropdown.isExpanded()) {
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(0, 0, 200);
                dropdown.renderExpandedList(pGuiGraphics, pMouseX, pMouseY);
                pGuiGraphics.pose().popPose();
            }
        }
    }
}