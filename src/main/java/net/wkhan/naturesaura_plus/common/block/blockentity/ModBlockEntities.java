package net.wkhan.naturesaura_plus.common.block.blockentity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.common.block.blockentity.oven.OvenCoreBlockEntity;
import net.wkhan.naturesaura_plus.common.block.blockentity.oven.OvenEdgeBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES, NaturesAuraPlus.MODID);

    public static final RegistryObject<BlockEntityType<OvenCoreBlockEntity>> OVEN_CORE = BLOCK_ENTITIES.register("oven_core",
            () -> BlockEntityType.Builder.of(OvenCoreBlockEntity::new, ModBlocks.AURIC_OVEN_BRICK.get()).build(null));
    public static final RegistryObject<BlockEntityType<OvenEdgeBlockEntity>> OVEN_EDGE = BLOCK_ENTITIES.register("oven_edge",
            () -> BlockEntityType.Builder.of(OvenEdgeBlockEntity::new, ModBlocks.AURIC_OVEN_BRICK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
