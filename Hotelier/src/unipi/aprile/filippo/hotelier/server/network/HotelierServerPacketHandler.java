package unipi.aprile.filippo.hotelier.server.network;

import java.util.Collections;
import java.util.Comparator;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;
import unipi.aprile.filippo.hotelier.common.entities.HotelierReview;
import unipi.aprile.filippo.hotelier.common.entities.HotelierUser;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacket;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketBadge;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketBadgeResponse;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketErrorResponse;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketHotel;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketHotelList;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketHotelListResponse;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketHotelResponse;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketLogin;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketLoginResponse;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketLogout;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketLogoutResponse;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketReview;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketReviewResponse;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterHotels;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterReviews;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterUsers;

public class HotelierServerPacketHandler {
	
	/**
	 * La classe HotelierServerPacketHandler gestisce i pacchetti ricevuti dal client 
	 * e li smista ai metodi appropriati per la loro elaborazione.
	 * La classe utilizza i registri per utenti, hotel e recensioni per mantenere e aggiornare 
	 * le informazioni necessarie e il loginHandler per gestire le sessioni di login degli utenti.
	 */
	
	// utente relativo alla connessione Tcp, utilizzato per controllare se loggato o meno
	private HotelierUser userClient;
	// registro degli hotel
	private final HotelierServerRegisterHotels hotelRegister;
	// registro degli utenti
	private final HotelierServerRegisterUsers userRegister;
	// registro delle recensioni
	private final HotelierServerRegisterReviews reviewRegister;
	// handler utenti loggati
	private final HotelierServerLoginHandler loginHandler;

	public HotelierServerPacketHandler() {
		// recupero istanza registro degli hotel
		hotelRegister = HotelierServerRegisterHotels.getInstance();
		// recupero istanza registro degli utenti
		userRegister = HotelierServerRegisterUsers.getInstance();
		// recupero istanza registro delle recensioni
		reviewRegister = HotelierServerRegisterReviews.getInstance();
		// recupero istanza handler login
		loginHandler = HotelierServerLoginHandler.getInstance();
	}

	// restituisce pacchetto di risposta in base al pacchetto passato come paramentro
	public HotelierPacket handlePacket(HotelierPacket packet) {
		
		// filtro rispetto a instanza del pacchetto passato
		return switch (packet) {
			case HotelierPacketLogin loginPacket -> handleLoginPacket((HotelierPacketLogin) packet);
			case HotelierPacketLogout logoutPacket -> handleLogoutPacket((HotelierPacketLogout) packet);
			case HotelierPacketHotel hotelPacket -> handleHotelPacket((HotelierPacketHotel) packet);
			case HotelierPacketHotelList hotelListPacket -> handleHotelListPacket((HotelierPacketHotelList) packet);
			case HotelierPacketReview reviewPacket -> handleReviewPacket((HotelierPacketReview) packet);
			case HotelierPacketBadge badgePacket -> handleBadgePacket((HotelierPacketBadge) packet);
			default -> packetErrorResponse("Errore: Pacchetto non supportato!");
		};
	}
	
	// restitusce pacchetto di risposta login in caso di successo, pacchetto di errore in caso di fallimento
	private HotelierPacket handleLoginPacket(HotelierPacketLogin packet) {

		// ottengo username e password dal pacchetto
		var username = packet.getUsername();
		var password = packet.getPassword();

		// controllo se utente ha già effettuato il login 
		if (userClient != null) {
			// restituisco un pacchetto di errore in cui chiedo di fare il logout per effetter un nuovo login
			return packetErrorResponse("Login già effettuato per utente " + userClient.getUsername() + "! Eseguire logout per effetturare un nuovo login");
		}
		
		// controllo se esiste utente avente username passato
		if(userRegister.getUserByName(username) == null) {
			// restituisco un pacchetto di errore in cui chiedo di effettuare la registrazione
			return packetErrorResponse("Utente non esiste, si prega di effettuare la registrazione!");
		}
		// recupero utente avente username e password passati
		var user = userRegister.auth(username, password);
		// controllo che la password passata sia corretta
		if (user == null) {
			// restituisco un pacchetto di errore in cui notfico password errata
			return packetErrorResponse("Password errata!");
		}
		
		// controllo se è già attiva un sessione di login per user su un altro client
		if (loginHandler.isLoggedIn(user)) {

			// restituisco un pacchetto di errore in cui notifico sessione già attiva per utente user su un altro client
			return packetErrorResponse("Sessione già attiva per utente " + user.getUsername() + " su un altro client");
		}
		// utente tovato e login non ancora effettuata
		// assegno a userClient utente trovato per memorizzarne il login senza dover tutte le volte passare dal loginHandler
		userClient = user;
		// aggiungo userClient alla lista di utenti loggati
		loginHandler.addUser(userClient);

		// restituisco un pacchetto di risposta in cui notifico il login avvenuto con successo
		HotelierPacketLoginResponse packetLoginResponse = new HotelierPacketLoginResponse("Login effettuato correttamente!");
		return packetLoginResponse;

	}
	
