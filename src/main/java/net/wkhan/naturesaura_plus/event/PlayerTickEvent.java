package net.wkhan.naturesaura_plus.event;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import static net.wkhan.naturesaura_plus.item.custom.ItemBreakPreventionAll.Events.isBroken;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class PlayerTickEvent {

    public static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

    @SubscribeEvent
    public static void onPlayerTick (TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide) return;

        int tickCounter = player.tickCount % 5;

        if (tickCounter == 0)
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getItemBySlot(slot);
                if (isBroken(stack)) {
                    unequipVanillaItem(player, slot, stack);
                }
            }

        if (CURIOS_LOADED && tickCounter == 1) handleCuriosUnequip(player);
    }

    private static void unequipVanillaItem(Player player, EquipmentSlot slot, ItemStack stack) {
            player.setItemSlot(slot, ItemStack.EMPTY);
            handleItemTransfer(player, stack, "Some of your armor broke!");
        }

    private static void handleCuriosUnequip(Player player) {
        var optionalHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        if (!optionalHandler.isPresent()) return;

        ICuriosItemHandler handler = optionalHandler.resolve().orElseThrow();
        var equipped = handler.getEquippedCurios();

        for (int i = 0; i < equipped.getSlots(); i++) {
            ItemStack stack = equipped.getStackInSlot(i);
            if (isBroken(stack)) {
                ItemStack extracted = equipped.extractItem(i, 1, false);
                handleItemTransfer(player, extracted, "One of your curio broke!");
            }
        }
    }

    private static void handleItemTransfer(Player player, ItemStack stack, String messagePrefix) {
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

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ITEM_BREAK,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );
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
