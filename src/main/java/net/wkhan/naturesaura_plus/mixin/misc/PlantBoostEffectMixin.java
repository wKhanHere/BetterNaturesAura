package net.wkhan.naturesaura_plus.mixin.misc;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.effect.PlantBoostEffect;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static net.wkhan.naturesaura_plus.common.tag.ModTags.Blocks.AURA_BONEMEAL_BLACKLIST;
import static net.wkhan.naturesaura_plus.common.tag.ModTags.Blocks.AURA_BONEMEAL_WHITELIST;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.AURA_BONEMEAL_COST;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.ENABLE_WHITELIST_AURA_BONEMEAL;

@Mixin(PlantBoostEffect.class)
public abstract class PlantBoostEffectMixin {
    @Shadow(remap = false) protected abstract boolean calcValues(Level level, BlockPos pos, Integer spot);
    @Shadow(remap = false) private int amount;
    @Shadow(remap = false) @Final public static Set<Block> EXCEPTIONS;
    @Shadow(remap = false) private int dist;
    @Shadow(remap = false) @Final public static ResourceLocation NAME;

    @Inject(
            method = "update",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$blackListBoneMealableBlock(Level level, LevelChunk chunk, IAuraChunk auraChunk,
                                                             BlockPos pos, Integer spot, AuraChunk.DrainSpot actualSpot, CallbackInfo ci) {
        ci.cancel();
        if (!this.calcValues(level, pos, spot))
            return;

        for(int i = this.amount / 2 + level.random.nextInt(this.amount / 2); i >= 0; --i) {
            int x = Mth.floor((float)pos.getX() + (2.0F * level.random.nextFloat() - 1.0F) * this.dist);
            int y = Mth.floor((float)pos.getY() + (2.0F * level.random.nextFloat() - 1.0F) * this.dist / 2.0F);
            int z = Mth.floor((float)pos.getZ() + (2.0F * level.random.nextFloat() - 1.0F) * this.dist);
            BlockPos plantPos = Helper.getClosestAirAboveGround(level, new BlockPos(x, y, z), this.dist / 2).below();
            if (plantPos.distSqr(pos) > this.dist * this.dist || !level.isLoaded(plantPos) ||
                    NaturesAuraAPI.instance().isEffectPowderActive(level, plantPos, NAME))
                continue;

            BlockState state = level.getBlockState(plantPos);
            if (state.is(AURA_BONEMEAL_BLACKLIST) || (ENABLE_WHITELIST_AURA_BONEMEAL.get() && !state.is(AURA_BONEMEAL_WHITELIST)))
                continue;
            Block block = state.getBlock();
            if (!(block instanceof BonemealableBlock growable))
                continue;
            if (EXCEPTIONS.contains(block) || !growable.isValidBonemealTarget(level, plantPos, state, false))
                continue;

            try {
                growable.performBonemeal((ServerLevel)level, level.random, plantPos, state);
            } catch (Exception ignored) {}

            BlockPos closestSpot = IAuraChunk.getHighestSpot(level, plantPos, 25, pos);
            IAuraChunk.getAuraChunk(level, closestSpot).drainAura(closestSpot, AURA_BONEMEAL_COST.get());
            PacketHandler.sendToAllAround(level, plantPos, 32, new PacketParticles(
                    (float)plantPos.getX(), (float)plantPos.getY(), (float)plantPos.getZ(), PacketParticles.Type.PLANT_BOOST));
        }
    }
}
