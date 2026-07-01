package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STRIPPED_ANCIENT_BARK.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.STRIPPED_ANCIENT_LOG.get())
                .unlockedBy("has_log", has(ModBlocks.STRIPPED_ANCIENT_LOG.get()))
                .save(pWriter, NaturesAuraPlus.MODID + ":stripped_ancient_wood_from_logs");
        Item ancientPlanks = ForgeRegistries.ITEMS.getValue(ResourceLocation
                .fromNamespaceAndPath("naturesaura","ancient_planks"));
        if (ancientPlanks == null) return;
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ancientPlanks, 2)
                .requires(Ingredient.of(ModBlocks.STRIPPED_ANCIENT_LOG.get(), ModBlocks.STRIPPED_ANCIENT_BARK.get()))
                .unlockedBy("has_ancient_log", has(ModBlocks.STRIPPED_ANCIENT_LOG.get()))
                .unlockedBy("has_ancient_wood", has(ModBlocks.STRIPPED_ANCIENT_LOG.get()))
                .save(pWriter, NaturesAuraPlus.MODID + ":planks_from_stripped_ancient_log");
    }
}
