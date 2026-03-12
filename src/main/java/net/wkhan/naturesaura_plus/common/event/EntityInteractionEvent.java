package net.wkhan.naturesaura_plus.common.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.data.entity.EntityInteractionRules;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public final class EntityInteractionEvent {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) { //Is never effective lol
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        Entity entity = event.getEntity();
        Level level = event.getLevel();

        if(!isTokenAppliedBroken(stack)) return;

        boolean ruleMatch = EntityInteractionRules.match(stack, target);
        if (!ruleMatch) return;

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
}