	// restitusce pacchetto di risposta logout in caso di successo, pacchetto di errore in caso di fallimento
	private HotelierPacket handleLogoutPacket(HotelierPacketLogout packet) {

		// controllo se utente non ha effettuato il login
		if (userClient == null) {
			// restituisco un pacchetto di errore in cui chiedo di effettuare il login per poter effettuare il logout
			return packetErrorResponse("Utente non loggato. Effettua il login prima di eseguire il logout.");
		}

		// utente loggato
		// rimuovo utente alla lista di utenti loggati
		loginHandler.removeUser(userClient);
		// Resetto userClient a null 
		userClient = null;
		// restituisco un pacchetto di risposta in cui notifico il logout avvenuto con successo
		HotelierPacketLogoutResponse packetLogoutResponse = new HotelierPacketLogoutResponse("Logout effettuato correttamente!");
		return packetLogoutResponse;
	}
	
	// restitusce pacchetto di risposta hotel in caso di successo, pacchetto di errore in caso di fallimento
	private HotelierPacket handleHotelPacket(HotelierPacketHotel packet) {

		// ottengo nome e citta dell' hotel 
		var hotelName = packet.getHotelName();
		var city = packet.getCity();
		// ottengo hotel avente nome e città passati
		var hotel = hotelRegister.getHotelByNameAndCity(hotelName, city);
		// hotel non trovato
		if (hotel == null) {
			// restituisco un pacchetto di errore in cui notifico che l' hotel non esiste
			return packetErrorResponse("Hotel non trovato.");
		}

		//hotel trovato
		// restituisco un pacchetto di risposta contentente hotel richiesto
		HotelierPacketHotelResponse packetHotelResponse = new HotelierPacketHotelResponse(hotel);
		return packetHotelResponse;
	}
	
	// restitusce pacchetto di risposta hotelList in caso di successo, pacchetto di errore in caso di fallimento
	private HotelierPacket handleHotelListPacket(HotelierPacketHotelList packet) {

		// ottengo città degli hotel 
		var city = packet.getCity();
		// ottengo lista di hotel aventi città passata
		var hotels = hotelRegister.getHotelsByCity(city);
		// nessun hotel trovato
		if (hotels.isEmpty()) {
			// restituisco un pacchetto di errore in cui notifico che non esiste nessun hotel per quella città
			return packetErrorResponse("Nessun hotel trovato.");
		}
		// hotel trovati
		// ordino lista di hotel in modo crescente rispetto al rank locale
		Collections.sort(hotels, Comparator.comparingInt(HotelierHotel::getLocalRank));
		// restituisco un pacchetto di risposta contentente la lista di hotel ordinata
		HotelierPacketHotelListResponse packetHotelListResponse = new HotelierPacketHotelListResponse(hotels);
		return packetHotelListResponse;
	}
	
