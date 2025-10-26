package com.skybird.create_jp_signal;

import com.skybird.create_jp_signal.item.SignalConfiguratorItem;
import com.skybird.create_jp_signal.item.SignalMastWithSignalItem;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JpSignals.MODID);

    public static final RegistryObject<Item> SIGNAL_CONFIGURATOR = ITEMS.register("signal_configurator",
        () -> new SignalConfiguratorItem(new Item.Properties()));
    
    public static final RegistryObject<Item> SIGNAL_MAST_WITH_SIGNAL = ITEMS.register("signal_mast_with_signal",
        () -> new SignalMastWithSignalItem(new Item.Properties()));
    /*
    public static final Item[] TAB_ITEMS ={
        SIGNAL_CONFIGURATOR.get(),
        AllBlocks.SIGNAL_MAST_ITEM.get(),
        AllBlocks.DEBUG_INPUT_ITEM.get(),
        AllBlocks.CONTROL_BOX_ITEM.get()
        
    };
    */

}
