package net.wkhan.naturesaura_plus.common.block.blockentity.oven;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.wkhan.naturesaura_plus.common.block.blockentity.ModBlockEntities.OVEN_EDGE;

public class OvenEdgeBlockEntity extends BlockEntity {
    public OvenEdgeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(OVEN_EDGE.get(), p_155229_, p_155230_);
    }

    private final String frontIndexTag = "frontIndex";
    public static final ModelProperty<Integer> FRONT_INDEX = new ModelProperty<>();
    private int frontIndex = -1;
    private final String corePosTag = "corePos";
    private BlockPos corePos;

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
        setChanged();
    }
    public BlockPos getCorePos() {
        return corePos;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (corePos == null || level == null)
            return super.getCapability(cap, side);
        if (level.getBlockEntity(corePos) instanceof OvenCoreBlockEntity ovenCore)
            return ovenCore.getCapability(cap, side);
        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(FRONT_INDEX, this.frontIndex).build();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.corePos == null)
            return;
        tag.put(corePosTag, NbtUtils.writeBlockPos(this.corePos));
        tag.putInt(frontIndexTag, frontIndex);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (!tag.contains(corePosTag))
            return;
        this.corePos = NbtUtils.readBlockPos(tag.getCompound(corePosTag));
        this.frontIndex = tag.getInt(frontIndexTag);
        if (level == null || !level.isClientSide)
            return;
        requestModelDataUpdate();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
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
