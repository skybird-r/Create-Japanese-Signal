package com.skybird.create_jp_signal.command;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.skybird.create_jp_signal.JpSignals;
import com.skybird.create_jp_signal.create.train.schedule.TrainFlagSavedData;
import com.skybird.create_jp_signal.util.Lang;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JpSignals.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JpSignalCommands {

    private JpSignalCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("jpsignal")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("flag")
                .then(Commands.literal("list")
                    .executes(JpSignalCommands::listFlags))
                .then(Commands.literal("set")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .executes(context -> setFlag(context, 0))
                        .then(Commands.argument("seconds_ago", IntegerArgumentType.integer(0))
                            .executes(context -> setFlag(
                                context,
                                IntegerArgumentType.getInteger(context, "seconds_ago")
                            )))))
                .then(Commands.literal("remove")
                    .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(JpSignalCommands::suggestFlagNames)
                        .executes(JpSignalCommands::removeFlag))))
        );
    }

    private static int listFlags(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        TrainFlagSavedData flags = TrainFlagSavedData.get(source.getLevel());
        if (flags == null) {
            source.sendFailure(Lang.translatable("command.flag.unavailable"));
            return 0;
        }

        List<Map.Entry<String, Long>> entries = flags.getFlags();
        if (entries.isEmpty()) {
            source.sendSuccess(() -> Lang.translatable("command.flag.list.empty"), false);
            return 0;
        }

        long gameTime = TrainFlagSavedData.getSharedGameTime(source.getLevel());
        source.sendSuccess(() -> Lang.translatable("command.flag.list.header", entries.size()), false);
        for (Map.Entry<String, Long> entry : entries) {
            long elapsedSeconds = Math.max(0, gameTime - entry.getValue()) / 20;
            Component line = Lang.translatable(
                "command.flag.list.entry",
                entry.getKey(),
                entry.getValue(),
                elapsedSeconds
            );
            source.sendSuccess(() -> line, false);
        }
        return entries.size();
    }

    private static int setFlag(CommandContext<CommandSourceStack> context, int secondsAgo) {
        CommandSourceStack source = context.getSource();
        String name = validatedName(source, StringArgumentType.getString(context, "name"));
        if (name == null) {
            return 0;
        }

        TrainFlagSavedData flags = TrainFlagSavedData.get(source.getLevel());
        if (flags == null) {
            source.sendFailure(Lang.translatable("command.flag.unavailable"));
            return 0;
        }

        long setTime = TrainFlagSavedData.getSharedGameTime(source.getLevel()) - (long) secondsAgo * 20;
        if (!flags.setIfAbsent(name, setTime)) {
            source.sendFailure(Lang.translatable("command.flag.set.exists", name));
            return 0;
        }

        if (secondsAgo == 0) {
            source.sendSuccess(() -> Lang.translatable("command.flag.set.success", name), true);
        } else {
            source.sendSuccess(
                () -> Lang.translatable("command.flag.set.success.backdated", name, secondsAgo),
                true
            );
        }
        return 1;
    }

    private static int removeFlag(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String name = validatedName(source, StringArgumentType.getString(context, "name"));
        if (name == null) {
            return 0;
        }

        TrainFlagSavedData flags = TrainFlagSavedData.get(source.getLevel());
        if (flags == null) {
            source.sendFailure(Lang.translatable("command.flag.unavailable"));
            return 0;
        }

        if (!flags.clear(name)) {
            source.sendFailure(Lang.translatable("command.flag.remove.missing", name));
            return 0;
        }

        source.sendSuccess(() -> Lang.translatable("command.flag.remove.success", name), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestFlagNames(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder
    ) {
        TrainFlagSavedData flags = TrainFlagSavedData.get(context.getSource().getLevel());
        return SharedSuggestionProvider.suggest(flags == null ? List.of() : flags.getFlagNames(), builder);
    }

    private static String validatedName(CommandSourceStack source, String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            source.sendFailure(Lang.translatable("command.flag.name.empty"));
            return null;
        }
        if (trimmed.length() > TrainFlagSavedData.MAX_FLAG_NAME_LENGTH) {
            source.sendFailure(Lang.translatable(
                "command.flag.name.too_long",
                TrainFlagSavedData.MAX_FLAG_NAME_LENGTH
            ));
            return null;
        }
        return trimmed;
    }
}
