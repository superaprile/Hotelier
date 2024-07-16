package unipi.aprile.filippo.hotelier.client.cli.command.handler;

import java.io.IOException;

import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand;
import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand.CommandType;
import unipi.aprile.filippo.hotelier.client.rmi.HotelierClientRmi;

public class HotelierClientCommandHandler {
	
	/**
	 * La classe HotelierClientCommandHandler gestisce i comandi del client smistandoli all'handler appropriato 
	 * in base al loro tipo(TCP, RMI, LOCAL), nel seguente modo:
	 * • TCP, gestiti mediante tcpHandler;
	 * • RMI, gestiti mediante rmiHandler;
	 * • LOCAL, gestiti da quest' ultima.
	 */
	
	// client rmi
	private final HotelierClientRmi clientRmi;
	// handler comandi tcp 
	private final HotelierClientTcpHandler hotelierClientTcpHandler;
	// handler comandi rmi
	private final HotelierClientRmiHandler hotelierClientRmiHandler;

	public HotelierClientCommandHandler(HotelierClientRmi clientRmi) throws Exception {
		
		// setto client rmi a quello passato per paramento
		this.clientRmi = clientRmi;
		// instanzio un commandTcpHandler
		hotelierClientTcpHandler = new HotelierClientTcpHandler();
		// instanzio un commandRmiHandler passansolgi il client rmi ricevuto 
		hotelierClientRmiHandler = new HotelierClientRmiHandler(clientRmi);
	}
	
	// gestisce il comando e ne restituisce la rispsota
	public String handleCommand(HotelierClientCommand command) throws IOException {
		
		// filtro i comandi rispetto al loro tipo
		return switch (command.getCommandType()) {
			// TCP: gestione del comando affidata a handler tcp
			case CommandType.TCP -> hotelierClientTcpHandler.handleTcpCommand(command);
			// TCP: gestione del comando affidata a handler rmi
			case CommandType.RMI -> hotelierClientRmiHandler.handleRmiCommand(command);
			// LOCAL: gestione del comando affidata a command handler (non richiede la rete)
			case CommandType.LOCAL -> handleLocalCommand(command);
			// comando non supportato restiuisco null
			default -> null;
		};
	}
	
	// gestisce comando locale e ne restituisce la risposta
	private String handleLocalCommand(HotelierClientCommand command) {
		
		// filtro i comandi locali rispetto al loro nome
		return switch (command.getName()) {
			// invoco handleHelpCommand per gestione del commando help
			case "help" -> handleHelpCommand(command);
			// invoco handleShowLocalRanks per gestione del commando ShowLocalRanks
			case "showlocalranks" -> handleShowLocalRanks(command);
			// comando non supportato restiuisco null
			default -> null;
		};
	}

	// restituisce la lista dei comandi disponibili
	private String handleHelpCommand(HotelierClientCommand command) {

		StringBuilder response = new StringBuilder();
		response.append("Comandi disponibili:\n");
		response.append("-- help - Stampa la lista di comandi disponibili\n");
		response.append("-- register \"username\" \"password\" - Registra un nuovo utente con username e password forniti\n");
		response.append("-- login \"username\" \"password\" - Effettua il login con username e password forniti\n");
		response.append("-- logout - Effettua il logout\n");
		response.append("-- searchHotel \"nomeHotel\" \"città\" - Stampa hotel avente nome e città forniti\n");
		response.append("-- searchAllHotels \"città\" - Stampa tutti lista di hotel situati nella città fornita , ordinati per rank locale\n");
		response.append("-- insertReview \"nomeHotel\" \"nomeCittà\" \"GlobalScore\" \"CleaningScore\" \"PositionScore\" \"ServicesScore\" \"QualityScore\" - Inserisce una recensione per hotel avente parametri forniti\n");
		response.append("-- showMyBadges - Stampa il badge dell'utente corrispondente al livello raggiunto\n");
		response.append("-- showLocalRanks - Stampa le liste di hotel delle città di interesse, ordinate per rank locale\n");
		response.append("-- exit - Termina il client");

		return response.toString();
	}

	// restiusce gli hotels delle città di interesse ordinati per rank locale
	private String handleShowLocalRanks(HotelierClientCommand command) {

		return clientRmi.localRankMapToString();
	}
	
	// restituisce istanza dell handler dei comandi tcp
	public HotelierClientTcpHandler getHotelierClientTcpHandler() {
		return hotelierClientTcpHandler;
	}
}
