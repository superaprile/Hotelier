package unipi.aprile.filippo.hotelier.server.register;

import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.USERS_PATH_JSON;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import unipi.aprile.filippo.hotelier.common.entities.HotelierUser;
import unipi.aprile.filippo.hotelier.common.utils.HotelierCommonUtils;

public class HotelierServerRegisterUsers {
	
	/**
	 * La classe HotelierServerRegisterUsers gestisce il registro degli utenti all'interno di  Hotelier.
	 * Fornisce metodi per l'autenticazione degli utenti, la registrazione di nuovi utenti, il recupero di utenti
	 * per username.
	 * Utilizza una lista sincronizzata per garantire l'accesso concorrente agli utentu e offre funzionalità per la serializzazione
	 * e deserializzazione degli utenti tramite JSON per la persistenza su disco.
	 */
	

	private static HotelierServerRegisterUsers instance = null;

	public static HotelierServerRegisterUsers getInstance() {
		if (instance == null) {
			instance = new HotelierServerRegisterUsers();
		}
		return instance;
	}
	
	// lista utenti del registro
	private List<HotelierUser> users;

	private HotelierServerRegisterUsers() {
		users = new ArrayList<>();
	}
	
	
	// restituisce utente avente username e password passati per parametro, null se utente non trovato
	public HotelierUser auth(String username, String password) {
		// acquisico la lock sulla lista degli utenti
		synchronized (users) {
			// itero la lista di utenti
			for (HotelierUser user : users) {
				// controllo se username e password user corrispondono a quelli passati (ignoreCase)
				if (StringUtils.equalsIgnoreCase(username, user.getUsername()) && StringUtils.equalsIgnoreCase(password, user.getPassword())) {
					// restituisco utente
					return user;
				}
			}
		}
		// restituisco null
		return null;
	}
	
	// restituisce risposta (esito) registrazione nuovo utente avente username e password passati per parametro
	public String register(String username, String password) {

		// controllo se almeno uno dei campi inviati dal client sono vuoti
		if (username.isEmpty() || password.isEmpty()) {
			// restituisco messaggio errore corrispondente
			return "[ERRORE] Username / Password non possono essere vuoti !";
		}
		
		// controllo se nome utente o password contengono degli spazi
		if (StringUtils.containsWhitespace(username) || password.isEmpty()) {
			// restituisco messaggio errore corrispondente
			return "[ERRORE] Username/password non possono contenere spazi !";
		}
		
		// acquisisco la lock sulla lista di utenti 
		synchronized (users) {
			// itero la lista di utenti
			for (HotelierUser user : users) {
				// controllo se username già registrato
				if (StringUtils.equalsIgnoreCase(username, user.getUsername())) {
					// restituisco messaggio errore corrispondente
					return "[ERRORE] Username già registrato !";
				}
			}
			
			// creo una nuova utente avente username e password passati
			HotelierUser user = new HotelierUser(username, password);
			// aggiungo l'utente alla lista di utenti del registro
			users.add(user);
		}
		
		// persisto la lista aggiornata su disco
		serialize();
		
		// restituisco messaggio di successo
		return "Utente : " + username + " registrato con successo !";
	}

	// restituisco utente che ha come username quello passato per parametro, null se utente non trovato
	public HotelierUser getUserByName(String username) {
		// acquisisco la lock sulla lista di utenti 
		synchronized (users) {
			// itero la lista di utenti
			for (HotelierUser user : users) {
				// controllo se username user corrisponde a quello passato (ignoreCase)
				if (StringUtils.equalsIgnoreCase(username, user.getUsername())) {
					// restituisco utente
					return user;
				}
			}
		}
		//restituisco null
		return null;
	}
	
	// persiste la lista degli utenti del registro sul disco
	public void serialize() {
		
		try {
			// acquisisco la lock sulla lista di utenti 
			synchronized (users) {
				// serializzo la lista di utenti in Json
				String usersJson = HotelierCommonUtils.serialize(users);
				// scrivo la lista seriliazzata sul file al path USERS_PATH_JSON
				HotelierCommonUtils.writeFile(usersJson, new File(USERS_PATH_JSON));
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void deserialize() {
		
		try {
			// acquisisco la lock sulla lista di utenti 
			synchronized (users) {
				// ottengo il file contenente la lista di utenti
				var userFile = new File(USERS_PATH_JSON);
				// leggo la lista di utenti serializzata in Json
				var usersJSON = HotelierCommonUtils.readFile(userFile);
				// deserializzo la lista di utenti
				var deserializedUsers = Arrays.asList(HotelierCommonUtils.deserialize(usersJSON, HotelierUser[].class));
				// aggiungo la lista di utenti deserializzata alla lista di utenti del registro
				users.addAll(deserializedUsers);
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
