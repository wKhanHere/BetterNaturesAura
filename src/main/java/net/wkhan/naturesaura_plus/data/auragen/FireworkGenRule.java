package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record FireworkGenRule(
        float explosionFlickerFactor,
        float explosionTrailFactor,
        List<Float> explosionTypesListFactor,
        float explosionColorFactor,
        int flightTimeScale,
        int flatReleaseTimer,
        float finalScale,
        boolean doFlightTimeScaling
) {

    private static final List<Float> defaultTypesValues = List.of(0.0F, 1.0F, 0.5F, 20.0F, 0.5F);

    public static final Codec<FireworkGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("flicker_factor", 1F).forGetter(FireworkGenRule::explosionFlickerFactor),
                    Codec.FLOAT.optionalFieldOf("trail_factor", 8F).forGetter(FireworkGenRule::explosionTrailFactor),
                    Codec.FLOAT.listOf().optionalFieldOf("explosion_type_factors", defaultTypesValues)
                            .forGetter(FireworkGenRule::explosionTypesListFactor),
                    Codec.FLOAT.optionalFieldOf("color_factor", 0.75F).forGetter(FireworkGenRule::explosionColorFactor),
                    Codec.INT.optionalFieldOf("flight_time_scale", 15).forGetter(FireworkGenRule::flightTimeScale),
                    Codec.INT.optionalFieldOf("flat_release_timer", 40).forGetter(FireworkGenRule::flatReleaseTimer),
                    Codec.FLOAT.optionalFieldOf("final_scale", 10000.0F).forGetter(FireworkGenRule::finalScale),
                    Codec.BOOL.optionalFieldOf("scale_timer_to_flight_time", false).forGetter(FireworkGenRule::doFlightTimeScaling)
            ).apply(instance, FireworkGenRule::new)
    );
}
