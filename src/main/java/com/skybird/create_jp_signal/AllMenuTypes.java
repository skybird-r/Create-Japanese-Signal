package com.skybird.create_jp_signal;

import com.skybird.create_jp_signal.menu.SpeedLimitSignMenu;

import com.skybird.create_jp_signal.menu.GradientPostMenu;

import com.skybird.create_jp_signal.menu.KilometerPostMenu;

import com.skybird.create_jp_signal.menu.ControlBoxMenu;
import com.skybird.create_jp_signal.menu.PenaltyMenu;
import com.skybird.create_jp_signal.menu.ReservationLimitMenu;
import com.skybird.create_jp_signal.menu.SignalLinkMenu;
import com.skybird.create_jp_signal.menu.SpeedLimitMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JpSignals.MODID);

    public static final RegistryObject<MenuType<ControlBoxMenu>> CONTROL_BOX_MENU =
        MENUS.register("control_box_menu", () -> IForgeMenuType.create(ControlBoxMenu::new));

    public static final RegistryObject<MenuType<com.skybird.create_jp_signal.menu.BlockSignalNumberMenu>> BLOCK_SIGNAL_NUMBER_MENU =
        MENUS.register("block_signal_number_menu", () -> IForgeMenuType.create(com.skybird.create_jp_signal.menu.BlockSignalNumberMenu::new));

    public static final RegistryObject<MenuType<SpeedLimitSignMenu>> SPEED_LIMIT_SIGN_MENU =
        MENUS.register("speed_limit_sign_menu", () -> IForgeMenuType.create(SpeedLimitSignMenu::new));

    public static final RegistryObject<MenuType<GradientPostMenu>> GRADIENT_POST_MENU =
        MENUS.register("gradient_post_menu", () -> IForgeMenuType.create(GradientPostMenu::new));

    public static final RegistryObject<MenuType<KilometerPostMenu>> KILOMETER_POST_MENU =
        MENUS.register("kilometer_post_menu", () -> IForgeMenuType.create(KilometerPostMenu::new));

    public static final RegistryObject<MenuType<com.skybird.create_jp_signal.menu.SignalNameMenu>> SIGNAL_NAME_MENU =
        MENUS.register("signal_name_menu", () -> IForgeMenuType.create(com.skybird.create_jp_signal.menu.SignalNameMenu::new));

    // public static final RegistryObject<MenuType<MastConfigMenu>> MAST_CONFIG_MENU =
    //     MENUS.register("mast_config_menu", () -> IForgeMenuType.create(MastConfigMenu::new));

    // public static final RegistryObject<MenuType<MastLinkMenu>> MAST_LINK_MENU =
    //     MENUS.register("mast_link_menu", () -> IForgeMenuType.create(MastLinkMenu::new));

    public static final RegistryObject<MenuType<SignalLinkMenu>> SIGNAL_LINK_MENU =
        MENUS.register("signal_link_menu", () -> IForgeMenuType.create(SignalLinkMenu::new));

    public static final RegistryObject<MenuType<SpeedLimitMenu>> SPEED_LIMIT_MENU =
        MENUS.register("speed_limit_menu", () -> IForgeMenuType.create(SpeedLimitMenu::new));

    public static final RegistryObject<MenuType<PenaltyMenu>> PENALTY_MENU =
        MENUS.register("penalty_menu", () -> IForgeMenuType.create(PenaltyMenu::new));

    public static final RegistryObject<MenuType<ReservationLimitMenu>> RESERVATION_LIMIT_MENU =
        MENUS.register("reservation_limit_menu", () -> IForgeMenuType.create(ReservationLimitMenu::new));
}
