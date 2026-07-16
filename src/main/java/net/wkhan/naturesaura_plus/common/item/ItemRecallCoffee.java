package net.wkhan.naturesaura_plus.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.PET_RECALL_RANGE;

public class ItemRecallCoffee extends Item {
    public ItemRecallCoffee(Properties p_41383_) {
        super(p_41383_);
    }

    public record ServerLevelVec3SpawnAnglePack(ServerLevel serverLevel, Vec3 pos, float angle) {}

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity) { //particles no work, no bueno
        if(level.isClientSide) return super.finishUsingItem(stack, level, entity);
        if(!(entity instanceof Player player)) return super.finishUsingItem(stack, level, entity);
        ServerLevelVec3SpawnAnglePack levelNVec3 = simulateRespawnCheck(player);
        Vec3 spawnPos = levelNVec3.pos;
        ServerLevel serverLevel = levelNVec3.serverLevel;
        float spawnAngle = levelNVec3.angle;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        List<LivingEntity> pets = level.getEntitiesOfClass(LivingEntity.class,
                serverPlayer.getBoundingBox().inflate(PET_RECALL_RANGE.get()), target -> {
            if (target instanceof TamableAnimal pet)
                return serverPlayer.getUUID().equals(pet.getOwnerUUID());
            else if (target instanceof AbstractHorse horse)
                return serverPlayer.getUUID().equals(horse.getOwnerUUID());
            return false;
        });

        ChunkPos targetChunk = new ChunkPos(BlockPos.containing(spawnPos));
        serverLevel.getChunkSource()
                .addRegionTicket(TicketType.POST_TELEPORT, targetChunk, 1, player.getId());
        serverLevel.getChunk(targetChunk.x, targetChunk.z);

        pets.forEach(pet -> {
            if (pet.level() == serverLevel) {
                pet.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
                return;
            }
            pet.changeDimension(serverLevel, new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld,
                                          float yaw, Function<Boolean, Entity> repositionEntity) {
                    Entity recreatedPet = repositionEntity.apply(false);
                    if (recreatedPet != null)
                        recreatedPet.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
                    return recreatedPet;
                }
            });
        });

        serverPlayer.teleportTo(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z, spawnAngle, 0.0F);
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, List<Component> toolTip, @NotNull TooltipFlag p_41424_) {
        toolTip.add(Component.translatable("info.naturesaura_plus.aura_coffee")
                .setStyle(Style.EMPTY.withItalic(true).applyFormat(ChatFormatting.GRAY)));
    }

    public static ServerLevelVec3SpawnAnglePack simulateRespawnCheck(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return null;
        BlockPos respawnPos = serverPlayer.getRespawnPosition();
        float respawnAngle = serverPlayer.getRespawnAngle();
        boolean isSpawnForced = serverPlayer.isRespawnForced();
        MinecraftServer server = serverPlayer.getServer();
        if (server == null)
            return null;
        ServerLevel defaultDimension = server.overworld();
        ServerLevel targetDimension = serverPlayer.server.getLevel(serverPlayer.getRespawnDimension());

        if (respawnPos == null || targetDimension == null)
            return new ServerLevelVec3SpawnAnglePack(defaultDimension,
                    Vec3.atBottomCenterOf(defaultDimension.getSharedSpawnPos()),respawnAngle);

        Optional<Vec3> actualSpawnPoint = Player.findRespawnPositionAndUseSpawnBlock
                (targetDimension, respawnPos, respawnAngle, isSpawnForced, true);
        return actualSpawnPoint.map(vec3 -> new ServerLevelVec3SpawnAnglePack(targetDimension, vec3, respawnAngle))
                .orElseGet(() -> new ServerLevelVec3SpawnAnglePack
                        (defaultDimension, Vec3.atBottomCenterOf(defaultDimension.getSharedSpawnPos()), respawnAngle));
    }
}
