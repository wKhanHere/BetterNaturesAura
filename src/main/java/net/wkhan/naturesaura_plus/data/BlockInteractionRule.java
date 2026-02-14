package net.wkhan.naturesaura_plus.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInteractionRule implements Comparable<BlockInteractionRule> {

    // --- JSON DATA FIELDS ---

    @SerializedName("block") // Maps to "block": "minecraft:grass_block"
    private String blockId;

    @SerializedName("item") // Maps to "item": "minecraft:shears"
    private String itemId;

    @SerializedName("priority")
    private int priority = 0; //Default to 0 if not specified.


    // --- RUNTIME CACHE ---
    private transient Block cachedBlock;
    private transient Item cachedItem;
    private transient boolean rulesResolved = false;


    // --- LOGIC ---

    /**
     * Resolves string IDs to actual Registry objects.
     * @return true if valid, false if IDs are invalid/typos.
     */
    public boolean resolve() {
        if (rulesResolved) return true;

        // 1. Resolve Block (if specified)
        if (blockId != null && !blockId.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(blockId);
            if (loc != null && ForgeRegistries.BLOCKS.containsKey(loc)) {
                this.cachedBlock = ForgeRegistries.BLOCKS.getValue(loc);
            } else {
                System.err.println("Block Rule Error: Invalid Block ID '" + blockId + "'");
                return false;
            }
        }

        // 2. Resolve Item (if specified)
        if (itemId != null && !itemId.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(itemId);
            if (loc != null && ForgeRegistries.ITEMS.containsKey(loc)) {
                this.cachedItem = ForgeRegistries.ITEMS.getValue(loc);
            } else {
                System.err.println("Block Rule Error: Invalid Item ID '" + itemId + "'");
                return false;
            }
        }

        this.rulesResolved = true;
        return true;
    }

    public boolean matches(ItemStack stack, BlockState state) {
        if (!rulesResolved) resolve();

        // 1. Check Block
        if (cachedBlock != null) {
            if (!state.is(cachedBlock)) {
                return false;
            }
        }

        // 2. Check Item
        if (cachedItem != null) {
            return stack.is(cachedItem);
        }

        return true;
    }

    // --- GETTERS ---
    public int getPriority() { return priority; }
    // For sorting/manager use
    public Block getTargetBlock() { return cachedBlock; }
    public String getRawBlockId() { return blockId; }

    @Override
    public int compareTo(BlockInteractionRule other) {
        // Sort specifically by priority (Higher number = processed first)
        return Integer.compare(other.priority, this.priority);
    }
}