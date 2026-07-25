package net.wkhan.naturesaura_plus.compat.kubejs.event;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class CalculateAuraGenByFireworkEvent extends EventJS {
    public final ItemStack stack;
    private int auraToGenerate;
    private int releaseTimer;
    private boolean kubeOverrideForAuraGen;
    private List<Integer> colors;

    public CalculateAuraGenByFireworkEvent(ItemStack stack) {
        this.stack = stack;
    }

    public void setAuraGen(int aura, int releaseTimer,@Nullable List<Integer> colors) {
        this.auraToGenerate = aura;
        this.releaseTimer = releaseTimer;
        this.colors = colors;
        this.kubeOverrideForAuraGen = true;
    }

    public int getAuraToGenerate() {
        return this.auraToGenerate;
    }

    public int getReleaseTimer() {
        return this.releaseTimer;
    }

    @Nullable
    public List<Integer> getColors() {
        return this.colors;
    }

    public boolean isKubeOverrideForAuraGen() {
        return this.kubeOverrideForAuraGen;
    }
}
