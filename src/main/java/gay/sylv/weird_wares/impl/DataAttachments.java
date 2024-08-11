/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl;

import gay.sylv.weird_wares.impl.util.Codecs;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@SuppressWarnings({"unused", "UnstableApiUsage"})
@org.jetbrains.annotations.ApiStatus.Internal
public final class DataAttachments implements Initializable {
	public static final DataAttachments INSTANCE = new DataAttachments();
	
	public static AttachmentType<Set<BlockPos>> GLINT;
	
	private DataAttachments() {}
	
	@Override
	public void initialize() {
		GLINT = AttachmentRegistry.<Set<BlockPos>>builder()
				.initializer(HashSet::new)
				.persistent(Codecs.POS_LIST)
				.buildAndRegister(modId("glint"));
	}
	
	public static <A> A getAttachedOrCreate(ChunkAccess chunk, AttachmentType<A> attachmentType) {
		return chunk.getAttachedOrCreate(attachmentType);
	}
	
	public static <A> void setAttached(ChunkAccess chunk, AttachmentType<A> attachmentType, A attachment) {
		chunk.setAttached(attachmentType, attachment);
	}
	
	public static <A> A getAttached(ChunkAccess chunk, AttachmentType<A> attachmentType) {
		return chunk.getAttached(attachmentType);
	}
	
	public static <A> void removeAttached(ChunkAccess chunk, AttachmentType<A> attachmentType) {
		chunk.removeAttached(attachmentType);
	}
	
	public static Set<BlockPos> getGlint(ChunkAccess chunk) {
		return getAttachedOrCreate(chunk, GLINT);
	}
	
	public static void setGlint(ChunkAccess chunk, Set<BlockPos> glint) {
		setAttached(chunk, GLINT, glint);
	}
	
	public static Optional<Set<BlockPos>> getGlintOptional(ChunkAccess chunk) {
		return Optional.ofNullable(getGlint(chunk));
	}
	
	public static void removeGlint(ChunkAccess chunk) {
		removeAttached(chunk, GLINT);
	}
}
