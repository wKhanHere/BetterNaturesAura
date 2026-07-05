package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityMossGenerator;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import net.wkhan.naturesaura_plus.data.auragen.AuraGenRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.MOSS_GENERATIONS;
import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.mossGenMemorySize;
import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.mossGenRange;

@Mixin(BlockEntityMossGenerator.class)
public abstract class MossGenMixin extends BlockEntityImpl {
    public MossGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique
    private final NaturesAuraPlusUtils.circularBuffer<Block> naturesaura_plus$mossMemory = new NaturesAuraPlusUtils.circularBuffer<>(mossGenMemorySize) {};

    //Magic Particles straight from Elpeck's code.
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$mossAuraGenerator(CallbackInfo ci) {
        ci.cancel();

        if (this.level.isClientSide()) return;

        if (this.level.getGameTime() % 20L != 0L) {
            return;
        }

        LevelData data = (LevelData) ILevelData.getLevelData(this.level);
        List<BlockPos> possibleOffsets = new ArrayList<>();
        int range = mossGenRange;

        for(int x = -range; x <= range; ++x) {
            for(int y = -range; y <= range; ++y) {
                for(int z = -range; z <= range; ++z) {
                    BlockPos offset = this.worldPosition.offset(x, y, z);
                    boolean isRecent = data.recentlyConvertedMossStones.contains(offset);
                    Block block = this.level.getBlockState(offset).getBlock();
                    if (!(MOSS_GENERATIONS.containsKey(block))) continue;
                    if (naturesaura_plus$mossMemory.getRepeatAfterSimulatedPush(block,1) > 0) continue; //Maybe make this repeat check data driven too
                    if (isRecent) {
                        data.recentlyConvertedMossStones.remove(offset);
                        continue;
                    }
                    possibleOffsets.add(offset);
                }
            }
        }

        if (possibleOffsets.isEmpty()) return;

        BlockPos offset = possibleOffsets.get(this.level.random.nextInt(possibleOffsets.size()));
        BlockState state = this.level.getBlockState(offset);
        Block block = state.getBlock();
        naturesaura_plus$mossMemory.writeObject(block);
        AuraGenRules.deMossedBlockAuraAmountPair resultAuraAmountPair = MOSS_GENERATIONS.get(block);

        Block result = resultAuraAmountPair.deMossedBlock();
        int auraAmount = resultAuraAmountPair.auraAmount();
        if (!this.canGenerateRightNow(auraAmount)) return;

        this.generateAura(auraAmount);
        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(
                (float)offset.getX(), (float)offset.getY(), (float)offset.getZ(), PacketParticles.Type.MOSS_GENERATOR)
        );

        this.level.levelEvent(2001, offset, Block.getId(state));
        this.level.setBlockAndUpdate(offset, result.defaultBlockState());

    }

}
