package net.wkhan.naturesaura_plus.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> NATURES_AURA_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "naturesaura");

    public static final RegistryObject<Block> STRIPPED_ANCIENT_LOG = NATURES_AURA_BLOCKS.register("stripped_ancient_log",
            () -> new ModWoodBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));

    public static final RegistryObject<Block> STRIPPED_ANCIENT_BARK = NATURES_AURA_BLOCKS.register("stripped_ancient_bark",
            () -> new ModWoodBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));

    public static void register(IEventBus eventBus) {
        NATURES_AURA_BLOCKS.register(eventBus);
    }
}
