package net.wkhan.naturesaura_plus.data.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.data.recipe.impl.OvenRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.GsonHelper.*;
import static net.minecraft.world.item.crafting.ShapedRecipe.itemStackFromJson;

public class OvenRecipeSerializer implements RecipeSerializer<OvenRecipe> {
    public static final OvenRecipeSerializer INSTANCE = new OvenRecipeSerializer();

    @Override
    public @NotNull OvenRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        Ingredient input = Ingredient.fromJson(getAsJsonObject(json, "ingredient"));
        ItemStack primaryOutput = itemStackFromJson(getAsJsonObject(json, "result"));
        int processingTime = getAsInt(json, "processing_time", 200);
        int auraCost = getAsInt(json, "aura_cost", 10000);

        NonNullList<OvenRecipe.BonusOutput> bonusOutputs = NonNullList.create();
        if (json.has("bonus_outputs")) {
            for (JsonElement jsonElement : getAsJsonArray(json, "bonus_outputs")) {
                JsonObject bonusObj = jsonElement.getAsJsonObject();
                ItemStack stack = itemStackFromJson(bonusObj);
                bonusOutputs.add(new OvenRecipe.BonusOutput(stack, getAsFloat(bonusObj, "chance", 1.0f)));
            }
        }

        FluidStack fluidOutput = FluidStack.EMPTY;
        if (json.has("result_fluid")) {
            JsonObject fluidObj = getAsJsonObject(json, "result_fluid");
            ResourceLocation fluidId = ResourceLocation.parse(getAsString(fluidObj, "fluid"));
            int amount = getAsInt(fluidObj, "amount");

            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
            if (fluid != null && fluid != Fluids.EMPTY)
                fluidOutput = new FluidStack(fluid, amount);
        }
        return new OvenRecipe(id, input, primaryOutput, bonusOutputs, fluidOutput, processingTime, auraCost);
    }

    @Override
    public @Nullable OvenRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
        Ingredient input = Ingredient.fromNetwork(buf);
        ItemStack primaryOutput = buf.readItem();
        int processingTime = buf.readVarInt();
        int auraCost = buf.readVarInt();
        int bonusCount = buf.readVarInt();
        NonNullList<OvenRecipe.BonusOutput> bonusOutputs = NonNullList.create();
        for (int i = 0; i < bonusCount; i++) {
            bonusOutputs.add(new OvenRecipe.BonusOutput(buf.readItem(), buf.readFloat()));
        }
        FluidStack fluidOutput = buf.readFluidStack();

        return new OvenRecipe(id, input, primaryOutput, bonusOutputs, fluidOutput, processingTime, auraCost);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, OvenRecipe recipe) {
        recipe.getInput().toNetwork(buf);
        buf.writeItem(recipe.getPrimaryOutput());
        buf.writeVarInt(recipe.getProcessingTime());
        buf.writeVarInt(recipe.getAuraCost());
        buf.writeVarInt(recipe.getBonusOutputs().size());
        for (OvenRecipe.BonusOutput bonus : recipe.getBonusOutputs()) {
            buf.writeItem(bonus.stack());
            buf.writeFloat(bonus.chance());
        }
        buf.writeFluidStack(recipe.getOutputFluid());
    }
}
