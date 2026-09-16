package net.wkhan.naturesaura_plus.data.recipe.impl;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.wkhan.naturesaura_plus.data.recipe.ModRecipeTypes;
import net.wkhan.naturesaura_plus.data.recipe.serializer.OvenRecipeSerializer;
import net.wkhan.naturesaura_plus.data.recipe.types.AuraRecipe;
import org.jetbrains.annotations.NotNull;

public class OvenRecipe implements AuraRecipe {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack primaryOutput;
    private final NonNullList<BonusOutput> bonusOutputs;
    private final FluidStack outputFluid;
    private final int processingTime;
    private final int auraCost;
    public record BonusOutput(ItemStack stack, float chance) {}

    public OvenRecipe(ResourceLocation id, Ingredient input, ItemStack primaryOutput, NonNullList<BonusOutput> bonusOutputs,
             FluidStack outputFluid, int processingTime, int auraCost) {
        this.id = id;
        this.input = input;
        this.primaryOutput = primaryOutput;
        this.bonusOutputs = bonusOutputs;
        this.outputFluid = outputFluid;
        this.processingTime = processingTime;
        this.auraCost = auraCost;
    }

    @Override
    public Ingredient getInput() {
        return input;
    }
    public ItemStack getPrimaryOutput() {
        return primaryOutput;
    }
    public NonNullList<BonusOutput> getBonusOutputs() {
        return bonusOutputs;
    }
    public FluidStack getOutputFluid() {
        return outputFluid;
    }
    public int getProcessingTime() {
        return processingTime;
    }
    public int getAuraCost() {
        return auraCost;
    }


    @Override
    public boolean isIncomplete() {
        return input.isEmpty();
    }
    @Override
    public boolean matches(Container container, @NotNull Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
        return primaryOutput.copy();
    }
    @Override public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    @Override public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        return primaryOutput.copy();
    }
    @Override public @NotNull ResourceLocation getId() {
        return id;
    }
    @Override public @NotNull RecipeSerializer<?> getSerializer() {
        return OvenRecipeSerializer.INSTANCE;
    }
    @Override public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.AURA_OVEN.get();
    }
}
