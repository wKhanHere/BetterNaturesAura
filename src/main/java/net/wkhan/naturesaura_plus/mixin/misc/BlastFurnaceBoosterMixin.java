package net.wkhan.naturesaura_plus.mixin.misc;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityBlastFurnaceBooster;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFurnaceHeater;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.*;

@Mixin(BlockEntityBlastFurnaceBooster.class)
public abstract class BlastFurnaceBoosterMixin extends BlockEntityImpl {
    public BlastFurnaceBoosterMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$ourFurnaceBooster(CallbackInfo ci) {
        ci.cancel();
        if (this.level == null || this.level.isClientSide())
            return;
        int toUse = FURNACE_BOOSTER_AURA_COST.get();
        if (!this.canUseRightNow(toUse))
            return;

        BlockEntity below = this.level.getBlockEntity(this.worldPosition.below());
        if (below == null || !below.getBlockState().is(ModTags.Blocks.FURNACE_FOR_BOOSTER))
            return;
        AbstractFurnaceBlockEntity tile = (AbstractFurnaceBlockEntity) below;
        Recipe<?> recipe = this.level.getRecipeManager()
                .getRecipeFor(BlockEntityFurnaceHeater.getRecipeType(tile), tile, this.level).orElse(null);
        if (recipe == null || !this.naturesaura_plus$isApplicable(recipe.getIngredients()))
            return;
        ContainerData data = BlockEntityFurnaceHeater.getFurnaceData(tile);
        int doneDiff = data.get(3) - data.get(2);
        if (doneDiff > 1)
            return;

        if (this.level.random.nextFloat() > (float) FURNACE_BOOSTER_CHANCE.get() / 100) {
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) this.worldPosition.getX(),
                    (float) this.worldPosition.getY(), (float) this.worldPosition.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 0));
            return;
        }


        ItemStack output = tile.getItem(2);
        if (output.getCount() >= output.getMaxStackSize())
            return;

        if (output.isEmpty()) {
            ItemStack result = recipe.getResultItem(this.level.registryAccess());
            tile.setItem(2, result.copy());
        }
        else
            output.grow(1);

        BlockPos pos = IAuraChunk.getHighestSpot(this.level, this.worldPosition, 30, this.worldPosition);
        IAuraChunk.getAuraChunk(this.level, pos).drainAura(pos, toUse);
        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) this.worldPosition.getX(),
                (float) this.worldPosition.getY(), (float) this.worldPosition.getZ(), PacketParticles.Type.BLAST_FURNACE_BOOSTER, 1));
    }

    @Unique
    private boolean naturesaura_plus$isApplicable(NonNullList<Ingredient> ingredients) {
        if (!CHECK_TAG_FOR_FURNACE_BOOST.get())
            return true;

        for(Ingredient ing : ingredients) {
            for(ItemStack stack : ing.getItems()) {
                if (stack.is(ModTags.Items.VALID_SMELTABLE_TO_BOOST))
                    return true;
            }
        }

        return false;
    }
}
