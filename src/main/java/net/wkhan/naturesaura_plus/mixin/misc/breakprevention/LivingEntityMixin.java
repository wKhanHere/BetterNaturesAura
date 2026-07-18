package net.wkhan.naturesaura_plus.mixin.misc.breakprevention;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.data.trackers.HandHeldItemTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(
            method = "getItemInHand",
            at = @At("RETURN"),
            cancellable = true
    )
    private void naturesaura_plus$returnEmptyHand(InteractionHand p_21121_, CallbackInfoReturnable<ItemStack> cir) {
        if (!HandHeldItemTracker.MASK_HAND_EMPTY.get())
            return;
        if (!isTokenAppliedBroken(cir.getReturnValue()))
            return;
        cir.setReturnValue(ItemStack.EMPTY);
    }
}
