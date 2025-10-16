package com.skybird.create_jp_signal;

import com.skybird.create_jp_signal.block.Signal3LBlockEntity;
import com.skybird.create_jp_signal.block.signal.ControlBoxBlockEntity;
import com.skybird.create_jp_signal.block.signal.SignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.debug.DebugInputBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.ColorSingleRoundSignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.signal_mast.RepeaterSingleSignalMastBlockEntity;
import com.skybird.create_jp_signal.block.signal.source.SignalRepeaterBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JpSignals.MODID);

    public static final RegistryObject<BlockEntityType<Signal3LBlockEntity>> SIGNAL_3L_ENTITY =
        BLOCK_ENTITIES.register("signal_3l_entity", () ->
                BlockEntityType.Builder.<Signal3LBlockEntity>of(Signal3LBlockEntity::new, AllBlocks.SIGNAL_3LIGHT.get()) // <Signal3LBlockEntity> を追加、vscodeがerror表示する
                        .build(null));

    public static final RegistryObject<BlockEntityType<ControlBoxBlockEntity>> CONTROL_BOX_ENTITY =
        BLOCK_ENTITIES.register("control_box_entity", () ->
            BlockEntityType.Builder.<ControlBoxBlockEntity>of(ControlBoxBlockEntity::new, AllBlocks.CONTROL_BOX.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<SignalMastBlockEntity>> SIGNAL_MAST_ENTITY =
        BLOCK_ENTITIES.register("signal_mast_entity", () ->
            BlockEntityType.Builder.<SignalMastBlockEntity>of(SignalMastBlockEntity::new, AllBlocks.SIGNAL_MAST.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<DebugInputBlockEntity>> DEBUG_INPUT_ENTITY =
        BLOCK_ENTITIES.register("debug_input_entity", () ->
            BlockEntityType.Builder.<DebugInputBlockEntity>of(DebugInputBlockEntity::new, AllBlocks.DEBUG_INPUT.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<SignalRepeaterBlockEntity>> SIGNAL_REPEATER_ENTITY =
        BLOCK_ENTITIES.register("signal_repeater_entity", () ->
            BlockEntityType.Builder.<SignalRepeaterBlockEntity>of(SignalRepeaterBlockEntity::new, AllBlocks.SIGNAL_REPEATER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<ColorSingleRoundSignalMastBlockEntity>> COLOR_SINGLE_ROUND_SIGNAL_MAST_ENTITY =
        BLOCK_ENTITIES.register("color_single_round_signal_mast_entity", () ->
            BlockEntityType.Builder.<ColorSingleRoundSignalMastBlockEntity>of(ColorSingleRoundSignalMastBlockEntity::new, AllBlocks.COLOR_SINGLE_ROUND_SIGNAL_MAST.get())
                    .build(null));
    
    public static final RegistryObject<BlockEntityType<RepeaterSingleSignalMastBlockEntity>> REPEATER_SINGLE_SIGNAL_MAST_ENTITY =
        BLOCK_ENTITIES.register("repeater_single_signal_mast_entity", () ->
            BlockEntityType.Builder.<RepeaterSingleSignalMastBlockEntity>of(RepeaterSingleSignalMastBlockEntity::new, AllBlocks.REPEATER_SINGLE_SIGNAL_MAST.get())
                    .build(null));
}
