package gay.sylv.weird_wares.impl.client;

import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.client.render.Rendering;
import gay.sylv.weird_wares.impl.network.client.ClientPackets;
import gay.sylv.weird_wares.impl.network.client.RequestGlintSyncPayload;
import gay.sylv.weird_wares.impl.util.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@org.jetbrains.annotations.ApiStatus.Internal
public final class MainClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME + "/Client");
	
	@Override
	public void onInitializeClient() {
		Rendering.INSTANCE.initialize();
		ClientPackets.INSTANCE.initialize();
		
		ClientChunkEvents.CHUNK_LOAD.register((_, chunk) -> ClientPlayNetworking.send(new RequestGlintSyncPayload(chunk.getPos())));
		
		ClientPlayerBlockBreakEvents.AFTER.register((level, _, pos, _) -> {
			ChunkAccess chunk = level.getChunk(pos);
			Set<BlockPos> glints = DataAttachments.getGlint(chunk);
			if (glints.contains(pos)) {
				glints.remove(pos);
				DataAttachments.setGlint(chunk, glints);
				
				SectionPos sectionPos = SectionPos.of(pos);
				Rendering.markGlintDirty(sectionPos);
			}
		});
	}
}
