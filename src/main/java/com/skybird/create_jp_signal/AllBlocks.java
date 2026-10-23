package com.skybird.create_jp_signal;

import com.skybird.create_jp_signal.block.sign.RailwaySignBlock;
import com.skybird.create_jp_signal.block.sign.RailwaySignBlockItem;
import com.skybird.create_jp_signal.block.sign.RailwaySignType;

import com.skybird.create_jp_signal.block.sign.GradientPostBlock;
import com.skybird.create_jp_signal.block.sign.GradientPostBlockItem;

import com.skybird.create_jp_signal.block.sign.KilometerPostBlock;
import com.skybird.create_jp_signal.block.sign.KilometerPostBlockItem;

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
import com.skybird.create_jp_signal.block.track.PenaltyBlock;
import com.skybird.create_jp_signal.block.track.PenaltyBlockItem;
import com.skybird.create_jp_signal.block.track.ReservationLimitBlock;
import com.skybird.create_jp_signal.block.track.ReservationLimitBlockItem;
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

    public static final RegistryObject<Block> BLOCK_SIGNAL_NO_PLATE = BLOCKS.register("block_signal_no_plate",
        () -> new RailwaySignBlock(BlockBehaviour.Properties.of(), RailwaySignType.BLOCK_SIGNAL_NO_PLATE));
    public static final RegistryObject<Item> BLOCK_SIGNAL_NO_PLATE_ITEM = ITEMS.register("block_signal_no_plate",
        () -> new RailwaySignBlockItem(BLOCK_SIGNAL_NO_PLATE.get(), new Item.Properties()));

    public static final RegistryObject<Block> SPEED_LIMIT_SIGN = BLOCKS.register("speed_limit_sign",
        () -> new RailwaySignBlock(BlockBehaviour.Properties.of(), RailwaySignType.SPEED_LIMIT));
    public static final RegistryObject<Item> SPEED_LIMIT_SIGN_ITEM = ITEMS.register("speed_limit_sign",
        () -> new RailwaySignBlockItem(SPEED_LIMIT_SIGN.get(), new Item.Properties()));

    public static final RegistryObject<Block> SPEED_LIMIT_END_SIGN = BLOCKS.register("speed_limit_end_sign",
        () -> new RailwaySignBlock(BlockBehaviour.Properties.of(), RailwaySignType.SPEED_LIMIT_END));
    public static final RegistryObject<Item> SPEED_LIMIT_END_SIGN_ITEM = ITEMS.register("speed_limit_end_sign",
        () -> new RailwaySignBlockItem(SPEED_LIMIT_END_SIGN.get(), new Item.Properties()));

    public static final RegistryObject<Block> TRAIN_STOP_LIMIT_SIGN = BLOCKS.register("train_stop_limit_sign",
        () -> new RailwaySignBlock(BlockBehaviour.Properties.of(), RailwaySignType.TRAIN_STOP_LIMIT));
    public static final RegistryObject<Item> TRAIN_STOP_LIMIT_SIGN_ITEM = ITEMS.register("train_stop_limit_sign",
        () -> new RailwaySignBlockItem(TRAIN_STOP_LIMIT_SIGN.get(), new Item.Properties()));

    public static final RegistryObject<Block> SHUNT_LIMIT_SIGN = BLOCKS.register("shunt_limit_sign",
        () -> new RailwaySignBlock(BlockBehaviour.Properties.of(), RailwaySignType.SHUNT_LIMIT));
    public static final RegistryObject<Item> SHUNT_LIMIT_SIGN_ITEM = ITEMS.register("shunt_limit_sign",
        () -> new RailwaySignBlockItem(SHUNT_LIMIT_SIGN.get(), new Item.Properties()));

    public static final RegistryObject<Block> TRACK_END_SIGN = BLOCKS.register("track_end_sign",
        () -> new RailwaySignBlock(BlockBehaviour.Properties.of(), RailwaySignType.TRACK_END));
    public static final RegistryObject<Item> TRACK_END_SIGN_ITEM = ITEMS.register("track_end_sign",
        () -> new RailwaySignBlockItem(TRACK_END_SIGN.get(), new Item.Properties()));

    public static final RegistryObject<Block> GRADIENT_POST = BLOCKS.register("gradient_post",
        () -> new GradientPostBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> GRADIENT_POST_ITEM = ITEMS.register("gradient_post",
        () -> new GradientPostBlockItem(GRADIENT_POST.get(), new Item.Properties()));

    public static final RegistryObject<Block> KILOMETER_POST = BLOCKS.register("kilometer_post",
        () -> new KilometerPostBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> KILOMETER_POST_ITEM = ITEMS.register("kilometer_post",
        () -> new KilometerPostBlockItem(KILOMETER_POST.get(), new Item.Properties()));

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

    public static final RegistryObject<Block> PENALTY = BLOCKS.register("penalty",
        () -> new PenaltyBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> PENALTY_ITEM = ITEMS.register("penalty",
        () -> new PenaltyBlockItem(PENALTY.get(), new Item.Properties()));

    public static final RegistryObject<Block> RESERVATION_LIMIT = BLOCKS.register("reservation_limit",
        () -> new ReservationLimitBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> RESERVATION_LIMIT_ITEM = ITEMS.register("reservation_limit",
        () -> new ReservationLimitBlockItem(RESERVATION_LIMIT.get(), new Item.Properties()));

        
}
