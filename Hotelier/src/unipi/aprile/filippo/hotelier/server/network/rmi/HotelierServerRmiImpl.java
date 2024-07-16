package unipi.aprile.filippo.hotelier.server.network.rmi;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierClientInterface;
import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierServerInterface;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterUsers;

public class HotelierServerRmiImpl implements HotelierServerInterface {
	
	/**
	 * La classe HotelierServerRmiImpl fornisce le funzionalità necessarie per gestire invocazione remota dei metodi da clientRmi e callbacks. 
	 * Per le callbacks utilizza una mappa delle callbacks (localRankMap), avente: 
	 * • chiave: interfaccia client rmi; 
	 * • valore: la lista delle città di interesse. 
	 * La classe utilizza la sincronizzazione per garantire la coerenza dei dati quando accede/modifica la mappa delle callback.
	 */
	
	// mappa delle callbacks con chiave: interfaccia client rmi e valore: la lista delle città di interesse
	private final Map<HotelierClientInterface, List<String>> clientsCallback;
	// registro utenti Hotelier
	private HotelierServerRegisterUsers userRegistry;

	public HotelierServerRmiImpl() {
		userRegistry = HotelierServerRegisterUsers.getInstance();
		clientsCallback = new HashMap<>();
	}
	
	// restituisce risposta (esito) richiesta di registrazione di un nuovo utente
	@Override
	public String registerUser(String username, String password) throws RemoteException {
		return userRegistry.register(username, password);
	}
	
	// registra callbacks per le città di interesse dell' interfaccia rmi client passato
	@Override
	public synchronized void registerCallback(HotelierClientInterface callbackClient, List<String> cities) throws RemoteException {
		// aggiunge stub client e relativa lista delle città di interesse alla mappa
		clientsCallback.put(callbackClient, cities);
	}
	
	// deregistra callbacks per interfaccia rmi client passato
	@Override
	public synchronized void unregisterCallback(HotelierClientInterface callbackClient) throws RemoteException {
		// rimuove stub client dalla mappa
		clientsCallback.remove(callbackClient);
	}
	
	// restituisce mappa delle callbacks
	public synchronized Map<HotelierClientInterface, List<String>> getClientsCallback() {
		return clientsCallback;
	}

}
