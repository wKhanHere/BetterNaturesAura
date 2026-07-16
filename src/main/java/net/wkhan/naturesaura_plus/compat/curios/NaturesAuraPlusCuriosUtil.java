package net.wkhan.naturesaura_plus.compat.curios;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.inventoryAuraContainerTick;
import static net.wkhan.naturesaura_plus.common.event.PlayerTickEvent.handleItemTransfer;
import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

public class NaturesAuraPlusCuriosUtil {

    private static final Capability<ICurio> CURIOS_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    public static void attachMergedCapability(AttachCapabilitiesEvent<ItemStack> event) {
        final ItemStack stack = event.getObject();

        ICurio curioWrapper = new ICurio() {
            @Override
            public ItemStack getStack() {
                return event.getObject();
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                inventoryAuraContainerTick(stack, slotContext.entity().level(), slotContext.entity(), slotContext.index());
            }
        };

        ICapabilityProvider provider = new ICapabilityProvider() {
            private final LazyOptional<ICurio> curioCapOpt = LazyOptional.of(() -> curioWrapper);

            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == CURIOS_CAP)
                    return curioCapOpt.cast();
                return LazyOptional.empty();
            }
        };

        event.addCapability(ResourceLocation.fromNamespaceAndPath("naturesaura_plus", "curio_item"), provider);
    }

    public static void handleCuriosUnequip(Player player) {
        LazyOptional<ICuriosItemHandler> optionalHandler = CuriosApi.getCuriosInventory(player);
        if (!optionalHandler.isPresent())
            return;
        ICuriosItemHandler handler = optionalHandler.resolve().orElseThrow();
        IItemHandlerModifiable equipped = handler.getEquippedCurios();

        for (int i = 0; i < equipped.getSlots(); i++) {
            if (!isTokenAppliedBroken(equipped.getStackInSlot(i)))
                return;
            ItemStack extracted = equipped.extractItem(i, 1, false);
            handleItemTransfer(player, extracted, "One of your curio broke!");
        }
    }
}
