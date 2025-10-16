package com.skybird.create_jp_signal.client.gui.widget;

import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.SignalAspect;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AspectRuleListWidget extends AbstractWidget implements ContainerEventHandler {

    private final List<AspectRuleEntry> entries = new ArrayList<>();
    private final List<SignalAspect.State> validAspects;
    private final Runnable onListChanged;
    private double scrollAmount;
    private boolean isDragging;
    @Nullable
    private GuiEventListener focused;

    public AspectRuleListWidget(int x, int y, int width, int height, List<SignalAspect.State> validAspects, Runnable onListChanged) {
        super(x, y, width, height, Component.empty());
        this.validAspects = validAspects;
        this.onListChanged = onListChanged;
    }
    
    public void updateRules(AspectMapping mapping, Consumer<AspectRuleEntry> onDelete) {
        this.entries.clear();
        this.focused = null;
        mapping.getRules().forEach((index, aspect) -> {
            this.entries.add(new AspectRuleEntry(0, 0, index, aspect, this.validAspects, onDelete));
        });
    }

    public AspectMapping getMappingFromWidgets(AspectMapping originalMapping) {
        AspectMapping newMapping = new AspectMapping();
        // 元のマッピングからrouteの情報を引き継ぐ
        newMapping.setRoute(originalMapping.getRoute());
        for (AspectRuleEntry entry : this.entries) {
            try {
                int index = Integer.parseInt(entry.indexBox.getValue());
                SignalAspect.State aspect = entry.aspectDropdown.getSelectedOption();
                newMapping.addRule(index, aspect);
            } catch (NumberFormatException e) {}
        }
        return newMapping;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        gg.enableScissor(getX() - 1, getY(), getX() + width + 1, getY() + height);
        for (AspectRuleEntry entry : this.entries) {
            entry.setPosition(this.getX() + 1, this.getY() + 2 + this.entries.indexOf(entry) * 20 - (int)this.scrollAmount);
            entry.render(gg, mouseX, mouseY, partialTick);
        }
        gg.disableScissor();
    }

    public void renderExpandedDropdowns(GuiGraphics gg, int mouseX, int mouseY) {
        for (AspectRuleEntry entry : this.entries) {
            if (entry.aspectDropdown.isExpanded()) {
                gg.pose().pushPose();
                gg.pose().translate(0, 0, 200);
                entry.aspectDropdown.renderExpandedList(gg, mouseX, mouseY);
                gg.pose().popPose();
            }
        }
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
    
    /**
     * 展開されているドロップダウンがあれば、そのクリック処理を試みる。
     * @return いずれかのドロップダウンがクリックを消費した場合はtrue
     */
    public boolean mouseClickedExpandedDropdowns(double mouseX, double mouseY, int button) {
        for (AspectRuleEntry entry : this.entries) {
            if (entry.aspectDropdown.isExpanded()) {
                if (entry.aspectDropdown.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        // どのドロップダウンもクリックを消費しなかった
        return false;
    }
    
    @Override public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return getFocused() != null && getFocused().keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    
    @Override public boolean charTyped(char pCodePoint, int pModifiers) {
        return getFocused() != null && getFocused().charTyped(pCodePoint, pModifiers);
    }

    @Override public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isMouseOver(pMouseX, pMouseY)) {
            double maxScroll = Math.max(0, this.entries.size() * 20 - 20);
            this.scrollAmount = Mth.clamp(this.scrollAmount - pDelta * 10, 0, maxScroll);
            return true;
        }
        return false;
    }
    
    @Override protected void updateWidgetNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {}
}