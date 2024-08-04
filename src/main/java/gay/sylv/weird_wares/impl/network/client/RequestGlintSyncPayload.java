package gay.sylv.weird_wares.impl.network.client;

import gay.sylv.weird_wares.impl.network.Networking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

public record RequestGlintSyncPayload(ChunkPos chunkPos) implements CustomPacketPayload {
	public static final Type<RequestGlintSyncPayload> TYPE = new Type<>(modId("request_glint_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestGlintSyncPayload> CODEC = StreamCodec.composite(
			Networking.Codecs.CHUNK_POS, RequestGlintSyncPayload::chunkPos,
			RequestGlintSyncPayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
