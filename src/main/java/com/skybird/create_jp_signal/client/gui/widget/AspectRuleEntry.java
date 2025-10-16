package com.skybird.create_jp_signal.client.gui.widget;

import com.skybird.create_jp_signal.block.signal.SignalAspect;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class AspectRuleEntry extends AbstractWidget implements ContainerEventHandler {
    public final EditBox indexBox;
    public final DropdownWidget<SignalAspect.State> aspectDropdown;
    public final Button deleteButton;
    private final List<GuiEventListener> children;
    @Nullable private GuiEventListener focused;
    private boolean isDragging;

    public AspectRuleEntry(int x, int y, int index, SignalAspect.State aspect, List<SignalAspect.State> validAspects, Consumer<AspectRuleEntry> onDelete) {
        super(x, y, 105, 16, Component.empty());
        this.indexBox = new EditBox(Minecraft.getInstance().font, x, y, 25, 16, Component.empty());
        this.indexBox.setValue(String.valueOf(index));
        this.aspectDropdown = new DropdownWidget<>(x + 30, y, 79, 16,
            validAspects, (state) -> Component.literal(state.getDisplayName()), (state) -> {});
        this.aspectDropdown.setCurrentOption(aspect);
        // ボタンが押されたら、自分自身(this)を onDelete コールバックに渡す
        this.deleteButton = Button.builder(Component.literal("X"), (btn) -> onDelete.accept(this))
            .bounds(x + 114, y, 16, 16).build();
        this.children = List.of(this.indexBox, this.aspectDropdown, this.deleteButton);
    }

    public void setPosition(int x, int y) {
        this.setX(x); this.setY(y);
        this.indexBox.setX(x); this.indexBox.setY(y);
        this.aspectDropdown.setX(x + 30); this.aspectDropdown.setY(y);
        this.deleteButton.setX(x + 114); this.deleteButton.setY(y);
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.children.forEach(c -> ((AbstractWidget)c).render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override @Nonnull public List<? extends GuiEventListener> children() { return this.children; }
    @Override public boolean isDragging() { return this.isDragging; }
    @Override public void setDragging(boolean pDragging) { this.isDragging = pDragging; }
    @Nullable @Override public GuiEventListener getFocused() { return this.focused; }

    @Override
    public void setFocused(@Nullable GuiEventListener pListener) {
        if (this.focused != null) this.focused.setFocused(false);
        this.focused = pListener;
        if (this.focused != null) this.focused.setFocused(true);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        // 子要素のいずれかがクリックイベントを処理したら、その子にフォーカスを当てる
        for (GuiEventListener child : this.children()) {
            if (child.mouseClicked(pMouseX, pMouseY, pButton)) {
                this.setFocused(child);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        // フォーカスされている子（EditBoxなど）にキーボード入力を転送する
        return this.getFocused() != null && this.getFocused().keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return this.getFocused() != null && this.getFocused().charTyped(pCodePoint, pModifiers);
    }
    
    @Override protected void updateWidgetNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {}
}