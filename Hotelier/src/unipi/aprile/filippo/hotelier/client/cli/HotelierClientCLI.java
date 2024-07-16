package unipi.aprile.filippo.hotelier.client.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import unipi.aprile.filippo.hotelier.client.cli.command.HotelierClientCommand;
import unipi.aprile.filippo.hotelier.client.cli.command.handler.HotelierClientCommandHandler;
import unipi.aprile.filippo.hotelier.client.cli.command.parser.HotelierClientCommandParser;
import unipi.aprile.filippo.hotelier.client.multicast.HotelierClientMulticastReciever;
import unipi.aprile.filippo.hotelier.client.rmi.HotelierClientRmi;

public class HotelierClientCLI implements Runnable {
	
	// client rmi
	private final HotelierClientRmi clientRmi;
	// multicast reciever
	private final HotelierClientMulticastReciever multicastReciever;
	// handler dei comandi
	private final HotelierClientCommandHandler commandHandler;

	public HotelierClientCLI(HotelierClientRmi clientRmi, HotelierClientMulticastReciever multicastReciever) throws Exception {
		// inizializzo il client rmi a quello passato per parametro
		this.clientRmi = clientRmi;
		// inizializzo il multicast reciever a quello passato per parametro
		this.multicastReciever = multicastReciever;
		// instanzio un command handler per la gestione dei comandi
		commandHandler = new HotelierClientCommandHandler(clientRmi);
		
		// creo il thread per la gestione dei comandi
		Thread thread = new Thread(this);
		// avvio il thread
		thread.start();
	}

	@Override
	public void run() {
		
		// instanzio uno scanner per leggere input utente
		try (Scanner scanner = new Scanner(System.in)) {
			
			System.out.println("Benvenuto su Hotelier! Digita 'help' per ottenere la lista dei comandi disponinili, 'exit' per terminare il client.\n");
			
			// itero finchè il thread non viene interotto
			while (!Thread.interrupted()) {
				
				System.out.print("> ");
				// recupero input inserito dall' utente
				String input = scanner.nextLine();
				
				// se input è vuoto skip iterazione
				if (input.isEmpty()) {
					continue;
				}
				
				// parso il comando dall' input inserito dall' utente
				HotelierClientCommand command = HotelierClientCommandParser.parseCommand(input);
				// controllo se il comando è supportato
				if (command != null) {
					
					// Comando supportato: controllo se è stata richiesta una exit
					if (StringUtils.equalsIgnoreCase(command.getName(), "exit")) {
						// effettuo la chiusura delle risorse allocate per il client
						close();
						// termino il client
						break;
					}

					try {
						
						// gestisco e recupero relativa risposta del comando
						String response = commandHandler.handleCommand(command);
						// stampo la rispsota
						System.out.println("\n" + response);
						
						// controllo se è stata richiesta una login ed ha avuto successo
						if (StringUtils.equals(response, "Login effettuato correttamente!")) {
							
							// chiedo all' utente di inserire città di interesse per callback rmi
							System.out.print("\nInserire le città di interesse: ");
							// recupero input inserito dall' utente
							input = scanner.nextLine();
							
							// parso le città inserite (devono essere passate tra "") e le salvo in un array
							String[] cities = StringUtils.substringsBetween(input, "\"", "\"");
							
							// controllo se utente ha inserito almeno una città di interesse
							if (cities != null && cities.length != 0) {
								
								// registro client per callback rmi sul cambiamento del rank locale delle città di interesse
								clientRmi.registerInterests(Arrays.asList(cities));
								System.out.println("\nCittà di interesse registrate con successo !");
							} else {
								// segnalo all' utente che non si è registrato per nessuna città
								System.out.println("\n[WARNING] Nessuna città di interesse registrata.");
							}
							
							// aggiungo client al gruppo multicast per ricevere notifiche Udp su cambiamento prima posizione del rank locale di qualsiasi città
							multicastReciever.joinGroup();
						}

						// controllo se è stata richiesta una logout ed ha avuto successo
						if (StringUtils.equals(response, "Logout effettuato correttamente!")) {
							
							// richiesta logout con successo
							// controllo se utente aveva inserito delle città di interesse per callback rmi
							if (!clientRmi.islocalRankMapEmpty()) {
								
								//deregistro client per callback rmi
								clientRmi.unregisterInterests();
								
								clientRmi.resetLocalRankMap();
							}
							
							//rimuovo client dal gruppo multicast per notifiche Udp
							multicastReciever.leaveGroup();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				System.out.println("");
			}
		}
	}
	
	// effettua la chiusura delle risorse associate per il client
	private void close() {
		
		// deregistro client per callback rmi se utente aveva inserito città di interesse e rimuovo esportazione dello stub per le callback
		clientRmi.close();
		// chiudo la multicast socket se ancora aperta
		multicastReciever.close();
		var tcpHandler = commandHandler.getHotelierClientTcpHandler();
		// chiudo socket Tcp e stream associati se socket ancora aperta
		tcpHandler.close();

	}

}
