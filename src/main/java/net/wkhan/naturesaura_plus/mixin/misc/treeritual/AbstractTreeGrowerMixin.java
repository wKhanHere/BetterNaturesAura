package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.updateWoodStandMemoryIfRitual;

@Mixin(AbstractTreeGrower.class)
public abstract class AbstractTreeGrowerMixin {

    @WrapMethod(
            method = "growTree"
    )
    private boolean naturesaura_plus$getTreeBlocks(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state,
                                  RandomSource random, Operation<Boolean> original) {
        return updateWoodStandMemoryIfRitual(
                level, pos,
                () -> original.call(level, chunkGenerator, pos, state, random),
                result -> result
        );
    }
}
