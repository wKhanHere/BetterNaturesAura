package net.wkhan.naturesaura_plus.common.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.compat.botania.ItemAuraManaHolder;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isBotaniaLoaded;
import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isCuriosLoaded;
import static net.wkhan.naturesaura_plus.compat.curios.NaturesAuraPlusCuriosUtil.attachMergedCapability;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCurioCapabilityEvent {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (!isBotaniaLoaded || !(event.getObject().getItem() instanceof ItemAuraManaHolder))
            return;
        if (isCuriosLoaded)
            attachMergedCapability(event);
    }
}
