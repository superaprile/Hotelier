package unipi.aprile.filippo.hotelier.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacket;

public class HotelierServerNIO implements Runnable {
	
	/**
	 * La classe HotelierServerNIO gestisce le richieste TCP tramite multiplexing dei canali in NIO.
	 * La connessione tra il client e il server è persistente.
	 * 
	 * Il serverNIO si occupa di:
	 * • accettare nuove connessioni client e registrarle al selettore per la gestione degli eventi di lettura e scrittura;
	 * • gestire i pacchetti ricevuti dai client delegandoli ad una thread pool per elaborarne le richieste in modo concorrente;
	 * • gestire la disconnessione dei client, chiudendeone le risorse associate e rimuovendone la chiave dal selettore;
	 * 
	 * Quando viene accettata un nuova connessiona da un client gli viene asseganata un nuova instanza di HotelierServerClientHandler tramite 
	 * attachment della chiave la quale espone i metodi per la lettura e gestione del paccheto di richiesta, scrittura del paccheto di risposta
	 * e gestione della disconessione del client.
	 */
	
	// indirizzo socket TCP
	private final String serverAddress;
	// porta socket TCP
	private final int tcpPort;
	// threadpool per la gestione dei pacchetti
	private ExecutorService requestPool;

	public HotelierServerNIO(String serverAddress, int tcpPort) {
		// setto serverAddress a quello passato
		this.serverAddress = serverAddress;
		// setto tcpPort a quella passato
		this.tcpPort = tcpPort;
		// creo cached threadpool per la gestion dei pacchetti
		requestPool = Executors.newCachedThreadPool();
		
		// avvio il thread
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {

		try {
			
			// ottengo serverSocketAddress tramite parametri passati
			var serverScoketAddress = new InetSocketAddress(serverAddress, tcpPort);
			// apro una ServerSocketChannel e la configuro per non bloccante
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			// effettuo il binding del ServerSocketChannel al serverScoketAddress
			serverSocketChannel.bind(serverScoketAddress);
			// apro un selettore per la gestione dei canali non bloccanti
			Selector serverSelector = Selector.open();
			// registro la serverSocketChannel sul selettore per operazioni di accept 
			serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
			
			// itero finchè il thread non viene interotto
			while (!Thread.interrupted()) {
				
				// mi blocco in attesa di chiavi di canali pronti per IO
				serverSelector.select();
				
				// ottengo il set delle chiavi dei canali pronti
				Set<SelectionKey> selectedKeys = serverSelector.selectedKeys();
				// creo un iterato per il set di chiavi dei canali pronti
				Iterator<SelectionKey> selectedKeysIterator = selectedKeys.iterator();
				
				// itero il set delle chiavi
				while (selectedKeysIterator.hasNext()) {
					
					// prendo la chiave dall' iteratore
					SelectionKey selectedKey = selectedKeysIterator.next();
					
					// Se la chiave è predisposta alle operazioni di accecpt ed è arrivata una nuova richiesta di connessione
					if (selectedKey.isAcceptable()) {
						
						// ottengo il canale  associato al client accettato e la configuro non bloccante
						SocketChannel client = serverSocketChannel.accept();
						client.configureBlocking(false);
						
						// registro il client sul selettore per operazione di scrittura e lettura
						SelectionKey clientKey = client.register(serverSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						
						// allego un clientHandler alla chiave del client per gestirne le operazioni
						clientKey.attach(new HotelierServerClientHandler(client));

					}
					
					// Se la chiave è valida, predisposta alle operazioni di lettura ed è possibile leggere dal canale associato
					if (selectedKey.isValid() && selectedKey.isReadable()) {
						
						// ottengo il clienthandler associato alla chiave del client
						HotelierServerClientHandler clientHandler = (HotelierServerClientHandler) selectedKey.attachment();
						// eseguo la lettura della richiesta del client e ottengo il pacchetto di richiesta
						HotelierPacket packet = clientHandler.handleRead();
						
						// controllo di aver terminato correttamente la lettura del pacchetto di richiesta e che sia supportato
						if (packet != null) {
							
							// delego la gestion del pacchetto alla threadpool
							requestPool.submit(() -> {
								clientHandler.handlePacket(packet);
							});
						}
						
						// controllo se il client si è disconesso
						if (!clientHandler.isConnected()) {
							
							// chiudo le risosre associate alla comunicazione con  il client
							clientHandler.close();
							// richiedo la rimozione della chiave associata al client dal selettore
							selectedKey.cancel();
						}
					}
					
					// Se la chiave è valida, predisposta alle operazioni di scrittura ed è possibile scrivere sul canale associato
					if (selectedKey.isValid() && selectedKey.isWritable()) {
						
						// ottengo il clienthandler associato alla chiave del client
						HotelierServerClientHandler clientHandler = (HotelierServerClientHandler) selectedKey.attachment();
						// eseguo la scrittura del pacchetto di risposta per il client
						clientHandler.handleWrite();
						
						// controllo se il client si è disconesso
						if (!clientHandler.isConnected()) {
							
							// chiudo le risosre associate alla comunicazione con  il client
							clientHandler.close();
							// richiedo la rimozione della chiave associata al client dal selettore
							selectedKey.cancel();
						}
					}
					
					// rimuovo la chiave dal set delle chiavi pronte 
					selectedKeysIterator.remove();
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
