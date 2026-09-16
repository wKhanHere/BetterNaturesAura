package net.wkhan.naturesaura_plus.common.block;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import net.wkhan.naturesaura_plus.common.block.blockentity.ModBlockEntities;
import net.wkhan.naturesaura_plus.common.block.blockentity.oven.OvenCoreBlockEntity;
import net.wkhan.naturesaura_plus.common.block.blockentity.oven.OvenEdgeBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.wkhan.naturesaura_plus.common.block.blockentity.ModBlockEntities.OVEN_CORE;
import static net.wkhan.naturesaura_plus.common.block.blockentity.ModBlockEntities.OVEN_EDGE;
import static net.wkhan.naturesaura_plus.common.tag.ModTags.Items.OVEN_HAMMER;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.*;

public class OvenPartBlock extends Block implements EntityBlock {
    public OvenPartBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(OVEN_PART, OvenPart.BASE));
    }

    public static final DirectionProperty FACING_DIRECTION = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<OvenPart> OVEN_PART = EnumProperty.create("part", OvenPart.class);
    public enum OvenPart implements StringRepresentable { //todo: add progressing states for progressing texture (or dont and use IDynamicBakedModel)
        BASE, CORE, EDGE,
        FRONT_0, FRONT_1, FRONT_2,
        FRONT_3, FRONT_4, FRONT_5,
        FRONT_6, FRONT_7, FRONT_8;

        @Override public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OVEN_PART, FACING_DIRECTION);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return switch (state.getValue(OVEN_PART)) {
            case CORE -> OVEN_CORE.get().create(pos, state);
            case BASE -> null;
            default -> OVEN_EDGE.get().create(pos, state);
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide() || state.getValue(OVEN_PART) != OvenPart.CORE)
            return null;
        return type == ModBlockEntities.OVEN_CORE.get() ?
                (lvl, pos, st, blockEntity) -> OvenCoreBlockEntity.tick(lvl, (OvenCoreBlockEntity) blockEntity, pos, st) : null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile == null)
            return checkAndBuildMulti(level, pos, player, hand, hit) ? InteractionResult.CONSUME : InteractionResult.PASS;

        LazyOptional<IFluidHandler> fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection());
        if (ALLOW_FLUID_EXTRACTION_BY_BUCKET.get() && fluidHandler.isPresent() && fluidHandler.resolve().isPresent() &&
                FluidUtil.interactWithFluidHandler(player, hand, fluidHandler.resolve().get())) {
            if (tile instanceof OvenEdgeBlockEntity edge && level.getBlockEntity(edge.getCorePos()) instanceof OvenCoreBlockEntity ovenCore) {
                ovenCore.setChanged();
                return InteractionResult.SUCCESS;
            }
            tile.setChanged();
            return InteractionResult.SUCCESS;
        }
        if (tile instanceof OvenCoreBlockEntity core) {
            if (!level.isClientSide)
                NetworkHooks.openScreen((ServerPlayer) player, core, pos);
            return InteractionResult.SUCCESS;
        }
        if (!(tile instanceof OvenEdgeBlockEntity edge))
            return InteractionResult.PASS;
        OvenCoreBlockEntity ovenCore = (OvenCoreBlockEntity) level.getBlockEntity(edge.getCorePos());
        if (ovenCore != null && ovenCore.multiblockData != null) {
            if (!level.isClientSide)
                NetworkHooks.openScreen((ServerPlayer) player, ovenCore, edge.getCorePos());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.equals(newState))
            return;
        OvenPart currentPart = state.getValue(OVEN_PART);
        boolean isSameBlock = state.is(newState.getBlock());
        OvenPart newPart = isSameBlock ? newState.getValue(OVEN_PART) : OvenPart.BASE;

        BlockPos corePos = null;
        BlockEntity ovenPart = level.getBlockEntity(pos);

        if (isSameBlock && newPart == OvenPart.BASE && currentPart != OvenPart.BASE) {
            if (ovenPart != null)
                ovenPart.setRemoved();
            level.removeBlockEntity(pos);
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }
        if (isSameBlock || currentPart == OvenPart.BASE) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        if (ovenPart instanceof OvenEdgeBlockEntity ovenEdgePart)
            corePos = ovenEdgePart.getCorePos();
        else if (ovenPart instanceof OvenCoreBlockEntity)
            corePos = pos;
        if (corePos == null) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        if (level.getBlockEntity(corePos) instanceof OvenCoreBlockEntity ovenCore)
            ovenCore.dropInventory(level, corePos);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos partPos = corePos.offset(i, j, k);
                    if (partPos.equals(pos))
                        continue;
                    BlockState partState = level.getBlockState(partPos);
                    if (!(partState.getBlock() instanceof OvenPartBlock) || partState.getValue(OVEN_PART) == OvenPart.BASE)
                        continue;
                    BlockEntity ovenPartEntity = level.getBlockEntity(partPos);
                    if (ovenPartEntity != null)
                        ovenPartEntity.setRemoved();
                    level.setBlock(partPos, partState.setValue(OVEN_PART, OvenPart.BASE), 3);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private boolean checkAndBuildMulti(Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level == null || level.isClientSide())
            return false;
        if (!(level.getBlockState(pos).getBlock() instanceof OvenPartBlock))
            return false;

        BlockPos corePos = new BlockPos(pos);
        List<BlockPos> ovenPartBlocks = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (!(level.getBlockState(pos.relative(direction)).getBlock() instanceof OvenPartBlock))
                continue;
            corePos = corePos.relative(direction);
        }

        Direction playerOppositeDir = hitResult == null || hitResult.getDirection().getAxis().isVertical()
                ? player.getDirection().getOpposite() : hitResult.getDirection();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos partPos = new BlockPos(corePos.getX() + i, corePos.getY() + j, corePos.getZ() + k);
                    BlockState partState = level.getBlockState(partPos);
                    if (!(partState.getBlock() instanceof OvenPartBlock) || partState.getValue(OVEN_PART) != OvenPart.BASE)
                        //this does mean you cant form the oven if other occupied bricks are touching this non-occupied one

                        //if you wanna add decorators make this a tag check and
                        // add a new list which contains oven blocks to be converted to tiles
                        //however, that might mess up with the lazy core pos evaluation we have
                        return false;
                    if (i == 0 && j == 0 && k == 0)
                        continue;
                    ovenPartBlocks.add(partPos);
                }
            }
        }

        ItemStack stack = player.getItemInHand(hand);
        Optional<IAuraContainer> auraCapability = stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER).resolve();
        if (IF_AURA_CONSUME_FOR_OVEN_CREATION.get()) {
            if (auraCapability.isEmpty())
                return false;
            if (auraCapability.get().drainAura(AURA_CONSUME_FOR_OVEN_CREATION.get(),true) != AURA_CONSUME_FOR_OVEN_CREATION.get())
                return false;
            auraCapability.get().drainAura(AURA_CONSUME_FOR_OVEN_CREATION.get(),false);
        }
        else if (!player.getItemInHand(hand).is(OVEN_HAMMER))
            return false;
        if (ITEM_DURABILITY_CONSUMED_ON_AURA_OVEN_FORM.get() > 0 && stack.isDamageableItem())
            stack.hurtAndBreak(ITEM_DURABILITY_CONSUMED_ON_AURA_OVEN_FORM.get(), (ServerPlayer) player, (p) -> p.broadcastBreakEvent(hand));
        else if (AMOUNT_ITEM_CONSUMED_ON_AURA_OVEN_FORM.get() > 0 && !stack.isEmpty() && stack.getCount() >= AMOUNT_ITEM_CONSUMED_ON_AURA_OVEN_FORM.get())
            stack.shrink(AMOUNT_ITEM_CONSUMED_ON_AURA_OVEN_FORM.get());

        level.setBlock(corePos, level.getBlockState(corePos).setValue(OvenPartBlock.OVEN_PART, OvenPart.CORE), 3);
        for (BlockPos ovenPartBlockPos : ovenPartBlocks) {
            int faceIndex = getFaceIndex(ovenPartBlockPos, corePos, playerOppositeDir);
            BlockState partState = level.getBlockState(ovenPartBlockPos);
            if (faceIndex != -1)
                level.setBlock(ovenPartBlockPos, partState.setValue(OVEN_PART, OvenPart.valueOf("FRONT_" + faceIndex))
                        .setValue(FACING_DIRECTION, playerOppositeDir), 3);
            else
                level.setBlock(ovenPartBlockPos, partState.setValue(OVEN_PART, OvenPart.EDGE)
                        .setValue(FACING_DIRECTION, playerOppositeDir), 3);
            OvenEdgeBlockEntity ovenEdge = (OvenEdgeBlockEntity) level.getBlockEntity(ovenPartBlockPos);
            ovenEdge.setCorePos(corePos);
        }
        return true;
    }

    private static int getFaceIndex(BlockPos ovenPartBlockPos, BlockPos corePos, Direction playerOppositeDir) {
        int i = ovenPartBlockPos.getX() - corePos.getX();
        int j = ovenPartBlockPos.getY() - corePos.getY();
        int k = ovenPartBlockPos.getZ() - corePos.getZ();

        int faceIndex = -1;
        if ((playerOppositeDir.getAxis() == Direction.Axis.X && i == playerOppositeDir.getStepX()) ||
                (playerOppositeDir.getAxis() == Direction.Axis.Z && k == playerOppositeDir.getStepZ())) {
            int row = 1 - j;
            int col = 1;
            switch (playerOppositeDir) {
                case NORTH -> col = 1 - i;
                case SOUTH -> col = i + 1;
                case WEST  -> col = k + 1;
                case EAST  -> col = 1 - k;
            }
            faceIndex = (row * 3) + col;
        }
        return faceIndex;
    }
}
