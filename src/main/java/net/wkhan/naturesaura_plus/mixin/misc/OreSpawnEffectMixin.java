package net.wkhan.naturesaura_plus.mixin.misc;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.chunk.AuraChunk;
import de.ellpeck.naturesaura.chunk.effect.OreSpawnEffect;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.wkhan.naturesaura_plus.data.OreSpawnRules;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.wkhan.naturesaura_plus.data.OreSpawnRules.ORE_SPAWNS;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.*;

@Mixin(OreSpawnEffect.class)
public abstract class OreSpawnEffectMixin {
    @Shadow(remap = false) private int amount;
    @Shadow(remap = false) private int dist;
    @Shadow(remap = false) protected abstract boolean calcValues(Level level, BlockPos pos, Integer spot);
    @Shadow(remap = false) public abstract ResourceLocation getName();
    @Shadow(remap = false) @Final public static Set<BlockState> SPAWN_EXCEPTIONS;

    @Inject(
            method = "calcValues",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$calcOurValues(Level level, BlockPos pos, Integer spot, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (spot <= 0) {
            cir.setReturnValue(false);
            return;
        }
        Pair<Integer, Integer> auraAndSpots = IAuraChunk.getAuraAndSpotAmountInArea(level, pos, 30);
        int aura = auraAndSpots.getLeft();
        if (aura <= MIN_AURA_FOR_ORE_SPAWN.get()) {
            cir.setReturnValue(false);
            return;
        }

        this.amount = Math.min(MAX_ITERATION_FOR_ORE_SPAWN.get(),
                Mth.ceil( (double) Math.abs(aura) / auraAndSpots.getRight() / ORE_SPAWN_ITER_SCALE.get()));
        if (this.amount <= 0) {
            cir.setReturnValue(false);
            return;
        }
        this.dist = Mth.ceil(Mth.clamp(Math.abs(aura) / ORE_SPAWN_RANGE_SCALE.get(), MIN_ORE_SPAWN_RANGE.get(), MAX_ORE_SPAWN_RANGE.get()));
        cir.setReturnValue(true);
    }

    @Inject(
            method = "update",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$customOreSpawn(Level level, LevelChunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot,
                                                 AuraChunk.DrainSpot actualSpot, CallbackInfo ci) {
        ci.cancel();
        if (level.isClientSide() || level.getGameTime() % 40L != 0L)
            return;
        if (!this.calcValues(level, pos, spot))
            return;
        Optional<ResourceKey<DimensionType>> dimensionType = chunk.getLevel().dimensionTypeRegistration().unwrapKey();
        Optional<ResourceKey<Biome>> biomeType = chunk.getLevel().getBiome(pos).unwrapKey();
        if (dimensionType.isEmpty())
            return;
        OreSpawnRules.OreSpawnValues oreSpawnValues = ORE_SPAWNS.get(dimensionType.get());
        if (oreSpawnValues == null)
            return;
        if (oreSpawnValues.biomes() != null && !oreSpawnValues.biomes().isEmpty() && (biomeType.isEmpty() || !oreSpawnValues.biomes().contains(biomeType.get())))
            return;
        Reference2IntOpenHashMap<Block> baseBlocks = oreSpawnValues.baseBlockAndAuraDrain();
        SimpleWeightedRandomList<Block> outputOres = oreSpawnValues.outputOres();
        List<Tuple<Vec3, Integer>> powders = NaturesAuraAPI.instance()
                .getActiveEffectPowders(level, (new AABB(pos)).inflate(this.dist), getName());
        if (powders.isEmpty() || baseBlocks.isEmpty() || outputOres.isEmpty())
            return;
        naturesaura_plus$tryPlaceOre(powders, baseBlocks, outputOres, level, pos);
    }

    @Unique
    private void naturesaura_plus$tryPlaceOre(List<Tuple<Vec3, Integer>> powders, Reference2IntOpenHashMap<Block> baseBlocks,
                                              SimpleWeightedRandomList<Block> outputOres, Level level, BlockPos pos) {
        FakePlayer player = FakePlayerFactory.getMinecraft((ServerLevel)level);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        BlockHitResult ray = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, ray));
        for(int i = 0; i < this.amount; ++i) {
            Tuple<Vec3, Integer> powder = powders.get(i % powders.size());
            Vec3 powderPos = powder.getA();
            int range = powder.getB();
            BlockPos orePos = naturesaura_plus$tryOreSpawn(baseBlocks.keySet(), powderPos, pos, range, level, MAX_ORE_PLACE_ATTEMPTS.get());
            if (orePos == null)
                continue;
            Block toPlace = outputOres.getRandomValue(level.getRandom()).orElse(Blocks.AIR);
            if (toPlace == Blocks.AIR)
                continue;
            BlockState stateToPlace = toPlace.getStateForPlacement(context);
            if (stateToPlace == null)
                continue;
            if (SPAWN_EXCEPTIONS.contains(stateToPlace))
                continue;
            BlockPos highestSpot = IAuraChunk.getHighestSpot(level, orePos, 30, pos);
            IAuraChunk.getAuraChunk(level, highestSpot).drainAura(highestSpot, baseBlocks.getInt(level.getBlockState(orePos).getBlock()));
            level.setBlockAndUpdate(orePos, stateToPlace);
            level.levelEvent(2001, orePos, Block.getId(stateToPlace));
        }
    }

    @Unique
    private BlockPos naturesaura_plus$tryOreSpawn(Set<Block> validBase, Vec3 powderPos, BlockPos pos, int range, Level level, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            BlockPos orePos = new BlockPos(
                    Mth.floor(powderPos.x + level.random.nextGaussian() * (double) range),
                    Mth.floor(powderPos.y + level.random.nextGaussian() * (double) range),
                    Mth.floor(powderPos.z + level.random.nextGaussian() * (double) range)
            );
            if (!level.isLoaded(orePos))
                continue;
            if (!CUBICAL_ORE_SPAWN.get() && !(orePos.distToCenterSqr(powderPos.x, powderPos.y, powderPos.z) <= (range * range) &&
                    orePos.distSqr(pos) <= (this.dist * this.dist)))
                continue;
            if (!validBase.contains(level.getBlockState(orePos).getBlock()))
                continue;
            return orePos;
        }

        return null;
    }
}


