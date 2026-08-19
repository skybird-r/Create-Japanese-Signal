package com.skybird.create_jp_signal.create.train.schedule;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.mixin_interface.ITrain;
import com.skybird.create_jp_signal.util.Lang;

import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlagOperationCondition extends ScheduleWaitCondition {

    public enum Operation {
        SET_NOW("set_now"),
        SET_ON_DEPARTURE("set_on_departure"),
        CLEAR("clear");

        private final String translationKey;

        Operation(String translationKey) {
            this.translationKey = translationKey;
        }

        public MutableComponent getDisplayName() {
            return Lang.translatable("schedule.condition.flag_operation.operation." + translationKey);
        }

        public MutableComponent getShortDisplayName() {
            return Lang.translatable("schedule.condition.flag_operation.operation." + translationKey + ".short");
        }
    }

    public FlagOperationCondition() {
        data.putString("FlagName", "");
        data.putInt("Operation", Operation.SET_NOW.ordinal());
    }

    public String getFlagName() {
        return TrainFlagSavedData.normalizeName(textData("FlagName"));
    }

    public Operation getOperation() {
        return enumData("Operation", Operation.class);
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(new ItemStack(Items.NAME_TAG), Component.literal(getFlagName()));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of(
            getOperation().getDisplayName().withStyle(ChatFormatting.GOLD),
            Lang.translatable("schedule.condition.flag.common.named", Component.literal(getFlagName()))
                .withStyle(ChatFormatting.DARK_AQUA)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 82, (box, tooltip) -> {
            box.setMaxLength(TrainFlagSavedData.MAX_FLAG_NAME_LENGTH);
        }, "FlagName");

        List<Component> options = new ArrayList<>();
        for (Operation operation : Operation.values()) {
            options.add(operation.getShortDisplayName());
        }
        builder.addSelectionScrollInput(84, 37, (input, label) -> {
            input.forOptions(options)
                .titled(Lang.translatable("schedule.condition.flag_operation.operation"));
        }, "Operation");
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        String flagName = getFlagName();
        if (flagName.isEmpty()) {
            return true;
        }

        TrainFlagSavedData flags = TrainFlagSavedData.get(level);
        if (flags == null) {
            return false;
        }

        switch (getOperation()) {
            case SET_NOW -> flags.setIfAbsent(flagName, TrainFlagSavedData.getSharedGameTime(level));
            case SET_ON_DEPARTURE -> {
                if (!flags.contains(flagName)) {
                    ((ITrain) train).getPendingDepartureFlags().add(flagName);
                }
            }
            case CLEAR -> flags.clear(flagName);
        }
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return JpSignals.asResource("flag_operation");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        return Lang.translatable("schedule.condition.flag_operation.status");
    }
}
