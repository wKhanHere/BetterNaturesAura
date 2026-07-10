package net.wkhan.naturesaura_plus.mixin.misc.breakprevention;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.data.HandHeldItemTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(
            method = "interactOn",
            at = @At("HEAD")
    )
    private void naturesaura_plus$tellLiesAboutHeldItem(Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        if (isTokenAppliedBroken(stack))
            HandHeldItemTracker.MASK_HAND_EMPTY.set(true);
    }

    @Inject(
            method = "interactOn",
            at = @At("RETURN")
    )
    private void naturesaura_plus$stopTellingLiesAboutHeldItem(Entity p_36158_, InteractionHand p_36159_, CallbackInfoReturnable<InteractionResult> cir) {
        HandHeldItemTracker.MASK_HAND_EMPTY.set(false);
    }
}
