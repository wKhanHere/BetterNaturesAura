/*
 * Portions of this file are derived from Nature's Aura ItemBreakPrevention java class.
 * https://github.com/Ellpeck/NaturesAura/blob/main/src/main/java/de/ellpeck/naturesaura/items/ItemBreakPrevention.java
 *
 * Copyright (c) Ellpeck
 * Licensed under the MIT License
 */

package net.wkhan.naturesaura_plus.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.wkhan.naturesaura_plus.data.AnvilCostRules;
import net.wkhan.naturesaura_plus.common.tag.ModTags;

import java.util.List;

public class ItemBreakPreventionAll extends Item {
    public ItemBreakPreventionAll(Properties p_41383_) {
        super(p_41383_);
        MinecraftForge.EVENT_BUS.register(new ItemBreakPreventionEventListener());
    }

    public static boolean isTokenAppliedBroken(ItemStack stack) {
        return willTokenAppliedBroken(stack,0);
    }

    public static boolean willTokenAppliedBroken(ItemStack stack, int damageAmount) {
        if (!isTokenApplied(stack)) return false;
        return ((stack.getDamageValue() + damageAmount) >= stack.getMaxDamage() - 1);
    }

    public static boolean isTokenApplied(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.hasTag()) return false;
        return stack.getTag().getBoolean("naturesaura_plus:break_prevention");
    }

    public static class ItemBreakPreventionEventListener {
        public ItemBreakPreventionEventListener() {}

        @SubscribeEvent
        public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (!isTokenAppliedBroken(stack)) return;
            event.setNewSpeed(0.0F);
        }

        @SubscribeEvent
        public void onUseStart(LivingEntityUseItemEvent.Start event) {
            if (!event.isCancelable()) return;
            if (event.isCanceled()) return;
            ItemStack stack = event.getItem();
            if (!isTokenAppliedBroken(stack)) return;
            event.setCanceled(true);
            Player player = (Player) event.getEntity();
            Level level = player.level();
            level.playSound(
                    player,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0F,
                    1.0F
                );
            player.displayClientMessage(
                        Component.literal("The item is broken, you can't use it!").withStyle(ChatFormatting.YELLOW),
                        true
            );
        }

        @SubscribeEvent
        public void onUseTick(LivingEntityUseItemEvent.Tick event) {
            if (!event.isCancelable()) return;
            if (event.isCanceled()) return;
            ItemStack stack = event.getItem();
            if (!isTokenAppliedBroken(stack)) return;
            event.setCanceled(true);
            Player player = (Player) event.getEntity();
            player.displayClientMessage(
                    Component.literal("The item is broken, you can't use it!").withStyle(ChatFormatting.YELLOW),
                    true
            );
        }

        @SubscribeEvent
        public void onAnvilUpdate(AnvilUpdateEvent event) {

            ItemStack right = event.getRight();
            if (!(right.getItem() == ModItems.BREAK_PREVENTION.get())) {
                return;
            }

            ItemStack left = event.getLeft();
            if (!left.isDamageableItem()) {
                return;
            }
            if (left.hasTag()) {
                if (left.getTag().getBoolean("naturesaura_plus:break_prevention") || left.getTag().getBoolean("Unbreakable")) return;
            }

            if (left.is(ModTags.Items.CANNOT_APPLY_BREAK_PREVENTION)) {
                return;
            }

            ItemStack output = left.copy();
            CompoundTag outputCompoundTag = output.getOrCreateTag();
            outputCompoundTag.putBoolean("naturesaura_plus:break_prevention", true);
            outputCompoundTag.remove("naturesaura:break_prevention");
            event.setOutput(output);
            event.setCost(AnvilCostRules.getCost(ResourceLocation.parse("naturesaura_plus:anvil_cost/steel_token")));
            event.setMaterialCost(1);
        }

        //Portion of code of this method is derived from NaturesAura - Credit to Ellpeck.
        @SubscribeEvent
        public void onTooltip(ItemTooltipEvent event) {
            if (event.getEntity() == null || !event.getEntity().level().isClientSide) return;
            ItemStack stack = event.getItemStack();
            if (!isTokenApplied(stack)) return;
            List<Component> tooltip = event.getToolTip();
            tooltip.add(Component.translatable(
                    "info.naturesaura_plus.break_prevention_token")
                    .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY))
            );
            Component head = tooltip.get(0);
            if (head instanceof MutableComponent) {
                if (!isTokenAppliedBroken(stack)) return;
                ((MutableComponent) head).append(Component.translatable("info.naturesaura_plus.broken").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            }
        }
    }
}

