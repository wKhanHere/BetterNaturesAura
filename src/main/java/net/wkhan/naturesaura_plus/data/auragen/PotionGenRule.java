package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public record PotionGenRule(
        MobEffect potion,
        int flatAmplifier,
        int finalScale,
        int flatAmplifierScale,
        List<MobEffect> incompatibleEffects,
        boolean doAmplifierScaling,
        boolean doDurationScaling
) {
    public static final Codec<PotionGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("potion").forGetter(PotionGenRule::potion),
                    Codec.INT.optionalFieldOf("flat_amplifier_scale", 7).forGetter(PotionGenRule::flatAmplifierScale),
                    Codec.INT.optionalFieldOf("flat_duration_scale", 4).forGetter(PotionGenRule::finalScale),
                    Codec.INT.optionalFieldOf("flat_amplifier", 1).forGetter(PotionGenRule::flatAmplifier),
                    ForgeRegistries.MOB_EFFECTS.getCodec().listOf()
                            .optionalFieldOf("incompatible_effects", new ArrayList<>()).forGetter(PotionGenRule::incompatibleEffects),
                    Codec.BOOL.optionalFieldOf("do_amplifier_scaling", true).forGetter(PotionGenRule::doAmplifierScaling),
                    Codec.BOOL.optionalFieldOf("do_duration_scaling", true).forGetter(PotionGenRule::doDurationScaling)
            ).apply(instance, PotionGenRule::new)
    );
}
