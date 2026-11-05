package net.wkhan.naturesaura_plus.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.ellpeck.naturesaura.compat.jei.JEINaturesAuraPlugin;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(JEINaturesAuraPlugin.class)
public abstract class JEINaturesAuraPluginMixin {

    @WrapOperation(
            method = "registerItemSubtypes",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/registration/ISubtypeRegistration;registerSubtypeInterpreter(Lmezz/jei/api/ingredients/IIngredientTypeWithSubtypes;Ljava/lang/Object;Lmezz/jei/api/ingredients/subtypes/IIngredientSubtypeInterpreter;)V"
            ),
            remap = false
    )
    private static <B, I> void naturesaura_plus$fixNullEffectRegister(ISubtypeRegistration instance,
                                                                      IIngredientTypeWithSubtypes<B, I> vanillaItemStackType, B powder,
                                                                      IIngredientSubtypeInterpreter<I> iiIngredientSubtypeInterpreter,
                                                                      Operation<Void> original) {
        original.call(instance, vanillaItemStackType, powder, (IIngredientSubtypeInterpreter<I>) (stack, context) -> {
            ResourceLocation effect = ItemEffectPowder.getEffect((ItemStack) stack);
            return effect == null ? IIngredientSubtypeInterpreter.NONE : effect.toString();
        });
    }
}
