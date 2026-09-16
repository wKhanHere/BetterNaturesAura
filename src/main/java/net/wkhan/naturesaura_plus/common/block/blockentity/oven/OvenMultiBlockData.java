package net.wkhan.naturesaura_plus.common.block.blockentity.oven;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.wkhan.naturesaura_plus.data.handler.item.SlotSpecificItemStackHandler;
import net.wkhan.naturesaura_plus.data.recipe.ModRecipeTypes;
import net.wkhan.naturesaura_plus.data.recipe.impl.OvenRecipe;

import java.util.List;
import java.util.function.Supplier;

import static net.minecraftforge.fluids.FluidUtil.tryFillContainerAndStow;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.*;

public class OvenMultiBlockData {
    public final BlockPos corePos;
    public final SlotSpecificItemStackHandler itemHandler;
    public final FluidTank fluidTank;
    public final IAuraContainer auraContainer;
    private final String INVENTORY_TAG = "inventory";
    private final String FLUID_TANK_TAG = "fluidTank";
    private final String PROGRESS_TAG = "progress";
    private final String AURA_TAG = "aura";

    public int progress = 0;
    public int maxProgress = 200; //Default
    public OvenRecipe cachedRecipe = null;

    public boolean isChanged = false;
    public boolean isSlotFiveChanged = true; //Set to true at start to allow to keep extracting even after unload & load

    public OvenMultiBlockData(BlockPos corePos, Supplier<Level> levelSupplier) {
        this.corePos = corePos;
        this.itemHandler = new SlotSpecificItemStackHandler(7, () -> ALLOW_BUCKET_OUTPUT_EXTRACTION.get() ? List.of(0, 5) : List.of(0, 5, 6),
                levelSupplier, () -> this.isChanged = true, () -> this.isSlotFiveChanged = true);
        this.fluidTank = new FluidTank(OVEN_FLUID_TANK_CAPACITY.get()); //todo: make this map correctly across reload to world blocks
        this.auraContainer = new IAuraContainer() { //todo: make this its own constructor class and use this for the few other times in mod as well
            // (anonymous classes are odd)
            private int aura = 0;
            public void setAura(int aura) {
                this.aura = aura;
            }
            @Override
            public int storeAura(int auraToStore, boolean simulate) {
                int store = Math.min(auraToStore, OVEN_AURA_CAPACITY.get() - this.getStoredAura());
                if (!simulate)
                    this.setAura(this.getStoredAura() + store);
                return store;
            }
            @Override
            public int drainAura(int auraToDrain, boolean simulate) {
                int drain = Math.min(auraToDrain, this.getStoredAura());
                if (!simulate)
                    this.setAura(this.getStoredAura() - drain);
                return drain;
            }
            @Override
            public int getStoredAura() {
                return aura;
            }
            @Override
            public int getMaxAura() {
                return OVEN_AURA_CAPACITY.get();
            }
            @Override
            public int getAuraColor() {
                return 0xFF4CAF50;
            }
            @Override
            public boolean isAcceptableType(IAuraType iAuraType) {
                return true;
            }
        };
    }

    public void tick(Level level) {
        ItemStack inputStack = itemHandler.getStackInSlot(0);
        if (level.getGameTime() % OVEN_TICK_RATE_FOR_AURA_DRAIN.get() == 0) {
            int toStore = Math.min(IAuraChunk.getAuraInArea(level, corePos, 20), //maybe change the 20 radius or something
                    this.auraContainer.storeAura(OVEN_AURA_DRAIN_AMOUNT_PER_DRAIN.get(), true));
            BlockPos spot = IAuraChunk.getHighestSpot(level, corePos, 20, corePos);
            IAuraChunk.getAuraChunk(level, spot).drainAura(spot, toStore);
            this.auraContainer.storeAura(toStore, false);
            isChanged = true;
        }

        if (isSlotFiveChanged && !itemHandler.getStackInSlot(5).isEmpty()) {
            ItemStack inputBucket = itemHandler.getStackInSlot(5).copy();
            inputBucket.setCount(1);
            FluidActionResult fluidActionResult = tryFillContainerAndStow(inputBucket, fluidTank, null, Integer.MAX_VALUE, null, false);
            if (fluidActionResult.isSuccess() && itemHandler.insertItemInternal(6, fluidActionResult.getResult(), true).isEmpty()) {
                tryFillContainerAndStow(inputBucket, fluidTank, null, Integer.MAX_VALUE, null, true);
                itemHandler.extractItemInternal(5, 1, false);
                itemHandler.insertItemInternal(6, fluidActionResult.getResult(), false);
                isChanged = true;
            }
            else
                isSlotFiveChanged = true;
        }

        if (inputStack.isEmpty()) {
            cachedRecipe = null;
            if (progress <= 0)
                return;
            invalidateRecipe();
            return;
        }

        if (cachedRecipe == null || !cachedRecipe.getInput().test(inputStack)) {
            cachedRecipe = ModRecipeTypes.AURA_OVEN.get().getInputCache().getRecipe(level, inputStack);
            if (cachedRecipe == null) {
                invalidateRecipe();
                return;
            }
        }

        maxProgress = cachedRecipe.getProcessingTime();
        if (!canCraft(cachedRecipe)) {
            invalidateRecipe();
            return;
        }
        progress++;
        isChanged = true;
        if (progress < maxProgress)
            return;
        craftItem(level, cachedRecipe);
        progress = 0;
    }

