package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record FlowerGenRule(
        Either<Block, TagKey<Block>> blockToConvertId,
        int auraAmount,
        byte lucidity,
        byte obscurity,
        float obscurityScale
) {

    public static final Codec<FlowerGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK)
                            .fieldOf("block_to_convert").forGetter(FlowerGenRule::blockToConvertId),
                    Codec.INT.fieldOf("aura_gain").forGetter(FlowerGenRule::auraAmount),
                    Codec.BYTE.optionalFieldOf("lucidity", (byte) 0).forGetter(FlowerGenRule::lucidity),
                    Codec.BYTE.fieldOf("obscurity").forGetter(FlowerGenRule::obscurity),
                    Codec.FLOAT.optionalFieldOf("obscurity_scale", 2F).forGetter(FlowerGenRule::obscurityScale)
            ).apply(instance, FlowerGenRule::new)
    );

    public boolean isTag() {
        return this.blockToConvertId.right().isPresent();
    }

    public Block getBlockInput() {
        return this.blockToConvertId.left().orElse(null);
    }

    public TagKey<Block> getBlockInputTag() {
        return this.blockToConvertId.right().orElse(null);
    }
}

