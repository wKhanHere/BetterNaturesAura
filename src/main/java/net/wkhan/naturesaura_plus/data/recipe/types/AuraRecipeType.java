package net.wkhan.naturesaura_plus.data.recipe.types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.data.recipe.IAuraRecipeTypeProvider;
import net.wkhan.naturesaura_plus.data.recipe.lookup.cache.IInputRecipeCache;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuraRecipeType<RECIPE extends AuraRecipe, CACHE extends IInputRecipeCache>
        implements RecipeType<RECIPE>, IAuraRecipeTypeProvider<RECIPE, CACHE> {
    private final ResourceLocation registryName;
    private final CACHE inputCache;
    private List<RECIPE> cachedRecipes = Collections.emptyList();

    public AuraRecipeType(ResourceLocation name, Function<AuraRecipeType<RECIPE, CACHE>, CACHE> cacheCreator) {
        this.registryName = name;
        this.inputCache = cacheCreator.apply(this);
    }

    @Override
    public CACHE getInputCache() {
        return inputCache;
    }

    public void clearCaches() {
        this.cachedRecipes = Collections.emptyList();
        this.inputCache.clear();
    }

    public List<RECIPE> getRecipes(Level level) {
        if (!this.cachedRecipes.isEmpty() || level == null)
            return this.cachedRecipes;

        RecipeManager recipeManager = level.getRecipeManager();
        List<RECIPE> rawRecipes = recipeManager.getAllRecipesFor(this);
        this.cachedRecipes = rawRecipes.stream().filter(recipe -> !recipe.isIncomplete()).collect(Collectors.toList());
        return this.cachedRecipes;
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
