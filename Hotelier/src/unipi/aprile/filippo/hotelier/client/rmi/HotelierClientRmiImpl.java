package unipi.aprile.filippo.hotelier.client.rmi;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;
import unipi.aprile.filippo.hotelier.common.entities.HotelierLocalRank;
import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierClientInterface;

public class HotelierClientRmiImpl implements HotelierClientInterface {

	/**
	 * La classe HotelierClientRmiImpl fornisce le funzionalità necessarie per gestire le callback da HotelierServerRmi, 
	 * mediante la mappa dei rank locali (localRankMap), avente: 
	 * • chiave: città; 
	 * • valore: la lista degli hotel ordinati per rank locale. 
	 * A seguito della ricezione di una callback, deserializza il rank locale ricevuto e aggiorna la entry corrispondente 
	 * della mappa con la nuova lista di hotel ordinati per rank locale. 
	 * La classe utilizza la sincronizzazione per garantire la coerenza dei dati quando accede/modifica la mappa dei rank locali.
	 */

	// mappa dei rank locali con chiave: città e valore: lista hotel ordinati per rank locale
	private Map<String, List<HotelierHotel>> localRankMap;

	public HotelierClientRmiImpl() {

		localRankMap = new HashMap<>();
	}
	
	// metodo invocato da remoto da HotelierServerRmi per notificare cambiamento di un local rank
	@Override
	public synchronized void notifyInterest(String serializedLocalRank) throws RemoteException {
		
		// instanzio un gson per deserializzare il local rank
		var gson = new GsonBuilder().serializeNulls().create();
		// deserializzo il local rank
		var localRank = gson.fromJson(serializedLocalRank, HotelierLocalRank.class);
		// ottengo città del local rank
		var city = localRank.getCity();
		// ottengo lista hotel ordinate del local rank
		var hotels = localRank.getHotels();
		
		// eseguo update della mappa dei local rank
		synchronized (localRankMap) {
			localRankMap.put(city, hotels);
		}
	}
	
	// restituisco la mappa dei local rank
	public Map<String, List<HotelierHotel>> getLocalRankMap() {
		return localRankMap;
	}

}
