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

    @SerializedName("block")
    private String blockId;

    @SerializedName("item")
    private String itemId;

    @SerializedName("priority")
    private int priority = 0;

    private transient Block cachedBlock;
    private transient Item cachedItem;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }

    /**
     * Resolves string IDs to actual Registry objects.
     * @return true if valid, false if invalid.
     */
    public boolean resolve() {
        if (rulesResolved) return true;

        if (!itemResolve()) {
            logError("Failed to resolve Item ID: '" + itemId + "'");
            return false;
        }

        if (!blockResolve()) {
            logError("Failed to resolve Block ID: '" + blockId + "'");
            return false;
        }

        this.rulesResolved = true;
        return true;
    }

    private boolean itemResolve(){
        if (itemId == null || itemId.isEmpty()) {
            System.err.println("Block Rule Error: Missing Item ID'" + itemId + "'");
            return false;
        }
        if (itemId.equals("*")) {
            this.cachedItem = null; // Wildcard: matches any item
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(itemId);
        if (loc != null && ForgeRegistries.ITEMS.containsKey(loc)) {
            this.cachedItem = ForgeRegistries.ITEMS.getValue(loc);
            return true;
        } else {
            System.err.println("Block Rule Error: Invalid Item ID '" + itemId + "'");
            return false;
        }
    }

    private boolean blockResolve() {
        if (blockId == null || blockId.isEmpty()) {
            System.err.println("Block Rule Error: Missing Block ID'" + blockId + "'");
            return false;
        }
        if (blockId.equals("*")) {
            this.cachedBlock = null; // Wildcard: matches any block
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(blockId);
        if (loc != null && ForgeRegistries.BLOCKS.containsKey(loc)) {
            this.cachedBlock = ForgeRegistries.BLOCKS.getValue(loc);
            return true;
        } else {
            System.err.println("Block Rule Error: Invalid Block ID '" + blockId + "'");
            return false;
        }
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

    private void logError(String message) {
        if (sourceFile != null) {
            System.err.println("Block Rule Error in " + sourceFile + ": " + message);
        } else {
            System.err.println("Block Rule Error: (Invalid SourceFile? <- Seen when sourceFile resolves to null) " + message);
        }
    }

    public Item getTargetItem() { return cachedItem;}
    public Block getTargetBlock() { return cachedBlock; }
    public String getRawBlockId() { return blockId; }

    @Override
    public int compareTo(BlockInteractionRule other) {
        // Sort specifically by priority (Higher number = processed first)
        return Integer.compare(other.priority, this.priority); //Dont ask why this even exists.
    }
}