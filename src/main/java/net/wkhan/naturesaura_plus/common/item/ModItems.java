package net.wkhan.naturesaura_plus.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NaturesAuraPlus.MODID);

    public static final RegistryObject<Item> BREAK_PREVENTION = ITEMS.register("break_prevention_token",
            () -> new ItemBreakPreventionAll(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COFFEE = ITEMS.register("coffee",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat().nutrition(4).saturationMod(0.8f)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1), 1).build())
                    .stacksTo(16).rarity(Rarity.COMMON)) {
                @Override public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
                    return UseAnim.DRINK;
                }
                @Override public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> toolTip, TooltipFlag p_41424_) {
                    toolTip.add(Component.translatable("info.naturesaura_plus.coffee")
                            .setStyle(Style.EMPTY.withItalic(true).applyFormat(ChatFormatting.GRAY)));
                }
            });

    public static final RegistryObject<Item> AURA_COFFEE = ITEMS.register("aura_coffee",
            () -> new ItemRecallCoffee(new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
                    .nutrition(1).saturationMod(0.2f).build())
                    .stacksTo(16).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> STRIPPED_ANCIENT_LOG = ITEMS.register("stripped_ancient_log",
            () -> new BlockItem(ModBlocks.STRIPPED_ANCIENT_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Item> STRIPPED_ANCIENT_BARK = ITEMS.register("stripped_ancient_bark",
            () -> new BlockItem(ModBlocks.STRIPPED_ANCIENT_BARK.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
