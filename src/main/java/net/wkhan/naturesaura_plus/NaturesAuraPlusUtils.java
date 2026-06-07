package net.wkhan.naturesaura_plus;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.*;
import java.util.function.Predicate;

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
        private final Object[] buffer;
        private int head = 0;
        private int tail = 0;
        private int count = 0;

        public circularBuffer(int capacity) {
            this.capacity = capacity;
            this.buffer = new Object[capacity];
        }

        public T readObject() {
            T oldestBlock = (T) buffer[head];
            head = (head + 1) % capacity;
            count -= 1;
            return oldestBlock;
        }

        public void writeObject(T object) {
            buffer[tail] = object;
            if (count == capacity) {
                head = tail;
            }
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
            //buffer = new Object[capacity];
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
    }
}
