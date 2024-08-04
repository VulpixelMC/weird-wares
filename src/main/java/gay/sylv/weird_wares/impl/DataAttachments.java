package gay.sylv.weird_wares.impl;

import gay.sylv.weird_wares.impl.util.Codecs;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public final class DataAttachments implements Initializable {
	public static final DataAttachments INSTANCE = new DataAttachments();
	
	public static AttachmentType<List<BlockPos>> GLINT;
	
	private DataAttachments() {}
	
	@Override
	public void initialize() {
		GLINT = AttachmentRegistry.<List<BlockPos>>builder()
				.initializer(ArrayList::new)
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
	
	public static List<BlockPos> getGlint(ChunkAccess chunk) {
		return getAttachedOrCreate(chunk, GLINT);
	}
	
	public static void setGlint(ChunkAccess chunk, List<BlockPos> glint) {
		setAttached(chunk, GLINT, glint);
	}
	
	public static Optional<List<BlockPos>> getGlintOptional(ChunkAccess chunk) {
		return Optional.ofNullable(getGlint(chunk));
	}
}
