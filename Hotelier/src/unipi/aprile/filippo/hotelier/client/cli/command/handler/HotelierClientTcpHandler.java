package unipi.aprile.filippo.hotelier.client.cli.command.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand;
import unipi.aprile.filippo.hotelier.client.config.HotelierClientConfigManager;
import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;
import unipi.aprile.filippo.hotelier.common.entities.rating.HotelierHotelRating;
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
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketRegistry;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketReview;
import unipi.aprile.filippo.hotelier.common.network.packets.HotelierPacketReviewResponse;

public class HotelierClientTcpHandler {
	
	/**
	 * La classe HotelierClientTcpHandler gestisce i comandi TCP inviati dal client al server HotelierTCp tramite socket Tcp
	 * nel seguente modo:
	 * 1. crea il pacchetto relativo al comando Tcp parsando i valori dagli argomenti di quest'ultimo;
	 * 2. invia il pacchetto al sever HotelierTCp.
	 * 3. si blocca attendo il pacchetto di risposta.
	 * 4. recuper la risposta dal pacchetto ricevuto e la restituisce.
	 * In caso di eccezzione segnala impossibilità di contattare serverTcp.
	 */
	

	// socket connessione Tcp
	private Socket socket;
	// outputStream associato alla socket
	private OutputStream outputStream;
	// inputStream associato alla socket
	private InputStream inputStream;
	// gson per serializzazione/deserializzazione pacchetti
	private Gson gson;

	public HotelierClientTcpHandler() throws Exception {

		// ottengo i config del client
		var clientConfig = HotelierClientConfigManager.getClientConfig();
		// creo la socket per connessione Tcp con address e porta presenti nel file config
		this.socket = new Socket(clientConfig.getServerAddress(), clientConfig.getTcpPort());
		// ottengo outputStream associata alla socket
		outputStream = socket.getOutputStream();
		// ottengo inputStream associata alla socket
		inputStream = socket.getInputStream();
		// instanzio un gson per serializzare/deserializzare i pacchetti
		gson = new GsonBuilder().serializeNulls().create();
	}
	
	// gestisce comando Tcp e ne restiuisce la risposta
	public String handleTcpCommand(HotelierClientCommand command) {

		try {
			// creo il pacchetto tramite parsing del comando passato
			HotelierPacket requestPacket = createPacket(command);

			// controllo che il pacchetto sia diverso da null, ovvero sia supportato
			if (requestPacket != null) {

				// pacchetto supportato
				// invio il paccheto al server 
				sendPacket(requestPacket);
				// attendo il pacchetto di risposta
				HotelierPacket responsePacket = recievePacket();
				// restituisco la risposta 
				return getResponse(responsePacket);
			}

		} catch (IOException e) {

			// in caso di eccezzione segnalo che non è possibile contattare il HotelierServerTcp
			return "[ERRORE] Impossibile contattare server HotelierTcp. Controllare che il server sia acceso!";
		}

		// comando tcp non supportato restituisco null
		return null;
	}

	// restituisce il pacchetto creato rispetto al comando passato
	private HotelierPacket createPacket(HotelierClientCommand command) {

		// ottengo il nome del comando passato
		String commandName = command.getName();
		// ottengo gli argomenti del comando passato
		String[] commandArgs = command.getArguments();

		// filtro rispetto al nome del comando
		return switch (commandName) {
			// invoco createPacketLogin per creazione pacchetto di login a seguito di richiesta di login
			case "login" -> createPacketLogin(commandArgs);
			// invoco createPacketLogout per creazione pacchetto di logout a seguito di richiesta di logout
			case "logout" -> createPacketLogout();
			// invoco createPacketHotel per creazione pacchetto hotel a seguito di richiesta di searchhotel
			case "searchhotel" -> createPacketHotel(commandArgs);
			// invoco createPacketHotelList per creazione pacchetto hotelList a seguito di richiesta searchallhotels
			case "searchallhotels" -> createPacketHotelList(commandArgs);
			// invoco createPacketReview per creazione pacchetto review a seguito di richiesta insertreview
			case "insertreview" -> createPacketReview(commandArgs);
			// invoco createPacketBadge per creazione pacchetto badge a seguito di richiesta showmybadges
			case "showmybadges" -> createPacketBadge();
			// comando tcp non supportato restituisco null
			default -> null;
		};
	}

