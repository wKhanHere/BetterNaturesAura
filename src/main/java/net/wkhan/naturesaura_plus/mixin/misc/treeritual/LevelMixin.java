package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.wkhan.naturesaura_plus.data.trackers.TreeRitualTreeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static net.wkhan.naturesaura_plus.common.tag.ModTags.Blocks.*;

@Mixin(Level.class)
public abstract class LevelMixin extends CapabilityProvider<Level> {
    protected LevelMixin(Class<Level> baseClass) {
        super(baseClass);
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD")
    )
    private void naturesaura_plus$getPlacedBlock(BlockPos pos, BlockState state, int p_46607_, int p_46608_, CallbackInfoReturnable<Boolean> cir) {
        Set<BlockPos> stems = TreeRitualTreeTracker.STEM_CACHE.get();
        if (stems == null)
            return;

        Set<BlockPos> leaves = TreeRitualTreeTracker.LEAF_CACHE.get();
        if (leaves == null)
            return;

        Set<BlockPos> decorators = TreeRitualTreeTracker.DECORATOR_CACHE.get();
        if (decorators == null)
            return;

        if (state.is(EXCLUDE_IN_TREE_RITUAL_CLEANUP))
            return;

        BlockPos immutablePos = pos.immutable();

        if (state.is(TREE_RITUAL_STEMS)) {
            stems.add(immutablePos);
            leaves.remove(immutablePos);
            decorators.remove(immutablePos);
        }
        else if (state.is(TREE_RITUAL_LEAVES)) {
            leaves.add(immutablePos);
            stems.remove(immutablePos);
            decorators.remove(immutablePos);
        }
        else {
            decorators.add(immutablePos);
            stems.remove(immutablePos);
            leaves.remove(immutablePos);
        }
    }
}
