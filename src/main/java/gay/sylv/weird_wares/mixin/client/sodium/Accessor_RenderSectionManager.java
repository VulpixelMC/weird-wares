package gay.sylv.weird_wares.mixin.client.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSectionManager.class)
public interface Accessor_RenderSectionManager {
	@Accessor("renderLists")
	SortedRenderLists getRenderSections();
}
