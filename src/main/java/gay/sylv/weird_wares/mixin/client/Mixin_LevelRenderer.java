/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
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
