package com.skybird.create_jp_signal.client.blockentityinstance.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skybird.create_jp_signal.block.signal.BaseSignalBlockEntity;
import com.skybird.create_jp_signal.block.signal.SignalHead;
import com.skybird.create_jp_signal.block.signal.SignalName;
import com.skybird.create_jp_signal.client.PartialModelRegistry;
import com.skybird.create_jp_signal.client.blockentityinstance.signal.SignalInstanceManager.SignalModelData;
import com.skybird.create_jp_signal.client.blockentityrenderer.signal.SignalNameBoardLayout;
import com.skybird.create_jp_signal.client.text.FlywheelText;
import com.skybird.create_jp_signal.client.text.SignText;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

final class SignalNameBoardInstance {
    private static final class Preparation { volatile boolean ready; }
    private static final RendererReloadCache<String, Preparation> PREPARATIONS = new RendererReloadCache<>(name -> new Preparation());
    private final List<SignalModelData> parts = new ArrayList<>();
    private final SignalInstanceManager manager;

    SignalNameBoardInstance(SignalInstanceManager manager) { this.manager = manager; }

    void init(SignalHead head, BaseSignalBlockEntity be, PoseStack pose, Vec3 offset, Pair<Double, Double> rotation) {
        delete();
        if (!SignalNameBoardLayout.visible(head)) return;
        String name = head.getSignalName();
        var cells = head.getSignalNameCells();
        int lamps = SignalNameBoardLayout.lampCount(head);
        pose.pushPose();
        pose.translate(.5, 0, .5);
        SignalNameBoardLayout.head(pose, offset, rotation);
        pose.pushPose();
        SignalNameBoardLayout.post(pose, lamps, cells.size());
        add(manager.create(PartialModelRegistry.SIGNAL_NAME_BOARD_POST), pose);
        pose.popPose();
        pose.pushPose();
        SignalNameBoardLayout.board(pose, lamps, cells.size());
        add(manager.create(PartialModelRegistry.SIGNAL_NAME_BOARD), pose);
        pose.popPose();
        Preparation preparation = PREPARATIONS.get(name);
        if (!preparation.ready) {
            // Glyph atlases are uploaded by Minecraft. Always prepare arbitrary names
            // on the render thread before Flywheel worker threads consume their meshes.
            Minecraft.getInstance().execute(() -> {
                if (!preparation.ready) {
                    cells.forEach(cell -> FlywheelText.prepare(cell.text(), SignText.FontMode.CURRENT));
                    preparation.ready = true;
                }
                be.clientVisualChanged = true;
            });
        } else {
            for (int i = 0; i < cells.size(); i++) {
                var cell = cells.get(i);
                var glyph = FlywheelText.model(cell.text(), SignText.FontMode.CURRENT);
                pose.pushPose();
                SignalNameBoardLayout.cell(pose, lamps, cells.size(), i, cell, glyph);
                var part = manager.create(glyph.model());
                part.setColor(255, 255, 255);
                add(part, pose);
                pose.popPose();
            }
        }
        pose.popPose();
    }

    private void add(SignalModelData part, PoseStack pose) {
        part.setTransform(pose);
        parts.add(part);
    }
    void updateLight(Level level, BlockPos pos) { parts.forEach(part -> part.updateLight(level, pos)); }
    void delete() { parts.forEach(SignalModelData::delete); parts.clear(); }
    void collectCrumblingInstances(Consumer<Instance> consumer) { parts.forEach(part -> consumer.accept(part.instance())); }
}
