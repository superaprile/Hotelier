package unipi.aprile.filippo.hotelier.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HotelierCommonUtils {
	
	/**
	 * La classe `HotelierCommonUtils` fornisce metodi di utilità per la serializzazione, 
	 * deserializzazione, lettura e scrittura di file su disco (config e strutture da persistere).
	 * I file vegono serializzati e deserializzati in Json con prety printing.
	 */

	private static final Gson gson;

	static {
		gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	}

	public static String readFile(File file) throws IOException {

		StringBuilder text = new StringBuilder();

		try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				text.append(line).append(System.lineSeparator());
			}

			return text.toString();
		}
	}

	public static void writeFile(String text, File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(text);
		}
	}

	public static String serialize(Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T deserialize(String jsonData, Class<T> objectClass) {

		return gson.fromJson(jsonData, objectClass);

	}

}