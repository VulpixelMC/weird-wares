/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.network.client;

import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.network.server.AddGlintPayload;
import gay.sylv.weird_wares.impl.network.server.RemoveGlintPayload;
import gay.sylv.weird_wares.impl.network.server.SyncGlintPayload;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles packets received on the client-side.
 */
@org.jetbrains.annotations.ApiStatus.Internal
public final class ClientPackets implements Initializable {
	public static final ClientPackets INSTANCE = new ClientPackets();
	
	private ClientPackets() {}
	
	@Override
	public void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(
				SyncGlintPayload.TYPE,
				(payload, context) -> DataAttachments
						.setGlint(
								context
										.player()
										.level()
										.getChunk(
												payload.chunkPos().x,
												payload.chunkPos().z
										),
								payload.glints()
						)
		);
		ClientPlayNetworking.registerGlobalReceiver(
				AddGlintPayload.TYPE,
				(payload, context) -> {
					LevelChunk chunk = context
							.player()
							.level()
							.getChunk(
									payload.chunkPos().x,
									payload.chunkPos().z
							);
					Set<BlockPos> glints = DataAttachments.getGlint(chunk);
					glints.addAll(payload.glints());
					DataAttachments.setGlint(chunk, glints);
				}
		);
		ClientPlayNetworking.registerGlobalReceiver(
				RemoveGlintPayload.TYPE,
				(payload, context) -> {
					LevelChunk chunk = context
							.player()
							.level()
							.getChunk(
									payload.chunkPos().x,
									payload.chunkPos().z
							);
					DataAttachments
							.setGlint(
									chunk,
									DataAttachments
											.getGlint(chunk)
											.stream()
											.filter(pos -> !payload.glints().contains(pos))
											.collect(Collectors.toSet())
							);
				}
		);
	}
}
