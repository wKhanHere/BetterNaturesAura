package net.wkhan.naturesaura_plus.data.recipe.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.data.recipe.ModRecipeTypes;
import org.jetbrains.annotations.NotNull;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.MODID;

@JeiPlugin
public class NAPlusJEIPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new OvenRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(OvenRecipeCategory.TYPE, ModRecipeTypes.AURA_OVEN.get().getRecipes(Minecraft.getInstance().level));
    }
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.AURIC_OVEN_BRICK.get()), OvenRecipeCategory.TYPE);
    }
}
