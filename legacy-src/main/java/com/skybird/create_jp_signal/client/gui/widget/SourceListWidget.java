package com.skybird.create_jp_signal.client.gui.widget;

import com.skybird.create_jp_signal.block.signal.AspectMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SourceListWidget extends AbstractWidget implements ContainerEventHandler {

    private final List<SourceListEntry> entries = new ArrayList<>();
    private double scrollAmount;
    private boolean isDragging;
    @Nullable private BlockPos selectedPos;
    @Nullable private GuiEventListener focused;

    public SourceListWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void updateSources(Map<BlockPos, AspectMapping> sourceMappings, @Nullable BlockPos selectedPos,
                              Consumer<BlockPos> onSelect, Consumer<BlockPos> onDelete) {
        this.entries.clear();
        this.selectedPos = selectedPos;
        this.focused = null;
        sourceMappings.forEach((pos, mapping) -> {
            this.entries.add(new SourceListEntry(pos, mapping, onSelect, onDelete));
        });
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        gg.enableScissor(getX(), getY(), getX() + width, getY() + height);

        for (int i = 0; i < this.entries.size(); i++) {
            SourceListEntry entry = this.entries.get(i);
            int entryX = this.getX() + 2;
            int entryY = this.getY() + 2 + i * 20 - (int) this.scrollAmount;
            
            // ★ハイライト処理★
            if (entry.getPos().equals(this.selectedPos)) {
                gg.fill(entryX - 1, entryY - 1, entryX + entry.getWidth() + 1, entryY + entry.getHeight() + 1, 0xFF888888);
            }

            entry.setPosition(entryX, entryY);
            entry.render(gg, mouseX, mouseY, partialTick);
        }

        gg.disableScissor();
    }
    
    // ポップアップ描画用
    public void renderExpandedDropdowns(GuiGraphics gg, int mouseX, int mouseY) {
        for (SourceListEntry entry : this.entries) {
            if (entry.routeDropdown.isExpanded()) {
                gg.pose().pushPose();
                gg.pose().translate(0, 0, 200);
                entry.routeDropdown.renderExpandedList(gg, mouseX, mouseY);
                gg.pose().popPose();
            }
        }
    }

    // ポップアップクリック処理用
    public boolean mouseClickedExpandedDropdowns(double mouseX, double mouseY, int button) {
        for (SourceListEntry entry : this.entries) {
            if (entry.routeDropdown.isExpanded()) {
                if (entry.routeDropdown.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isMouseOver(pMouseX, pMouseY)) {
            // 1行の高さを20として計算
            double maxScroll = Math.max(0, this.entries.size() * 20 - 20);
            this.scrollAmount = Mth.clamp(this.scrollAmount - pDelta * 10, 0, maxScroll);
            return true;
        }
        return false;
    }

    @Override @Nonnull public List<? extends GuiEventListener> children() { return this.entries; }
    @Override public boolean isDragging() { return this.isDragging; }
    @Override public void setDragging(boolean pDragging) { this.isDragging = pDragging; }
    @Nullable @Override public GuiEventListener getFocused() { return this.focused; }

    @Override public void setFocused(@Nullable GuiEventListener pFocused) {
        if (this.focused != null) this.focused.setFocused(false);
        this.focused = pFocused;
        if (this.focused != null) this.focused.setFocused(true);
    }
    
    @Override public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isMouseOver(pMouseX, pMouseY)) {
            for (GuiEventListener child : this.children()) {
                if (child.mouseClicked(pMouseX, pMouseY, pButton)) {
                    this.setFocused(child);
                    this.setDragging(true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override protected void updateWidgetNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {}
}