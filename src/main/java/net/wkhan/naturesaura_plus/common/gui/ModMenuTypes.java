package net.wkhan.naturesaura_plus.common.gui;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.gui.oven.OvenMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NaturesAuraPlus.MODID);

    public static final RegistryObject<MenuType<OvenMenu>> OVEN_MENU =
            MENUS.register("oven_menu", () -> IForgeMenuType.create(OvenMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
