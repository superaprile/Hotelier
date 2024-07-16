package unipi.aprile.filippo.hotelier.client.cli.command.parser;

import org.apache.commons.lang3.StringUtils;

import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand;
import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand.CommandType;

public class HotelierClientCommandParser {
	
	/**
	 * La classe HotelierClientCommandParser è una classe statica con il compito di parsare i comandi dall' input inserito dall' utente.
	 * Ogni comando è formato da: un nome, un array di argomenti (stringhe) e un tipo (TCP, RMI, LOCAL), ottenuti come segue:
	 * • nome: stringa prima del primo spazio in input;
	 * • argomenti: stringhe contenute tra virgolette ("argomento");
	 * • tipo: una volta controllata la validità degli argomenti viene assegnato in base al nome del comando.
	 */

	public static HotelierClientCommand parseCommand(String input) {
		
		// nome del comando
		String commandName;
		// argomenti del comando
		String[] commandArgs;
		// converto input a lowerCase e effetto il trim
		input = StringUtils.lowerCase(input).trim();
		// parso il nome del comando
		commandName = StringUtils.substringBefore(input, " ");
		// parso gli argomenti del comando (argomenti devono essere passati tra "")
		commandArgs = StringUtils.substringsBetween(input, "\"", "\"");
		// creo il comando avente nome e argomenti ottenuti
		HotelierClientCommand command = createCommand(commandName, commandArgs);
		// restituisco il comando
		return command;

	}
	
	// restiuisce il comando avente nome e argomenti passati, controllando che gli argomenti siano validi e in numero corretto
	private static HotelierClientCommand createCommand(String commandName, String[] arguments) {
		
		// filtro rispetto al nome del comando
		switch (commandName) {
			case "help":
				// controllo che non siano stati passati argomenti
				if (arguments == null) {
					// restituisco comando help
					return new HotelierClientCommand(commandName, arguments, CommandType.LOCAL);
				}
			case "exit":
				// controllo che non siano stati passati argomenti
				if (arguments == null) {
					// restituisco comando exit
					return new HotelierClientCommand(commandName, arguments, CommandType.LOCAL);
				}
			case "showlocalranks":
				// controllo che non siano stati passati argomenti
				if (arguments == null) {
					// restituisco comando showlocalranks
					return new HotelierClientCommand(commandName, arguments, CommandType.LOCAL);
				}
			case "register":
				// controllo siano stati passati due argomenti
				if (arguments != null && arguments.length == 2) {
					// restituisco comando register
					return new HotelierClientCommand(commandName, arguments, CommandType.RMI);
				}
				break;
			case "login":
				// controllo siano stati passati due argomenti
				if (arguments != null && arguments.length == 2) {
					// restituisco comando login
					return new HotelierClientCommand(commandName, arguments, CommandType.TCP);
				}
				break;
			case "logout":
				// controllo che non siano stati passati argomenti
				if (arguments == null) {
					// restituisco comando logout
					return new HotelierClientCommand(commandName, arguments, CommandType.TCP);
				}
				break;
			case "searchhotel":
				// controllo siano stati passati due argomenti
				if (arguments != null && arguments.length == 2) {
					// restituisco comando searchhotel
					return new HotelierClientCommand(commandName, arguments, CommandType.TCP);
				}
				break;
			case "searchallhotels":
				// controllo sia stato passato un solo argomento
				if (arguments != null && arguments.length == 1) {
					// restituisco comando searchallhotels
					return new HotelierClientCommand(commandName, arguments, CommandType.TCP);
				}
				break;
			case "insertreview":
				// controllo siano stati passati argomenti validi e corretti in numero
				if (arguments != null && isValidInsertReviewArguments(arguments)) {
					// restituisco comando insertreview
					return new HotelierClientCommand(commandName, arguments, CommandType.TCP);
				}
				break;
			case "showmybadges":
				// controllo che non siano stati passati argomenti
				if (arguments == null) {
					// restituisco comando showmybadges
					return new HotelierClientCommand(commandName, arguments, CommandType.TCP);
				}

			default:
				// segnalo comando non supportato e restuisco null
				System.out.println("\nComando " + commandName + " non supportato. Digita 'help' per ottenere la lista dei comandi disponibili");
				return null;
		}
		// segnalo argomenti invalidi/mancanti per il comando e restituisco null
		System.out.println("\nArgomenti invalidi/mancanti per il comando " + commandName);
		return null;
	}
	
	// restituisce true sei gli argomenti per il comando insertReview sono validi e in numero corretto, false altrimenti
	private static boolean isValidInsertReviewArguments(String[] arguments) {
		
		// controllo siano stati passati sette argomenti
		if (arguments.length != 7) {
			// se numero argomenti diverso da sette restitusco false
			return false; 
		}

		// itero dal secondo al settimo argomento, ovvero i valori dei rate
		for (int i = 2; i <= 6; i++) {
			// controllo che siano interi e validi (compresi tra 0 e 5)
			if (!isInteger(arguments[i]) || !isValidScore(arguments[i])) {
				// se almeno uno non rispetta non è valido restituisco false
				return false; 
			}
		}
		
		// controlli sono andati a buon fine, restituisco true
		return true;
	}
	
	// restituisce true se punteggio valido (intero e compreso tra 0 e 5), false altrimenti
	private static boolean isValidScore(String score) {
		
		try {
			int num = Integer.parseInt(score);
			return num >= 0 && num <= 5;
		} catch (NumberFormatException e) {
			return false; 
		}
	}
	
	// restisce true se la stringa contine un itero, false altrimenti
	private static boolean isInteger(String s) {
		
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
