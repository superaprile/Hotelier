package unipi.aprile.filippo.hotelier.server.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import unipi.aprile.filippo.hotelier.common.entities.HotelierUser;

public class HotelierServerLoginHandler {
	
	/**
	 * La classe HotelierServerLoginHandler si occupa della gestione delgi utenti loggati al sever, nel seguente modo:
	 * • metodo per aggiunta di utenti;
	 * • metodo per rimozione di utenti;
	 * • metodo per verifica se un utente.
	 * Viene utilizzato il pattern singletone per garantire un unica instanza condivisa da tutti i thread della pool del serverNIO.
	 * Utilizza sincronizzazione per garantire l'accesso/modifica concorrente degli utenti loggati. 
	 */

	private static HotelierServerLoginHandler instance = null;

	private HotelierServerLoginHandler() {
		loggedUsers = new ArrayList<>();
	}

	public static HotelierServerLoginHandler getInstance() {
		if (instance == null) {
			instance = new HotelierServerLoginHandler();
		}
		return instance;
	}
	
	// lista utenti loggati
	private List<HotelierUser> loggedUsers;
	
	
	// aggiunge utente alla lista di utenti loggati
	public synchronized void addUser(HotelierUser user) {

		loggedUsers.add(user);
	}
	
	// rimuove utente alla lista di utenti loggati 
	public synchronized void removeUser(HotelierUser user) {
		loggedUsers.remove(user);
	}
	
	// restituisce true se utente passato risulta loggato, false altrimenti
	public synchronized boolean isLoggedIn(HotelierUser user) {
		
		// itero la lista degli utenti loggati
		for (HotelierUser loggedUser : loggedUsers) {
			// controllo se username utente passato corrisponde a username loggedUser (ignoreCase)
			if (StringUtils.equalsIgnoreCase(loggedUser.getUsername(), user.getUsername())) {
				// restituisco true
				return true;
			}
		}
		// restituisco false
		return false;
	}

}
