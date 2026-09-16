package net.wkhan.naturesaura_plus.common.gui.oven;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.common.block.blockentity.oven.OvenCoreBlockEntity;
import net.wkhan.naturesaura_plus.common.gui.ModMenuTypes;
import net.wkhan.naturesaura_plus.data.handler.item.SlotSpecificSlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static net.minecraftforge.common.ForgeMod.BLOCK_REACH;

public class OvenMenu extends AbstractContainerMenu {
    public final OvenCoreBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    //Client
    public OvenMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); //the hell is "this"?
    }

    //Server
    public OvenMenu(int id, Inventory inv, BlockEntity tile, ContainerData data) {
        super(ModMenuTypes.OVEN_MENU.get(), id);
        checkContainerSize(inv, 7);
        this.blockEntity = ((OvenCoreBlockEntity) tile);
        this.level = inv.player.level();
        this.data = data;
        addDataSlots(data);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            //Input
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 0, 15, 32));
            //One main output, then 3 bonus outputs
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 1, 67, 32));
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 2, 99, 10));
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 3, 99, 32));
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 4, 99, 54));
            //Bucket slots
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 5, 150, 16));
            this.addSlot(new SlotSpecificSlotItemHandler(itemHandler, 6, 150, 48));
        });
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public boolean isCrafting() {
        return data.get(0) > 0; //Progress > 0
    }

    public float getFractionalProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 ? (float) progress / maxProgress : 1;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = this.blockEntity.getBlockPos();
        if (this.level == null || this.level.getBlockEntity(pos) != this.blockEntity)
            return false;
        double reach = player.getAttributeValue(BLOCK_REACH.get()) + 3.5;
        return this.level.getBlockState(pos).is(ModBlocks.AURIC_OVEN_BRICK.get()) &&
                player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= reach * reach;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem())
            return ItemStack.EMPTY;

        ItemStack originalStack = slot.getItem();
        ItemStack copyStack = originalStack.copy();
        int playerInvStart = 7;
        int playerInvEnd = playerInvStart + 27;
        int hotbarEnd = playerInvEnd + 9;

        if (index < playerInvStart) {
            if (!this.moveItemStackTo(originalStack, playerInvStart, hotbarEnd, true))
                return ItemStack.EMPTY;
            slot.onQuickCraft(originalStack, copyStack);
        }
        else if (!this.moveItemStackTo(originalStack, 0, 1, false) &&
                !this.moveItemStackTo(originalStack, 5, 6, false) &&
                ((index < playerInvEnd && !this.moveItemStackTo(originalStack, playerInvEnd, hotbarEnd, false))
                || !this.moveItemStackTo(originalStack, playerInvStart, playerInvEnd, false))) {
            return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty())
            slot.set(ItemStack.EMPTY);
        else
            slot.setChanged();
        if (originalStack.getCount() == copyStack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, originalStack);
        return copyStack;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
