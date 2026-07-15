package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.gen.LevelGenAncientTree;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.updateWoodStandMemoryIfRitual;

@Mixin(LevelGenAncientTree.class)
public abstract class AncientTreeFeatureMixin extends Feature<NoneFeatureConfiguration> {
    public AncientTreeFeatureMixin(Codec<NoneFeatureConfiguration> p_65786_) {
        super(p_65786_);
    }

    @WrapMethod(
            method = "place",
            remap = false
    )
    private boolean naturesaura_plus$getAncientTreeBlocks(FeaturePlaceContext<NoneFeatureConfiguration> ctx, Operation<Boolean> original) {
        return updateWoodStandMemoryIfRitual(
                ctx.level().getLevel(),
                ctx.origin(),
                () -> original.call(ctx),
                result -> result
        );
    }
}
