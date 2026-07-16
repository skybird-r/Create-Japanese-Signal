package com.skybird.create_jp_signal.create.train.schedule;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.AllItems;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.util.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MinimumReservationDistanceInstruction extends ScheduleInstruction {

    public MinimumReservationDistanceInstruction() {
		super();
		data.putInt("Value", 0);
	}
	
	@Override
	public Pair<ItemStack, Component> getSummary() {
		return Pair.of(icon(), formatted());
	}

	private MutableComponent formatted() {
		return Components.literal(String.valueOf(intData("Value")) + " m");
	}

	@Override
	public ResourceLocation getId() {
		return JpSignals.asResource("minimum_reservation_distance");
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
		builder.addScrollInput(0, 50, (si, l) -> {
			si.withRange(0, 2001)
				.withStepFunction(c -> c.shift ? 50 : 10)
				.titled(Lang.translatable("schedule.instruction.minimum_reservation_distance.name"));
			l.withSuffix(" m");
		}, "Value");
	}

	public double getMinimumReservationDistance() {
		return intData("Value");
	}

	private ItemStack icon() {
		return new ItemStack(AllItems.SIGNAL_MAST_WITH_SIGNAL.get());
	}

	@Override
	public List<Component> getSecondLineTooltip(int slot) {
		return ImmutableList.of(Lang.translatable("schedule.instruction.minimum_reservation_distance.tooltip.0"),
			Lang.translatable("schedule.instruction.minimum_reservation_distance.tooltip.1").withStyle(ChatFormatting.GRAY));
	}
    
}
