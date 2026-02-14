package net.wkhan.naturesaura_plus.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.BlockInteractionRule;
import net.wkhan.naturesaura_plus.data.BlockInteractionRules;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class BlockInteractionEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        BlockState state = event.getLevel().getBlockState(event.getPos());
        Entity entity = event.getEntity();
        Level level = event.getLevel();
        if (stack.isEmpty()) return;
        if (!stack.hasTag()) return;
        if (!(stack.getTag().getBoolean("naturesaura_plus:break_prevention") && stack.getDamageValue() == stack.getMaxDamage() - 1)) return;

        BlockInteractionRule rule =
                BlockInteractionRules.match(stack, state);

        if (rule == null) return;

        level.playSound( //Doesnt work
                entity,
                entity.blockPosition(),
                net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.8F,
                (float) (0.8F + Math.random() * 0.4F)
        );
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);


//        if (rule.damage() > 0) {
//            stack.hurtAndBreak(
//                    rule.damage(),
//                    event.getEntity(),
//                    p -> p.broadcastBreakEvent(event.getHand())
//            );
//        }
    }
}
