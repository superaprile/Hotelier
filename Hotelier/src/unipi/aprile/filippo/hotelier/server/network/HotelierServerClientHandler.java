package unipi.aprile.filippo.hotelier.server.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.GsonBuilder;

import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacket;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketRegistry;

public class HotelierServerClientHandler {

	/**
	 * La classe HotelierServerClientHandler gestisce la comunicazione TCP tra
	 * il server e un singolo client. La classe si occupa di: 
	 * 	• leggere i pacchetti inviati dal client e deserializzarli; 
	 * 	• gestire i pacchetti ricevuti tramite il gestore dei pacchetti (HotelierServerPacketHandler); 
	 * 	• serializzare e scrivere i pacchetti di risposta al client; 
	 * 	• gestire la disconnessione del client e chiudere la connessione;
	 * 
	 * I pacchetti di risposta restituiti da HotelierServerPacketHandler vengono
	 * salvati in una coda sincronizzata. Quando il canale associato al client è
	 * pronto per la scrittura viene recuperato il pacchetto di risposta dalla
	 * coda (LinkedList mantiene ordine di inserimento), serializzato e scritto
	 * sul canale.
	 * 
	 * I campi requestHeader, requestPayload e responseBuffer vengono utilizzati
	 * rispettivamente da handleRead() e handleWrite() per gestire correttamente
	 * letture/scritture parziali che possono avvenire quando si lavora con NIO.
	 */

	// canale associato al client
	private final SocketChannel client;
	// handler dei pacchetti
	private final HotelierServerPacketHandler hotelierPacketHandler;
	// coda pacchetti di risposta
	private final Queue<HotelierPacket> responsePacketQueue;

	// byte buffer per lettura delle richiesta e scrittura delle risposte
	private ByteBuffer requestHeader, requestPayload;
	private ByteBuffer responseBuffer;

	// booleano per gestire la connessione del client
	private boolean isConnected;

	public HotelierServerClientHandler(SocketChannel client) {
		// setto client a quello passato per parametro
		this.client = client;
		// creo una nuova instanza dell' handler dei pacchetti
		hotelierPacketHandler = new HotelierServerPacketHandler();
		// inizializzo la coda dei pacchetti di risposta a una LinkedList
		responsePacketQueue = new LinkedList<>();
		// setto boolean a true (client connesso)
		isConnected = true;
	}

	// restituisce il pacchetto di richiesta inviato dal client
	public HotelierPacket handleRead() {

		// controllo che il client sia connesso
		if (isConnected) {

			try {
				// controllo che sia stato ancora letto nulla
				if (requestHeader == null) {

					// alloco byteBuffer per contenere header del messaggio di richiesta, lunghezza pacchetta + id
					requestHeader = ByteBuffer.allocate(8);
				}

				// controllo se non è stata ancora completata la lettura degli header
				if (requestHeader.hasRemaining()) {

					// leggo i byte rimanenti
					if (client.read(requestHeader) == -1) {

						// se lettura restituisce -1 throwo eccezzione per settare client connesso a false
						throw new IOException();
					}
				}

				// controllo se completato lettura degli header e non ho ancora inzializzato byteBuffere per il payaload, ovvero il pacchetto di richiesta
				if (!requestHeader.hasRemaining() && requestPayload == null) {

					// flippo il byteBuffer contenente headers per prepararlo in lettura
					requestHeader.flip();
					// ottengo la lunghezza del pacchetto di richiesta dal byteBuffer
					int payloadSize = requestHeader.getInt();
					// alloco byteBuffer per contenere pacchetto di richiesta serializzato
					requestPayload = ByteBuffer.allocate(payloadSize);
				}

				// controllo se ho inizializzato il byteBuffer per il payload e non ho completato la lettura del pacchetto di richiesta serializzato
				if (requestPayload != null && requestPayload.hasRemaining()) {

					// leggo i byte rimanenti
					if (client.read(requestPayload) == -1) {
						// se lettura restituisce -1 throwo eccezzione per settare client connesso a false
						throw new IOException();
					}
				}

				// controllo se ho terminato la lettura degli header e del payload
				if (requestPayload != null && !requestPayload.hasRemaining()) {

					// ottengo id del pacchetto di richiesta dal byteBuffer
					int packetID = requestHeader.getInt();
					// flippo il byteBuffer contenente payload per prepararlo in lettura
					requestPayload.flip();

					// deserializzo il pacchetto di richiesta
					HotelierPacket packet = deserializeRequest(packetID, requestPayload);

					// resetto headers e payload per preparmi per una nuova lettura
					requestHeader = null;
					requestPayload = null;

					// restituisco il pacchetto di risposta
					return packet;
				}
			} catch (IOException exception) {

				// in caso di eccezzione setto isConnected a false in quanto il client si è disconesso
				isConnected = false;
			}
		}
		
		return null;

	}
	
