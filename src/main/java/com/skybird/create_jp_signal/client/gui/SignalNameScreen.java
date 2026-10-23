package com.skybird.create_jp_signal.client.gui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.lwjgl.glfw.GLFW;
import com.skybird.create_jp_signal.block.signal.SignalName;
import com.skybird.create_jp_signal.menu.SignalNameMenu;
import com.skybird.create_jp_signal.network.PacketHandler;
import com.skybird.create_jp_signal.network.SetSignalNamesPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SignalNameScreen extends AbstractContainerScreen<SignalNameMenu> {
    private final Map<UUID, String> names = new LinkedHashMap<>();
    private EditBox nameField;
    private int selected;
    private boolean cancelled;

    public SignalNameScreen(SignalNameMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 270;
        imageHeight = 150;
        menu.entries.forEach(entry -> names.put(entry.id(), entry.name()));
    }

    private UUID selectedId() { return menu.entries.get(selected).id(); }

    @Override protected void init() {
        if (nameField != null) names.put(selectedId(), nameField.getValue());
        super.init();
        nameField = new EditBox(font, leftPos + 16, topPos + 49, imageWidth - 32, 20,
            Component.translatable("create_jp_signal.gui.signal_name.name"));
        nameField.setMaxLength(SignalName.MAX_CODE_POINTS * 2);
        nameField.setFilter(value -> value.codePointCount(0, value.length()) <= SignalName.MAX_CODE_POINTS);
        nameField.setValue(names.get(selectedId()));
        nameField.setResponder(value -> names.put(selectedId(), value));
        addRenderableWidget(nameField);
        setInitialFocus(nameField);
        if (menu.entries.size() > 1) {
            addRenderableWidget(Button.builder(headLabel(), button -> {
                names.put(selectedId(), nameField.getValue());
                selected = (selected + 1) % menu.entries.size();
                nameField.setValue(names.get(selectedId()));
                button.setMessage(headLabel());
            }).bounds(leftPos + 16, topPos + 23, imageWidth - 32, 20).build());
        }
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
            .bounds(leftPos + 16, topPos + 120, 112, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
            cancelled = true;
            onClose();
        }).bounds(leftPos + 142, topPos + 120, 112, 20).build());
    }

    private Component headLabel() {
        return Component.translatable("create_jp_signal.gui.signal_name.head." + menu.entries.get(selected).slot().name().toLowerCase(java.util.Locale.ROOT));
    }

    @Override protected void containerTick() {
        super.containerTick();
        if (nameField != null) nameField.tick();
    }

    @Override public void onClose() {
        if (!cancelled && nameField != null) {
            names.put(selectedId(), nameField.getValue());
            PacketHandler.sendToServer(new SetSignalNamesPacket(menu.pos, menu.containerId, Map.copyOf(names)));
        }
        super.onClose();
    }

    @Override public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) { onClose(); return true; }
        if (nameField != null && nameField.isFocused() && key != GLFW.GLFW_KEY_ESCAPE) {
            if (nameField.keyPressed(key, scanCode, modifiers)) return true;
            // Do not let the inventory key close the editor while entering a name.
            if (minecraft.options.keyInventory.matches(key, scanCode)) return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3F3F3F);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(font, title, imageWidth / 2, 8, 0xFFFFFF);
        if (menu.entries.size() == 1) graphics.drawString(font, headLabel(), 16, 29, 0xFFFFFF, false);
        graphics.drawString(font, Component.translatable("create_jp_signal.gui.signal_name.empty"), 16, 78, 0xCCCCCC, false);
        graphics.drawString(font, Component.translatable("create_jp_signal.gui.signal_name.vertical"), 16, 92, 0xCCCCCC, false);
        graphics.drawString(font, Component.translatable("create_jp_signal.gui.signal_name.limit", SignalName.MAX_CODE_POINTS), 16, 106, 0xAAAAAA, false);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
