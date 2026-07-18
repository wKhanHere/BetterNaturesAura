package net.wkhan.naturesaura_plus.mixin.auragen.oakgen;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOakGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.wkhan.naturesaura_plus.data.duckfaces.OakGeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.data.trackers.CanopyDiminisherTreeTracker.OAK_GEN_POS;

@Mixin(AbstractTreeGrower.class)
public abstract class AbstractTreeGrowerMixin {

    @Inject(
            method = "growTree",
            at = @At("RETURN")
    )
    private void naturesaura_plus$genAuraIfTreeGrew(ServerLevel serverLevel, ChunkGenerator p_222906_, BlockPos pos,
                                                    BlockState p_222908_, RandomSource p_222909_, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
                return;
        BlockPos oakGenPos = OAK_GEN_POS.get();
        if (oakGenPos == null)
            return;
        try {
            BlockEntity be = serverLevel.getBlockEntity(oakGenPos);
            if (!(be instanceof BlockEntityOakGenerator oakGen))
                return;
            int toAdd = ((OakGeneration) oakGen).naturesaura_plus$scheduledBigTreesAuraGainRemove();
            Level level = oakGen.getLevel();
            if (level == null)
                return;
            boolean canGen = oakGen.canGenerateRightNow(toAdd);
            if (canGen)
                oakGen.generateAura(toAdd);
            PacketHandler.sendToAllAround(level, oakGenPos, 32, new PacketParticles((float) oakGenPos.getX(),
                    (float) oakGenPos.getY(), (float) oakGenPos.getZ(), PacketParticles.Type.OAK_GENERATOR,
                    pos.getX(), pos.getY(), pos.getZ(), canGen ? 1 : 0));
        }
        finally {
            OAK_GEN_POS.remove();
        }
    }
}
