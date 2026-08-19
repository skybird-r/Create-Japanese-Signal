package com.skybird.create_jp_signal.create.train.schedule;

import java.util.List;
import java.util.OptionalLong;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.TimedWaitCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.skybird.create_jp_signal.JpSignals;
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

public class FlagElapsedCondition extends TimedWaitCondition {

    public FlagElapsedCondition() {
        data.putString("FlagName", "");
        data.putInt("Consume", 0);
        data.putInt("TimeUnit", TimeUnit.SECONDS.ordinal());
    }

    public String getFlagName() {
        return TrainFlagSavedData.normalizeName(textData("FlagName"));
    }

    public boolean shouldConsume() {
        return intData("Consume") != 0;
    }

    public int getWaitSeconds() {
        return intData("Value");
    }

    @Override
    public int totalWaitTicks() {
        return getWaitSeconds() * 20;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(new ItemStack(Items.CLOCK), Component.literal(getFlagName()));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        MutableComponent behavior = shouldConsume()
            ? Lang.translatable("schedule.condition.flag_elapsed.consume.clear")
            : Lang.translatable("schedule.condition.flag_elapsed.consume.keep");
        return ImmutableList.of(
            Lang.translatable("schedule.condition.flag_elapsed.summary", Component.literal(getFlagName()))
                .withStyle(ChatFormatting.GOLD),
            Lang.translatable("schedule.condition.flag_elapsed.detail",
                Component.literal(getWaitSeconds() + " s"), behavior)
                .withStyle(ChatFormatting.DARK_AQUA)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 68, (box, tooltip) -> {
            box.setMaxLength(TrainFlagSavedData.MAX_FLAG_NAME_LENGTH);
        }, "FlagName");

        builder.addScrollInput(70, 34, (input, label) -> {
            input.titled(Component.translatable("create.generic.duration"))
                .withShiftStep(60)
                .withRange(0, 3601);
            label.withSuffix("s");
        }, "Value");

        builder.addSelectionScrollInput(106, 15, (input, label) -> {
            input.forOptions(ImmutableList.of(
                Lang.translatable("schedule.condition.flag_elapsed.consume.keep.short"),
                Lang.translatable("schedule.condition.flag_elapsed.consume.clear.short")
            )).titled(Lang.translatable("schedule.condition.flag_elapsed.consume"));
        }, "Consume");
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        String flagName = getFlagName();
        if (flagName.isEmpty()) {
            return false;
        }

        TrainFlagSavedData flags = TrainFlagSavedData.get(level);
        if (flags == null) {
            return false;
        }

        OptionalLong setTime = flags.getSetTime(flagName);
        if (setTime.isEmpty()) {
            updateStatusContext(context, Long.MIN_VALUE, -1);
            return false;
        }

        long elapsed = Math.max(0, TrainFlagSavedData.getSharedGameTime(level) - setTime.getAsLong());
        long remaining = Math.max(0, (long) totalWaitTicks() - elapsed);
        int remainingSeconds = (int) Math.min(Integer.MAX_VALUE, (remaining + 19) / 20);
        updateStatusContext(context, setTime.getAsLong(), remainingSeconds);

        if (elapsed < totalWaitTicks()) {
            return false;
        }

        if (shouldConsume()) {
            // Clearing here, in the same server tick as completion, makes consuming
            // the flag atomic with respect to other trains checking this condition.
            flags.clear(flagName);
        }
        return true;
    }

    private void updateStatusContext(CompoundTag context, long setTime, int remainingSeconds) {
        if (context.getLong("ObservedSetTime") == setTime
            && context.getInt("RemainingSeconds") == remainingSeconds) {
            return;
        }
        context.putLong("ObservedSetTime", setTime);
        context.putInt("RemainingSeconds", remainingSeconds);
        requestStatusToUpdate(context);
    }

    @Override
    public ResourceLocation getId() {
        return JpSignals.asResource("flag_elapsed");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag context) {
        TrainFlagSavedData flags = TrainFlagSavedData.get(level);
        if (flags == null || flags.getSetTime(getFlagName()).isEmpty()) {
            return Lang.translatable("schedule.condition.flag_elapsed.status.missing", getFlagName());
        }

        int remainingSeconds = Math.max(0, context.getInt("RemainingSeconds"));
        return Lang.translatable("schedule.condition.flag_elapsed.status.remaining", getFlagName(), remainingSeconds);
    }
}
