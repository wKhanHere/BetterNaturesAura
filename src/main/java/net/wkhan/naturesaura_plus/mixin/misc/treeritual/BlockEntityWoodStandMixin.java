package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.data.duckfaces.AbstractWoodStand;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.wkhan.naturesaura_plus.client.render.DynamicWoodStandModel.STAND_MATERIAL;

@Mixin(BlockEntityWoodStand.class)
public abstract class BlockEntityWoodStandMixin extends BlockEntityImpl implements AbstractWoodStand {
    public BlockEntityWoodStandMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private BlockState naturesaura_plus$standMaterial;
    @Unique private Set<BlockPos> naturesaura_plus$treeCache = null;
    @Unique private Set<BlockPos> naturesaura_plus$treeCacheLeaf = null;
    @Unique private Set<BlockPos> naturesaura_plus$treeCacheDecorator = null;

    @Unique
    private void naturesaura_plus$abortRitual() {
        this.ritualPos = null;
        this.recipe = null;
        this.timer = 0;
        this.naturesaura_plus$treeCache = null;
        this.naturesaura_plus$treeCacheLeaf = null;
    }

    @Shadow(remap = false) private BlockPos ritualPos;
    @Shadow(remap = false) private TreeRitualRecipe recipe;
    @Shadow(remap = false) private int timer;

    @Shadow(remap = false) private boolean isRitualOkay() {return false;}//I swear this is to make intelliJ shut up

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

