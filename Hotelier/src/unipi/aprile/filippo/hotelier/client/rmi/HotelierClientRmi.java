package unipi.aprile.filippo.hotelier.client.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;
import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierClientInterface;
import unipi.aprile.filippo.hotelier.common.network.rmi.HotelierServerInterface;

public class HotelierClientRmi {

	/**
	 * La classe HotelierClientRmi gestisce la connessione RMI (Remote Method
	 * Invocation) tra il client e il server Hotelier. Inizializza e esporta lo
	 * stub per la comunicazione remota, permettendo al server di invocare
	 * metodi remoti sul client (callback). Permette l' invocazione dei seguenti
	 * metodi remoti su HotelierServerRmi: • richiesta registrazione di un nuovo
	 * utente; • richiesta registrazione e deregistrazione di callback per le
	 * città di interesse;
	 */

	// instanza classe che implementa metodi interfaccia client Rmi
	private final HotelierClientRmiImpl client;
	// stub del server per invocazione remota di metodi
	private HotelierServerInterface stubServer;
	// stub del client per ricezione delle callback
	private HotelierClientInterface stubClient;

	public HotelierClientRmi(String serverAddress, String rmiRemoteReference, int rmiPort) throws Exception {
		// instanzio un nuovo HotelierClientRmiImpl
		client = new HotelierClientRmiImpl();
		// ottengo il registro tramite indirizzo e porta passati
		Registry registry = LocateRegistry.getRegistry(serverAddress, rmiPort);
		// ottengo lo stub del server tramite rmiRemoteReference passata
		stubServer = (HotelierServerInterface) registry.lookup(rmiRemoteReference);
		// esporto lo stub del client per le callback
		stubClient = (HotelierClientInterface) UnicastRemoteObject.exportObject(client, 0);
	}

	// richiede la registrazione di un nuovo utente e restituisce la risposta
	public String requestRegister(String username, String password) throws RemoteException {

		// eseguo l' invocazione remota del metodo sul server per la registrazione di un nuovo utente
		return stubServer.registerUser(username, password);
	}

	// richiede la registrazione di una callback per le città di interesse
	public void registerInterests(List<String> cities) throws RemoteException {

		// eseguo l' invocazione remota del metodo sul server per la registrazione di un callback per le città di interesse
		stubServer.registerCallback(stubClient, cities);
	}

	// richiede la deregistrazione della callback per le città di interesse
	public void unregisterInterests() throws RemoteException {

		// eseguo l' invocazione remota del metodo sul server per la deregistrazione della callback per le città di interesse
		stubServer.unregisterCallback(stubClient);
	}

	// restituisce la mappa dei local rank formattata per la stampa
	public String localRankMapToString() {

		var localRankMap = client.getLocalRankMap();

		StringBuilder sb = new StringBuilder();
		String separator = "--------------------------------------------------\n";

		for (Map.Entry<String, List<HotelierHotel>> entry : localRankMap.entrySet()) {

			sb.append("================" + entry.getKey() + "================").append("\n\n");

			for (HotelierHotel hotel : entry.getValue()) {

				sb.append(hotel).append("\n");
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	// restituisce true se utente non ha inserito città di interesse a seguito della login, false altrimenti
	public boolean islocalRankMapEmpty() {

		return client.getLocalRankMap().isEmpty();
	}

	// resetta mappa rank locali
	public void resetLocalRankMap() {
		client.getLocalRankMap().clear();;
	}

	// chiudo le risorse associate alla comunicaizone tcp
	public void close() {
		try {

			// controllo se utente si era registrato per delle callback a seguito della logn
			if (!islocalRankMapEmpty()) {
				// deregistro utente dalle callback per le città di interesse
				unregisterInterests();
			}
			// rimuovo esportazione stub client per le callback
			UnicastRemoteObject.unexportObject(client, true);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
