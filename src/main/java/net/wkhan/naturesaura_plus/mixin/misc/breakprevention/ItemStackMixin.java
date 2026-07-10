package net.wkhan.naturesaura_plus.mixin.misc.breakprevention;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin extends CapabilityProvider<ItemStack> {

    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @Inject(
            method = "hurtAndBreak",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"
            ),
            cancellable = true
    )
    private <T extends LivingEntity> void naturesaura_plus$preventItemBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_, CallbackInfo ci) {
        ItemStack instance = (ItemStack) (Object) this;
        if (!isTokenAppliedBroken(instance))
            return;
        ci.cancel();
        instance.setDamageValue(instance.getMaxDamage() - 1);
    }

    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    private void naturesaura_plus$preventBrokenItemUse(
            Level p_41683_, Player p_41684_, InteractionHand p_41685_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        ItemStack stack = (ItemStack)(Object)this;
        if(!isTokenAppliedBroken(stack))
            return;
        p_41683_.playSound(
                    p_41684_,
                    p_41684_.blockPosition(),
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.8F,
                    0.8F + p_41683_.random.nextFloat() * 0.4F
        );
        cir.setReturnValue(InteractionResultHolder.fail(stack));
    }

}
