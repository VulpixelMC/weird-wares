package gay.sylv.weird_wares.mixin.client;

import gay.sylv.weird_wares.impl.duck.Accessor_LevelRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public abstract class Mixin_LevelRenderer implements Accessor_LevelRenderer {
	private Mixin_LevelRenderer() {}
	
	@Override
	@Accessor("visibleSections")
	public abstract ObjectArrayList<SectionRenderDispatcher.RenderSection> weird_wares$getVisibleSections();
}