        if (this.naturesaura_plus$treeCache == null || this.naturesaura_plus$treeCache.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        for (BlockPos pos : this.naturesaura_plus$treeCache) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ModTags.Blocks.TREE_RITUAL_STEMS))
                continue;
            cir.setReturnValue(false);
            return;
        }

        if (this.naturesaura_plus$treeCacheLeaf != null) {
            for (BlockPos pos : this.naturesaura_plus$treeCacheLeaf) {
                BlockState state = level.getBlockState(pos);
                if (state.is(ModTags.Blocks.TREE_RITUAL_LEAVES))
                    continue;
                cir.setReturnValue(false);
                return;
            }
        }

        //Most of this entire item validation is straight from the base class of BlockEntityWoodStand, so credit is to Ellpeck.
        List<Ingredient> required = new ArrayList<>(Arrays.asList(this.recipe.ingredients));
        boolean fine = Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof BlockEntityWoodStand woodStand) {
                ItemStack stack = woodStand.items.getStackInSlot(0);
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

    //Most of this tick function other than the leaves clearing and some restructuring is straight from the base class of BlockEntityWoodStand, so credit is to Ellpeck.
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$RitualTick(CallbackInfo ci) {
        ci.cancel();
        Level level = this.getLevel();
        if (level.isClientSide() || this.ritualPos == null || this.recipe == null || level.getGameTime() % 5L != 0L) {
            return;
        }

        if (!this.isRitualOkay()) {
            naturesaura_plus$abortRitual();
            return;
        }

        this.timer += 5;
        boolean isOverHalf = this.timer >= this.recipe.time / 2;
        if (!isOverHalf) {
            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                BlockEntity tile = level.getBlockEntity(pos);
                if (!(tile instanceof BlockEntityWoodStand stand) || stand.items.getStackInSlot(0).isEmpty()) return true;
                PacketHandler.sendToAllAround(level, this.worldPosition, 32,
                        new PacketParticleStream((float)pos.getX() + 0.2F + level.random.nextFloat() * 0.6F,
                                (float)pos.getY() + 0.85F,
                                (float)pos.getZ() + 0.2F + level.random.nextFloat() * 0.6F,
                                (float)this.ritualPos.getX() + 0.5F,
                                (float)this.ritualPos.getY() + level.random.nextFloat() * 3.0F + 2.0F,
                                (float)this.ritualPos.getZ() + 0.5F,
                                level.random.nextFloat() * 0.04F + 0.04F, 9030711,
                                level.random.nextFloat() + 1.0F));
                return true;
            });
        }

        PacketHandler.sendToAllAround(level, this.ritualPos, 32, new PacketParticles((float)this.ritualPos.getX(), (float)this.ritualPos.getY(), (float)this.ritualPos.getZ(), PacketParticles.Type.TR_GOLD_POWDER));
        if (this.timer >= this.recipe.time) {
            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'G', (pos, matcher) -> {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                return true;
            });

            if (this.naturesaura_plus$treeCache != null)
                for (BlockPos logPos : this.naturesaura_plus$treeCache) {
                    level.setBlockAndUpdate(logPos, Blocks.AIR.defaultBlockState());
                    PacketHandler.sendToAllAround(level, logPos, 32, new PacketParticles((float) logPos.getX(), (float) logPos.getY(), (float) logPos.getZ(), PacketParticles.Type.TR_DISAPPEAR));
                }

            if (this.naturesaura_plus$treeCacheLeaf != null)
                for (BlockPos leafPos : this.naturesaura_plus$treeCacheLeaf) {
                    level.setBlockAndUpdate(leafPos, Blocks.AIR.defaultBlockState());
                    PacketHandler.sendToAllAround(level, leafPos, 32, new PacketParticles((float) leafPos.getX(), (float) leafPos.getY(), (float) leafPos.getZ(), PacketParticles.Type.TR_DISAPPEAR));
                }

            if (this.naturesaura_plus$treeCacheDecorator != null) {
                for (BlockPos leafPos : this.naturesaura_plus$treeCacheDecorator) {
                    level.setBlockAndUpdate(leafPos, Blocks.AIR.defaultBlockState());
                    PacketHandler.sendToAllAround(level, leafPos, 32, new PacketParticles((float) leafPos.getX(), (float) leafPos.getY(), (float) leafPos.getZ(), PacketParticles.Type.TR_DISAPPEAR));
                }
            }

            Multiblocks.TREE_RITUAL.forEach(this.ritualPos, 'W', (pos, matcher) -> {
                BlockEntity tile = level.getBlockEntity(pos);
                if (tile instanceof BlockEntityWoodStand stand) {
                    if (!stand.items.getStackInSlot(0).isEmpty()) {
                        PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticles((float) stand.getBlockPos().getX(), (float) stand.getBlockPos().getY(), (float) stand.getBlockPos().getZ(), PacketParticles.Type.TR_CONSUME_ITEM));
                        level.playSound(null, (double) stand.getBlockPos().getX() + (double) 0.5F, (double) stand.getBlockPos().getY() + (double) 0.5F, (double) stand.getBlockPos().getZ() + (double) 0.5F, SoundEvents.WOOD_STEP, SoundSource.BLOCKS, 0.5F, 1.0F);
                        stand.items.setStackInSlot(0, ItemStack.EMPTY);
                        stand.sendToClients();
                    }
                }
                return true;
            });

            ItemEntity item = new ItemEntity(level,
                    (double) this.ritualPos.getX() + (double) 0.5F,
                    (double) this.ritualPos.getY() + (double) 4.5F,
                    (double) this.ritualPos.getZ() + (double) 0.5F, this.recipe.result.copy());
            level.addFreshEntity(item);
            PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticles((float) item.getX(), (float) item.getY(), (float) item.getZ(), PacketParticles.Type.TR_SPAWN_RESULT));
            level.playSound(null, (double) this.worldPosition.getX() + (double) 0.5F,
                    (double) this.worldPosition.getY() + (double) 0.5F,
                    (double) this.worldPosition.getZ() + (double) 0.5F,
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.65F, 1.0F);
            naturesaura_plus$abortRitual();
        }
    }

    @Inject(
            method = "writeNBT",
            at = @At("TAIL"),
            remap = false
    )
    private void naturesaura_plus$writeNBTWoodType(CompoundTag compound, SaveType type, CallbackInfo ci) {
        if (type == SaveType.BLOCK) return;
        if (this.naturesaura_plus$standMaterial == null) this.naturesaura_plus$standMaterial = Blocks.AIR.defaultBlockState();
        ResourceLocation blockResourceLocation = ForgeRegistries.BLOCKS.getKey(this.naturesaura_plus$standMaterial.getBlock());
        if (blockResourceLocation == null) {
            compound.putString("render_material", Blocks.AIR.toString());
            return;
        }
        compound.putString("render_material", blockResourceLocation.toString());
    }

    @Inject(
            method = "readNBT",
            at = @At("TAIL"),
            remap = false
    )
    private void naturesaura_plus$readNBTWoodType(CompoundTag compound, SaveType type, CallbackInfo ci) {
        if (type == SaveType.BLOCK) return;
        if (!compound.contains("render_material")) {
            this.naturesaura_plus$standMaterial = Blocks.AIR.defaultBlockState();
            return;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse(compound.getString("render_material"));
        if (resourceLocation == null) {
            this.naturesaura_plus$standMaterial = Blocks.AIR.defaultBlockState();
            return;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);
        if (block == null) {
            this.naturesaura_plus$standMaterial = Blocks.AIR.defaultBlockState();
            return;
        }
        this.naturesaura_plus$standMaterial = block.defaultBlockState();

        Level level = this.getLevel();
        if(level != null && level.isClientSide())
            this.requestModelDataUpdate();
    }

    @Override
    public BlockState naturesaura_plus$getWoodStandMaterialBlockState() {
        return this.naturesaura_plus$standMaterial;
    }

    @Override
    public void naturesaura_plus$setWoodStandMaterialBlockState(BlockState material) {
        this.naturesaura_plus$standMaterial = material;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(STAND_MATERIAL, this.naturesaura_plus$standMaterial).build();
    }

    @Override
    public void naturesaura_plus$setTreeStemCache(Set<BlockPos> treeCache) {
        this.naturesaura_plus$treeCache = treeCache;
    }

    @Override
    public void naturesaura_plus$setTreeLeafCache(Set<BlockPos> treeCache) {
        this.naturesaura_plus$treeCacheLeaf = treeCache;
    }

    @Override
    public void naturesaura_plus$setTreeDecoratorCache(Set<BlockPos> treeCache) {
        this.naturesaura_plus$treeCacheDecorator = treeCache;
    }
}