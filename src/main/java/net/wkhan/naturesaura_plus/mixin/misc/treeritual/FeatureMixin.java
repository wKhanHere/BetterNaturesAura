package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.wkhan.naturesaura_plus.data.TreeRitualTreeTracker;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Feature.class)
public abstract class FeatureMixin {

    @Inject(
            method = "setBlock",
            at = @At("HEAD")
    )
    private void naturesaura_plus$captureFeatureBlocks(LevelWriter level, BlockPos pos, BlockState state, CallbackInfo ci) {
        Set<BlockPos> stems = TreeRitualTreeTracker.STEM_CACHE.get();
        if (stems == null)
            return;

        Set<BlockPos> caps = TreeRitualTreeTracker.LEAF_CACHE.get();
        if (caps == null)
            return;

        // 2. We ARE in a ritual! Sort the blocks based on your tags.
        if (state.is(ModTags.Blocks.TREE_RITUAL_STEMS))
            stems.add(pos.immutable());
        else if (state.is(ModTags.Blocks.TREE_RITUAL_LEAVES))
            caps.add(pos.immutable());
    }
}