	// restituisce pacchetto di risposta relativo al pacchetto di richiesta passato
	public void handlePacket(HotelierPacket packet) {
		
		// restituiscO pacchetto di risposta relativo al pacchetto di richiesta passato
		HotelierPacket responsePacket = hotelierPacketHandler.handlePacket(packet);
		
		// controllo che il pacchetto di richiesta fosse supportato
		if (responsePacket != null) {
			//acquisisco la lock sulla coda dei pacchetti di risposta
			synchronized (responsePacketQueue) {
				// aggiungo il pacchetto di risposta alla coda
				responsePacketQueue.add(responsePacket);
			}
		}

	}
	
	// scrive il messaggio di risposta sul canale associato al client
	public void handleWrite() {
		
		// controllo che il client sia connesso
		if (isConnected) {
			try {
				// controllo che non sia stato ancora scritto nulla
				if (responseBuffer == null) {
					// acquisisco la lock sulla coda dei pacchetti di risposta
					synchronized (responsePacketQueue) {
						// controllo se ci sono pacchetti da scrivere
						HotelierPacket responsePacket = responsePacketQueue.peek();
						
						if (responsePacket != null) {
							// ottengo il messaggio di risposta serializzato formato da: lunghezza pacchetto + id + pacchetto di risposta e lo assegno a response buffer
							responseBuffer = serializeResponse(responsePacket);
						}
					}
				}
				
				// controllo se è presente un messaggio di risposta da scivere
				if (responseBuffer != null) {
					// scrivo il messaggio di risposta sul canale associato al client
					client.write(responseBuffer);
					
					
					// controllo di aver finito di scrivere il messaggio di risposta (potrebbero avvenire scritture parziali)
					if (!responseBuffer.hasRemaining()) {
						//acquisisco la lock sulla coda dei pacchetti di risposta 
						synchronized (responsePacketQueue) {
							// rimuovo il primo pacchetto di risposta
							responsePacketQueue.poll();
						}
						
						// resetto responseBuffer per preparmi per una nuova scrittura
						responseBuffer = null; // Reset the buffer for the next packet
					}
				}

			} catch (IOException exception) {
				
				// in caso di eccezione a seguito della write setto isConnected a false in quanto il client si è disconesso
				isConnected = false;
			}
		}
	}
	
	// restitusce il byteBuffer contente il messaggio di risposta relativo al pacchetto di risposta passato
	private ByteBuffer serializeResponse(HotelierPacket packet) {
		
		// instanzio un gson per la serializzazione del pachetto di risposta
		var gson = new GsonBuilder().serializeNulls().create();
		
		// serializzo il pacchetto di risposta
		String serializedPacket = gson.toJson(packet);
		// ottengo id del pacchetto di risposta
		int packetID = HotelierPacketRegistry.getIDFromPacket(packet);
		// controllo che il pacchetto sia supportato
		if (packetID != -1) {
			// ottengo la lunghezza del pacchetto di risposta serializzato
			int payloadSize = serializedPacket.getBytes().length;
			// alloco byteBuffer per contenere: lunghezza pacchetto di risposta + id + pacchetto di risposta serializzato
			var serializedResponse = ByteBuffer.allocate(4 + 4 + payloadSize);
			// inserisco nel byteBuffer lunghezza del pacchetto di risposta
			serializedResponse.putInt(payloadSize);
			// inserisco nel byteBuffer id del pacchetto di risposta
			serializedResponse.putInt(packetID);
			// inserisco nel byteBuffer pacchetto di risposta serializzato
			serializedResponse.put(serializedPacket.getBytes());
			// preparo il buffer in lettura
			serializedResponse.flip();
			
			// restituisco il messaggio di risposta
			return serializedResponse;
		}
		
		// pacchetto non supportato restituisco null
		return null;

	}
	
	// restituisce il pacchetto di richiesta deserializzato
	private HotelierPacket deserializeRequest(int packetID, ByteBuffer requestPayload) {

		return HotelierPacketRegistry.getPacketFromID(packetID, requestPayload);
	}
	
	// restituisce true se il client è connesso, false altrimenti
	public boolean isConnected() {
		return isConnected;
	}
	
	// chiude le risorse associate al client e setta isConnecte a false 
	public void close() throws IOException {
		hotelierPacketHandler.handleClientDisconnect();
		client.close();

	}

}
