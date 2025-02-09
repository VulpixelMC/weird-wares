/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.network.client;

import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.client.MainClient;
import gay.sylv.weird_wares.impl.network.server.SyncGlintPayload;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Handles packets received on the client-side.
 */
@org.jetbrains.annotations.ApiStatus.Internal
public final class ClientPackets implements Initializable {
	public static final ClientPackets INSTANCE = new ClientPackets();
	
	private ClientPackets() {}
	
	@Override
	public void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(SyncGlintPayload.TYPE, (payload, context) -> {
			MainClient.LOGGER.info("Received SyncGlintPayload");
			//noinspection resource
			DataAttachments.setGlint(context.player().level().getChunk(payload.chunkPos().x, payload.chunkPos().z), payload.glints());
		});
	}
}
