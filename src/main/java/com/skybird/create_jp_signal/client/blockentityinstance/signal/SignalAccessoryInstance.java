package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.BasicData;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.skybird.create_jp_signal.block.signal.SignalAccessory;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.client.PartialModelRegistry;

public class SignalAccessoryInstance {
    
    private final List<BasicData> allModels = new ArrayList<>();

    private final List<ModelData> lamps = new ArrayList<>();
    private final List<OrientedData> staticParts = new ArrayList<>();
    private final MaterialManager materialManager;
    private final SignalHead signalHead;

    public SignalAccessoryInstance(MaterialManager materialManager, SignalHead signalHead) {
        this.materialManager = materialManager;
        this.signalHead = signalHead;
    }

    public void init() {
        delete();

        SignalAccessory.Type type = null;
        if (signalHead != null) {
            type = signalHead.getAppearance().getAccessory().getType();
        }
        int lampCount = 0;
        if (type == SignalAccessory.Type.FORECAST) {
            
        } else if (type == SignalAccessory.Type.INDICATOR_HOME) {

        } else if (type == SignalAccessory.Type.INDICATOR_DEPARTURE) {

        } else if (type == SignalAccessory.Type.INDICATOR_SHUNT) {

        }
        for (int i = 0; i < lampCount; i++) {
            ModelData light = materialManager.defaultCutout()
                .material(Materials.TRANSFORMED)
                .getModel(PartialModelRegistry.SIGNAL_LIGHT)
                .createInstance();
            lamps.add(light);
            allModels.add(light);
        }
    }

    public void delete() {
        lamps.forEach(ModelData::delete);
        staticParts.forEach(OrientedData::delete);
        lamps.clear();
        staticParts.clear();
    }
}
