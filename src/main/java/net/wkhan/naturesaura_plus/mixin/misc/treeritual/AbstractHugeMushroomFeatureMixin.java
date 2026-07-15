package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.updateWoodStandMemoryIfRitual;

@Mixin(AbstractHugeMushroomFeature.class)
public abstract class AbstractHugeMushroomFeatureMixin extends Feature<HugeMushroomFeatureConfiguration> {
    public AbstractHugeMushroomFeatureMixin(Codec<HugeMushroomFeatureConfiguration> p_65786_) {
        super(p_65786_);
    }

    @WrapMethod(method = "place")
    private boolean naturesaura_plus$takeMushroomCache(
            FeaturePlaceContext<HugeMushroomFeatureConfiguration> context, Operation<Boolean> original) {
        return updateWoodStandMemoryIfRitual(
                context.level().getLevel(), context.origin(),
                () -> original.call(context),
                result -> result
        );
    }
}
