package net.wkhan.naturesaura_plus.data.recipe.lookup;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.data.recipe.lookup.cache.IInputRecipeCache;
import net.wkhan.naturesaura_plus.data.recipe.types.AuraRecipe;
import net.wkhan.naturesaura_plus.data.recipe.types.AuraRecipeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleInputRecipeCache<RECIPE extends AuraRecipe> implements IInputRecipeCache {
    private final AuraRecipeType<RECIPE, ?> recipeType;
    private final Map<Item, RECIPE> cache = new HashMap<>();

    public SingleInputRecipeCache(AuraRecipeType<RECIPE, ?> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    public RECIPE getRecipe(Level level, ItemStack input) {
        if (input.isEmpty())
            return null;
        if (!this.cache.isEmpty())
            return this.cache.get(input.getItem());

        List<RECIPE> recipes = recipeType.getRecipes(level);
        for (RECIPE recipe : recipes) {
            this.cache.put(recipe.getInput().getItems()[0].getItem(), recipe);
        }
        return this.cache.get(input.getItem());
    }
}
