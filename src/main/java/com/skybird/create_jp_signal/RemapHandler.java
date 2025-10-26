package com.skybird.create_jp_signal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod.EventBusSubscriber(modid = JpSignals.MODID)
public class RemapHandler {
    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        for (MissingMappingsEvent.Mapping<Block> mapping : event.getMappings(ForgeRegistries.Keys.BLOCKS, JpSignals.MODID)) {
            
            ResourceLocation oldId = mapping.getKey();

            if (oldId.getPath().equals("color_signle_round_signal")) {
                
                System.out.println("Remapping old block: " + oldId);
                
                mapping.remap(AllBlocks.COLOR_SINGLE_ROUND_SIGNAL_MAST.get());
            }
            if (oldId.getPath().equals("repeater_signle_signal_mast")) {
                System.out.println("Remapping old block: " + oldId);
                mapping.remap(AllBlocks.REPEATER_SINGLE_SIGNAL_MAST.get()); 
            }
            
        }

        for (MissingMappingsEvent.Mapping<BlockEntityType<?>> mapping : event.getMappings(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, JpSignals.MODID)) {
            ResourceLocation oldId = mapping.getKey();
            if (oldId.getPath().equals("color_single_round_signal_entity")) {
                System.out.println("Remapping old blockentity: " + oldId);
                mapping.remap(AllBlockEntities.COLOR_SINGLE_ROUND_SIGNAL_MAST_ENTITY.get());
            }
        }
        
        // for (MissingMappingsEvent.Mapping<Item> mapping : event.getMappings(ForgeRegistries.Keys.ITEMS, JpSignals.MODID)) {
        //     ResourceLocation oldId = mapping.getKey();

        //     if (oldId.getPath().equals("color_signle_round_signal")) {
                
        //         System.out.println("Remapping old item: " + oldId);
                
        //         mapping.remap(AllBlocks.COLOR_SINGLE_ROUND_SIGNAL_MAST_ITEM.get());
        //     }

        //     if (oldId.getPath().equals("repeater_signle_signal_mast")) {
        //         System.out.println("Remapping old item: " + oldId);
        //         mapping.remap(AllBlocks.REPEATER_SINGLE_SIGNAL_MAST_ITEM.get()); 
        //     }
        // }
    }
}
