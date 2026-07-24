package com.skybird.create_jp_signal.create.train.schedule;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;
import com.skybird.create_jp_signal.util.Lang;

import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TickWaitBeforeDepartureInstruction extends ScheduleInstruction {

    public TickWaitBeforeDepartureInstruction() {
        data.putInt("Value", 40);
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(icon(), formatted());
    }

    private MutableComponent formatted() {
        return Component.literal(intData("Value") + " t");
    }

    @Override
    public ResourceLocation getId() {
        return JpSignals.asResource("tick_wait_before_departure");
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
            input.withRange(0, 201)
                .withStepFunction(context -> context.shift ? 20 : 1)
                .titled(Lang.translatable("schedule.instruction.tick_wait_before_departure.name"));
            label.withSuffix(" t");
        }, "Value");
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(
            Lang.translatable("schedule.instruction.tick_wait_before_departure.tooltip.0"),
            Lang.translatable("schedule.instruction.tick_wait_before_departure.tooltip.1")
                .withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    @Nullable
    public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
        ((ITrain) runtime.train).setTickWaitBeforeDeparture(intData("Value"));
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        runtime.currentEntry++;
        return null;
    }

    private ItemStack icon() {
        return new ItemStack(Items.CLOCK);
    }
}
