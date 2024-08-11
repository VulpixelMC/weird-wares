package gay.sylv.weird_wares.api;

import gay.sylv.weird_wares.impl.DataAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * A class for accessing glint data in chunks.
 */
@SuppressWarnings("unused")
public interface GlintData {
	static @Nullable Set<BlockPos> getGlint(ChunkAccess chunk) {
		return DataAttachments.getGlint(chunk);
	}
	
	static Optional<Set<BlockPos>> getGlintOptional(ChunkAccess chunk) {
		return DataAttachments.getGlintOptional(chunk);
	}
	
	static void setGlint(ChunkAccess chunk, @NotNull Set<BlockPos> glint) {
		DataAttachments.setGlint(chunk, glint);
	}
	
	static void removeGlint(ChunkAccess chunk) {
		DataAttachments.removeGlint(chunk);
	}
}
