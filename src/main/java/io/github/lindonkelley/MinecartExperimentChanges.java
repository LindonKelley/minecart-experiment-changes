package io.github.lindonkelley;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecartExperimentChanges implements ModInitializer {
	public static final String MOD_ID = "minecart_experiment_changes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("hello");
	}
}