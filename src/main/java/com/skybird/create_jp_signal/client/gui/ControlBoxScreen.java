package com.skybird.create_jp_signal.client.gui;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import com.skybird.create_jp_signal.block.signal.signal_type.ISignalType;
import com.skybird.create_jp_signal.client.gui.widget.AspectRuleListWidget;
import com.skybird.create_jp_signal.client.gui.widget.DropdownWidget;
import com.skybird.create_jp_signal.client.gui.widget.SourceListWidget;
import com.skybird.create_jp_signal.menu.ControlBoxMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.UnlinkIndexPacket;
import com.skybird.create_jp_signal.network.UpdateControlBoxDataPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ControlBoxScreen extends AbstractContainerScreen<ControlBoxMenu> {
    
    //private static final ResourceLocation TEXTURE = new ResourceLocation(JpSignals.MODID, "textures/gui/control_box_background.png");

    // GUIが編集するために保持する、データのローカルコピー
    private ISignalAppearance appearance;
    private Map<BlockPos, AspectMapping> sourceMappings;
    

    // GUIで選択されているIndexブロック
    private BlockPos selectedIndexPos;

    private Checkbox repeaterCheckbox;
    private AspectRuleListWidget ruleListWidget;
    private SourceListWidget sourceListWidget;
    
    public ControlBoxScreen(ControlBoxMenu pMenu, Inventory pInv, Component pTitle) {
        super(pMenu, pInv, pTitle);
    
        // まず、編集用のローカル変数を初期化
        this.sourceMappings = new HashMap<>();
        this.appearance = null;
        this.selectedIndexPos = null;
    
        // isLinked()のチェックを一度だけ行い、その中で全ての初期化を完了させる
        if (this.menu.blockEntity.isLinked()) {
            // --- リンク済みの場合 ---
            // BlockEntityからAppearanceとMappingをコピーする
            this.appearance = this.menu.initialAppearance.copy();
            this.menu.blockEntity.getSourceMappings().forEach((pos, mapping) -> {
                this.sourceMappings.put(pos, mapping.copy());
            });
            // 最初のIndexを選択状態にする
            this.selectedIndexPos = this.sourceMappings.keySet().stream().findFirst().orElse(null);
        }
        
        this.imageWidth = 320;
        this.imageHeight = 220;

        // プレイヤーインベントリのタイトルラベルを非表示にする
        this.titleLabelY = -9999;
        this.inventoryLabelY = -9999;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (!this.menu.blockEntity.isLinked() || this.appearance == null) {
            addRenderableWidget(Button.builder(Component.literal("信号柱に紐付けてください"), b -> this.onClose())
                .bounds(this.width / 2 - 100, this.height / 2 - 10, 200, 20).build());
            return;
        }

        // 上部
        int topY = y + 5;
        if (this.appearance instanceof ColorLightSignalAppearance clAppearance) {
            addRenderableWidget(new DropdownWidget<>(x + 5, topY, 120, 16,
                Arrays.asList(ColorLightSignalAppearance.HeadType.values()),
                (ht) -> Component.literal("タイプ: " + ht.getDisplayName()),
                (newHeadType) -> {
                    if (clAppearance.getHeadType() == newHeadType) return;
                    
                    clAppearance.setHeadType(newHeadType);
                    
                    // ローカルの全マッピングを、新しい灯数用のデフォルトにリセット
                    ISignalType type = this.menu.blockEntity.getSignalType();
                    if (type != null) {
                        this.sourceMappings.replaceAll((pos, oldMapping) -> {
                            AspectMapping newDefaultMapping = type.createDefaultMapping(clAppearance);
                            // routeの設定を引き継ぐ
                            newDefaultMapping.setRoute(oldMapping.getRoute());
                            return newDefaultMapping;
                        });
                    }
                    
                    this.init();
                }
            )).setCurrentOption(clAppearance.getHeadType());

            this.repeaterCheckbox = new Checkbox(x + 135, topY - 2 , 20, 20, Component.literal("紫灯"), clAppearance.isRepeater());
            addRenderableWidget(this.repeaterCheckbox);

            addRenderableWidget(new DropdownWidget<>(x + 195, topY, 120, 16,
                clAppearance.getValidAccesoryTypes(),
                (ap) -> Component.literal("附属機: " + ap.getDisplayName()),
                (newType) -> {
                    if (clAppearance.getAccessory().getType() == newType) return;
                    clAppearance.getAccessory().setType(newType);
                    this.init();
                }
            )).setCurrentOption(clAppearance.getAccessory().getType());
        } else if (this.appearance instanceof PositionLightRepeaterSignalAppearance plrAppearance) {
            addRenderableWidget(new DropdownWidget<>(x + 10, topY, 100, 16,
                Arrays.asList(PositionLightRepeaterSignalAppearance.RepeaterForm.values()),
                (ht) -> Component.literal("タイプ: " + ht.getDisplayName()),
                (newHeadType) -> {
                    if (plrAppearance.getForm() == newHeadType) return;
                    
                    plrAppearance.setForm(newHeadType);
                    
                    // ローカルの全マッピングを、新しい灯数用のデフォルトにリセット
                    ISignalType type = this.menu.blockEntity.getSignalType();
                    if (type != null) {
                        this.sourceMappings.replaceAll((pos, oldMapping) -> {
                            AspectMapping newDefaultMapping = type.createDefaultMapping(plrAppearance);
                            // routeの設定を引き継ぐ
                            newDefaultMapping.setRoute(oldMapping.getRoute());
                            return newDefaultMapping;
                        });
                    }
                    
                    this.init();
                }
            )).setCurrentOption(plrAppearance.getForm());

            addRenderableWidget(new DropdownWidget<>(x + 195, topY, 120, 16,
                plrAppearance.getValidAccesoryTypes(),
                (ap) -> Component.literal("附属機: " + ap.getDisplayName()),
                (newType) -> {
                    if (plrAppearance.getAccessory().getType() == newType) return;
                    plrAppearance.getAccessory().setType(newType);
                    this.init();
                }
            )).setCurrentOption(plrAppearance.getAccessory().getType());
        }

        // 左側：Indexブロックリスト
        int listY = y + 35;
        this.sourceListWidget = new SourceListWidget(x + 5, y + 35, 170, 160);
        this.sourceListWidget.updateSources(this.sourceMappings, this.selectedIndexPos,
                (selectedPos) -> {
                    if (this.ruleListWidget != null && this.selectedIndexPos != null) {
                        AspectMapping originalMapping = this.sourceMappings.get(this.selectedIndexPos);
                        AspectMapping updatedMapping = this.ruleListWidget.getMappingFromWidgets(originalMapping);
                        this.sourceMappings.put(this.selectedIndexPos, updatedMapping);
                    }
                    this.selectedIndexPos = selectedPos;
                    this.init();
                },
                (posToDelete) -> {
                    this.sourceMappings.remove(posToDelete);
                    this.selectedIndexPos = this.sourceMappings.keySet().stream().findFirst().orElse(null);
                    this.init();
                }
        );
        this.addRenderableWidget(this.sourceListWidget);
        

        // 右側：現示マッピング
        if (this.selectedIndexPos != null) {
            AspectMapping selectedMapping = this.sourceMappings.get(this.selectedIndexPos);
            ISignalType signalType = this.menu.blockEntity.getSignalType();
            List<SignalAspect.State> validStates = signalType != null ? signalType.getValidStates(this.appearance) : List.of();
    
            if (selectedMapping != null) {
                this.ruleListWidget = new AspectRuleListWidget(x + 185, y + 35, 130, 140, validStates, this::init);
                
                this.ruleListWidget.updateRules(selectedMapping,
                    (entryToDelete) -> {
                        if (entryToDelete != null) {
                            int index = Integer.parseInt(entryToDelete.indexBox.getValue());
                            selectedMapping.removeRule(index);
                        }
                        this.init();
                    }
                );
                this.addRenderableWidget(this.ruleListWidget);
            
                addRenderableWidget(Button.builder(Component.literal("+ ルールを追加"), (btn) -> {
                    selectedMapping.addRule(selectedMapping.getRules().size(), validStates.get(0));
                    this.init();
                }).bounds(x + 185, y + 180, 130, 16).build());
            }
        }
    
        // 下部：保存/キャンセル
        addRenderableWidget(Button.builder(Component.literal("保存"), (btn) -> {
            if (this.appearance instanceof ColorLightSignalAppearance clAppearance) {
                clAppearance.setRepeater(this.repeaterCheckbox.selected());
            }
            
            if (this.ruleListWidget != null && this.selectedIndexPos != null) {
                AspectMapping originalMapping = this.sourceMappings.get(this.selectedIndexPos);
                AspectMapping updatedMapping = this.ruleListWidget.getMappingFromWidgets(originalMapping);
                this.sourceMappings.put(this.selectedIndexPos, updatedMapping);
            }
            
            PacketHandler.sendToServer(new UpdateControlBoxDataPacket(
                this.menu.blockEntity.getBlockPos(),
                this.appearance,
                this.sourceMappings
            ));
            this.onClose();
        }).bounds(x + 175, y + 200, 120, 16).build());
        addRenderableWidget(Button.builder(Component.literal("キャンセル"), (btn) -> this.onClose())
            .bounds(x + 25, y + 200, 120, 16).build());
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        if (this.sourceListWidget != null && this.sourceListWidget.mouseClickedExpandedDropdowns(pMouseX, pMouseY, pButton)) {
            return true;
        }

        for (var widget : this.children()) {
            if (widget instanceof AspectRuleListWidget listWidget) {
                if (listWidget.mouseClickedExpandedDropdowns(pMouseX, pMouseY, pButton)) {
                    return true;
                }
            }
        }

        for (var widget : this.children()) {
            if (widget instanceof DropdownWidget<?> dropdown && dropdown.isExpanded()) {
                if (dropdown.mouseClicked(pMouseX, pMouseY, pButton)) {
                    return true;
                }
            }
        }

        // ポップアップがクリックを消費しなかった場合のみ、通常のクリック処理を実行
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void renderBg(GuiGraphics gg, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        //gg.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
    
    @Override
    public void render(GuiGraphics gg, int pMouseX, int pMouseY, float pPartialTick) {

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        gg.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF3F3F3F);
        
        super.render(gg, pMouseX, pMouseY, pPartialTick);
        
        //gg.drawCenteredString(this.font, this.title, this.width / 2, y + 7, 0x404040);
        gg.drawString(this.font, "入力元", x + 10, y + 25, 0xFFFFFF, false);
        if (this.selectedIndexPos != null) {
            gg.drawString(this.font, "選択中:" + this.selectedIndexPos.toShortString(), x + 80, y + 25, 0xFFFFFF, false);
        }

        this.renderTooltip(gg, pMouseX, pMouseY);
        
        // ドロップダウンを最後に描画
        for (var widget : this.children()) {
            if (widget instanceof DropdownWidget<?> dropdown && dropdown.isExpanded()) {
                gg.pose().pushPose();
                gg.pose().translate(0, 0, 200);
                dropdown.renderExpandedList(gg, pMouseX, pMouseY);
                gg.pose().popPose();
            }
            if (widget instanceof AspectRuleListWidget listWidget) {
                listWidget.renderExpandedDropdowns(gg, pMouseX, pMouseY);
            }
        }
        for (var widget : this.children()) {
            if (widget instanceof DropdownWidget<?> dropdown && dropdown.isExpanded()) {
                gg.pose().pushPose();
                gg.pose().translate(0, 0, 200);
                dropdown.renderExpandedList(gg, pMouseX, pMouseY);
                gg.pose().popPose();
            }
            if (widget instanceof AspectRuleListWidget listWidget) {
                listWidget.renderExpandedDropdowns(gg, pMouseX, pMouseY);
            }
            if (widget instanceof SourceListWidget listWidget) {
                listWidget.renderExpandedDropdowns(gg, pMouseX, pMouseY);
            }
        }
    }
}