	// restituisce pacchetto di login con parametri presenti negli argomenti del comando
	private HotelierPacket createPacketLogin(String[] commandArgs) {

		// ottengo username dagli argomenti del comando
		String username = commandArgs[0];
		// ottengo password dagli argomenti del comando
		String password = commandArgs[1];
		// istanzio un nuovo pacchetto di login avente username e password ottenuti
		HotelierPacketLogin packetLogin = new HotelierPacketLogin(username, password);
		// restituisco il paccheto di login creato
		return packetLogin;
	}

	// restituisce pacchetto di logout
	private HotelierPacket createPacketLogout() {

		// istanzio un nuovo pacchetto di logout
		HotelierPacketLogout packetLogout = new HotelierPacketLogout();
		// restituisco il paccheto di logout creato
		return packetLogout;
	}

	// restituisce pacchetto hotel con parametri presenti negli argomenti del comando
	private HotelierPacket createPacketHotel(String[] commandArgs) {

		// ottengo nome hotel dagli argomenti del comando
		String hotelName = commandArgs[0];
		// ottengo città hotel dagli argomenti del comando
		String city = commandArgs[1];
		// istanzio un nuovo pacchetto hotel avente nome hotel e città hotel ottenuti
		HotelierPacketHotel packetHotel = new HotelierPacketHotel(hotelName, city);
		// restituisco pacchetto hotel creato
		return packetHotel;
	}

	// restituisce pacchetto hotelList con parametri presenti negli argomenti del comando
	private HotelierPacket createPacketHotelList(String[] commandArgs) {

		// ottengo città hotels dagli argomenti del comando
		String city = commandArgs[0];
		// istanzio un nuovo pacchetto hotelList avente città hotels ottenuta
		HotelierPacketHotelList packetHotelList = new HotelierPacketHotelList(city);
		// restituisco pacchetto hotelList creato
		return packetHotelList;
	}

	// restituisce pacchetto review con parametri presenti negli argomenti del comando
	private HotelierPacket createPacketReview(String[] commandArgs) {

		// ottengo nome hotel dagli argomenti del comando
		String hotelName = commandArgs[0];
		// ottengo città hotel dagli argomenti del comando
		String city = commandArgs[1];
		// ottengo rate hotel dagli argomenti del comando
		int rate = Integer.parseInt(commandArgs[2]);
		// ottengo gli score dell' hotel hotel dagli argomenti del comando
		int cleaning = Integer.parseInt(commandArgs[3]);
		int position = Integer.parseInt(commandArgs[4]);
		int services = Integer.parseInt(commandArgs[5]);
		int quality = Integer.parseInt(commandArgs[6]);
		// instanzio un nuovo HotelierHotelRating con score ottenuti
		HotelierHotelRating rating = new HotelierHotelRating(cleaning, position, services, quality);
		// istanzio un nuovo pacchetto review avente nome hotel, città hotel, rate e rating ottenuti
		HotelierPacketReview packetReview = new HotelierPacketReview(hotelName, city, rate, rating);
		// restituisco pacchetto review creato
		return packetReview;
	}

	// restituisce pacchetto badge con parametri presenti negli argomenti del comando
	private HotelierPacket createPacketBadge() {

		// istanzio un nuovo pacchetto badge
		HotelierPacketBadge packetBadge = new HotelierPacketBadge();
		// restituisco pacchetto badge creato
		return packetBadge;
	}