	// restitusce pacchetto di risposta review in caso di successo, pacchetto di errore in caso di fallimento
	private HotelierPacket handleReviewPacket(HotelierPacketReview packet) {

		// controllo se utente non ha effettuato il login
		if (userClient == null) {
			// restituisco un pacchetto di errore in cui chiedo di effettuare il login per inserire una recensione
			return packetErrorResponse("Utente non loggato. Effettua il login per inserire una recensione.");
		}
		
		// utente loggato
		// ottengo nome hotel, citta , rate e rating dell' hotel
		var hotelName = packet.getHotelName();
		var city = packet.getCity();
		var rate = packet.getRate();
		var rating = packet.getRatings();

		// ottengo hotel avente nome e città passati
		var hotel = hotelRegister.getHotelByNameAndCity(hotelName, city);
		// hotel non trovato
		if (hotel == null) {
			// restituisco un pacchetto di errore in cui notifico che l' hotel non esiste e quindi la recensione non è stata registrata
			return packetErrorResponse("Recensione non registata. Hotel non trovato");
		}
		
		// hotel trovato
		// creo una nuova recensione avente parametri passati
		var review = new HotelierReview(userClient.getUsername(), hotel.getID(), rate, rating);
		// aggiungo la recensione alla lista di recensioni del registro 
		reviewRegister.addReview(review);
		// peristo la lista delle recensione del registro sul disco
		reviewRegister.serialize();
		// incremento il numero di recensioni effettuate dall' utente userClient di 1
		userClient.incrementReviewCount();
		// controllo se è stato raggiunto un nuovo livello di esperienza e in caso setto il badge di utente userClient di conseguenza
		userClient.updateBadge();
		// peristo la lista degli utenti del registro sul disco
		userRegister.serialize();
		// calcolo il nuovo rate medio 
		updateHotelRate(hotel, review);
		// calcolo i nuovi punteggi medi: cleaning, position, servicese quality dell' hotel e li aggiorni
		updateHotelRating(hotel, review);
		// incremento il numero di recensioni relative all' hotel di 1
		hotel.incrementReviewCount();
		// Aggiorni il rate medio dell' hotel
		// peristo la lista degli hotel del registro sul disco
		hotelRegister.serialize();

		// restituisco un pacchetto di risposta in cui notifico la registrazione della recensione avvenuto con successo
		HotelierPacketReviewResponse packetReviewResponse = new HotelierPacketReviewResponse("Recensione registrata con successo.");
		return packetReviewResponse;
	}
	
	// restitusce pacchetto di risposta badge in caso di successo, pacchetto di errore in caso di fallimento
	private HotelierPacket handleBadgePacket(HotelierPacketBadge packet) {

		// controllo se utente non ha effettuato il login
		if (userClient == null) {
			// restituisco un pacchetto di errore in cui chiedo di effettuare il login per richieder il badge
			return packetErrorResponse("Utente non loggato. Effettua il login per richiedere il badge.");
		}
		// utente loggato
		// restituisco un pacchetto di risposta contentente badge dell' utente
		HotelierPacketBadgeResponse packetBadgeResponse = new HotelierPacketBadgeResponse(userClient.getBadge());
		return packetBadgeResponse;
	}

	// Metodo per gestire la disconnessione del client
	public void handleClientDisconnect() {
		
		// controllo se userClient era loggato
		if (userClient != null) {
			
			// rimuovo userClient dalla lista di utenti loggati
			loginHandler.removeUser(userClient);
			// resetto userClient a null
			userClient = null;
		}
	}
	
	// restituisce pacchetto di errore avente messaggio passato
	private HotelierPacketErrorResponse packetErrorResponse(String message) {

		HotelierPacketErrorResponse errorResponse = new HotelierPacketErrorResponse("[ERRORE] " + message);
		return errorResponse;
	}

	// calcola il nuovo rate medio di hotel e lo aggiorna
	private void updateHotelRate(HotelierHotel hotel, HotelierReview review) {

		var reviewCount = hotel.getReviewCount();
		var avgRate = calculateNewAvg(reviewCount, hotel.getRate(), review.getRate());
		hotel.setRate(avgRate);
	}
	
	// calcola i nuovi punteggi medi di hotel e li aggiorna
	private void updateHotelRating(HotelierHotel hotel, HotelierReview review) {
		
		var hotelRating = hotel.getRating();
		var reviewRating = review.getRating();
		var reviewCount = hotel.getReviewCount();

		var avgCleaning = calculateNewAvg(reviewCount, hotelRating.getCleaning(), reviewRating.getCleaning());
		hotelRating.setCleaning(avgCleaning);
		var avgPosition = calculateNewAvg(reviewCount, hotelRating.getPosition(), reviewRating.getPosition());
		hotelRating.setPosition(avgPosition);
		var avgServices = calculateNewAvg(reviewCount, hotelRating.getServices(), reviewRating.getServices());
		hotelRating.setServices(avgServices);
		var avgQuality = calculateNewAvg(reviewCount, hotelRating.getQuality(), reviewRating.getQuality());
		hotelRating.setQuality(avgQuality);
	}
	
	// restituisce nuovo valore medio arrotondato a una cifra decimale tramite ultimo valore medio, nuovo valore e numero di valori
	private float calculateNewAvg(int reviewCount, float avg, float value) {

		var totalScore = avg * reviewCount;
		var newAvg = (totalScore + value) / (reviewCount + 1);
		return Math.round(newAvg * 10.0f) / 10.0f;
	}

}
