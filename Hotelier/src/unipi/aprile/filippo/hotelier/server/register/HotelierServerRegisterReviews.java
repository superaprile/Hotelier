package unipi.aprile.filippo.hotelier.server.register;

import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.REVIEWS_PATH_JSON;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;
import unipi.aprile.filippo.hotelier.common.entities.HotelierReview;
import unipi.aprile.filippo.hotelier.common.entities.HotelierUser;
import unipi.aprile.filippo.hotelier.common.utils.HotelierCommonUtils;

public class HotelierServerRegisterReviews {
	
	/**
	 * La classe HotelierServerRegisterReviews gestisce il registro delle recensioni all' interno di Hotelier.
	 * Fornisce metodi per aggiungere recensioni, recuperare recensioni di un utente specifico e recensioni di un hotel specifico.
	 * Utilizza una lista sincronizzata per garantire l'accesso concorrente alle recensioni e offre funzionalità per la serializzazione
	 * e deserializzazione delle recensioni tramite JSON per la persistenza su disco.
	 */

	private static HotelierServerRegisterReviews instance = null;

	public static HotelierServerRegisterReviews getInstance() {
		if (instance == null) {
			instance = new HotelierServerRegisterReviews();
		}
		return instance;
	}
	
	// lista di recensioni del registro
	private List<HotelierReview> reviews;

	private HotelierServerRegisterReviews() {
		reviews = new ArrayList<>();
	}
	
	// aggiunge la recensione alla lista di recensioni del registro
	public void addReview(HotelierReview review) {
		// acquisisco la lock sulla lista delle recensioni 	
		synchronized (reviews) {
			// aggiungo la recensione alla lista
			reviews.add(review);
		}
	}
	
	// restituisce la lista di recensioni effettuate da un utente
	public List<HotelierReview> getUserReviews(HotelierUser user) {
		
		// creo una nuova lista di recensioni
		List<HotelierReview> userReviews = new LinkedList<>();
		// acquisisco la lock sulla lista delle recensioni 	
		synchronized (reviews) {
			// itero la lista di recensioni del registro
			for (HotelierReview review : reviews) {
				// controllo che username dell' utente passato corrisponda a quello presente nella recensione (ignoreCase)
				if (StringUtils.equalsIgnoreCase(user.getUsername(), review.getUsername())) {
					// aggiungo la recensione alla lista
					userReviews.add(review);
				}
			}
		}
		// restituisco la lista di recensioni
		return userReviews;
	}
	
	// restituisce la lista di recensioni relative ad hotel passato
	public List<HotelierReview> getHotelReviews(HotelierHotel hotel) {
		
		// creo una nuova lista di recensioni
		List<HotelierReview> hotelReviews = new LinkedList<>();
		// acquisisco la lock sulla lista delle recensioni 	
		synchronized (reviews) {
			// itero la lista di recensioni del registro
			for (HotelierReview review : reviews) {
				// controllo che id hotel passato corrisponda a quello presente nella recensione
				if (review.gethotelID() == hotel.getID()) {
					// aggiungo la recensione alla lista
					hotelReviews.add(review);
				}
			}
		}
		// restituisco la lista di recensioni
		return hotelReviews;
	}
	
	// persiste la lista delle recensioni del registro sul disco
	public void serialize() {

		try {
			// acquisisco la lock sulla lista delle recensioni 	
			synchronized (reviews) {
				// serializzo la lista delle recensione in Json
				String reviewsJson = HotelierCommonUtils.serialize(reviews);
				// scrivo la lista seriliazzata sul file al path REVIEWS_PATH_JSON
				HotelierCommonUtils.writeFile(reviewsJson, new File(REVIEWS_PATH_JSON));
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	// deserializza la lista delle recensioni da disco e li aggiunge alla lista delle recensioni del registro
	public void deserialize() {

		try {
			// acquisisco la lock sulla lista delle recensioni 	
			synchronized (reviews) {
				// ottengo il file contenente la lista delle recensioni
				var reviewFile = new File(REVIEWS_PATH_JSON);
				// leggo la lista delle recensioni serializzata in Json
				var reviewsJSON = HotelierCommonUtils.readFile(reviewFile);
				// deserializzo la lista delle recensioni
				var deserializedReviews = Arrays.asList(HotelierCommonUtils.deserialize(reviewsJSON, HotelierReview[].class));
				// aggiungo la lista delle recensioni deserializzata alla lista delle recensioni del registro
				reviews.addAll(deserializedReviews);
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
