package net.wkhan.naturesaura_plus.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.EntityInteractionRule;
import net.wkhan.naturesaura_plus.data.EntityInteractionRules;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public final class EntityInteractionEvent {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) { //Is never effective lol
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        if (stack.isEmpty()) return;
        if (!stack.hasTag()) return;
        if (!(stack.getTag().getBoolean("naturesaura_plus:break_prevention") && stack.getDamageValue() == stack.getMaxDamage() - 1)) return;

        EntityInteractionRule rule =
                EntityInteractionRules.match(stack, target);

        if (rule == null) return;

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);


//        stack.hurtAndBreak(
//                rule.damage(),
//                event.getEntity(),
//                p -> p.broadcastBreakEvent(event.getHand())
//        );
    }
}
