package net.wkhan.naturesaura_plus.common.event;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;
import static net.wkhan.naturesaura_plus.compat.curios.NaturesAuraPlusCuriosUtil.handleCuriosUnequip;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class PlayerTickEvent {

    @SubscribeEvent
    public static void onPlayerTick (TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide) return;

        int tickCounter = player.tickCount % 5;

        if (tickCounter == 0)
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getItemBySlot(slot);
                if (isTokenAppliedBroken(stack)) {
                    unequipVanillaItem(player, slot, stack);
                }
            }

        if (NaturesAuraPlus.isCuriosLoaded && tickCounter == 1) handleCuriosUnequip(player);
    }

    private static void unequipVanillaItem(Player player, EquipmentSlot slot, ItemStack stack) {
            player.setItemSlot(slot, ItemStack.EMPTY);
            handleItemTransfer(player, stack, "Some of your armor broke!");
    }



    public static void handleItemTransfer(Player player, ItemStack stack, String messagePrefix) {
        if (stack.isEmpty()) return;

        if (!player.getInventory().add(stack)) {
            dropAsIndestructibleAndFrozen(player, stack);
            player.displayClientMessage(
                    Component.literal(messagePrefix + " It left your inventory!").withStyle(ChatFormatting.RED),
                    true
            );
        } else {
            player.displayClientMessage(
                    Component.literal(messagePrefix + " It was unequipped.").withStyle(ChatFormatting.YELLOW),
                    true
            );
        }

        player.playSound(SoundEvents.ITEM_BREAK, 1.0f, 1.0f);
    }

    private static void dropAsIndestructibleAndFrozen(Player player, ItemStack stack) {
        ItemEntity entity = new ItemEntity(
                player.level(),
                player.getX(),
                player.getY()-0.23,
                player.getZ(),
                stack
        );

        entity.setUnlimitedLifetime();
        entity.setInvulnerable(true);
        entity.setNoGravity(true);
        entity.setGlowingTag(true);

        entity.setDeltaMovement(0, 0, 0);
        entity.setPos(player.getX(), player.getEyeY(), player.getZ());
        entity.setPickUpDelay(40);

        player.level().addFreshEntity(entity);
    }
}
