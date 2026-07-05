package net.wkhan.naturesaura_plus.common.event;

import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.entity.EntityInteractionRules;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public final class EntityInteractionEvent {
    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();

        boolean ruleMatch = EntityInteractionRules.match(stack, target);
        if (!ruleMatch) return;

        String interactionType = EntityInteractionRules.getRule(stack, target).getInteractionType();
        if (interactionType == null) return;
        else if(interactionType.equals("all")) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }
//        else if (interactionType.equals("all_but_pet_reviver_on_abstract_horse")) {
//            boolean done = apply_pet_reviver_on_abstract_horse((AbstractHorse) target, stack);
//            if (!done) return;
//            event.setCancellationResult(InteractionResult.SUCCESS);
//            event.setCanceled(true);
//            return;
//        }
        else if(!interactionType.equals("broken_tool")) return;

        if(!isTokenAppliedBroken(stack)) return;

        Entity entity = event.getEntity();
        Level level = event.getLevel();

        level.playSound(
                entity,
                entity.blockPosition(),
                net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.8F,
                (float) (0.8F + Math.random() * 0.4F)
        );
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
    }

    private static boolean apply_pet_reviver_on_abstract_horse(AbstractHorse targetHorse, ItemStack stack) {
        if (!targetHorse.isTamed()) return false;
        if (targetHorse.getPersistentData().getBoolean("naturesaura:pet_reviver")) return false;
        if (stack.getItem() != ModItems.PET_REVIVER) return false;
        targetHorse.getPersistentData().putBoolean("naturesaura:pet_reviver", true);
        if (!targetHorse.level().isClientSide) {
            stack.shrink(1);
        }
        return true;
    }
}
