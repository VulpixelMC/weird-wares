package gay.sylv.weird_wares.impl.network;

import gay.sylv.weird_wares.impl.network.client.RequestGlintSyncPayload;
import gay.sylv.weird_wares.impl.network.server.ServerPackets;
import gay.sylv.weird_wares.impl.network.server.SyncGlintPayload;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Networking implements Initializable {
	public static final Networking INSTANCE = new Networking();
	
	private Networking() {}
	
	@Override
	public void initialize() {
		s2c(SyncGlintPayload.TYPE, SyncGlintPayload.CODEC);
		c2s(RequestGlintSyncPayload.TYPE, RequestGlintSyncPayload.CODEC);
		
		ServerPackets.INSTANCE.initialize();
	}
	
	private static <T extends CustomPacketPayload> void s2c(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		PayloadTypeRegistry.playS2C().register(type, codec);
	}
	
	private static <T extends CustomPacketPayload> void c2s(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		PayloadTypeRegistry.playC2S().register(type, codec);
	}
	
	@org.jetbrains.annotations.ApiStatus.Internal
	public static final class Codecs {
		public static final StreamCodec<RegistryFriendlyByteBuf, ChunkPos> CHUNK_POS = new StreamCodec<>() {
			@Override
			public void encode(RegistryFriendlyByteBuf buf, ChunkPos chunkPos) {
				buf.writeChunkPos(chunkPos);
			}
			
			@Override
			public @NotNull ChunkPos decode(RegistryFriendlyByteBuf buf) {
				return buf.readChunkPos();
			}
		};
		public static final StreamCodec<RegistryFriendlyByteBuf, Set<BlockPos>> POS_LIST = new StreamCodec<>() {
			@Override
			public void encode(RegistryFriendlyByteBuf buf, Set<BlockPos> list) {
				buf.writeCollection(list, BlockPos.STREAM_CODEC);
			}
			
			@Override
			public @NotNull Set<BlockPos> decode(RegistryFriendlyByteBuf buf) {
				return buf.readCollection(HashSet::new, BlockPos.STREAM_CODEC);
			}
		};
		
		private Codecs() {}
	}
}