    private void invalidateRecipe() {
        progress = 0;
        isChanged = true;
    }

    private boolean canCraft(OvenRecipe recipe) {
        if (auraContainer.getStoredAura() < recipe.getAuraCost())
            return false;
        if (fluidTank.fill(recipe.getOutputFluid(), IFluidHandler.FluidAction.SIMULATE) < recipe.getOutputFluid().getAmount())
            return false;
        return !areOutputsFilled(recipe, itemHandler);
    }

    private boolean areOutputsFilled(OvenRecipe recipe, SlotSpecificItemStackHandler inventory) {
        if (!inventory.insertItemInternal(1, recipe.getPrimaryOutput(), true).isEmpty())
            return true;
        for (OvenRecipe.BonusOutput bonusOutput : recipe.getBonusOutputs()) {
            ItemStack leftOverStack = bonusOutput.stack().copy();
            for (int slot = 2; slot < 5; slot++) {
                leftOverStack = inventory.insertItemInternal(slot, leftOverStack, true);
                if (leftOverStack.isEmpty())
                    break;
            }
            if (!leftOverStack.isEmpty())
                return true;
        }
        return false;
    }

    private void craftItem(Level level, OvenRecipe recipe) {
        auraContainer.drainAura(recipe.getAuraCost(), false);
        itemHandler.extractItemInternal(0, 1, false);
        itemHandler.insertItemInternal(1, recipe.getPrimaryOutput().copy(), false);

        for (OvenRecipe.BonusOutput bonusOutput : recipe.getBonusOutputs()) {
            if (level.random.nextFloat() > bonusOutput.chance())
                continue;
            ItemStack leftOverStack = bonusOutput.stack().copy();
            int emptySlot = -1;
            for (int slot = 2; slot < 5; slot++) {
                if (itemHandler.getStackInSlot(slot).isEmpty()) {
                    emptySlot = emptySlot == -1 ? slot : emptySlot;
                    continue;
                }
                leftOverStack = itemHandler.insertItemInternal(slot, leftOverStack, false);
                if (leftOverStack.isEmpty()) {
                    break;
                }
            }
            if (!leftOverStack.isEmpty() && emptySlot != -1)
                leftOverStack = itemHandler.insertItemInternal(emptySlot, leftOverStack, false);
            if (!leftOverStack.isEmpty()) {
                ItemEntity droppedStack = EntityType.ITEM.create(level);
                if (droppedStack == null)
                    continue;
                droppedStack.setPos(corePos.getCenter());
                droppedStack.setItem(leftOverStack);
                level.addFreshEntity(droppedStack);
            }

        } //Item Output filling
        fluidTank.fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE); //Fluid output filling
    }

    public void saveNBT(CompoundTag tag) {
        tag.put(INVENTORY_TAG, itemHandler.serializeNBT());
        tag.put(FLUID_TANK_TAG, fluidTank.writeToNBT(new CompoundTag()));
        tag.putInt(PROGRESS_TAG, progress);
        tag.putInt(AURA_TAG, auraContainer.getStoredAura());
    }

    public void loadNBT(CompoundTag tag) {
        itemHandler.deserializeNBT(tag.getCompound(INVENTORY_TAG));
        if (fluidTank.getCapacity() != OVEN_FLUID_TANK_CAPACITY.get())
            fluidTank.setCapacity(OVEN_FLUID_TANK_CAPACITY.get());
        fluidTank.readFromNBT(tag.getCompound(FLUID_TANK_TAG));
        progress = tag.getInt(PROGRESS_TAG);
        auraContainer.storeAura(tag.getInt(AURA_TAG), false);
    }
}
