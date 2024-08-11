/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.duck;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

@org.jetbrains.annotations.ApiStatus.Internal
public interface Accessor_LevelRenderer {
	ObjectArrayList<SectionRenderDispatcher.RenderSection> weird_wares$getVisibleSections();
}
