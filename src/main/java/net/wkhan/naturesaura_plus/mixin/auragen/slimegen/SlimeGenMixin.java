package net.wkhan.naturesaura_plus.mixin.auragen.slimegen;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySlimeSplitGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.data.auragen.AuraGenRules;
import net.wkhan.naturesaura_plus.data.duckfaces.SlimeGeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.SLIME_GENERATIONS;

@Mixin(BlockEntitySlimeSplitGenerator.class)
public abstract class SlimeGenMixin extends BlockEntityImpl implements SlimeGeneration {
    public SlimeGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow(remap = false) private int generationTimer;
    @Shadow(remap = false) private int amountToRelease;
    @Shadow(remap = false) private int color;

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$slimeTileAuraGeneratorTick(CallbackInfo ci) {
        ci.cancel();
        if (this.level.isClientSide() || this.level.getGameTime() % 10L != 0L) return;
        if (this.generationTimer <= 0) return;
        int amount = this.amountToRelease;
        if (this.canGenerateRightNow(amount)) {
            this.generateAura(amount);
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float)this.worldPosition.getX(), (float)this.worldPosition.getY(), (float)this.worldPosition.getZ(), PacketParticles.Type.SLIME_SPLIT_GEN_CREATE, this.color));
        }
        this.generationTimer -= 10;
    }

    @Override
    public void naturesaura_plus$slimeTileAuraGeneratorStart(Entity entity) {
        AuraGenRules.slimeValues slimeValues = SLIME_GENERATIONS.get(entity.getType());
        float size;
        size = slimeValues.sizeModifier();
        if (slimeValues.doSlimeSizeScaling()) {
            if (entity instanceof Slime slime) size = slime.getSize();
            else System.err.println("Entity is not a slime! Type: " + entity.getType() + "\nUsing size_modifier field as size instead.");
        }
        int genTime;
        if (slimeValues.isFlatGenerationTimer()) genTime = slimeValues.flatGenerationTimer();
        else genTime = Math.round(size * slimeValues.generationTimerModifier());
        this.generationTimer = genTime;
        this.amountToRelease = Math.round(size * slimeValues.auraAmount() / this.generationTimer);
        this.color = slimeValues.slimeColor();
        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                new PacketParticles(
                        (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), PacketParticles.Type.SLIME_SPLIT_GEN_START,
                        this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.color
                )
        );
    }
}
