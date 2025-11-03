package com.skybird.create_jp_signal;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AllCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JpSignals.MODID);

    public static final RegistryObject<CreativeModeTab> JP_SIGNAL_TAB = TABS.register(JpSignals.MODID,
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup."+JpSignals.MODID))
            .displayItems((param, output) -> {
                // AllItems.ITEMS に登録されている全てのアイテムを自動的にタブに追加する
                AllItems.ITEMS.getEntries().stream()
                    .map(RegistryObject::get)
                    .forEach(output::accept);
                
                // AllBlocksに登録されているBlockItemも同様に追加
                AllBlocks.ITEMS.getEntries().stream()
                    .map(RegistryObject::get)
                    .forEach(output::accept);
            })
            .build()
        );

    /*
    public static final RegistryObject<CreativeModeTab> JP_SIGNAL_TAB = TABS.register("jpsignal",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.jpsignal"))
            .displayItems((param,output) -> {
                for (Item item : AllItems.TAB_ITEMS) {
                    output.accept(item);
                }
            }).build());
    */
}
