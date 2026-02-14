package net.wkhan.naturesaura_plus.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.item.custom.ItemBreakPreventionAll;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NaturesAuraPlus.MODID);

    public static final RegistryObject<Item> BREAK_PREVENTION = ITEMS.register("break_prevention_token",
            ItemBreakPreventionAll::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
