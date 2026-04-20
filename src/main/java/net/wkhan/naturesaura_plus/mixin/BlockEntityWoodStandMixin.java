package net.wkhan.naturesaura_plus.mixin;

import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(BlockEntityWoodStand.class)
public abstract class BlockEntityWoodStandMixin extends BlockEntityImpl{
    public BlockEntityWoodStandMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private Set<BlockPos> naturesaura_plus$treeCache = null;

    @Unique
    private void naturesaura_plus$abortRitual() {
        this.ritualPos = null;
        this.recipe = null;
        this.timer = 0;
        this.naturesaura_plus$treeCache = null;
    }

    @Shadow private BlockPos ritualPos;
    @Shadow private TreeRitualRecipe recipe;
    @Shadow private int timer;

    @Shadow private boolean isRitualOkay() {return false;}//I swear this is to make intelliJ shut up

    @Inject(
            method = "isRitualOkay",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$RitualCheck(CallbackInfoReturnable<Boolean> cir) {
        Level level = this.getLevel();
        if (!Multiblocks.TREE_RITUAL.isComplete(level, this.ritualPos)) {
            cir.setReturnValue(false);
            return;
        }

        if (this.naturesaura_plus$treeCache == null) {
            this.naturesaura_plus$treeCache = NaturesAuraPlusUtils.crawlConnectedBlocks(
                    level, this.ritualPos, 500,
                    state -> state.is(ModTags.Blocks.TREE_RITUAL_STEMS)
            );
            if (this.naturesaura_plus$treeCache.isEmpty()) {
                cir.setReturnValue(false);
                return;
            }
        }
        else {
            for (BlockPos pos : this.naturesaura_plus$treeCache) {
                BlockState state = level.getBlockState(pos);
                if (state.is(ModTags.Blocks.TREE_RITUAL_STEMS)) {
                    continue;
                }
                this.naturesaura_plus$treeCache = null;
                cir.setReturnValue(false);
                return;
            }
        }

        //Most of this entire item validation is straight from the base class of BlockEntityWoodStand, so credit is to Ellpeck.
        List<Ingredient> required = new ArrayList(Arrays.asList(this.recipe.ingredients));
        boolean fine = Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
            BlockEntity tile = this.level.getBlockEntity(pos);
            if (tile instanceof BlockEntityWoodStand) {
                ItemStack stack = ((BlockEntityWoodStand)tile).items.getStackInSlot(0);
                if (stack.isEmpty() || stack.getItem() == Items.AIR) return true;
                for (int i = required.size() - 1; i >= 0; --i) {
                    Ingredient req = required.get(i);
                    if (req.test(stack)) {
                        required.remove(i);
                        return true;
                    }
                }
                    return false;
                }
            return true;
        });
        cir.setReturnValue(fine && required.isEmpty());
    }

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$RitualTick(CallbackInfo ci) {
        Level level = this.getLevel();
        if (level.isClientSide || this.ritualPos == null || this.recipe == null || level.getGameTime() % 5L != 0L) {
            return;
        }
        ci.cancel();

        if (!this.isRitualOkay()) {
            naturesaura_plus$abortRitual();
            return;
        }

        this.timer += 5;
        boolean isOverHalf = this.timer >= this.recipe.time / 2;
        if (!isOverHalf) {
            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                BlockEntity tile = this.level.getBlockEntity(pos);
                if (tile instanceof BlockEntityWoodStand && !((BlockEntityWoodStand)tile).items.getStackInSlot(0).isEmpty()) {
                    PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                            new PacketParticleStream((float)pos.getX() + 0.2F + this.level.random.nextFloat() * 0.6F,
                                    (float)pos.getY() + 0.85F,
                                    (float)pos.getZ() + 0.2F + this.level.random.nextFloat() * 0.6F,
                                    (float)this.ritualPos.getX() + 0.5F,
                                    (float)this.ritualPos.getY() + this.level.random.nextFloat() * 3.0F + 2.0F,
                                    (float)this.ritualPos.getZ() + 0.5F,
                                    this.level.random.nextFloat() * 0.04F + 0.04F, 9030711,
                                    this.level.random.nextFloat() + 1.0F));
                }
                return true;
            });
        }

        PacketHandler.sendToAllAround(this.level, this.ritualPos, 32, new PacketParticles((float)this.ritualPos.getX(), (float)this.ritualPos.getY(), (float)this.ritualPos.getZ(), PacketParticles.Type.TR_GOLD_POWDER, new int[0]));
        if (this.timer >= this.recipe.time) {
            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'G', (pos, matcher) -> {
                this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                return true;
            });

            if (this.naturesaura_plus$treeCache != null) {

                Set<BlockPos> leavesToHarvest = new HashSet<>();
                Queue<BlockPos> queue = new LinkedList<>(this.naturesaura_plus$treeCache);
                Set<BlockPos> visited = new HashSet<>(this.naturesaura_plus$treeCache);

                while (!queue.isEmpty() && leavesToHarvest.size() < 1500) {
                    BlockPos current = queue.poll();

                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = current.relative(dir);

                        if (visited.contains(neighbor)) continue;

                        visited.add(neighbor);
                        BlockState neighborState = level.getBlockState(neighbor);

                        if (neighborState.is(ModTags.Blocks.TREE_RITUAL_LEAVES)) {
                            leavesToHarvest.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }

                for (BlockPos leafPos : leavesToHarvest) {
                    level.setBlockAndUpdate(leafPos, Blocks.AIR.defaultBlockState());
                    PacketHandler.sendToAllAround(level, leafPos, 32, new PacketParticles((float) leafPos.getX(), (float) leafPos.getY(), (float) leafPos.getZ(), PacketParticles.Type.TR_DISAPPEAR, new int[0]));
                }
                for (BlockPos logPos : this.naturesaura_plus$treeCache) {
                    level.setBlockAndUpdate(logPos, Blocks.AIR.defaultBlockState());
                    PacketHandler.sendToAllAround(level, logPos, 32, new PacketParticles((float) logPos.getX(), (float) logPos.getY(), (float) logPos.getZ(), PacketParticles.Type.TR_DISAPPEAR, new int[0]));
                }
            }

            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                BlockEntity tile = this.level.getBlockEntity(pos);
                if (tile instanceof BlockEntityWoodStand stand) {
                    if (!stand.items.getStackInSlot(0).isEmpty()) {
                        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) stand.getBlockPos().getX(), (float) stand.getBlockPos().getY(), (float) stand.getBlockPos().getZ(), PacketParticles.Type.TR_CONSUME_ITEM, new int[0]));
                        this.level.playSound((Player) null, (double) stand.getBlockPos().getX() + (double) 0.5F, (double) stand.getBlockPos().getY() + (double) 0.5F, (double) stand.getBlockPos().getZ() + (double) 0.5F, SoundEvents.WOOD_STEP, SoundSource.BLOCKS, 0.5F, 1.0F);
                        stand.items.setStackInSlot(0, ItemStack.EMPTY);
                        stand.sendToClients();
                    }
                }
                return true;
            });


            ItemEntity item = new ItemEntity(this.level, (double) this.ritualPos.getX() + (double) 0.5F, (double) this.ritualPos.getY() + (double) 4.5F, (double) this.ritualPos.getZ() + (double) 0.5F, this.recipe.result.copy());
            this.level.addFreshEntity(item);
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.TR_SPAWN_RESULT, new int[0]));
            this.level.playSound((Player) null, (double) this.worldPosition.getX() + (double) 0.5F, (double) this.worldPosition.getY() + (double) 0.5F, (double) this.worldPosition.getZ() + (double) 0.5F, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.65F, 1.0F);
            this.ritualPos = null;
            this.recipe = null;
            this.timer = 0;
        }
//         else if (isOverHalf && !wasOverHalf) {
//            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
//                BlockEntity tile = this.level.getBlockEntity(pos);
//                if (tile instanceof BlockEntityWoodStand stand) {
//                    if (!stand.items.getStackInSlot(0).isEmpty()) {
//                        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float) stand.getBlockPos().getX(), (float) stand.getBlockPos().getY(), (float) stand.getBlockPos().getZ(), PacketParticles.Type.TR_CONSUME_ITEM, new int[0]));
//                        this.level.playSound((Player)null, (double) stand.getBlockPos().getX() + (double)0.5F, (double) stand.getBlockPos().getY() + (double)0.5F, (double) stand.getBlockPos().getZ() + (double)0.5F, SoundEvents.WOOD_STEP, SoundSource.BLOCKS, 0.5F, 1.0F);
//                        stand.sendToClients();
//                    }
//                }
//
//                return true;
//            });
//        }

    }

}


