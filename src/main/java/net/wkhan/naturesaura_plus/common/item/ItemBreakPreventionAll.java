/*
 * Portions of this file are derived from Nature's Aura ItemBreakPrevention java class.
 * https://github.com/Ellpeck/NaturesAura/blob/main/src/main/java/de/ellpeck/naturesaura/items/ItemBreakPrevention.java
 *
 * Copyright (c) Ellpeck
 * Licensed under the MIT License
 */

package net.wkhan.naturesaura_plus.common.item;

import de.ellpeck.naturesaura.items.ItemImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.wkhan.naturesaura_plus.common.data.AnvilCostRules;
import net.wkhan.naturesaura_plus.common.tag.ModTags;

import java.util.List;

public class ItemBreakPreventionAll extends ItemImpl {
    public ItemBreakPreventionAll() {
        super("break_prevention");
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static boolean isTokenAppliedBroken(ItemStack stack) {
        return willTokenAppliedBroken(stack,0);
    }

    public static boolean willTokenAppliedBroken(ItemStack stack, int damageAmount) {
        if (stack.isEmpty()) return false;
        if (!stack.hasTag()) return false;
        assert stack.getTag() != null;
        if (!stack.getTag().getBoolean("naturesaura_plus:break_prevention")) return false;
        return ((stack.getDamageValue() + damageAmount) >= stack.getMaxDamage() - 1);
    }

    public static class Events {
        public Events() {}

        @SubscribeEvent
        public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (!isTokenAppliedBroken(stack)) return;
            event.setNewSpeed(0.0F);
        }

        @SubscribeEvent
        public void onUseTick(LivingEntityUseItemEvent event) {
            if (event.isCanceled()) return;
            ItemStack stack = event.getItem();
            if (!isTokenAppliedBroken(stack)) return;
            if (!event.isCancelable()) return;
            event.setCanceled(true);
            Player player = (Player) event.getEntity();
            Level level = player.level();
            level.playSound(
                    null,
                    event.getEntity().getX(),
                    event.getEntity().getY(),
                    event.getEntity().getZ(),
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
        public void onAnvilUpdate(AnvilUpdateEvent event) {

            ItemStack right = event.getRight();
            if (!(right.getItem() == ModItems.BREAK_PREVENTION.get())) {
                return;
            }

            ItemStack left = event.getLeft();
            if (!left.isDamageableItem()) {
                return;
            }
            if (!left.hasTag()) {
                return;
            }
            if (left.getTag().getBoolean("naturesaura_plus:break_prevention") || left.getTag().getBoolean("Unbreakable")) {
                return;
            }
            if (left.is(ModTags.Items.CANNOT_APPLY_BREAK_PREVENTION)) {
                return;
            }

            ItemStack output = left.copy();
            output.getOrCreateTag().putBoolean("naturesaura_plus:break_prevention", true);
            event.setOutput(output);
            event.setCost(AnvilCostRules.getCost(ResourceLocation.parse("naturesaura_plus:anvil_cost/steel_token")));
            event.setMaterialCost(1);
        }

        //Portion of code of this method is derived from NaturesAura - Credit to Ellpeck.
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void onTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (!stack.hasTag())
                return;
            if (!stack.getTag().getBoolean("naturesaura_plus:break_prevention")) {
                return;
            }

            List<Component> tooltip = event.getToolTip();
            tooltip.add(Component.translatable("info.naturesaura_plus.break_prevention_token").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            Component head = tooltip.get(0);
            if (head instanceof MutableComponent) {
                ((MutableComponent) head).append(Component.translatable("info.naturesaura_plus.broken").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
            }
        }
    }
}

