package gay.sylv.example_mod.impl;

import gay.sylv.example_mod.impl.util.Constants;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);
	
	@Override
	public void onInitialize() {
		LOGGER.info("Hello World!");
	}
}
