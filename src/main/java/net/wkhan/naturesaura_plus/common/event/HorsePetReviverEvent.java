package net.wkhan.naturesaura_plus.common.event;


import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

import java.util.Optional;

//This whole class is practically ripped straight from natures aura, swapping TamableAnimal class check for AbstractHorse class check
@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class HorsePetReviverEvent {

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide && entity.level().getGameTime() % 20L == 0L && entity instanceof AbstractHorse tameable) {
            if (tameable.isTamed() && tameable.getPersistentData().getBoolean("naturesaura:pet_reviver")) {
                LivingEntity owner = tameable.getOwner();
                if (owner != null && !(owner.distanceToSqr(tameable) > (double)5*5)) {
                    if (entity.level().random.nextFloat() >= 0.65F) {
                        ((ServerLevel)entity.level()).sendParticles(ParticleTypes.HEART, entity.getX() + entity.level().random.nextGaussian() * (double)0.25F, entity.getEyeY() + entity.level().random.nextGaussian() * (double)0.25F, entity.getZ() + entity.level().random.nextGaussian() * (double)0.25F, entity.level().random.nextInt(2) + 1, 0.0F, 0.0F, 0.0F, 0.0F);
                    }
                }
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
            if (!target.getPersistentData().getBoolean("naturesaura:pet_reviver")) {
                ItemStack stack = event.getItemStack();
                if (stack.getItem() == ModItems.PET_REVIVER) {
                    target.getPersistentData().putBoolean("naturesaura:pet_reviver", true);
                    if (!target.level().isClientSide) {
                        stack.shrink(1);
                    }
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide && entity instanceof AbstractHorse tameable) {
            if (tameable.isTamed() && tameable.getPersistentData().getBoolean("naturesaura:pet_reviver")) {
                MinecraftServer server = tameable.level().getServer();
                if (server == null)
                    return;
                ServerLevel spawnLevel = server.overworld();
                Vec3 spawn = Vec3.atBottomCenterOf(spawnLevel.getSharedSpawnPos());
                LivingEntity owner = tameable.getOwner();
                if (owner instanceof ServerPlayer player) {
                    BlockPos pos = player.getRespawnPosition();
                    if (pos != null) {
                        float f = player.getRespawnAngle();
                        boolean b = player.isRespawnForced();
                        Optional<Vec3> bed = Player.findRespawnPositionAndUseSpawnBlock((ServerLevel)player.level(), pos, f, b, false);
                        if (bed.isPresent()) {
                            spawnLevel = (ServerLevel)player.level();
                            spawn = bed.get();
                        }
                    }
                }
                PacketHandler.sendToAllAround(tameable.level(), tameable.blockPosition(), 32, new PacketParticles((float)tameable.getX(), (float)tameable.getEyeY(), (float)tameable.getZ(), PacketParticles.Type.PET_REVIVER, 12731933));
                AbstractHorse spawnedPet = tameable;
                if (tameable.level() != spawnLevel) {
                    tameable.remove(Entity.RemovalReason.DISCARDED);
                    spawnedPet = (AbstractHorse)tameable.getType().create(spawnLevel);
                }

                spawnedPet.restoreFrom(tameable);

                BlockPos targetPos = BlockPos.containing(spawn.x, spawn.y, spawn.z);
                ChunkPos targetChunk = new ChunkPos(targetPos);
                spawnLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, targetChunk, 1, spawnedPet.getId());
                spawnLevel.getChunk(targetChunk.x, targetChunk.z);

                spawnedPet.setDeltaMovement(0.0F, 0.0F, 0.0F);
                spawnedPet.moveTo(spawn.x, spawn.y, spawn.z, tameable.getYRot(), tameable.getXRot());

                while(!spawnLevel.noCollision(spawnedPet)) {
                    spawnedPet.setPos(spawnedPet.getX(), spawnedPet.getY() + (double)1.0F, spawnedPet.getZ());
                }

                spawnedPet.setHealth(spawnedPet.getMaxHealth());
                spawnedPet.getNavigation().stop();
                spawnedPet.setJumping(false);
                spawnedPet.setTarget(null);
                if (tameable.level() != spawnLevel) {
                    spawnLevel.addFreshEntity(spawnedPet);
                    tameable.remove(Entity.RemovalReason.DISCARDED);
                }
                BlockPos auraPos = IAuraChunk.getHighestSpot(spawnLevel, spawnedPet.blockPosition(), 35, spawnedPet.blockPosition());
                IAuraChunk.getAuraChunk(spawnLevel, auraPos).drainAura(auraPos, 200000);
                PacketHandler.sendToAllAround(spawnedPet.level(), spawnedPet.blockPosition(), 32, new PacketParticles((float)spawnedPet.getX(), (float)spawnedPet.getEyeY(), (float)spawnedPet.getZ(), PacketParticles.Type.PET_REVIVER, 5093935));
                if (owner instanceof Player) {
                    owner.sendSystemMessage(Component.translatable("info.naturesaura.pet_reviver", spawnedPet.getDisplayName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
                }

                event.setCanceled(true);
            }
        }
    }
}

