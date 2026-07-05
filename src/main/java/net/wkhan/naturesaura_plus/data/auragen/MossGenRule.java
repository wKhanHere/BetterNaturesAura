package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record MossGenRule(
        Either<Block, TagKey<Block>> blockInputId,
        Block blockOutputId,
        int auraAmount
) {

    public static final Codec<MossGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK)
                            .fieldOf("block_to_convert").forGetter(MossGenRule::blockInputId),
                    ForgeRegistries.BLOCKS.getCodec().fieldOf("block_result").forGetter(MossGenRule::blockOutputId),
                    Codec.INT.fieldOf("aura_gain").forGetter(MossGenRule::auraAmount)
            ).apply(instance, MossGenRule::new)
    );

    public boolean isTag() {
        return this.blockInputId.right().isPresent();
    }

    public Block getBlockInput() {
        return this.blockInputId.left().orElse(null);
    }

    public TagKey<Block> getBlockInputTag() {
        return this.blockInputId.right().orElse(null);
    }

    public Block getBlockOutput() {
        return this.blockOutputId;
    }

}
