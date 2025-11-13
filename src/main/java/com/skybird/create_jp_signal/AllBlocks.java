package com.skybird.create_jp_signal;

import com.skybird.create_jp_signal.block.signal.ControlBoxBlock;
import com.skybird.create_jp_signal.block.signal.debug.DebugInputBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.ColorSingleRoundSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.ColorSingleSquareSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.ColorSingleTunnelSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.RepeaterSingleSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.RepeaterSingleTunnelSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.ShuntSingleSignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.SignalMastBlock;
import com.skybird.create_jp_signal.block.signal.signal_mast.SignalMastBlockItem;
import com.skybird.create_jp_signal.block.signal.source.SignalRepeaterBlock;
import com.skybird.create_jp_signal.block.track.SpeedLimitBlock;
import com.skybird.create_jp_signal.block.track.SpeedLimitBlockItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JpSignals.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JpSignals.MODID);

    // public static final RegistryObject<Block> SIGNAL_3LIGHT = BLOCKS.register("signal_3light",  
    //     () -> new Signal3LBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> SIGNAL_3LIGHT_ITEM = ITEMS.register("signal_3light",
    //     () -> new BlockItem(SIGNAL_3LIGHT.get(), new Item.Properties()));

    public static final RegistryObject<Block> CONTROL_BOX = BLOCKS.register("control_box",
        () -> new ControlBoxBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> CONTROL_BOX_ITEM = ITEMS.register("control_box",
        () -> new BlockItem(CONTROL_BOX.get(), new Item.Properties()));

    public static final RegistryObject<Block> SIGNAL_MAST = BLOCKS.register("signal_mast",  
        () -> new SignalMastBlock(BlockBehaviour.Properties.of().noOcclusion()));
    public static final RegistryObject<Item> SIGNAL_MAST_ITEM = ITEMS.register("signal_mast",
        () -> new SignalMastBlockItem(SIGNAL_MAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> DEBUG_INPUT = BLOCKS.register("debug_input",
        () -> new DebugInputBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> DEBUG_INPUT_ITEM = ITEMS.register("debug_input",
        () -> new BlockItem(DEBUG_INPUT.get(), new Item.Properties()));

    public static final RegistryObject<Block> SIGNAL_REPEATER = BLOCKS.register("signal_repeater",
        () -> new SignalRepeaterBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> SIGNAL_REPEATER_ITEM = ITEMS.register("signal_repeater",
        () -> new BlockItem(SIGNAL_REPEATER.get(), new Item.Properties()));

    public static final RegistryObject<Block> COLOR_SINGLE_ROUND_SIGNAL_MAST = BLOCKS.register("color_single_round_signal_mast",
        () -> new ColorSingleRoundSignalMastBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> COLOR_SINGLE_ROUND_SIGNAL_MAST_ITEM = ITEMS.register("color_single_round_signal_mast",
    //     () -> new BlockItem(COLOR_SINGLE_ROUND_SIGNAL_MAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> COLOR_SINGLE_SQUARE_SIGNAL_MAST = BLOCKS.register("color_single_square_signal_mast",
        () -> new ColorSingleSquareSignalMastBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> COLOR_SINGLE_SQUARE_SIGNAL_MAST_ITEM = ITEMS.register("color_single_square_signal_mast",
    //     () -> new BlockItem(COLOR_SINGLE_SQUARE_SIGNAL_MAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> COLOR_SINGLE_TUNNEL_SIGNAL_MAST = BLOCKS.register("color_single_tunnel_signal_mast",
        () -> new ColorSingleTunnelSignalMastBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> COLOR_SINGLE_TUNNEL_SIGNAL_MAST_ITEM = ITEMS.register("color_single_tunnel_signal_mast",
    //     () -> new BlockItem(COLOR_SINGLE_TUNNEL_SIGNAL_MAST.get(), new Item.Properties()));
    
    public static final RegistryObject<Block> REPEATER_SINGLE_SIGNAL_MAST = BLOCKS.register("repeater_single_signal_mast",
        () -> new RepeaterSingleSignalMastBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> REPEATER_SINGLE_SIGNAL_MAST_ITEM = ITEMS.register("repeater_single_signal_mast",
    //     () -> new BlockItem(REPEATER_SINGLE_SIGNAL_MAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> REPEATER_SINGLE_TUNNEL_SIGNAL_MAST = BLOCKS.register("repeater_single_tunnel_signal_mast",
        () -> new RepeaterSingleTunnelSignalMastBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> REPEATER_SINGLE_TUNNEL_SIGNAL_MAST_ITEM = ITEMS.register("repeater_single_tunnel_signal_mast",
    //     () -> new BlockItem(REPEATER_SINGLE_TUNNEL_SIGNAL_MAST.get(), new Item.Properties()));
    
    public static final RegistryObject<Block> SHUNT_SINGLE_SIGNAL_MAST = BLOCKS.register("shunt_single_signal_mast",
        () -> new ShuntSingleSignalMastBlock(BlockBehaviour.Properties.of()));
    // public static final RegistryObject<Item> SHUNT_SINGLE_SIGNAL_MAST_ITEM = ITEMS.register("shunt_single_signal_mast",
    //     () -> new BlockItem(SHUNT_SINGLE_SIGNAL_MAST.get(), new Item.Properties()));

    public static final RegistryObject<Block> SPEED_LIMIT = BLOCKS.register("speed_limit",
        () -> new SpeedLimitBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> SPEED_LIMIT_ITEM = ITEMS.register("speed_limit",
        () -> new SpeedLimitBlockItem(SPEED_LIMIT.get(), new Item.Properties()));

        
}
