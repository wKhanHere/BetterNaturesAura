package net.wkhan.naturesaura_plus.data.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.recipe.impl.OvenRecipe;
import net.wkhan.naturesaura_plus.data.recipe.lookup.SingleInputRecipeCache;
import net.wkhan.naturesaura_plus.data.recipe.lookup.cache.IInputRecipeCache;
import net.wkhan.naturesaura_plus.data.recipe.types.AuraRecipe;
import net.wkhan.naturesaura_plus.data.recipe.types.AuraRecipeType;

import java.util.function.Function;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, NaturesAuraPlus.MODID);

    public static final RegistryObject<AuraRecipeType<OvenRecipe, SingleInputRecipeCache<OvenRecipe>>> AURA_OVEN =
            register("oven", SingleInputRecipeCache::new);

    private static <RECIPE extends AuraRecipe, CACHE extends IInputRecipeCache> RegistryObject<AuraRecipeType<RECIPE, CACHE>> register(
            String name, Function<AuraRecipeType<RECIPE, CACHE>, CACHE> cacheCreator) {
        return RECIPE_TYPES.register(name, () -> new AuraRecipeType<>(ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, name), cacheCreator));
    }

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
