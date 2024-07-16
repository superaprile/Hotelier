package unipi.aprile.filippo.hotelier.client.cli.command.handler;

import java.rmi.RemoteException;

import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand;
import unipi.aprile.filippo.hotelier.client.rmi.HotelierClientRmi;

public class HotelierClientRmiHandler {
	
	/**
	 * La classe HotelierClientRmiHandler gestisce i comandi RMI invocati remotamente dal client sul  server HotelierRMI
	 * recuperando nome e parametri dal comando passato e invocando rispettivo handler.
	 * In caso di eccezzione segnala impossibilità di contattare serverRmi.
	 */
	
	// client rmi
	private final HotelierClientRmi clientRmi;

	public HotelierClientRmiHandler(HotelierClientRmi clientRmi) {
		
		// setto client rmi a quello passato per paramento
		this.clientRmi = clientRmi;
	}
	
	// gestisce comando Rmi e ne restituisce la risposta
	public String handleRmiCommand(HotelierClientCommand command) {
		
		// ottengo il nome del comando passato
		String commandName = command.getName();
		// ottengo gli argomenti del comando passato
		String[] commandArgs = command.getArguments();

		try {
			
			// filtro rispetto al nome del comando
			return switch (commandName) {
				// invoco handlerRegister per gestione del comando register
				case "register" -> handleRegister(commandArgs);
				// comando non supportato restiuisco null
				default -> null;
			};

		} catch (RemoteException e) {
			
			// in caso di eccezzione segnalo che non è possibile contattare HotelierServerRmi
			return "[ERRORE] Impossibile contattare server HotelierRmi. Controllare che il server sia acceso!";
		}
	}
	
	// restituisce risposta a seguito di una richiesta di registrazione
	private String handleRegister(String[] commandArgs) throws RemoteException {
		
		// ottengo username dagli argomenti del comando
		String username = commandArgs[0];
		// ottengo password dagli argomenti del comando
		String password = commandArgs[1];
		// effettuo richiesta di registrazione rmi con username e password forniti
		String respone = clientRmi.requestRegister(username, password);
		// restituisco la risposta
		return respone;
	}
}
