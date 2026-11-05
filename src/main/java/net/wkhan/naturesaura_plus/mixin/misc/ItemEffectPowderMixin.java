package net.wkhan.naturesaura_plus.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ItemImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEffectPowder.class)
public abstract class ItemEffectPowderMixin extends ItemImpl {
    @Shadow
    public static ResourceLocation getEffect(ItemStack stack) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    public ItemEffectPowderMixin(String baseName) {
        super(baseName);
    }

    @WrapMethod(
            method = "getName"
    )
    private Component naturesaura_plus$fixNullGetEffectInName(ItemStack stack, Operation<Component> original) {
        ResourceLocation effect = getEffect(stack);
        return Component.translatable(effect == null ? this.getDescriptionId(stack) : this.getDescriptionId(stack) + "." + effect);
    }
}
