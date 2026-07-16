package com.skybird.create_jp_signal.block.signal.signal_type;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.skybird.create_jp_signal.block.signal.ColorLightSignalAppearance;
import com.skybird.create_jp_signal.block.signal.ISignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightRepeaterSignalAppearance;
import com.skybird.create_jp_signal.block.signal.PositionLightShuntSignalAppearance;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AllSignalTypes {
    private static final Map<ResourceLocation, ISignalType> TYPES = new TreeMap<>();

    public static final ISignalType COLOR_LIGHT_SIGNAL = register(new ColorLightSignalType());
    public static final ISignalType POSITION_LIGHT_REPEATER_SIGNAL = register(new PositionLightRepeaterSignalType());
    public static final ISignalType POSITION_LIGHT_SHUNT_SIGNAL = register(new PositionLightShuntSignalType());

    private static <T extends ISignalType> T register(T type) {
        TYPES.put(type.getId(), type);
        return type;
    }
    
    public static Collection<ISignalType> getValues() {
        return TYPES.values();
    }

    public static ISignalType get(ResourceLocation id) {
        return TYPES.get(id);
    }

    public static ISignalAppearance createAppearanceFromId(String typeId, CompoundTag data) {
        if (typeId.equals(ColorLightSignalType.ID.getPath())) {
            return ColorLightSignalAppearance.fromNbt(data);
        }
        if (typeId.equals(PositionLightRepeaterSignalType.ID.getPath())) {
            return PositionLightRepeaterSignalAppearance.fromNbt(data);
        }
        if (typeId.equals(PositionLightShuntSignalType.ID.getPath())) {
            return PositionLightShuntSignalAppearance.fromNbt(data);
        }
        return ColorLightSignalAppearance.createDefault();
    }
}