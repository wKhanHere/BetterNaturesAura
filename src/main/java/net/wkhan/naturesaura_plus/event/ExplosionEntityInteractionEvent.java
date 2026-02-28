package net.wkhan.naturesaura_plus.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

import static net.wkhan.naturesaura_plus.item.custom.ItemBreakPreventionAll.Events.isBroken;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class ExplosionEntityInteractionEvent {

    //Prevents Broken items in world from being yeeted by explosions.
    @SubscribeEvent
    public static void onExplosionEntityInteraction(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                return isBroken(itemEntity.getItem());
            }
            return false;
        });
    }
}
