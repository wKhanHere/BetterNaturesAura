package net.wkhan.naturesaura_plus.common.block.blockentity.oven;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.wkhan.naturesaura_plus.common.block.OvenPartBlock;
import net.wkhan.naturesaura_plus.common.gui.oven.OvenMenu;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.wkhan.naturesaura_plus.common.block.blockentity.ModBlockEntities.OVEN_CORE;

public class OvenCoreBlockEntity extends BlockEntity implements MenuProvider {
    public final OvenMultiBlockData multiblockData;
    protected final ContainerData guiData;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<FluidTank> lazyFluidTank = LazyOptional.empty();
    private LazyOptional<IAuraContainer> lazyAuraHandler = LazyOptional.empty();

    public OvenCoreBlockEntity(BlockPos pos, BlockState state) {
        super(OVEN_CORE.get(), pos, state);

        this.multiblockData = new OvenMultiBlockData(pos, this::getLevel);
        this.guiData = new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> multiblockData.progress;
                    case 1 -> multiblockData.maxProgress;
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {
                switch (index) {
                    case 0 -> multiblockData.progress = value;
                    case 1 -> multiblockData.maxProgress = value;
                }
            }
            @Override public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> multiblockData.itemHandler);
        lazyFluidTank = LazyOptional.of(() -> multiblockData.fluidTank);
        lazyAuraHandler = LazyOptional.of(() -> multiblockData.auraContainer);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidTank.invalidate();
        lazyAuraHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (this.getBlockState().hasProperty(OvenPartBlock.OVEN_PART) &&
                this.getBlockState().getValue(OvenPartBlock.OVEN_PART) == OvenPartBlock.OvenPart.BASE) {
            return super.getCapability(cap, side);
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return lazyFluidTank.cast();
        if (cap == NaturesAuraAPI.CAP_AURA_CONTAINER)
            return lazyAuraHandler.cast();
        if (cap != ForgeCapabilities.ITEM_HANDLER)
            return super.getCapability(cap, side);
        return lazyItemHandler.cast();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.naturesaura_plus.auric_oven");
    }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new OvenMenu(containerId, playerInventory, this, this.guiData);
    }

    public static void tick(Level level, OvenCoreBlockEntity entity, BlockPos pos, BlockState state) {
        if (entity.multiblockData == null)
            return;
        entity.multiblockData.tick(level);
        if (!entity.multiblockData.isChanged)
            return;
        entity.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
        entity.multiblockData.isChanged = false;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        multiblockData.saveNBT(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        multiblockData.loadNBT(tag);
    }

    public void dropInventory(Level level, BlockPos pos) {
        SimpleContainer inventory = new SimpleContainer(multiblockData.itemHandler.getSlots());
        for (int i = 0; i < multiblockData.itemHandler.getSlots(); i++) {
            inventory.setItem(i, multiblockData.itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, pos, inventory);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}