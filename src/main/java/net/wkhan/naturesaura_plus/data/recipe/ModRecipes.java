package net.wkhan.naturesaura_plus.data.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.recipe.impl.OvenRecipe;
import net.wkhan.naturesaura_plus.data.recipe.serializer.OvenRecipeSerializer;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NaturesAuraPlus.MODID);

    public static final RegistryObject<RecipeSerializer<OvenRecipe>> OVEN_SERIALIZER =
            SERIALIZERS.register("oven", () -> OvenRecipeSerializer.INSTANCE); //todo: figure out if im actually using this registry object

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
