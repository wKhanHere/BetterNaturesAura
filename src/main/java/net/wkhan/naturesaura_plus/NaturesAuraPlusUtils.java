package net.wkhan.naturesaura_plus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;
import net.wkhan.naturesaura_plus.data.TreeRitualTreeTracker;
import net.wkhan.naturesaura_plus.data.duckfaces.AbstractWoodStand;
import net.wkhan.naturesaura_plus.data.duckfaces.MultiBlockUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NaturesAuraPlusUtils {
    public static Set<BlockPos> crawlConnectedBlocks(Level level, BlockPos startPos, int maxBlocks, Predicate<BlockState> isValid) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            BlockPos current = queue.poll();

            // 26-Way Volumetric Check (Checks all diagonals and corners)
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos neighbor = current.offset(x, y, z);

                        if (visited.contains(neighbor) || !isValid.test(level.getBlockState(neighbor))) continue;
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return visited;
    }

    public static List<BlockPos> crawlConnectedBlocks(Level level, BlockPos startPos, int maxBlocks,
                                                     Predicate<BlockState> stemTest, Predicate<BlockState> capTest) {
        List<BlockPos> visited = new ArrayList<>();
        Set<BlockPos> visitedCheckList = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(startPos);
        visited.add(startPos);
        visitedCheckList.add(startPos);

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            BlockPos current = queue.poll();
            List<BlockPos> visitedTransient = new ArrayList<>();
            int currentChanges = 0;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        BlockPos neighbor = current.offset(x, y, z);
                        if (visitedCheckList.contains(neighbor)) continue;
                        BlockState neighbourState = level.getBlockState(neighbor);
                        boolean isStem = stemTest.test(neighbourState);
                        boolean isCap = capTest.test(neighbourState);
                        if (isStem) {
                            visitedTransient.add(neighbor);
                            visitedCheckList.add(neighbor);
                            queue.add(neighbor);
                            currentChanges++;
                            continue;
                        }
                        if (isCap) {
                            visitedTransient.add(neighbor);
                            visitedCheckList.add(neighbor);
                            currentChanges++;
                        }
                    }
                }
            }
            if (currentChanges > 1) visitedTransient.sort(Comparator.comparingDouble(pos -> pos.distSqr(current)));
            visited.addAll(visitedTransient);
        }
        return visited;
    }

    public static List<BlockPos> scanSphereAgainstTag(Level level, BlockPos center, int radius, TagKey<Block> targetTag) {
        List<BlockPos> foundTargets = new ArrayList<>();

        long radiusSq = (long) radius * radius;

        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;

        int minY = Math.max(level.getMinBuildHeight(), center.getY() - radius);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, center.getY() + radius);

        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;

        for (int cX = minChunkX; cX <= maxChunkX; cX++) {
            for (int cZ = minChunkZ; cZ <= maxChunkZ; cZ++) {

                ChunkAccess chunk = level.getChunk(cX, cZ, ChunkStatus.FULL, false);
                if (chunk == null) continue;

                LevelChunkSection[] sections = chunk.getSections();

                for (int i = 0; i < sections.length; i++) {
                    LevelChunkSection section = sections[i];
                    if (section == null || section.hasOnlyAir()) continue;

                    int sectionBottomY = level.getMinBuildHeight() + (i * 16);
                    int sectionTopY = sectionBottomY + 15;

                    if (sectionTopY < minY || sectionBottomY > maxY) continue;

                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {

                                int worldY = sectionBottomY + y;
                                if (worldY < minY || worldY > maxY) continue;

                                int worldX = (cX << 4) + x;
                                int worldZ = (cZ << 4) + z;

                                long dx = worldX - center.getX();
                                long dy = worldY - center.getY();
                                long dz = worldZ - center.getZ();

                                if (!(dx * dx + dy * dy + dz * dz <= radiusSq)) continue;
                                BlockState state = section.getBlockState(x, y, z);
                                if (state.is(targetTag)) foundTargets.add(new BlockPos(worldX, worldY, worldZ));
                            }
                        }
                    }
                }
            }
        }

        return foundTargets;
    }

    public static class circularBuffer<T> {
        private final int capacity;
        private Object[] buffer;
        private int head = 0;
        private int tail = 0;
        private int count = 0;

        public circularBuffer(int capacity) {
            if (capacity < 1) throw new IllegalArgumentException("Capacity must be at least 1");
            this.capacity = capacity;
            this.buffer = new Object[capacity];
        }

        public T readObject() {
            T oldestBlock = (T) buffer[head];
            buffer[head] = null; //not thought of well yet
            head = (head + 1) % capacity;
            count -= 1;
            return oldestBlock;
        }

        public void writeObject(T object) {
            buffer[tail] = object;
            if (count == capacity)  head = (head + 1) % capacity;
            else count++;
            tail = (tail + 1) % capacity;
        }

        public int countObject(T targetObject) {
            int count = 0;
            for (Object o : buffer) {
                if (o == null) continue;
                if (o == targetObject || o.equals(targetObject)) {
                    count++;
                }
            }
            return count;
        }

        public int countObjectAny() {
            int count = 0;
            for (Object o : buffer) {
                if (o == null) continue;
                count++;
            }
            return count;
        }

        public boolean isEmpty() {
            return buffer.length == 0;
        }

        public Object[] getBuffer() {
            return buffer;
        }

        public void clear() {
            head = 0;
            tail = 0;
            count = 0;
            buffer = new Object[capacity];
        }
        
        public int getHead() {
            return head;
        }

        public int getTail() {
            return tail;
        }

        public int getCapacity(){
            return capacity;
        }

        public int getRepeatAfterSimulatedPush(T objectToBeInserted, int countAllowed) {
            int currentRepeat = this.countObject(objectToBeInserted) - countAllowed;
            if (currentRepeat < 0) return 0;
            if (count < capacity) return currentRepeat + 1;
            boolean overwritingTarget = Objects.equals(buffer[head], objectToBeInserted);
            if (overwritingTarget) return currentRepeat;
            return currentRepeat + 1;
        }
    }

    public static <T> Codec<Either<T, TagKey<T>>> elementOrTagCodec(IForgeRegistry<T> forgeRegistry, ResourceKey<Registry<T>> registryKey) {
        return Codec.STRING.comapFlatMap(str -> {
                    if (str.startsWith("#")) {
                        ResourceLocation tagId = ResourceLocation.tryParse(str.substring(1));
                        if (tagId != null) return DataResult.success(Either.right(TagKey.create(registryKey, tagId)));
                        return DataResult.error(() -> "Invalid Tag: '" + str + "'");
                    }
                    ResourceLocation elementId = ResourceLocation.tryParse(str);
                    if (elementId != null && forgeRegistry.containsKey(elementId)) return DataResult.success(Either.left(forgeRegistry.getValue(elementId)));
                    return DataResult.error(() -> "Unknown Registry ID: '" + str + "'");
                },
                either -> either.map(
                        element -> forgeRegistry.getKey(element).toString(),
                        tag -> "#" + tag.location()
                )
        );
    }

    public static <T> List<T> generateListFromEither(Either<T,TagKey<T>> either, IForgeRegistry<T> forgeRegistry) {
        return either.map(
                List::of,
                right -> forgeRegistry.tags().getTag(right).stream().toList()
        );
    }

    public static <T> T updateWoodStandMemoryIfRitual(Level level, BlockPos saplingPos, Supplier<T> originalCall, Predicate<T> grewSuccessfully) {
        if (level == null || level.isClientSide())
            return originalCall.get();

        ((MultiBlockUtil) Multiblocks.TREE_RITUAL).naturesaura_plus$allowAirInRitual(true);
        boolean isRitual = Multiblocks.TREE_RITUAL.isComplete(level, saplingPos);
        ((MultiBlockUtil) Multiblocks.TREE_RITUAL).naturesaura_plus$allowAirInRitual(false);
        if (!isRitual)
            return originalCall.get();

        Set<BlockPos> capturedStems = new HashSet<>();
        Set<BlockPos> capturedLeaves = new HashSet<>();
        Set<BlockPos> capturedDecorators = new HashSet<>();
        TreeRitualTreeTracker.STEM_CACHE.set(capturedStems);
        TreeRitualTreeTracker.LEAF_CACHE.set(capturedLeaves);
        TreeRitualTreeTracker.DECORATOR_CACHE.set(capturedDecorators);

        T result;
        try {
            result = originalCall.get();
        } finally {
            TreeRitualTreeTracker.STEM_CACHE.remove();
            TreeRitualTreeTracker.LEAF_CACHE.remove();
            TreeRitualTreeTracker.DECORATOR_CACHE.remove();
        }

        if (!grewSuccessfully.test(result) || capturedStems.isEmpty())
            return result;

        for (BlockPos leafPos : capturedLeaves) {
            BlockState leafState = level.getBlockState(leafPos);
            if (leafState.hasProperty(BlockStateProperties.PERSISTENT) && !leafState.getValue(BlockStateProperties.PERSISTENT))
                level.setBlock(leafPos, leafState.setValue(BlockStateProperties.PERSISTENT, true), 3);
        }

        Multiblocks.TREE_RITUAL.forEach(saplingPos, 'W', (standPos, matcher) -> {
            BlockEntity tile = level.getBlockEntity(standPos);
            if (tile instanceof AbstractWoodStand woodStand) {
                woodStand.naturesaura_plus$setTreeStemCache(capturedStems.isEmpty() ? null : capturedStems);
                woodStand.naturesaura_plus$setTreeLeafCache(capturedLeaves.isEmpty() ? null : capturedLeaves);
                woodStand.naturesaura_plus$setTreeDecoratorCache(capturedDecorators.isEmpty() ? null : capturedDecorators);
            }
            return true;
        });
        return result;
    }

    public static void inventoryAuraContainerTick(@NotNull ItemStack stackIn, @NotNull Level levelIn,
                                                  @NotNull Entity entityIn, int itemSlot) {
        if (levelIn.isClientSide() || !(entityIn instanceof Player player) || !player.isShiftKeyDown() || stackIn.isEmpty())
            return;
        LazyOptional<IAuraContainer> containerCap = stackIn.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER);
        if (!containerCap.isPresent())
            return;
        Optional<IAuraContainer> optionalCap = containerCap.resolve();
        if (optionalCap.isEmpty())
            return;
        Inventory inventory = player.getInventory();
        IAuraContainer container = optionalCap.get();
        int[] slotsToRecharge = new int[]{
                inventory.selected, // Mainhand-slot
                40,                 // Offhand-slot
                36, 37, 38, 39      // Armor-slots (Boots, Leggings, Chestplate, Helmet)
        };

        for (int i : slotsToRecharge) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            LazyOptional<IAuraRecharge> recharge = stack.getCapability(NaturesAuraAPI.CAP_AURA_RECHARGE);
            if (recharge.isPresent() && recharge.resolve().isPresent()) {
                boolean isSelectedItem = (i == inventory.selected || i == 40);
                if (recharge.resolve().get().rechargeFromContainer(container, itemSlot, i, isSelectedItem))
                    break;
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
}
