package unipi.aprile.filippo.hotelier.server.register;

import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.HOTELS_PATH_JSON;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;
import unipi.aprile.filippo.hotelier.common.utils.HotelierCommonUtils;

public class HotelierServerRegisterHotels {
	
	/**
	 * La classe singletone HotelierServerRegisterHotels gestisce il registro degli hotel all' interno di Hotelier.
	 * Fornisce metodi per recuperare, aggiornare e gestire informazioni sugli hotel, inclusa la ricerca per ID, nome e città.
	 * Utilizza una lista sincronizzata per garantire l'accesso concorrente agli hotel e offre funzionalità per la serializzazione
	 * e deserializzazione degli hotel tramite JSON per la persistenza su disco.
	 */
	
	private static HotelierServerRegisterHotels instance = null;

	public static HotelierServerRegisterHotels getInstance() {
		if (instance == null) {
			instance = new HotelierServerRegisterHotels();
		}
		return instance;
	}
	
	// lista degli hotel del registro
	private List<HotelierHotel> hotels;

	private HotelierServerRegisterHotels() {
		// inzializzo la lista di hotel a ArryList
		hotels = new ArrayList<>();
	}
	
	// restituisce hotel avente id passato, null se hotel non trovato
	public HotelierHotel getHotelByID(int hotelID) {
	// acquisisco la lock sulla lista di hotel 	
	synchronized (hotels) {
			// itero la lista di tutti gli hotel del registro
			for (var hotel : hotels) {
				// controllo se id hotel corrisponde a quello passato
				if (hotel.getID() == hotelID) {
					// restituisco hotel
					return hotel;
				}
			}
		}
		// restituisco null
		return null;
	}
	
	// restituisce list di hotel aventi città passata
	public List<HotelierHotel> getHotelsByCity(String city) {
		
		// creo una nuova lista di hotel
		List<HotelierHotel> hotelsCity = new ArrayList<>();
		// acquisisco la lock sulla lista di hotel 	
		synchronized (hotels) {
			// itero la lista di tutti gli hotel del registro
			for (HotelierHotel hotel : hotels) {
				// controllo se città hotel corrisponde a quella passato (ingnoreCase)
				if (StringUtils.equalsIgnoreCase(hotel.getCity(), city)) {
					// aggiungo hotel a hotelsCity
					hotelsCity.add(hotel);
				}
			}
		}
		// restituisco la lista di hotel
		return hotelsCity;
	}
	
	// restituisce hotel avente id e città passati, null se hotel non trovato
	public HotelierHotel getHotelByNameAndCity(String hotelName, String city) {
		
		// ottengo la lista di hotel aventi città passata
		List<HotelierHotel> cityHotels = getHotelsByCity(city);
		// acquisisco la lock sulla lista di hotel 	
		synchronized (hotels) {
			// itero la lista di hotel
			for (var hotel : cityHotels) {
				// controllo se nome hotel corrisponde a quella passato (ingnoreCase)
				if (StringUtils.equalsIgnoreCase(hotel.getName(), hotelName)) {
					// restituisco hotel 
					return hotel;
				}
			}
		}
		// restituisco null
		return null;
	}
	
	// restituisce la lista di tutte le città degli hotel presenti nel registro
	public List<String> getCities() {
		
		// creo un set di città per evitare duplicati
		Set<String> cities = new HashSet<>();
		// acquisisco la lock sulla lista di hotel 	
		synchronized (hotels) {
			// itero la lista di tutti gli hotel del registro
			for (HotelierHotel hotel : hotels) {
				// aggiungo hotel a hotelsCity
				cities.add(hotel.getCity());
			}
		}
		
		// restituisco lista delle città
		return new ArrayList<>(cities);
	}

	// restituisce lista degli hotel del registro
	public List<HotelierHotel> getHotels() {
		// restituisco lista degli hotel del registro
		return hotels;
	}

	// persiste la lista di hotel del registro sul disco
	public void serialize() {

		try {
			// acquisisco la lock sulla lista di hotel 	
			synchronized (hotels) {
				// serializzo la lista di hotel in Json
				String hotelsJson = HotelierCommonUtils.serialize(hotels);
				// scrivo la lista seriliazzata sul file al path HOTELS_PATH_JSON
				HotelierCommonUtils.writeFile(hotelsJson, new File(HOTELS_PATH_JSON));
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	// deserializza la lista di hotel da disco e li aggiunge alla lista di hotel del registro
	public void deserialize() {

		try {
			// acquisisco la lock sulla lista di hotel 	
			synchronized (hotels) {
				// ottengo il file contenente la lista di hotel
				var hotelFile = new File(HOTELS_PATH_JSON);
				// leggo la lista di hotel serializzata in Json
				var hotelsJSON = HotelierCommonUtils.readFile(hotelFile);
				// deserializzo la lista di hotel 
				var deserializedHotels = Arrays.asList(HotelierCommonUtils.deserialize(hotelsJSON, HotelierHotel[].class));
				// aggiungo la lista di hotel deserializzata alla lista di hotel del registro
				hotels.addAll(deserializedHotels);
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
