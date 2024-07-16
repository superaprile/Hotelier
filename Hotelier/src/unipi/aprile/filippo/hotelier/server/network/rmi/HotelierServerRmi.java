package unipi.aprile.filippo.hotelier.server.network.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.GsonBuilder;

import unipi.aprile.filippo.hotelier.common.entities.HotelierLocalRank;
import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierClientInterface;
import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierServerInterface;

public class HotelierServerRmi {

	/**
	 * La classe HotelierServerRmi gestisce la connessione RMI (Remote Method
	 * Invocation) tra il client e il server Hotelier. Inizializza e esporta lo
	 * stub per la comunicazione remota, permettendo al client di invocare
	 * metodi remoti sul server. Permette l' invocazione dei seguenti metodi
	 * remoti su HotelierClientRmi: • callback notifica cambiamento prima
	 * posizione rank locale;
	 */

	// instanza classe che implementa metodi interfaccia server Rmi
	private final HotelierServerRmiImpl serverImpl;

	public HotelierServerRmi(String rmiRemoteReference, int rmiPort) throws Exception {

		// instanzio un nuovo HotelierServerRmiImpl
		serverImpl = new HotelierServerRmiImpl();
		// esporto lo stub del server per invocazione remota metodi
		HotelierServerInterface stub = (HotelierServerInterface) UnicastRemoteObject.exportObject(serverImpl, 0);
		// creo registro rmi alla porta indicata
		LocateRegistry.createRegistry(rmiPort);
		// ottengo il registro rmi
		Registry registry = LocateRegistry.getRegistry();
		// effettuo il binding dello stub del server nel registo a rmiRemoteReference
		registry.bind(rmiRemoteReference, stub);
	}

	//notifica cambiamento rank locale a tutti client registrati per quella città d interesse
	public void notifyLocalRank(HotelierLocalRank localRank) {

		// instanzio un gson per la serializzazione di localRank
		var gson = new GsonBuilder().serializeNulls().create();
		// ottengo la mappa delle callback
		var clientsCallback = serverImpl.getClientsCallback();

		// inizializzo una lista di clientRmiInterface 
		List<HotelierClientInterface> clientsToRemove = new ArrayList<>();
		
		// acquisisco la lock sulla mappa
		synchronized (clientsCallback) {
			// itero la mappa delle callback
			for (Entry<HotelierClientInterface, List<String>> clientCallback : clientsCallback.entrySet()) {

				// ottengo la lista di città di interesse del client
				var cities = clientCallback.getValue();
				// ottengo la città di localRank
				var city = localRank.getCity();
				// controllo se il client aveva registrato interesse per la città di localRankl
				if (containsCity(cities, city)) {

					// interesse registrato
					// serializzo local rank in Json
					var serializedLocalRank = gson.toJson(localRank); //HotelierCommonUtils.serialize(localRank);
					// ottengo stub del client
					var clientInterface = clientCallback.getKey();

					try {

						//effettuo callback al client per notificare cambiamento rank locale
						clientInterface.notifyInterest(serializedLocalRank);

					} catch (RemoteException e) {
						// in caso di eccezzione aggiungo lo stub del client alla lista di client da rimuover
						clientsToRemove.add(clientInterface);
					}
				}
			}

			// Rimuovo dalla mappa delle callback tutti i client noticati che hanno sollevato un eccezione
			for (HotelierClientInterface client : clientsToRemove) {
				clientsCallback.remove(client);
			}
		}

	}

	// Restituisce true se cities contiente cityToCheck (ignoreCase), false altrimenti
	private boolean containsCity(List<String> cities, String cityToCheck) {

		for (String city : cities) {

			if (StringUtils.equalsIgnoreCase(city, cityToCheck)) {
				return true;
			}
		}

		return false;
	}

}
