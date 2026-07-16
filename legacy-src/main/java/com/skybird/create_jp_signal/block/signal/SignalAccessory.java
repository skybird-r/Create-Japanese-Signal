package com.skybird.create_jp_signal.block.signal;

import java.util.List;
import java.util.Map;

import com.skybird.create_jp_signal.block.signal.SignalAspect.LampColor;

import net.minecraft.nbt.CompoundTag;

public class SignalAccessory {
    
    public enum Route {

        NONE("none"),
        CENTER("center"),
        LEFT("left"),
        RIGHT("right");

        private final String translationKey;

        Route(String key) {
            this.translationKey = key;
        };

        public String getTranslationKey() {
            return "signal.accesory.route." + translationKey;
        }
    }

    public enum Type {
        NONE("none", 0),
        FORECAST("forecast", 2),
        INDICATOR_HOME("indicator_home", 9),
        INDICATOR_DEPARTURE("indicator_departure", 4),
        INDICATOR_SHUNT("indicator_shunt", 4);

        private String translationKey;
        private int lampCount;

        Type(String key, int lampCount) {
            this.translationKey = key;
            this.lampCount = lampCount;
        }

        public String getTranslationKey() {
            return "signal.accesory.type." + translationKey;
        }

        public int getLampCount() {
            return lampCount;
        }
    }

    public static List<LampColor> getLampColors(Type type, Route route) {
        return switch (type) {
            case FORECAST ->
                switch (route) {
                    case NONE   -> List.of(LampColor.OFF,   LampColor.OFF);
                    case LEFT   -> List.of(LampColor.WHITE, LampColor.OFF);
                    case CENTER -> List.of(LampColor.WHITE, LampColor.WHITE);
                    case RIGHT  -> List.of(LampColor.OFF,   LampColor.WHITE);
                };
            case INDICATOR_HOME ->
                switch (route) {
                    case NONE   -> List.of(LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF);
                    case LEFT   -> List.of(LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.WHITE);
                    case CENTER -> List.of(LampColor.OFF,   LampColor.WHITE, LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.OFF);
                    case RIGHT  -> List.of(LampColor.WHITE, LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.WHITE, LampColor.WHITE);
                };
            case INDICATOR_DEPARTURE ->
                switch (route) {
                    case NONE   -> List.of(LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF);
                    case LEFT   -> List.of(LampColor.WHITE, LampColor.WHITE, LampColor.WHITE, LampColor.OFF);
                    case CENTER -> List.of(LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF); // なし
                    case RIGHT  -> List.of(LampColor.WHITE, LampColor.OFF,   LampColor.WHITE, LampColor.WHITE);
                };
            case INDICATOR_SHUNT ->
                switch (route) {
                    case NONE   -> List.of(LampColor.OFF,   LampColor.OFF,   LampColor.OFF,   LampColor.OFF);
                    case LEFT   -> List.of(LampColor.OFF,   LampColor.OFF,   LampColor.WHITE, LampColor.WHITE);
                    case CENTER -> List.of(LampColor.OFF,   LampColor.WHITE, LampColor.OFF,   LampColor.WHITE);
                    case RIGHT  -> List.of(LampColor.WHITE, LampColor.OFF,   LampColor.OFF,   LampColor.WHITE);
                };
            case NONE -> List.of();
        };
    }


    private Type type = Type.NONE;

    public SignalAccessory() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SignalAccessory copy() {
        SignalAccessory newAccessory = new SignalAccessory();
        //newAccessory.setRoute(this.route);
        newAccessory.setType(this.type);
        return newAccessory;
    }

    
    public void writeNbt(CompoundTag tag) {
        //tag.putString("Route", this.route.name());
        tag.putString("Type", this.type.name());
    }

    public static SignalAccessory fromNbt(CompoundTag tag) {
        SignalAccessory accessory = new SignalAccessory();
        try {
            //if (tag.contains("Route")) {
            //    accessory.setRoute(Route.valueOf(tag.getString("Route")));
            //}
            if (tag.contains("Type")) {
                accessory.setType(Type.valueOf(tag.getString("Type")));
            }
        } catch (IllegalArgumentException e) {
            // 不正
        }
        return accessory;
    }

}
