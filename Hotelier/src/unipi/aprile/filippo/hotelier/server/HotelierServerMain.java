package unipi.aprile.filippo.hotelier.server;

import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.APPLICATION_NAME;
import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.HOTELS_PATH_JSON;
import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.REVIEWS_PATH_JSON;
import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.SERVER_CONFIG_PATH_JSON;
import static unipi.aprile.filippo.hotelier.server.utils.HotelierServerUtils.USERS_PATH_JSON;

import java.io.File;
import java.io.IOException;

import unipi.aprile.filippo.hotelier.common.utils.HotelierCommonUtils;
import unipi.aprile.filippo.hotelier.server.config.HotelierServerConfigManager;
import unipi.aprile.filippo.hotelier.server.network.HotelierServerNIO;
import unipi.aprile.filippo.hotelier.server.network.mulitcast.HotelierServerMulticastSender;
import unipi.aprile.filippo.hotelier.server.network.rmi.HotelierServerRmi;
import unipi.aprile.filippo.hotelier.server.ranking.HotelierServerRanking;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterHotels;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterReviews;
import unipi.aprile.filippo.hotelier.server.register.HotelierServerRegisterUsers;

public class HotelierServerMain {

	public static void main(String[] args) {

		try {
			// inizializzo il server
			initialize();
			// avvio il server
			startServer();
			// stampo server avviato con successo e in esecuzione
			System.out.println("[OK] HotelierServer in esecuzione ...");

		} catch (Exception e) {
			
			// in caso di  eccezzione nella fase di startup segnalo impossibile effettuare avvio e termino il server
			System.out.println("[ERRORE] Impossibile avviare il server. Controllare che sia già acceso !");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	// inizializza il server
	private static void initialize() throws IOException {
		
		//  controllo che esista la cartella contenente file config e strutture da persistere
		File serverFolder = new File(APPLICATION_NAME);
		// se folder non esiste lo segnalo e throwo IOException per terminare il server
		if (!serverFolder.exists()) {
			System.out.println("[ERRORE] Folder file server non trovata. Crearla al path: " + APPLICATION_NAME + " e caricare i file necessari");
			System.out.println("[ERRORE] Terminazione Server ...");
			throw new IOException();
		}
		
		//  controllo che esistano tutti i file richiesta per il funzionameto del server
		File hotelFile = new File(HOTELS_PATH_JSON);
		File usersFile = new File(USERS_PATH_JSON);
		File reviewsFile = new File(REVIEWS_PATH_JSON);
		File configFile = new File(SERVER_CONFIG_PATH_JSON);
		
		// se il file json hotel non esiste lo segnalo e throwo IOException per terminare il server
		if (!hotelFile.exists()) {
			System.out.println("[ERRORE] Hotel file json non trovato. Cariare il file nel path : " + HOTELS_PATH_JSON);
			System.out.println("[ERRORE] Terminazione Server ...");
			throw new IOException();
		}
		
		// se il file json users non esiste lo creo inzializzandola con una lista vuota
		if (!usersFile.exists()) {
			usersFile.createNewFile();
			HotelierCommonUtils.writeFile("[]", usersFile);
		}
		
		// se il file json reviews non esiste lo creo inzializzandola con una lista vuota
		if (!reviewsFile.exists()) {
			reviewsFile.createNewFile();
			HotelierCommonUtils.writeFile("[]", reviewsFile);
		}
		
		// se il file json reviews non esiste lo creo inzializzandola con il file config server di default
		if (!configFile.exists()) {
			configFile.createNewFile();
			HotelierServerConfigManager.createDefaultConfig();
		} else {
			// se esiste lo deserializzo da disco
			HotelierServerConfigManager.loadServerConfig();
		}
		
		// ottengo istanza del registro degli hotel
		var hotelRegister = HotelierServerRegisterHotels.getInstance();
		// ottengo istanza del registro degli utenti
		var userRegister = HotelierServerRegisterUsers.getInstance();
		// ottengo istanza del registro delle recensioni
		var reviewRegister = HotelierServerRegisterReviews.getInstance();
		
		// deserializzo la lista di hotel da disco all' interno del registro
		hotelRegister.deserialize();
		// deserializzo la lista di utenti da disco all' interno del registro
		userRegister.deserialize();
		// deserializzo la lista di recensioni da disco all' interno del registro
		reviewRegister.deserialize();
	}
	
	
	// avvia il server
	private static void startServer() throws Exception {
		
		// ottengo i server config
		var serverConfig = HotelierServerConfigManager.getServerConfig();
		
		
		// inizializzo serverNIO per la gestione delle comunicazioni Tcp passandogli indirizzo e porta per la socket Tcp
		new HotelierServerNIO(serverConfig.getServerAddress(), serverConfig.getTcpPort());
		
		// inizializzo serverRmi passandogli remoteReference per esportarzione dello stub e porta per il registro Rmi
		var hotelierServerRmi = new HotelierServerRmi(serverConfig.getRmiRemoteReference(), serverConfig.getRmiPort());
		
		// inizializzo sender multicast passandogli mulitcast address e porta per inviare notifiche Udp quando 
		// cambia primo rank locale di qualsiasi città a tutti i client registrati al gruppo (utenti loggati)
		var hotelierServerMulticast = new HotelierServerMulticastSender(serverConfig.getMcastAddress(), serverConfig.getMcastPort());
		
		// inizializzo serverRanking passadongli rankingInterval (secondi che intercorrono tra le sue esecuzioni), serverRmi
		// e multicasT 
		new HotelierServerRanking(serverConfig.getRankingInterval(), hotelierServerRmi, hotelierServerMulticast);
	}

}
