/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.network.server;

import gay.sylv.weird_wares.impl.network.Networking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public record AddGlintPayload(ChunkPos chunkPos, Set<BlockPos> glints) implements CustomPacketPayload {
	public static final Type<AddGlintPayload> TYPE = new Type<>(modId("add_glint"));
	public static final StreamCodec<RegistryFriendlyByteBuf, AddGlintPayload> CODEC = StreamCodec.composite(
			Networking.Codecs.CHUNK_POS, AddGlintPayload::chunkPos,
			Networking.Codecs.POS_LIST, AddGlintPayload::glints,
			AddGlintPayload::new
	);
	
	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
