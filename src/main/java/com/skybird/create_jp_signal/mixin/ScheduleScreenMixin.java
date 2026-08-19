package com.skybird.create_jp_signal.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

@Mixin(value = ScheduleScreen.class, remap = false)
public abstract class ScheduleScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void create_jp_signal_keepInventoryKeyInTextBox(
        int keyCode,
        int scanCode,
        int modifiers,
        CallbackInfoReturnable<Boolean> cir
    ) {
        GuiEventListener focused = ((ScheduleScreen) (Object) this).getFocused();
        if (!(focused instanceof ContainerEventHandler container)
            || !(container.getFocused() instanceof EditBox)) {
            return;
        }

        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
            // Printable characters are inserted by charTyped(). Consuming the key
            // press here only prevents AbstractContainerScreen from closing.
            cir.setReturnValue(true);
        }
    }
}
