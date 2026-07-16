package net.wkhan.naturesaura_plus.compat.botania;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.api.mana.ManaItem;

import java.util.List;
import java.util.Optional;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.inventoryAuraContainerTick;
import static net.wkhan.naturesaura_plus.compat.curios.NaturesAuraPlusCuriosUtil.tryEquipCurio;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.*;

public class ItemAuraManaHolder extends Item {
    public ItemAuraManaHolder(Properties p_41383_) {
        super(p_41383_);
    }

    private static final String MANA_TAG = "mana";
    private static final String AURA_TAG = "aura";
    private static final String CREATIVE_TAG = "creative";
    private static final String DISPLAY_MODE_TAG = "displayMode";
    public static final Capability<ManaItem> MANA_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    private static final Capability<ICurioItem> CURIO_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new DualAuraManaItemImpl(stack);
    }

    private static boolean isCreativeStack(ItemStack stack) {
        return stack.hasTag() && stack.getTag() != null && stack.getTag().getBoolean(CREATIVE_TAG);
    }

    public static void setCreativeStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(CREATIVE_TAG, true);
        tag.putInt(AURA_TAG,SASH_AURA_CAPACITY.get());
        tag.putInt(MANA_TAG,SASH_MANA_CAPACITY.get());
    }

    public static class DualAuraManaItemImpl implements ICurioItem, ManaItem, IAuraContainer, ICapabilityProvider {
        private final ItemStack stack;
        public DualAuraManaItemImpl(ItemStack stack) {
            this.stack = stack;
        }

        private final LazyOptional<IAuraContainer> auraCapOpt = LazyOptional.of(() -> this);
        private final LazyOptional<ManaItem> manaCapOpt = LazyOptional.of(() -> this);
        private final LazyOptional<ICurioItem> curioCapOpt = LazyOptional.of(() -> this); // Add Curios

        @Override
        public void curioTick(SlotContext slotContext, ItemStack stack) {
            inventoryAuraContainerTick(stack, slotContext.entity().level(), slotContext.entity(), slotContext.index());
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == NaturesAuraAPI.CAP_AURA_CONTAINER) {
                return auraCapOpt.cast();
            }
            if (cap == MANA_CAP) {
                return manaCapOpt.cast();
            }
            if (cap == CURIO_CAP) {
                return curioCapOpt.cast();
            }
            return LazyOptional.empty();
        }

        public record RecordDualAuraMana(int aura, int max_aura, int mana, int max_mana) implements TooltipComponent {}

        @Override
        public int storeAura(int aura_to_store, boolean simulate) {
            int store = Math.min(aura_to_store, SASH_AURA_CAPACITY.get() - this.getStoredAura());
            if (!simulate && !isCreativeStack(stack)) {
                this.setAura(stack,this.getStoredAura() + store);
            }
            return store;
        }

        @Override
        public int drainAura(int aura_to_drain, boolean simulate) {
            int drain = Math.min(aura_to_drain, this.getStoredAura());
            if (!simulate && !isCreativeStack(stack)) {
                this.setAura(stack, this.getStoredAura() - drain);
            }
            return drain;
        }

        private void setAura(ItemStack stack,int aura) {
            if (aura > 0) {
                stack.getOrCreateTag().putInt(AURA_TAG,aura);
                return;
            }
            stack.removeTagKey(AURA_TAG);
        }

        @Override
        public int getStoredAura() {
            if (!stack.hasTag() || stack.getTag() == null)
                return 0;
            return stack.hasTag() ? stack.getTag().getInt(AURA_TAG) : 0;
        }

        @Override
        public int getMaxAura() {
            return SASH_AURA_CAPACITY.get();
        }

        @Override
        public int getAuraColor() {
            return 0xFF4CAF50;
        }

        @Override
        public boolean isAcceptableType(IAuraType iAuraType) {
            return true;
        }

        @Override
        public int getMana() {
            if (!stack.hasTag() || stack.getTag() == null)
                return 0;
            return (stack.getTag().contains(MANA_TAG)) ? stack.getTag().getInt(MANA_TAG) : 0;
        }

        @Override
        public int getMaxMana() {
            return SASH_MANA_CAPACITY.get();
        }

        @Override
        public void addMana(int i) {
            setMana(stack, Math.min(SASH_MANA_CAPACITY.get(), getMana() + i));
        }

        public void setMana(ItemStack stack, int mana) {
            if (isCreativeStack(stack)) return;
            if (mana > 0) {
                stack.getOrCreateTag().putInt(MANA_TAG,mana);
                return;
            }
            stack.removeTagKey(MANA_TAG);
        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity blockEntity) {
            return true;
        }

        @Override
        public boolean canReceiveManaFromItem(ItemStack itemStack) {
            return !isCreativeStack(itemStack);
        }

        @Override
        public boolean canExportManaToPool(BlockEntity blockEntity) {
            return true;
        }

        @Override
        public boolean canExportManaToItem(ItemStack itemStack) {
            return true;
        }

        @Override
        public boolean isNoExport() {
            return false;
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && !isCreativeStack(stack))
            return toggleBarDisplay(player, stack, level);
        else if (!player.isShiftKeyDown())
            return tryEquipCurio(player, stack, "belt", SoundEvents.ARMOR_EQUIP_LEATHER);
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stackIn, @NotNull Level levelIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
        inventoryAuraContainerTick(stackIn, levelIn, entityIn, itemSlot);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        int aura = 0;
        int mana = 0;
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundTag tag = stack.getTag();
            aura = tag.getInt(AURA_TAG);
            mana = tag.getInt(MANA_TAG);
        }
        return Optional.of(new DualAuraManaItemImpl.RecordDualAuraMana(aura, SASH_AURA_CAPACITY.get(), mana, SASH_MANA_CAPACITY.get()));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, List<Component> toolTip, @NotNull TooltipFlag p_41424_) {
        toolTip.add(Component.translatable(
             "info.naturesaura_plus.aura_mana_holder")
             .setStyle(Style.EMPTY.withItalic(true).applyFormat(ChatFormatting.GRAY))
        );
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return !isCreativeStack(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int aura;
        int mana;
        String displayMode;
        if (!stack.hasTag() || stack.getTag() == null)
            return 0;

        CompoundTag tag = stack.getTag();
        displayMode = tag.getString(DISPLAY_MODE_TAG);
        if (displayMode.equals(AURA_TAG)) {
            aura = tag.getInt(AURA_TAG);
            return Math.round((float) (13 * aura) / SASH_AURA_CAPACITY.get());
        }
        else if (displayMode.equals(MANA_TAG)) {
            mana = tag.getInt(MANA_TAG);
            return Math.round((float) (13 * mana) / SASH_MANA_CAPACITY.get());
        }
        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        String displayMode;
        if (!stack.hasTag() || stack.getTag() == null)
            return 0;

        CompoundTag tag = stack.getTag();
        displayMode = tag.getString(DISPLAY_MODE_TAG);
        if (displayMode.equals(AURA_TAG)) {
            return 0xFF4CAF50;
        }
        else if (displayMode.equals(MANA_TAG)) {
            return 0xFF2196F3;
        }
        return 0xFFFFFFFF;
    }

    private InteractionResultHolder<ItemStack> toggleBarDisplay(Player player, ItemStack stack, Level level) {
        CompoundTag tag = stack.getOrCreateTag();
        player.swing(InteractionHand.MAIN_HAND, true);
        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        if (tag.getString(DISPLAY_MODE_TAG).equals(AURA_TAG)) {
            tag.putString(DISPLAY_MODE_TAG, MANA_TAG);
            player.swing(InteractionHand.MAIN_HAND, true);
        }
        else {
            tag.putString(DISPLAY_MODE_TAG, AURA_TAG);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
