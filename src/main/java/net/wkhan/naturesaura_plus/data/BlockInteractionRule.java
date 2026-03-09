package net.wkhan.naturesaura_plus.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class BlockInteractionRule {

    @SerializedName("block")
    private String blockId;

    @SerializedName("item")
    private String itemId;

    private transient Block cachedBlock;
    private transient TagKey<Block> cachedBlockTag;
    private transient Item cachedItem;
    private transient TagKey<Item> cachedItemTag;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

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
            this.cachedItem = null;
            return true;
        }
        if (itemId.startsWith("#")) {
            String tagId = itemId.substring(1);
            this.cachedItemTag = TagKey.create(Registries.ITEM, new ResourceLocation(tagId));
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
            this.cachedBlock = null;
            return true;
        }
        if (blockId.startsWith("#")) {
            String tagId = blockId.substring(1);
            this.cachedBlockTag = TagKey.create(Registries.BLOCK, new ResourceLocation(tagId));
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

        if (cachedBlock != null) {
            if (state == null) return true;
            if (!state.is(cachedBlock)) return false;
        }
        if (cachedBlockTag != null) {
            if (!Objects.requireNonNull(ForgeRegistries.BLOCKS.tags()).getTag(cachedBlockTag).contains(state.getBlock())) return false;
        }
        if (cachedItem != null) {
            if (stack == null) return true;
            if (!stack.is(cachedItem)) return false;
        }
        if (cachedItemTag != null) {
            if (!Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(cachedItemTag).contains(stack.getItem())) return false;
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

    public Item getTargetItem() {
        return cachedItem;
    }
    public Block getTargetBlock() {
        return cachedBlock;
    }
    public String getRawBlockId() {
        return blockId;
    }
    public String getRawItemId() {
        return itemId;
    }
    public TagKey<Block> getTargetBlockTag() {
        return cachedBlockTag;
    }
    public TagKey<Item> getTargetItemTag() {
        return cachedItemTag;
    }
}