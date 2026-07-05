package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.wkhan.naturesaura_plus.data.duckfaces.AbstractWoodStand;
import net.wkhan.naturesaura_plus.data.duckfaces.MultiBlockUtil;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin extends Feature<TreeConfiguration> {
    public TreeFeatureMixin(Codec<TreeConfiguration> p_65786_) {
        super(p_65786_);
    }

    @WrapOperation(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/TreeFeature;doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer$FoliageSetter;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)Z"
            )
    )
    private boolean naturesaura_plus$takeTreeCache(TreeFeature instance, WorldGenLevel worldGenLevel, RandomSource randomSource,
                                                   BlockPos saplingPos, BiConsumer<BlockPos, BlockState> rootSetter,
                                                   BiConsumer<BlockPos, BlockState> trunkSetter, FoliagePlacer.FoliageSetter foliageSetter,
                                                   TreeConfiguration config, Operation<Boolean> original) {
        if (!(worldGenLevel instanceof net.minecraft.server.level.ServerLevel))
            return original.call(instance, worldGenLevel, randomSource, saplingPos, rootSetter, trunkSetter, foliageSetter, config);
        ((MultiBlockUtil) Multiblocks.TREE_RITUAL).naturesaura_plus$allowAirInRitual();
        boolean isRitual = Multiblocks.TREE_RITUAL.isComplete(worldGenLevel.getLevel(), saplingPos);

        if (!isRitual)
            return original.call(instance, worldGenLevel, randomSource, saplingPos, rootSetter, trunkSetter, foliageSetter, config);

        Set<BlockPos> capturedStem = new HashSet<>();
        Set<BlockPos> capturedLeaves = new HashSet<>();
        Set<BlockPos> capturedDecorators = new HashSet<>();

        BiConsumer<BlockPos, BlockState> wrappedTrunk = (p, s) -> {
            if (s.is(ModTags.Blocks.TREE_RITUAL_STEMS))
                capturedStem.add(p.immutable());
            else if (s.is(ModTags.Blocks.TREE_RITUAL_LEAVES))
                capturedLeaves.add(p.immutable());
            else
                capturedDecorators.add(p.immutable());
            trunkSetter.accept(p, s);
        };

        FoliagePlacer.FoliageSetter wrappedFoliage = new FoliagePlacer.FoliageSetter() {
            @Override
            public void set(BlockPos p, BlockState s) {
                if (s.is(ModTags.Blocks.TREE_RITUAL_STEMS))
                    capturedStem.add(p.immutable());
                else if (s.is(ModTags.Blocks.TREE_RITUAL_LEAVES))
                    capturedLeaves.add(p.immutable());
                else
                    capturedDecorators.add(p.immutable());
                foliageSetter.set(p, s);
            }

            @Override
            public boolean isSet(BlockPos p) {
                return foliageSetter.isSet(p);
            }
        };

        boolean grewSuccessfully = original.call(instance, worldGenLevel, randomSource, saplingPos, rootSetter, wrappedTrunk, wrappedFoliage, config);

        if(!grewSuccessfully || capturedStem.isEmpty()) return grewSuccessfully;

        Multiblocks.TREE_RITUAL.forEach(saplingPos, 'W', (standPos, matcher) -> {
            BlockEntity tile = worldGenLevel.getBlockEntity(standPos);
            if (tile instanceof AbstractWoodStand woodStand) {
                woodStand.naturesaura_plus$setTreeStemCache(capturedStem.isEmpty() ? null : capturedStem);
                woodStand.naturesaura_plus$setTreeLeafCache(capturedLeaves.isEmpty() ? null : capturedLeaves);
                woodStand.naturesaura_plus$setTreeDecoratorCache(capturedDecorators.isEmpty() ? null : capturedDecorators);
            }
            return true;
        });
        return true;
    }
}
