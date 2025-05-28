package gay.sylv.weird_wares.mixin.client.sodium;

import gay.sylv.weird_wares.impl.client.render.Rendering;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SodiumWorldRenderer.class)
@Pseudo
public class Mixin_SodiumWorldRenderer {
	@Shadow
	private RenderSectionManager renderSectionManager;
	
	@Inject(
			method = "drawChunkLayer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;renderLayer(Lnet/caffeinemc/mods/sodium/client/render/chunk/ChunkRenderMatrices;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;DDD)V",
					ordinal = 2
			)
	)
	private void onDrawChunkLayer(RenderType renderLayer, ChunkRenderMatrices matrices, double x, double y, double z, CallbackInfo ci) {
		Rendering.renderGlint(((Accessor_RenderSectionManager) this.renderSectionManager).getRenderSections(), matrices, x, y, z);
	}
}
