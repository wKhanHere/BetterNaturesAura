package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityPotionGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.wkhan.naturesaura_plus.data.auragen.AuraGenRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.potionCapForGenPerTick;
import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.potionGenRange;

@Mixin(BlockEntityPotionGenerator.class)
public class PotionGenMixin extends BlockEntityImpl {
    public PotionGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

//    @Inject(
//            method = "tick",
//            at = @At("HEAD"),
//            remap = false,
//            cancellable = true
//    )
//    private void naturesaura_plus$potionGenTick(CallbackInfo ci) {
//        ci.cancel();
//        Level level = this.getLevel();
//        BlockPos pos = this.worldPosition;
//        if (level == null || level.isClientSide() || level.getGameTime() % 10L != 0L
//                || !Multiblocks.POTION_GENERATOR.isComplete(level, pos)) return;
//        int added = 0;
//        List<AreaEffectCloud> areaEffectCloudList = level
//                .getEntitiesOfClass(AreaEffectCloud.class, new AABB(pos).inflate(potionGenRange));
//        for (AreaEffectCloud areaEffectCloud : areaEffectCloudList) {
//            if (!areaEffectCloud.isAlive())
//                continue;
//
//            if (potionCapForGenPerTick != -1 && added > potionCapForGenPerTick) {
//                float newRadius = areaEffectCloud.getRadius() - 0.25F;
//                if (newRadius < 0.5F)
//                    areaEffectCloud.kill();
//                else
//                    areaEffectCloud.setRadius(newRadius);
//                break;
//            }
//
//            Potion potion = areaEffectCloud.getPotion();
//
//            for (MobEffectInstance effect : potion.getEffects()) {
//                MobEffect effectType = effect.getEffect();
//                AuraGenRules.potionValues potionValues = POTION_GENERATIONS.get(potion);
//                if (potionValues == null)
//                    continue;
//
//                int toAdd = 0;
//                if (potionValues.doAmplifierScaling()) toAdd += effectType.getAmplifier() * potionValues.flatAmplifierScale();
//                else toAdd += potionValues.flatAmplifierScale();
//
//                if (potionValues.doDurationScaling()) toAdd *= effectType.getDuration();
//                toAdd *= potionValues.flatDurationScale();
//
////              int toAdd = (effect.getAmplifier() * 7 + 1) * (effect.getDuration() / 25) * 100;
//                boolean canGen = this.canGenerateRightNow(toAdd);
//                if (canGen)
//                    this.generateAura(toAdd);
//
//                PacketHandler.sendToAllAround(level, pos, 32, new PacketParticles(
//                        pos.getX(), pos.getY(), pos.getZ(), PacketParticles.Type.POTION_GEN,
//                        PotionUtils.getColor(potion), canGen ? 1 : 0));
//
//                added++;
//                break;
//            }
//
//            float newRadius = areaEffectCloud.getRadius() - 0.25F;
//            if (newRadius < 0.5F)
//                areaEffectCloud.kill();
//            else
//                areaEffectCloud.setRadius(newRadius);
//        }
//    }
}
