package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.BlockAnimalGenerator;
import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityAnimalGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.wkhan.naturesaura_plus.data.auragen.AuraGenRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.animalGenRange;
import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.ANIMAL_GENERATIONS;

@Mixin(BlockAnimalGenerator.class)
public class BlockAnimalGenMixin extends BlockContainerImpl {
    public BlockAnimalGenMixin(String baseName, Class<? extends BlockEntity> tileClass, Properties properties) {
        super(baseName, tileClass, properties);
    }

    @Inject(
            method = "onLivingUpdate",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$onLivingUpdateAnimalGen(LivingEvent.LivingTickEvent event, CallbackInfo ci) { //Is this actually laggier than the original method enough for me to care?
        ci.cancel();
        LivingEntity entity = event.getEntity();
        if(entity.level().isClientSide() ||(entity.level().getGameTime() + entity.getId()) % 40L != 0L) return;
        EntityType<?> entityType = entity.getType();
        if(!ANIMAL_GENERATIONS.containsKey(entityType)) return;
        if(entity instanceof Animal animal && animal.isBaby() && !ANIMAL_GENERATIONS.get(entityType).isBabyValid()) return; //This does mean you can't ever make baby zombies/monsters not work
        CompoundTag data = entity.getPersistentData();
        int timeAlive = data.getInt("naturesaura:time_alive");
        data.putInt("naturesaura:time_alive", timeAlive + 40);
    }

    @Inject(
            method = "onEntityDeath",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$onEntityDeathAnimalGen(LivingDeathEvent event, CallbackInfo ci) {
        ci.cancel();
        LivingEntity entity = event.getEntity();
        if(entity.level().isClientSide()) return;
        EntityType<?> entityType = entity.getType();
        if(!ANIMAL_GENERATIONS.containsKey(entityType)) return;
        if (entity.getPersistentData().getBoolean("naturesaura:pet_reviver")) return;
        AuraGenRules.animalValues animalValues = ANIMAL_GENERATIONS.get(entityType);
        BlockPos pos = entity.blockPosition();
        Helper.getBlockEntitiesInArea(entity.level(), pos, animalGenRange, (tile) -> {
            if (!(tile instanceof BlockEntityAnimalGenerator gen)) return false;
            CompoundTag data = entity.getPersistentData();
            if (gen.isBusy()) return false;
            if (!(animalValues.doEntityDropLoot())) data.putBoolean("naturesaura:no_drops", true);
            int timeAlive = data.getInt("naturesaura:time_alive");
            int amount;
            int time;
            if (!animalValues.isFlatAuraGain()) {
                float amountMod = animalValues.timeAliveModifierForAuraAmount();
                amount = Math.min(Mth.floor((float)(timeAlive - animalValues.minimumTimeAliveForAuraAmount()) * amountMod), animalValues.maximumAuraAmount());
            }
            else amount = animalValues.maximumAuraAmount();
            if (!animalValues.isFlatGenerationTimer()) {
                float timeMod = animalValues.timeAliveModifierForGenerationTime();
                time = Math.min(Mth.floor((float)(timeAlive - animalValues.minimumTimeAliveForGenerationTime()) * timeMod), animalValues.maximumGenerationTime());
            }
            else time = animalValues.maximumGenerationTime();
            if (time <= 0 || amount <= 0) return false;
            gen.setGenerationValues(time, amount);
            BlockPos genPos = gen.getBlockPos();
            PacketHandler.sendToAllAround(entity.level(), pos, 32,
                    new PacketParticles((float)entity.getX(), (float)entity.getY(), (float)entity.getZ(),
                            PacketParticles.Type.ANIMAL_GEN_CONSUME, 0, (int)(entity.getEyeHeight() * 10.0F),
                            genPos.getX(), genPos.getY(), genPos.getZ())); //psure ternary's 2nd is what evaluates to on false, if not then change data: 0 to data: 1
            return true;
        });
    }

    @Inject(
            method = "getVisualizationBounds",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$visualizationBoundsAnimalGen(Level level, BlockPos pos, CallbackInfoReturnable<AABB> cir) {
        cir.cancel();
        cir.setReturnValue((new AABB(pos)).inflate(animalGenRange));
    }
}
