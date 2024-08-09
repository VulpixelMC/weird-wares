package gay.sylv.weird_wares.impl.compat.client;

import gay.sylv.weird_wares.impl.duck.Accessor_LevelRenderer;
import gay.sylv.weird_wares.impl.util.Constants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A utility class for Sodium compatibility.
 * <h2>Warning!</h2>
 * This class is not guaranteed to maintain compatibility between Sodium versions.
 */
@Environment(EnvType.CLIENT)
public final class SodiumCompatibility {
	public static Set<SectionPos> getRenderedSections(LevelRenderer levelRenderer) {
		if (Constants.hasSodium() && Minecraft.getInstance().cameraEntity != null) {
			int renderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();
			return SectionPos.cube(SectionPos.of(Minecraft.getInstance().cameraEntity.position()), renderDistance)
					.filter(sectionPos -> {
						BlockPos origin = sectionPos.origin();
						BlockPos originLast = origin.offset(15, 15, 15);
						return SodiumWorldRenderer.instance().isBoxVisible(origin.getX(), origin.getY(), origin.getZ(), originLast.getX(), originLast.getY(), originLast.getZ());
					})
					.collect(Collectors.toSet());
		} else {
			ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections = ((Accessor_LevelRenderer) levelRenderer).weird_wares$getVisibleSections();
			return visibleSections
					.stream()
					.map(renderSection -> SectionPos.of(renderSection.getOrigin()))
					.collect(Collectors.toSet());
		}
	}
}