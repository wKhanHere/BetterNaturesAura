package net.wkhan.naturesaura_plus.compat.curios;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

import static net.wkhan.naturesaura_plus.common.event.PlayerTickEvent.handleItemTransfer;
import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

public class NaturesAuraPlusCuriosUtil {

    public static InteractionResultHolder<ItemStack> tryEquipCurio(Player player, ItemStack stack, String curioSlotId) {
        return tryEquipCurio(player, stack, curioSlotId, SoundEvents.EMPTY);
    }

    public static InteractionResultHolder<ItemStack> tryEquipCurio(Player player, ItemStack stack, String curioSlotId, SoundEvent soundEvent) {
        return tryEquipCurio(player, stack, curioSlotId, soundEvent, 1.0F, 1.0F);
    }

    public static InteractionResultHolder<ItemStack> tryEquipCurio(Player player, ItemStack stack, String curioSlotId,
                                                                   SoundEvent equipSoundEvent, float soundVol, float soundPitch) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player).resolve();
        if (curiosInventory.isEmpty())
            return InteractionResultHolder.pass(stack);

        ItemStack toInsert = stack.copy();
        toInsert.setCount(1);

        SlotContext context = new SlotContext(curioSlotId, player, 0, false, true);
        if (!CuriosApi.isStackValid(context, toInsert)) //I guess I need this for reasons?
            return InteractionResultHolder.pass(stack);

        Optional<ICurioStacksHandler> optHandler = curiosInventory.get().getStacksHandler(curioSlotId);
        if (optHandler.isEmpty())
            return InteractionResultHolder.pass(stack);
        IDynamicStackHandler dynamicStackHandler = optHandler.get().getStacks();

        for (int i = 0; i < dynamicStackHandler.getSlots(); i++) {
            if (!dynamicStackHandler.getStackInSlot(i).isEmpty())
                continue;
            dynamicStackHandler.insertItem(i, toInsert, false);
            stack.shrink(1);
            player.playSound(equipSoundEvent, soundVol, soundPitch);
            return InteractionResultHolder.sidedSuccess(stack, player.level().isClientSide());
        }

        ItemStack oldCurio = dynamicStackHandler.extractItem(0, 1, false);
        dynamicStackHandler.setStackInSlot(0, toInsert);
        player.playSound(equipSoundEvent, soundVol, soundPitch);

        stack.shrink(1);
        if (stack.isEmpty())
            return InteractionResultHolder.sidedSuccess(oldCurio, player.level().isClientSide());
        else if (!player.getInventory().add(oldCurio))
            player.drop(oldCurio, false);
        return InteractionResultHolder.sidedSuccess(stack, player.level().isClientSide());
    }

    public static void handleCuriosUnequip(Player player) {
        LazyOptional<ICuriosItemHandler> optionalHandler = CuriosApi.getCuriosInventory(player);
        if (!optionalHandler.isPresent())
            return;
        ICuriosItemHandler handler = optionalHandler.resolve().orElseThrow();//why do I have a or else throw here?
        IItemHandlerModifiable equipped = handler.getEquippedCurios();

        for (int i = 0; i < equipped.getSlots(); i++) {
            if (!isTokenAppliedBroken(equipped.getStackInSlot(i)))
                return;
            ItemStack extracted = equipped.extractItem(i, 1, false);
            handleItemTransfer(player, extracted, "One of your curio broke!");
        }
    }
}
