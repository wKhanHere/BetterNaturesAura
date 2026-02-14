/*
 * Portions of this file are derived from Nature's Aura ItemBreakPrevention java class.
 * https://github.com/Ellpeck/NaturesAura/blob/main/src/main/java/de/ellpeck/naturesaura/items/ItemBreakPrevention.java
 *
 * Copyright (c) Ellpeck
 * Licensed under the MIT License
 */

package net.wkhan.naturesaura_plus.item.custom;

import de.ellpeck.naturesaura.items.ItemImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.wkhan.naturesaura_plus.item.ModItems;
import net.wkhan.naturesaura_plus.util.ModTags;

import java.util.List;

public class ItemBreakPreventionAll extends ItemImpl
{
    public ItemBreakPreventionAll() {
        super("break_prevention");
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static class Events {
        public Events() {}

        @SubscribeEvent
        public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if(!stack.hasTag())
                    return;
                if (stack.getTag().getBoolean("naturesaura_plus:break_prevention")) {
                    if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {event.setNewSpeed(0.0F);}
                    // Get a broken sound byte here too.
                }
            }
        }

        //Add a entity interact cancel for shearing sheep lmao

        @SubscribeEvent
        public void onAnvilUpdate(AnvilUpdateEvent event) {
            ItemStack left = event.getLeft();
            if (!left.hasTag())
                return;
            if (left.getTag().getBoolean("naturesaura_plus:break_prevention") || left.getTag().getBoolean("Unbreakable") )
                return;
            if (left.is(ModTags.Items.CANNOT_APPLY_STEEL_TOKEN))
                return;
            if (left.isDamageableItem()) {
                ItemStack right = event.getRight();
                if (right.getItem() == ModItems.BREAK_PREVENTION.get())
                {
                    ItemStack output = left.copy();
                    output.getOrCreateTag().putBoolean("naturesaura_plus:break_prevention", true);
                    event.setOutput(output);
                    event.setCost(5);
                    event.setMaterialCost(1);
                }
            }
        }

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void onTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (!stack.hasTag())
                return;
            if (stack.getTag().getBoolean("naturesaura_plus:break_prevention")) {
                List<Component> tooltip = event.getToolTip();
                tooltip.add(Component.translatable("info.naturesaura_plus.break_prevention_token").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                if (!ElytraItem.isFlyEnabled(stack)) {
                    if (!tooltip.isEmpty()) {
                        Component head = tooltip.get(0);
                        if (head instanceof MutableComponent) {
                            ((MutableComponent)head).append(Component.translatable("info.naturesaura.broken").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
                        }

                    }
                }
            }
        }
    }
}
