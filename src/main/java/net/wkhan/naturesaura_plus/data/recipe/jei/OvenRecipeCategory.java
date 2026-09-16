package net.wkhan.naturesaura_plus.data.recipe.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.data.recipe.impl.OvenRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.MODID;
import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.buildFluidStackToolTip;
import static net.wkhan.naturesaura_plus.common.gui.oven.OvenScreen.renderFluid;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.OVEN_FLUID_TANK_CAPACITY;

public class OvenRecipeCategory implements IRecipeCategory<OvenRecipe> {
    public static final RecipeType<OvenRecipe> TYPE = RecipeType.create(MODID, "oven", OvenRecipe.class);

    private final IGuiHelper guiHelper;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic flameStatic;
    private FluidStack fluidStack = FluidStack.EMPTY;
    private final Map<Integer, IDrawableAnimated> flameCache = new HashMap<>();

    public OvenRecipeCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/oven_menu.png");
        this.background = helper.createDrawable(texture, 1, 1, 174, 75);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.AURIC_OVEN_BRICK.get()));
        this.guiHelper = helper;
        this.flameStatic = helper.createDrawable(texture, 176, 2, 13, 14);
    }
    @Override
    public @NotNull RecipeType<OvenRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.category.naturesaura_plus.oven");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull OvenRecipe recipe, @NotNull IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 14, 31).addItemStack(recipe.getInput().getItems()[0]);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 66, 31).addItemStack(recipe.getPrimaryOutput());
        NonNullList<OvenRecipe.BonusOutput> bonusOutputs = recipe.getBonusOutputs();
        int i = 0;
        for (OvenRecipe.BonusOutput bonusOutput : bonusOutputs) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 9 + 22*i).addItemStack(bonusOutput.stack())
                    .addTooltipCallback((view, tooltip) ->
                            tooltip.add(Component.translatable(
                                    "jei.tooltip.naturesaura_plus.chance",
                                    String.format("%.0f", bonusOutput.chance() * 100)
                            ).withStyle(ChatFormatting.GOLD)));
            i++;
        }
        fluidStack = recipe.getOutputFluid();
    }

    @Override
    public void draw(@NotNull OvenRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IDrawableAnimated flame = flameCache.computeIfAbsent(recipe.getProcessingTime(), time ->
                guiHelper.createAnimatedDrawable(flameStatic, time, IDrawableAnimated.StartDirection.TOP, true)
        );
        flame.draw(guiGraphics, 39, 32);

        renderFluid(guiGraphics, fluidStack, OVEN_FLUID_TANK_CAPACITY.get(), 126, 16, 16, 47);
        guiGraphics.blit(
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/oven_menu.png"),
                124,13, 176, 25, 20, 51
        );
        //todo: draw aura bar
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull OvenRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView,
                                                      double mouseX, double mouseY) {
        if (mouseX >= 126 && mouseX < 126 + 16 && mouseY >= 16 && mouseY < 16 + 47)
            return buildFluidStackToolTip(recipe.getOutputFluid(), OVEN_FLUID_TANK_CAPACITY.get());
        return List.of();
    }
}
