package com.skybird.create_jp_signal.block.signal;

import org.apache.commons.compress.archivers.dump.DumpArchiveEntry.TYPE;

import net.minecraft.nbt.CompoundTag;

public class SignalAccessory {
    
    public enum Route {

        NONE("なし"),
        CENTER("中央"),
        LEFT("左"),
        RIGHT("右");

        private final String displayName;

        Route(String name) {
            this.displayName = name;
        };

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Type {
        NONE("なし"),
        FORECAST("進路予告機"),
        INDICATOR_HOME("場内用進路表示機"),
        INDICATOR_DEPARTURE("出発用進路表示機");

        private String displayName;

        Type(String name) {
            this.displayName = name;
        }

        public String getDisplayName() {
            return displayName;
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
