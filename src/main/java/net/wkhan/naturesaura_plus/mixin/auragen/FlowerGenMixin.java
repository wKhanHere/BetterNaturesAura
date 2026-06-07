package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFlowerGenerator;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.wkhan.naturesaura_plus.Config.*;
import static net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules.FLOWER_GENERATIONS;
import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.circularBuffer;

@Mixin(BlockEntityFlowerGenerator.class)
public class FlowerGenMixin extends BlockEntityImpl {
    public FlowerGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private int naturesaura_plus$vitality = 100;

    @Unique private final circularBuffer<Block> naturesaura_plus$flowerMemory = new circularBuffer<>(flowerGenMemorySize) {};

    //Magic particles are copied straight from Elpeck's code
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$flowerAuraGenerator(CallbackInfo ci) {
        ci.cancel();
        Level level = this.level;
        if (level == null) return;
        if (level.isClientSide || level.getGameTime() % 10 != 0L) return;

        List<BlockPos> possible = new ArrayList<>();
        int range = flowerGenRange;
        for(int x = -range; x <= range; ++x) {
            for(int y = -1; y <= 1; ++y) {
                for(int z = -range; z <= range; ++z) {
                    BlockPos offset = this.worldPosition.offset(x, y, z);
                    BlockState state = level.getBlockState(offset);
                    if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
                            state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) continue;
                    Block block = state.getBlock();
                    if (FLOWER_GENERATIONS.containsKey(block)) {
                        possible.add(offset);
                    }
                }
            }
        }
        if (possible.isEmpty()) return;

        BlockPos pos = possible.get(level.random.nextInt(possible.size()));
        Block flower = level.getBlockState(pos).getBlock();
        naturesaura_plus$flowerMemory.writeObject(flower);
        this.setChanged();
        int repeatFlower = naturesaura_plus$flowerMemory.countObject(flower) - 1;
        AuraGenRules.flowerValues stats = FLOWER_GENERATIONS.get(flower);
        int lucidity = stats.lucidity();
        double auraFactor = (1 - Math.pow(((double) (100 - this.naturesaura_plus$vitality)/flowerGenVitalityFloor),flowerGenPowFactor));
        System.out.println("Before toAdd: " + auraFactor);
        System.out.println(stats + "\n Repeat Flower:" + repeatFlower + "\n Vitality:" + naturesaura_plus$vitality);
        int auraAmount = (int) (stats.auraAmount() * auraFactor);
        int toAdd = Math.max(0, auraAmount);

        if (lucidity != 0 && repeatFlower == 0) {
            System.out.println("Before Increase " + this.naturesaura_plus$vitality);
            if (this.naturesaura_plus$vitality != 100) this.naturesaura_plus$vitality = Math.min(this.naturesaura_plus$vitality + lucidity,100);
            System.out.println("After Increase " + this.naturesaura_plus$vitality);

        }
        else if (this.naturesaura_plus$vitality != 0) {
            System.out.println("Before Decrease " + this.naturesaura_plus$vitality);
            int obscurity = (int) (stats.obscurity() * Math.pow(stats.obscurityScale(),repeatFlower));
            this.naturesaura_plus$vitality = Math.max(this.naturesaura_plus$vitality - obscurity, 0);
            System.out.println("After Decrease " + this.naturesaura_plus$vitality);
        }

        //this.sendToClients(); // Not implemented, idk how this works yet

        if (toAdd > 0) {
            if (IAuraType.forLevel(level).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) && this.canGenerateRightNow(toAdd)) {
                this.generateAura(toAdd);
            } else {
                toAdd = 0;
            }
        }
        level.removeBlock(pos, false);
        int color = Helper.blendColors(6081584, 15023126, (float) auraFactor);
        if (toAdd > 0) {
            for(int i = level.random.nextInt(5) + 5; i >= 0; --i) {
                PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticleStream((float)pos.getX() + 0.25F + level.random.nextFloat() * 0.5F, (float)pos.getY() + 0.25F + level.random.nextFloat() * 0.5F, (float)pos.getZ() + 0.25F + level.random.nextFloat() * 0.5F, (float)this.worldPosition.getX() + 0.25F + level.random.nextFloat() * 0.5F, (float)this.worldPosition.getY() + 0.25F + level.random.nextFloat() * 0.5F, (float)this.worldPosition.getZ() + 0.25F + level.random.nextFloat() * 0.5F, level.random.nextFloat() * 0.02F + 0.1F, color, 1.0F));
            }

            PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticles((float)this.worldPosition.getX(), (float)this.worldPosition.getY(), (float)this.worldPosition.getZ(), PacketParticles.Type.FLOWER_GEN_AURA_CREATION));
        }
        PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticles((float)pos.getX(), (float)pos.getY(), (float)pos.getZ(), PacketParticles.Type.FLOWER_GEN_CONSUME, color));

    }


    @Inject(
            method = "writeNBT",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$writeNBT(CompoundTag compound, SaveType type, CallbackInfo ci) {
        ci.cancel();
        super.writeNBT(compound, type);
        if (type == SaveType.SYNC) return;
        IntTag vitality = IntTag.valueOf(naturesaura_plus$vitality);
        compound.put("vitality", vitality);
        if (naturesaura_plus$flowerMemory.isEmpty()) return;
        ListTag flowerMemoryList = new ListTag();
        Object[] flowerMemoryBuffer = naturesaura_plus$flowerMemory.getBuffer(); //Might wanna move this setup to be the circular buffer's own method
        int capacity = naturesaura_plus$flowerMemory.getCapacity();

        for (int i = 0; i < flowerMemoryBuffer.length; i++) {
            int orderedIndex = (naturesaura_plus$flowerMemory.getHead() + i) % capacity;
            Block flower = (Block) flowerMemoryBuffer[orderedIndex];

            if (flower == null) continue;
            CompoundTag tag = new CompoundTag();
            tag.putString("block", ForgeRegistries.BLOCKS.getKey(flower).toString());
            flowerMemoryList.add(tag);

        }

        compound.put("flower_memory", flowerMemoryList);
    }

    @Inject(
            method = "readNBT",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$readNBT(CompoundTag compound, SaveType type, CallbackInfo ci) {
        ci.cancel();
        super.readNBT(compound, type);
        if (type == SaveType.SYNC) return;
        naturesaura_plus$vitality = compound.getInt("vitality");
        for(Tag t : compound.getList("flower_memory", 10)) {
            CompoundTag tag = (CompoundTag)t;
            Block flower = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(tag.getString("block")));
            if (flower == null) continue;
            naturesaura_plus$flowerMemory.writeObject(flower);
        }
    }
}
