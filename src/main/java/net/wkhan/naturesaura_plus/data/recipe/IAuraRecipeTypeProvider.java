package net.wkhan.naturesaura_plus.data.recipe;

import net.wkhan.naturesaura_plus.data.recipe.lookup.cache.IInputRecipeCache;
import net.wkhan.naturesaura_plus.data.recipe.types.AuraRecipe;

public interface IAuraRecipeTypeProvider<RECIPE extends AuraRecipe, CACHE extends IInputRecipeCache> {
    CACHE getInputCache();
    //RECIPE still stays because semantics, and potential use in the future.
}
