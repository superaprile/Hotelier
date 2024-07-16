package unipi.aprile.filippo.hotelier.server.config;

import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.SERVER_CONFIG_PATH_JSON;

import java.io.File;
import java.io.IOException;

import unipi.aprile.filippo.hotelier.common.utils.HotelierCommonUtils;

public class HotelierServerConfigManager {

	private static HotelierServerConfig serverConfiguration;
	
	// crea un file json config server di default e lo serializza su disco 
	public static void createDefaultConfig() {
		try {
			serverConfiguration = new HotelierServerConfig(4316, 1099, 49152, 10, "localhost", "HOTELIER-SERVICE", "230.0.0.0");
			var configJSON = HotelierCommonUtils.serialize(serverConfiguration);
			HotelierCommonUtils.writeFile(configJSON, new File(SERVER_CONFIG_PATH_JSON));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	} 
	
	// deserializza da disco il file json config 
	public static void loadServerConfig() {
		try {
			var configJSON = HotelierCommonUtils.readFile(new File(SERVER_CONFIG_PATH_JSON));
			serverConfiguration = HotelierCommonUtils.deserialize(configJSON, HotelierServerConfig.class);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	
	// restituisce server config
	public static HotelierServerConfig getServerConfig() {
		return serverConfiguration;
	}

}
