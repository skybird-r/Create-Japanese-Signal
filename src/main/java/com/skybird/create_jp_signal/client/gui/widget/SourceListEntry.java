package com.skybird.create_jp_signal.client.gui.widget;

import com.skybird.create_jp_signal.block.signal.AspectMapping;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SourceListEntry extends AbstractWidget implements ContainerEventHandler {
    private final BlockPos pos;
    public final Button posButton;
    public final DropdownWidget<SignalAccessory.Route> routeDropdown;
    public final Button deleteButton;

    private final List<GuiEventListener> children;
    @Nullable private GuiEventListener focused;
    private boolean isDragging;

    public SourceListEntry(BlockPos pos, AspectMapping mapping, Consumer<BlockPos> onSelect, Consumer<BlockPos> onDelete) {
        super(0, 0, 166, 16, Component.empty());
        this.pos = pos;

        this.posButton = Button.builder(Component.literal(pos.toShortString()), b -> onSelect.accept(pos))
                .bounds(0, 0, 100, 16).build();

        this.routeDropdown = new DropdownWidget<>(105, 0, 40, 16,
                Arrays.asList(SignalAccessory.Route.values()),
                (ht) -> Component.literal(ht.getDisplayName()),
                (newRoute) -> {
                    if (mapping.getRoute() == newRoute) return;
                    mapping.setRoute(newRoute);
                }
        );
        this.routeDropdown.setCurrentOption(mapping.getRoute());

        this.deleteButton = Button.builder(Component.literal("X"), b -> onDelete.accept(pos))
                .bounds(150, 0, 16, 16).build();

        this.children = List.of(this.posButton, this.routeDropdown, this.deleteButton);
    }
    
    public BlockPos getPos() { return this.pos; }

    public void setPosition(int x, int y) {
        this.setX(x); this.setY(y);
        this.posButton.setX(x); this.posButton.setY(y);
        this.routeDropdown.setX(x + 105); this.routeDropdown.setY(y);
        this.deleteButton.setX(x + 150); this.deleteButton.setY(y);
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
        for (GuiEventListener child : this.children()) {
            if (child.mouseClicked(pMouseX, pMouseY, pButton)) {
                this.setFocused(child);
                return true;
            }
        }
        return false;
    }

    @Override protected void updateWidgetNarration(@Nonnull NarrationElementOutput pNarrationElementOutput) {}
}