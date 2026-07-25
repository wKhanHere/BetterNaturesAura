package net.wkhan.naturesaura_plus.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "de.ellpeck.naturesaura.items.ItemPetReviver$Events")
public abstract class PetReviverEventsMixin {

    @WrapOperation(
            method = "onLivingDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/TamableAnimal;moveTo(DDDFF)V"
            )
    )
    private void naturesaura_plus$loadChunkBeforePetTp(TamableAnimal spawnedPet, double x, double y, double z, float yRot,
                                                       float xRot, Operation<Void> original, @Local(name = "spawnLevel") ServerLevel spawnLevel) {
        BlockPos targetPos = BlockPos.containing(x, y, z);
        ChunkPos targetChunk = new ChunkPos(targetPos);
        spawnLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, targetChunk, 1, spawnedPet.getId());
        spawnLevel.getChunk(targetChunk.x, targetChunk.z);
        original.call(spawnedPet, x, y, z, yRot, xRot);
    }
}
