package unipi.aprile.filippo.hotelier.client.config;

import static unipi.aprile.filippo.hotelier.client.utils.HotelierClientUtils.CLIENT_CONFIG_PATH_JSON;

import java.io.File;
import java.io.IOException;

import unipi.aprile.filippo.hotelier.common.utils.HotelierCommonUtils;

public class HotelierClientConfigManager {

	private static HotelierClientConfig clientConfiguration;
	
	// crea un file json config client di default e lo serializza su disco 
	public static void createDefaultConfig() {
		try {
			clientConfiguration = new HotelierClientConfig(4316, 1099, 49152, "localhost", "HOTELIER-SERVICE", "230.0.0.0");
			var configJSON = HotelierCommonUtils.serialize(clientConfiguration);
			HotelierCommonUtils.writeFile(configJSON, new File(CLIENT_CONFIG_PATH_JSON));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	// deserializza da disco il file json config 
	public static void loadClientConfig() {
		try {
			var configJSON = HotelierCommonUtils.readFile(new File(CLIENT_CONFIG_PATH_JSON));
			clientConfiguration = HotelierCommonUtils.deserialize(configJSON, HotelierClientConfig.class);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	// restituisce config client
	public static HotelierClientConfig getClientConfig() {
		return clientConfiguration;
	}

}
