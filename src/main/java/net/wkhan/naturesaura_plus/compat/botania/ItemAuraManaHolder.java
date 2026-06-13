package net.wkhan.naturesaura_plus.compat.botania;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.api.mana.ManaItem;

import java.util.Optional;

public class ItemAuraManaHolder extends Item{
    public ItemAuraManaHolder(Properties p_41383_) {
        super(p_41383_);
        MinecraftForge.EVENT_BUS.register(new ItemAuraManaHolderEventListener());
    }

    private static final int MAX_AURA = 400000; //make config
    private static final int MAX_MANA = 500000; //make config
    private static final String MANA_TAG = "mana";
    private static final String AURA_TAG = "aura";
    private static final String CREATIVE_TAG = "creative";
    private static final String DISPLAY_MODE_TAG = "displayMode";
    public static final Capability<IAuraContainer> AURA_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<ManaItem> MANA_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new DualAuraManaItemImpl(stack);
    }

    private static boolean isCreativeStack(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(CREATIVE_TAG);
    }

    public static void setCreativeStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(CREATIVE_TAG, true);
        tag.putInt(AURA_TAG,MAX_AURA);
        tag.putInt(MANA_TAG,MAX_MANA);
    }

    public static class DualAuraManaItemImpl implements ICurioItem, ManaItem, IAuraContainer, ICapabilityProvider {
        private final ItemStack stack;
        public DualAuraManaItemImpl(ItemStack stack) {
            this.stack = stack;
        }

        private final LazyOptional<IAuraContainer> auraCapOpt = LazyOptional.of(() -> this);
        private final LazyOptional<ManaItem> manaCapOpt = LazyOptional.of(() -> this);


        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == NaturesAuraAPI.CAP_AURA_CONTAINER || cap == AURA_CAP) {
                return auraCapOpt.cast();
            }
            if (cap == MANA_CAP) {
                return manaCapOpt.cast();
            }
            return LazyOptional.empty();
        }

        public record RecordDualAuraMana(int aura, int max_aura, int mana, int max_mana) implements TooltipComponent {}

        @Override
        public int storeAura(int aura_to_store, boolean simulate) {
            int store = Math.min(aura_to_store, MAX_AURA - this.getStoredAura());
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
            return stack.hasTag() ? stack.getTag().getInt(AURA_TAG) : 0;
        }

        @Override
        public int getMaxAura() {
            return MAX_AURA;
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
            return stack.hasTag() ? stack.getTag().getInt(MANA_TAG) : 0;
        }

        @Override
        public int getMaxMana() {
            return MAX_MANA;
        }

        @Override
        public void addMana(int i) {
            setMana(stack, Math.min(MAX_MANA, getMana() + i));
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
    public void inventoryTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (levelIn.isClientSide || !(entityIn instanceof Player player) || !player.isShiftKeyDown()) return;
        LazyOptional<IAuraContainer> containerCap = stackIn.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER);
        if (!containerCap.isPresent()) return;
        Inventory inventory = player.getInventory();
        IAuraContainer container = containerCap.resolve().get();
        int[] slotsToRecharge = new int[]{
                inventory.selected, // Mainhand-slot
                40,                 // Offhand-slot
                36, 37, 38, 39      // Armor-slots (Boots, Leggings, Chestplate, Helmet)
        };

        for (int i : slotsToRecharge) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            LazyOptional<IAuraRecharge> recharge = stack.getCapability(NaturesAuraAPI.CAP_AURA_RECHARGE);
            if (recharge.isPresent()) {
                boolean isSelectedItem = (i == inventory.selected || i == 40);
                if (recharge.resolve().get().rechargeFromContainer(container, itemSlot, i, isSelectedItem)) {
                    break;
                }
            }
            else if (stack.getEnchantmentLevel(ModEnchantments.AURA_MENDING) > 0) {
                boolean isArmor = (i >= 36 && i <= 39);
                boolean isHand = (i == inventory.selected || i == 40);

                if ((isArmor || isHand) && Helper.rechargeAuraItem(stack, container, 1000)) {
                    break;
                }
            }
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        int aura = 0;
        int mana = 0;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            aura = tag.getInt(AURA_TAG);
            mana = tag.getInt(MANA_TAG);
        }

        return Optional.of(new DualAuraManaItemImpl.RecordDualAuraMana(aura, MAX_AURA, mana, MAX_MANA));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !isCreativeStack(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int aura;
        int mana;
        String displayMode;
        if (!stack.hasTag()) return 0;

        CompoundTag tag = stack.getTag();
        displayMode = tag.getString(DISPLAY_MODE_TAG);
        if (displayMode.equals(AURA_TAG)) {
            aura = tag.getInt(AURA_TAG);
            return Math.round((float) (13 * aura) / MAX_AURA);
        }
        else if (displayMode.equals(MANA_TAG)) {
            mana = tag.getInt(MANA_TAG);
            return Math.round((float) (13 * mana) / MAX_MANA);
        }
        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        String displayMode;
        if (!stack.hasTag()) return 0;

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


    public static class ItemAuraManaHolderEventListener {
        public ItemAuraManaHolderEventListener() {}

        @SubscribeEvent
        public void onAirClick(PlayerInteractEvent.RightClickItem event) {
            ItemStack stack = event.getItemStack();
            Player player = event.getEntity();
            if (!player.isShiftKeyDown()) return;
            if (!(stack.getItem() instanceof ItemAuraManaHolder)) return;
            if (isCreativeStack(stack)) return;
            CompoundTag tag = stack.getOrCreateTag();
            player.swing(InteractionHand.MAIN_HAND, true);
            event.getLevel().playSound(
                    player,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            if (tag.getString(DISPLAY_MODE_TAG).equals(AURA_TAG)) {
                tag.putString(DISPLAY_MODE_TAG, MANA_TAG);
                event.setResult(Event.Result.ALLOW);
                player.swing(InteractionHand.MAIN_HAND, true);
                return;
            }
            tag.putString(DISPLAY_MODE_TAG, AURA_TAG);
        }
    }
}
