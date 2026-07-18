package net.wkhan.naturesaura_plus.mixin.auragen;

import com.google.common.primitives.Ints;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFireworkGenerator;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.wkhan.naturesaura_plus.compat.kubejs.event.CalculateAuraGenByFireworkEvent;
import net.wkhan.naturesaura_plus.compat.kubejs.event.NaturesAuraPlusEvents;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.FIREWORK_GENERATION;
import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.FIREWORK_GEN_RANGE;

@Mixin(BlockEntityFireworkGenerator.class)
public abstract class FireworkGenMixin extends BlockEntityImpl {
    public FireworkGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow(remap = false) private FireworkRocketEntity trackedEntity;
    @Shadow(remap = false) private int releaseTimer;
    @Shadow(remap = false) private ItemStack trackedItem;
    @Shadow(remap = false) private int toRelease;

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$fireworkGenTick(CallbackInfo ci) {
        ci.cancel();
        if (this.level == null || this.level.isClientSide())
            return;

        if (this.level.getGameTime() % 10L == 0L)
            naturesaura_plus$iterateFireworksNearby(this.level);
        if (this.trackedEntity != null && !this.trackedEntity.isAlive())
            naturesaura_plus$calculateAuraToGen(this.level, this.trackedItem);
        if (this.releaseTimer <= 0)
            return;
        --this.releaseTimer;
        if (this.releaseTimer > 0)
            return;

        this.generateAura(this.toRelease);
        this.toRelease = 0;
        PacketHandler.sendToAllLoaded(this.level, this.worldPosition, new PacketParticles(
                (float)this.worldPosition.getX(), (float)this.worldPosition.getY(), (float)this.worldPosition.getZ(),
                PacketParticles.Type.FLOWER_GEN_AURA_CREATION)); //The code legit uses flower gen aura creation particle for this.
    }

    @Unique
    private void naturesaura_plus$iterateFireworksNearby(@NotNull Level level) {
        List<ItemEntity> droppedItems = level.getEntitiesOfClass(ItemEntity.class,
                (new AABB(this.worldPosition)).inflate((double) FIREWORK_GEN_RANGE.get()), Entity::isAlive);

        for (ItemEntity item : droppedItems) {
            if (item.hasPickUpDelay())
                continue;
            ItemStack stack = item.getItem();
            if (stack.isEmpty() || stack.getItem() != Items.FIREWORK_ROCKET)
                continue;
            if (this.trackedEntity != null || this.releaseTimer > 0)
                continue;
            FireworkRocketEntity entity = new FireworkRocketEntity(level, item.getX(), item.getY(), item.getZ(), stack);
            this.trackedEntity = entity;
            this.trackedItem = stack.copy();
            level.addFreshEntity(entity);
            stack.shrink(1);
            if (stack.isEmpty())
                item.kill();
            else
                item.setItem(stack);
        }
    }

    @Unique
    private void naturesaura_plus$calculateAuraToGen(@NotNull Level level, @NotNull ItemStack stack) {
        CalculateAuraGenByFireworkEvent event = new CalculateAuraGenByFireworkEvent(stack);
        NaturesAuraPlusEvents.CALCULATE_AURA_GEN_BY_FIREWORK.post(ScriptType.SERVER, event);
        if (event.isKubeOverrideForAuraGen()){
            int toAdd = event.getAuraToGenerate();
            if (this.canGenerateRightNow(toAdd)) {
                this.toRelease = toAdd;
                this.releaseTimer = event.getReleaseTimer();
            }

            List<Integer> data = new ArrayList<>();
            data.add(this.worldPosition.getX());
            data.add(this.worldPosition.getY());
            data.add(this.worldPosition.getZ());
            if (event.getColors() != null)
                data.addAll(event.getColors());
            PacketHandler.sendToAllLoaded(level, this.worldPosition, new PacketParticles(
                    (float)this.trackedEntity.getX(), (float)this.trackedEntity.getY(), (float)this.trackedEntity.getZ(),
                    PacketParticles.Type.FIREWORK_GEN, Ints.toArray(data)));
            return;
        }


        if (!stack.hasTag() || stack.getTag() == null) {
            this.trackedItem = null;
            this.trackedEntity = null;
            return;
        }
        float generateFactor = 0.0F;
        Set<Integer> usedColors = new HashSet<>();
        CompoundTag compound = stack.getTag();
        CompoundTag fireworks = compound.getCompound("Fireworks");
        int flightTime = fireworks.getInt("Flight");
        ListTag explosions = fireworks.getList("Explosions", 10);
        if (!explosions.isEmpty()) {
            generateFactor += (float) flightTime;

            for(Tag base : explosions) {
                CompoundTag explosion = (CompoundTag) base;
                ++generateFactor;
                boolean flicker = explosion.getBoolean("Flicker");
                if (flicker) {
                    generateFactor += (float) FIREWORK_GENERATION.get(0); //explosionFlickerFactor
                }

                boolean trail = explosion.getBoolean("Trail");
                if (trail) {
                    generateFactor += (float) FIREWORK_GENERATION.get(1); //explosionTrailFactor
                }

                byte type = explosion.getByte("Type");
                generateFactor += ((List<Float>) FIREWORK_GENERATION.get(2)).get(type); //explosionTypesListFactor
                //Maybe add try catch for potential IndexOutOfBounds and ClassCastException

                for(int color : explosion.getIntArray("Colors")) {
                    usedColors.add(color);
                }

                generateFactor += ((float) FIREWORK_GENERATION.get(3)) * usedColors.size(); //explosionColorFactor
            }
        }

        if (generateFactor > 0.0F) {
            int toAdd = Mth.ceil(generateFactor * (float) FIREWORK_GENERATION.get(6));
            if (this.canGenerateRightNow(toAdd)) {
                this.toRelease = toAdd;
                // this.releaseTimer = doFlightTimeScaling ? flightTimeScale * flightTime + flatReleaseTimer : flatReleaseTimer
                this.releaseTimer = ((int) FIREWORK_GENERATION.get(5));
                if ((boolean) FIREWORK_GENERATION.get(7)) //doFlightTimeScaling
                    this.releaseTimer += ((int) FIREWORK_GENERATION.get(4)) * flightTime;
            }

            List<Integer> data = new ArrayList<>();
            data.add(this.worldPosition.getX());
            data.add(this.worldPosition.getY());
            data.add(this.worldPosition.getZ());
            data.addAll(usedColors);
            PacketHandler.sendToAllLoaded(level, this.worldPosition, new PacketParticles(
                    (float)this.trackedEntity.getX(), (float)this.trackedEntity.getY(), (float)this.trackedEntity.getZ(),
                    PacketParticles.Type.FIREWORK_GEN, Ints.toArray(data)));
        }

        this.trackedEntity = null;
        this.trackedItem = null;
    }
}
