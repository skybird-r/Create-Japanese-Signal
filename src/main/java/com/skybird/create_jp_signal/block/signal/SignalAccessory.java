package com.skybird.create_jp_signal.block.signal;

import org.apache.commons.compress.archivers.dump.DumpArchiveEntry.TYPE;

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
        NONE("none"),
        FORECAST("forecast"),
        INDICATOR_HOME("indicator_home"),
        INDICATOR_DEPARTURE("indicator_departure"),
        INDICATOR_SHUNT("indicator_shunt");

        private String translationKey;

        Type(String key) {
            this.translationKey = key;
        }

        public String getTranslationKey() {
            return "signal.accesory.type." + translationKey;
        }
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
            // 不正なデータが書き込まれていた場合はデフォルト値のままにする
        }
        return accessory;
    }

}
