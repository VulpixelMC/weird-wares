package gay.sylv.weird_wares.impl.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public final class Constants {
	public static final String MOD_ID = "weird-wares";
	public static final String MOD_NAME = "Weird Wares";
	
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
}
