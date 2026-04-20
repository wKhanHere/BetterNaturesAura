package net.wkhan.naturesaura_plus.common.data.auragen;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class MossGenRule {

    @SerializedName("block_to_convert")
    private String blockInputId;

    @SerializedName("block_result")
    private String blockResultId;

    @SerializedName("aura_gain")
    private int auraAmount;

    private transient Block cachedBlockInput;
    private transient TagKey<Block> cachedBlockInputTag;
    private transient Block cachedBlockResult;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public boolean resolve() {
        if (rulesResolved) return true;

        if (!blockInputResolve()) {
            logError("Failed to resolve Block (Input Conversion By MossGen) ID: '" + blockInputId + "'");
            return false;
        }
        if (!blockResultResolve()) {
            logError("Failed to resolve Block (Result Conversion By MossGen) ID: '" + blockResultId + "'");
            return false;
        }

        this.rulesResolved = true;
        return true;
    }

    private boolean blockInputResolve(){
        if (blockInputId == null || blockInputId.isEmpty()) {
            System.err.println("MossAuraGen Rule Error: Missing Block (Input Conversion By MossGen) ID'" + blockInputId + "'");
            return false;
        }
        if (blockInputId.startsWith("#")) {
            String tagId = blockInputId.substring(1);
            this.cachedBlockInputTag = TagKey.create(Registries.BLOCK, new ResourceLocation(tagId));
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(blockInputId);
        if (loc != null && ForgeRegistries.BLOCKS.containsKey(loc)) {
            this.cachedBlockInput = ForgeRegistries.BLOCKS.getValue(loc);
            return true;
        } else {
            System.err.println("MossAuraGen Rule Error: Invalid Block (Input Conversion By MossGen) ID '" + blockInputId + "'");
            return false;
        }
    }

    private boolean blockResultResolve(){
        if (blockResultId == null || blockResultId.isEmpty()) {
            System.err.println("MossAuraGen Rule Error: Missing (Result Conversion By MossGen) ID'" + blockResultId + "'");
            return false;
        }
        if (blockResultId.startsWith("#")) {
            System.err.println("MossAuraGen Rule Error: Tags must not be used in result field -> '" + blockResultId + "'");
            return false;
        }
        ResourceLocation loc = ResourceLocation.tryParse(blockResultId);
        if (loc != null && ForgeRegistries.BLOCKS.containsKey(loc)) {
            this.cachedBlockResult = ForgeRegistries.BLOCKS.getValue(loc);
            return true;
        } else {
            System.err.println("MossAuraGen Rule Error: (Result Conversion By MossGen) ID'" + blockInputId + "'");
            return false;
        }
    }


    private void logError(String message) {
        if (sourceFile != null) {
            System.err.println("MossAuraGen Rule Error in " + sourceFile + ": " + message);
        } else {
            System.err.println("MossAuraGen Rule Error: (Invalid SourceFile? <- Seen when sourceFile resolves to null) " + message);
        }
    }


    public Block getBlockInput() {
        return this.cachedBlockInput;
    }
    public Block getBlockResult() {
        return this.cachedBlockResult;
    }
    public TagKey<Block> getBlockInputTag() {
        return this.cachedBlockInputTag;
    }
    public int getAuraAmount() {
        return auraAmount;
    }
}
