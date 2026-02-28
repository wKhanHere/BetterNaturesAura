package net.wkhan.naturesaura_plus.mixin;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.wkhan.naturesaura_plus.item.custom.ItemBreakPreventionAll.Events.isBroken;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> {

    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @ModifyVariable(
            method = "hurt",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"
                    )
            ),
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private int naturesaura_plus$preventBreak(int amount) {
        ItemStack stack = (ItemStack)(Object)this;
        if(!isBroken(stack)) return amount;
        int remaining = stack.getMaxDamage() - stack.getDamageValue() - 1;
        return Math.max(0, Math.min(amount, remaining));
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

        if(!isBroken(stack)) return;

        if (stack.getDamageValue() == stack.getMaxDamage() - 1) {
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

}