	// serializza il pacchetto e lo invia al server tramite outputStream associato allas socket Tcp
	private void sendPacket(HotelierPacket packet) throws IOException {

		// ottengo id del pacchetto rispetto alla sua instanza
		int packetId = HotelierPacketRegistry.getIDFromPacket(packet);
		// serializzo il pacchetto in stringa json
		String serializedPacket = gson.toJson(packet); // HotelierCommonUtils.serialize(packet);
		// ottengo i bytes del pacchetto serializzato
		byte[] packetBytes = serializedPacket.getBytes(StandardCharsets.UTF_8);
		// alloco ByteBuffer per contenere bytes di lunghezza pacchetto serializzato + id + pacchetto serializzato
		ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + packetBytes.length);
		// inserisco lunghezza pacchetto serializzato nel buffer
		buffer.putInt(packetBytes.length);
		// inserisco id del paccheto nel buffer
		buffer.putInt(packetId);
		// inserisco pacchetto serializzato nel buffer
		buffer.put(packetBytes);
		// eseguo la scrittura del buffer convertito in array di byte su outputStream
		outputStream.write(buffer.array());
		// eseguo il flush di outputStream
		outputStream.flush();
	}

	// restiusce il pacchetto di risposta deserializzato, ricevuto dal server tramite inputStream associato alla socket Tcp 
	private HotelierPacket recievePacket() throws IOException {

		// alloco array di byte per contenere lunghezza pacchetto serializzato + id
		byte[] responseHeaderBytes = new byte[8];
		// eseguo lettura dei bytes di lunghezza pacchetto di risposta e id da inputStream nell' array di byte
		readAllBytes(responseHeaderBytes);
		// eseguo il wrap dell' array di byte in un byteBuffer
		ByteBuffer responseHeader = ByteBuffer.wrap(responseHeaderBytes);
		// ottengo la lunghezza pacchetto di risposta dal buffer
		int payloadSize = responseHeader.getInt();
		// ottengo del id del pacchetto di risposta dal buffer
		int packetID = responseHeader.getInt();
		// alloco array di byte per contenere pacchetto di risposta
		byte[] responsePayloadBytes = new byte[payloadSize];
		// eseguo lettura dei bytes del pacchetto di risposta da inputStream nell' array di byte
		readAllBytes(responsePayloadBytes);
		// eseguo il wrap dell' array di byte in un byteBuffer
		ByteBuffer responsePayload = ByteBuffer.wrap(responsePayloadBytes);
		// restituisco pacchetto di risposta deserializzato
		return HotelierPacketRegistry.getPacketFromID(packetID, responsePayload);
	}

	// restituisce risposta contenuta nel pacchetto di risposta, null se pacchetto non è supportato
	private String getResponse(HotelierPacket packet) throws IOException {

		// filtro rispetto all' instaza del pacchetto di risposta e ottengo la relativa riposta
		return switch (packet) {
			case HotelierPacketLoginResponse loginPacket -> loginPacket.getResponse();
			case HotelierPacketLogoutResponse logoutPacket -> logoutPacket.getResponse();
			case HotelierPacketHotelResponse hotelPacket -> hotelPacket.getHotel().toString();
			case HotelierPacketHotelListResponse hotelListPacket -> formatHotelList(hotelListPacket.getHotels());
			case HotelierPacketReviewResponse reviewPacket -> reviewPacket.getResponse();
			case HotelierPacketBadgeResponse badgePacket -> badgePacket.getBadge().toString();
			case HotelierPacketErrorResponse errorPacket -> errorPacket.getResponse();
			default -> null;
		};
	}

	// assicura la lettura di tutti i byte richiesti da inputStream tenendo conto che il server utilizza NIO (non bloccante) quindi potrebbero avvenire scritture parziali
	private void readAllBytes(byte[] byteArray) throws IOException {

		// inizializzo numero byte letti a 0
		int totalBytesRead = 0;

		// itero fino a che non ho letto tutti i byte richiesti
		while (totalBytesRead < byteArray.length) {
			// eseguo la lettura dei byte rimanenti (lunghezza array byte - numero byte letti) in byteArray con offset numero byte letti
			int bytesRead = inputStream.read(byteArray, totalBytesRead, byteArray.length - totalBytesRead);
			// controllo se la socket è stata chiusa lato server (end of stream reached)
			if (bytesRead == -1) {
				// end of stream reached
				// lancio eccezzione IO per segnalere che non è possibile comunicare col HotelierServer Tcp
				throw new IOException();
			}

			// aggiorno numero byte letti sommando quelli letti 
			totalBytesRead += bytesRead;
		}
	}
	
	// formatta lista di hotel correttamente per la stampa
	private String formatHotelList(List<HotelierHotel> hotels) {

		StringBuilder sb = new StringBuilder();
		String separator = "--------------------------------------------------\n";

		for (HotelierHotel hotel : hotels) {
			sb.append(hotel).append("\n");
			sb.append(separator);
		}

		return sb.toString();
	}

	// effettua la chiusura delle risorse aperte per la comunicazione Tcp
	public void close() {
		
		try {
			if (!socket.isClosed()) {
				
				inputStream.close();
				outputStream.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
