package com.skybird.create_jp_signal.create.train.schedule;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.AllItems;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;
import com.skybird.create_jp_signal.util.Lang;

import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SignalStoppingDistanceInstruction extends ScheduleInstruction {

    public SignalStoppingDistanceInstruction() {
        data.putInt("Value", 0);
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(icon(), formatted());
    }

    private MutableComponent formatted() {
        return Component.literal(intData("Value") + " m");
    }

    @Override
    public ResourceLocation getId() {
        return JpSignals.asResource("signal_stopping_distance");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return icon();
    }

    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of(Lang
            .translatable("schedule." + type + "." + getId().getPath() + ".summary",
                formatted().withStyle(ChatFormatting.WHITE))
            .withStyle(ChatFormatting.GOLD));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addScrollInput(0, 50, (input, label) -> {
            input.withRange(0, 101)
                .withStepFunction(context -> context.shift ? 10 : 1)
                .titled(Lang.translatable("schedule.instruction.signal_stopping_distance.name"));
            label.withSuffix(" m");
        }, "Value");
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(
            Lang.translatable("schedule.instruction.signal_stopping_distance.tooltip.0"),
            Lang.translatable("schedule.instruction.signal_stopping_distance.tooltip.1")
                .withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    @Nullable
    public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
        ((ITrain) runtime.train).setSignalStoppingDistance(intData("Value"));
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
        return null;
    }

    private ItemStack icon() {
        return new ItemStack(AllItems.SIGNAL_MAST_WITH_SIGNAL.get());
    }
}
