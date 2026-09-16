package net.wkhan.naturesaura_plus.data.handler.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotSpecificSlotItemHandler extends SlotItemHandler {
    public SlotSpecificSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotIndex = index;
    }

    private final int slotIndex;

    @Override
    public boolean mayPickup(Player playerIn) {
        return !((SlotSpecificItemStackHandler) this.getItemHandler()).extractItemInternal(slotIndex, 1, true).isEmpty();
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return ((SlotSpecificItemStackHandler) this.getItemHandler()).extractItemInternal(slotIndex, amount, false);
    }
}
