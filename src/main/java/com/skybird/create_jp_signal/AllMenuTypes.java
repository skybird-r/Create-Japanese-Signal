package com.skybird.create_jp_signal;

import com.skybird.create_jp_signal.menu.ControlBoxMenu;
import com.skybird.create_jp_signal.menu.SignalLinkMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JpSignals.MODID);

    public static final RegistryObject<MenuType<ControlBoxMenu>> CONTROL_BOX_MENU =
        MENUS.register("control_box_menu", () -> IForgeMenuType.create(ControlBoxMenu::new));

    // public static final RegistryObject<MenuType<MastConfigMenu>> MAST_CONFIG_MENU =
    //     MENUS.register("mast_config_menu", () -> IForgeMenuType.create(MastConfigMenu::new));

    // public static final RegistryObject<MenuType<MastLinkMenu>> MAST_LINK_MENU =
    //     MENUS.register("mast_link_menu", () -> IForgeMenuType.create(MastLinkMenu::new));

    public static final RegistryObject<MenuType<SignalLinkMenu>> SIGNAL_LINK_MENU =
        MENUS.register("signal_link_menu", () -> IForgeMenuType.create(SignalLinkMenu::new));
}