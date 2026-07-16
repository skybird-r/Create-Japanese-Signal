package com.skybird.create_jp_signal.block.signal;

import java.util.Map;
import java.util.TreeMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class AspectMapping {

    private final TreeMap<Integer, SignalAspect.State> mapping = new TreeMap<>();
    private SignalAccessory.Route route = SignalAccessory.Route.CENTER;

    public SignalAspect.State getAspectFor(int index) {
        // index以下のキーの中で最大のものを探す
        Map.Entry<Integer, SignalAspect.State> entry = mapping.floorEntry(index);
        return entry != null ? entry.getValue() : SignalAspect.State.ALL_3;
    }
    
    public void addRule(int index, SignalAspect.State state) {
        mapping.put(index, state);
    }

    public void clearRules() {
        mapping.clear();
    }

    public void removeRule(int index) {
        mapping.remove(index);
    }

    public AspectMapping copy() {
        AspectMapping newMapping = new AspectMapping();
        newMapping.mapping.putAll(this.mapping);
        newMapping.route = this.route;
        return newMapping;
    }

    public void setRoute(SignalAccessory.Route route) {
        this.route = route;
    }

    public SignalAccessory.Route getRoute() {
        return route;
    }

    public Map<Integer, SignalAspect.State> getRules() {
        return this.mapping;
    }

    public int getMaxIndex() {
        if (mapping.isEmpty()) {
            return 0;
        }
        return mapping.lastKey();
    }

    public void writeNbt(CompoundTag tag) {
        ListTag list = new ListTag();
        mapping.forEach((index, state) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("Index", index);
            entry.putString("State", state.name());
            list.add(entry);
        });
        tag.put("Mapping", list);
        tag.putString("Route", this.route.name());
    }

    public static AspectMapping fromNbt(CompoundTag tag) {
        AspectMapping newMapping = new AspectMapping();
        ListTag list = tag.getList("Mapping", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            try {
                int index = entry.getInt("Index");
                SignalAspect.State state = SignalAspect.State.valueOf(entry.getString("State"));
                newMapping.addRule(index, state);
            } catch (IllegalArgumentException e) {
                // 不正なデータはスキップ
            }
        }
        if (tag.contains("Route")) {
            try {
                newMapping.route = SignalAccessory.Route.valueOf(tag.getString("Route"));
            } catch (IllegalArgumentException e) {

            }
        }
        return newMapping;
    }
}