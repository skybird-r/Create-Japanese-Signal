package com.skybird.create_jp_signal.create.train.schedule;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Pair;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.util.Lang;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OperationTypeInstruction extends ScheduleInstruction {

    public OperationTypeInstruction() {
        super();
        data.putInt("Operation", OperationType.TRAIN.ordinal());
    }
    
    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(icon(), formatted());
    }

    private MutableComponent formatted() {
        return getOperationType().getDisplayName().copy();
    }

    @Override
    public ResourceLocation getId() {
        return JpSignals.asResource("operation_type"); 
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
        List<Component> options = new ArrayList<>();
        for (OperationType op : OperationType.values()) {
            options.add(op.getDisplayName());
        }

        builder.addSelectionScrollInput(0, 100, (si, l) -> {
            si.forOptions(options);
            si.titled(Lang.translatable("schedule.instruction.operation_type.name"));
        }, "Operation");
    }

    public OperationType getOperationType() {
        return enumData("Operation", OperationType.class);
    }

    private ItemStack icon() {
        return new ItemStack(Items.COMPARATOR);
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(Component.translatable("schedule.instruction.operation_type.tooltip.0"),
            Component.translatable("schedule.instruction.operation_type.tooltip.1").withStyle(ChatFormatting.GRAY));
    }
}
