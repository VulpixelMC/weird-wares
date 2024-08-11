/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.network.server;

import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.network.client.RequestGlintSyncPayload;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Handles packets received on the server-side.
 */
@org.jetbrains.annotations.ApiStatus.Internal
public final class ServerPackets implements Initializable {
	public static final ServerPackets INSTANCE = new ServerPackets();
	
	private ServerPackets() {}
	
	@Override
	public void initialize() {
		ServerPlayNetworking.registerGlobalReceiver(RequestGlintSyncPayload.TYPE, (payload, context) -> {
			//noinspection resource
			DataAttachments
					.getGlintOptional(context.player().level().getChunk(payload.chunkPos().x, payload.chunkPos().z))
					.filter(glints -> !glints.isEmpty())
					.ifPresent(glints -> ServerPlayNetworking.send(context.player(), new SyncGlintPayload(payload.chunkPos(), glints)));
		});
	}
}
