package net.wkhan.naturesaura_plus.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class NaturesAuraPlusMixinPlugin implements IMixinConfigPlugin {

    private boolean isCreateLoaded;

    @Override
    public void onLoad(String s) {
        this.isCreateLoaded = LoadingModList.get().getModFileById("create") != null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("TreeFertilizerGrowFeatureEventPostMixin")) return isCreateLoaded;
        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
