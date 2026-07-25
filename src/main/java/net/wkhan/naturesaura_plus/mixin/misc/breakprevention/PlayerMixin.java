package net.wkhan.naturesaura_plus.mixin.misc.breakprevention;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.data.trackers.HandHeldItemTracker;
import org.spongepowered.asm.mixin.Mixin;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @WrapMethod(
            method = "interactOn"
    )
    private InteractionResult naturesaura_plus$tellLiesAboutHeldItem(Entity target, InteractionHand hand, Operation<InteractionResult> original) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        boolean tellLies = isTokenAppliedBroken(stack);
        if (tellLies)
            HandHeldItemTracker.MASK_HAND_EMPTY.set(true);
        try {
            return original.call(target, hand);
        }
        finally {
            if (tellLies)
                HandHeldItemTracker.MASK_HAND_EMPTY.remove();
        }
    }
}
