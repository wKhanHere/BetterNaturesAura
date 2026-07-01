package net.wkhan.naturesaura_plus.common.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.compat.botania.BotaniaModItems;
import net.wkhan.naturesaura_plus.common.item.ModItems;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isBotaniaLoaded;
import static net.wkhan.naturesaura_plus.compat.botania.ItemAuraManaHolder.setCreativeStack;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabItemsEvent {

    @SubscribeEvent
    public static void addCustomItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("naturesaura", "tab"))) return;
        event.accept(ModItems.STRIPPED_ANCIENT_LOG.get());
        event.accept(ModItems.STRIPPED_ANCIENT_BARK.get());
        event.accept(ModItems.BREAK_PREVENTION.get());
        event.accept(ModItems.COFFEE.get());
        event.accept(ModItems.AURA_COFFEE.get());
        if (isBotaniaLoaded) {
            event.accept(BotaniaModItems.AURA_MANA_HOLDER.get());
            ItemStack auraManaHolder = new ItemStack(BotaniaModItems.AURA_MANA_HOLDER.get());
            setCreativeStack(auraManaHolder);
            event.accept(auraManaHolder);
        }
    }
}
