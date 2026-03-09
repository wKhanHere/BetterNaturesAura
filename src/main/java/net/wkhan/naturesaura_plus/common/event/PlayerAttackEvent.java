package net.wkhan.naturesaura_plus.common.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class PlayerAttackEvent {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        Entity target = event.getTarget();

        if (isTokenAppliedBroken(stack)) {
            event.setCanceled(true);

            float cooldownScale = player.getAttackStrengthScale(0.5F);
            player.resetAttackStrengthTicker();

            if(player.level().isClientSide || !(target instanceof LivingEntity)) return;

            float damageToDeal = (0.2F + cooldownScale * cooldownScale * 0.8F);
            target.hurt(player.damageSources().playerAttack(player), damageToDeal);

            player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.PLAYER_ATTACK_NODAMAGE,
                    SoundSource.PLAYERS,
                    1.0F,
                    0.8F
            );
        }
    }
}
