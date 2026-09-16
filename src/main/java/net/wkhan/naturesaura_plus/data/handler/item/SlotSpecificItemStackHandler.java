package net.wkhan.naturesaura_plus.data.handler.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.wkhan.naturesaura_plus.data.recipe.ModRecipeTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class SlotSpecificItemStackHandler extends ItemStackHandler {
    public SlotSpecificItemStackHandler(int size, Supplier<List<Integer>> extractableItemSlotIds, Supplier<Level> levelSupplier, Runnable markDirty, Runnable markSlotFiveDirty) {
        super(size);
        this.notExtractableItemSlotIds = extractableItemSlotIds;
        this.levelSupplier = levelSupplier;
        this.markDirty = markDirty;
        this.markSlotFiveDirty = markSlotFiveDirty;
    }

    private final Runnable markDirty;
    private final Supplier<List<Integer>> notExtractableItemSlotIds;
    private final Supplier<Level> levelSupplier;
    private final Runnable markSlotFiveDirty;

    @Override
    protected void onContentsChanged(int slot) {
        markDirty.run();
        if (slot == 5)
            markSlotFiveDirty.run();
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (notExtractableItemSlotIds.get().contains(slot))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    //extractItem() without slot checks
    public ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return switch (slot) {
            case 0 -> {
                Level level = this.levelSupplier.get();
                yield level != null && ModRecipeTypes.AURA_OVEN.get().getInputCache().getRecipe(level, stack) != null;
                //todo: change this to use any cache (replace .get() till with a constructor var)
            }
            case 5 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
            default -> false;
        };
    }

    //insertItem() without isItemValid() check
    public ItemStack insertItemInternal(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        validateSlotIndex(slot);
        ItemStack existing = this.stacks.get(slot);
        int limit = getStackLimit(slot, stack);
        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;
            limit -= existing.getCount();
        }
        if (limit <= 0)
            return stack;
        boolean reachedLimit = stack.getCount() > limit;
        if (simulate)
            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
        if (existing.isEmpty())
            this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
        else
            existing.grow(reachedLimit ? limit : stack.getCount());
        onContentsChanged(slot);
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }
}
