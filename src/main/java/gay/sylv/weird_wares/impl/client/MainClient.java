/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.client;

import gay.sylv.weird_wares.impl.network.client.ClientPackets;
import gay.sylv.weird_wares.impl.util.Constants;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.jetbrains.annotations.ApiStatus.Internal
public final class MainClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME + "/Client");
	
	@Override
	public void onInitializeClient() {
		ClientPackets.INSTANCE.initialize();
	}
}
