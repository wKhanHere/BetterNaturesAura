package net.wkhan.naturesaura_plus.data.recipe.types;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public interface AuraRecipe extends Recipe<Container> {
    Ingredient getInput();
    boolean isIncomplete();
}
