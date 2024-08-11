/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Constants {
	public static final String MOD_ID = "weird-wares";
	public static final String MOD_NAME = "Weird Wares";
	public static final float INFINITE_BLAST_RESISTANCE = 3_600_000.0f;
	
	private Constants() {}
	
	public static ResourceLocation modId(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}
	
	public static boolean isEnvironment(EnvType envType) {
		return FabricLoader.getInstance().getEnvironmentType() == envType;
	}
	
	public static boolean isClient() {
		return isEnvironment(EnvType.CLIENT);
	}
	
	public static boolean hasSodium() {
		return FabricLoader.getInstance().isModLoaded("sodium");
	}
}
