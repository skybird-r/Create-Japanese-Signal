package com.skybird.create_jp_signal.create.train.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import javax.annotation.Nullable;

import com.skybird.create_jp_signal.JpSignals;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * World-wide named flags used by train schedules.
 *
 * The data is always stored in the overworld so that flags are shared by trains
 * in every dimension.
 */
public class TrainFlagSavedData extends SavedData {

    private static final String DATA_NAME = JpSignals.MODID + "_train_flags";
    public static final int MAX_FLAG_NAME_LENGTH = 64;

    private final Map<String, Long> flags = new HashMap<>();

    public static TrainFlagSavedData load(CompoundTag tag) {
        TrainFlagSavedData data = new TrainFlagSavedData();
        ListTag flagList = tag.getList("Flags", Tag.TAG_COMPOUND);
        for (int i = 0; i < flagList.size(); i++) {
            CompoundTag flagTag = flagList.getCompound(i);
            String name = normalizeName(flagTag.getString("Name"));
            if (!name.isEmpty()) {
                data.flags.put(name, flagTag.getLong("SetAt"));
            }
        }
        return data;
    }

    @Nullable
    public static TrainFlagSavedData get(Level level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return null;
        }
        return server.overworld().getDataStorage().computeIfAbsent(
            TrainFlagSavedData::load,
            TrainFlagSavedData::new,
            DATA_NAME
        );
    }

    public static long getSharedGameTime(Level level) {
        MinecraftServer server = level.getServer();
        return server == null ? level.getGameTime() : server.overworld().getGameTime();
    }

    public static String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.length() > MAX_FLAG_NAME_LENGTH) {
            normalized = normalized.substring(0, MAX_FLAG_NAME_LENGTH);
        }
        return normalized;
    }

    public boolean setIfAbsent(String name, long gameTime) {
        String normalized = normalizeName(name);
        if (normalized.isEmpty() || flags.containsKey(normalized)) {
            return false;
        }
        flags.put(normalized, gameTime);
        setDirty();
        return true;
    }

    public boolean clear(String name) {
        String normalized = normalizeName(name);
        if (normalized.isEmpty() || flags.remove(normalized) == null) {
            return false;
        }
        setDirty();
        return true;
    }

    public OptionalLong getSetTime(String name) {
        Long setAt = flags.get(normalizeName(name));
        return setAt == null ? OptionalLong.empty() : OptionalLong.of(setAt);
    }

    public boolean contains(String name) {
        return flags.containsKey(normalizeName(name));
    }

    public List<String> getFlagNames() {
        return flags.keySet().stream().sorted().toList();
    }

    public List<Map.Entry<String, Long>> getFlags() {
        return flags.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
            .toList();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag flagList = new ListTag();
        flags.forEach((name, setAt) -> {
            CompoundTag flagTag = new CompoundTag();
            flagTag.putString("Name", name);
            flagTag.putLong("SetAt", setAt);
            flagList.add(flagTag);
        });
        tag.put("Flags", flagList);
        return tag;
    }
}
