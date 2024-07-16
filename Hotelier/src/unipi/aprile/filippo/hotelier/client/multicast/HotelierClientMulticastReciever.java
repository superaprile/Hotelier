package unipi.aprile.filippo.hotelier.client.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HotelierClientMulticastReciever implements Runnable {
	
	/**
	 * La classe HotelierClientMulticastReciever avvia un thread per la ricezione delle notifiche riguardanti i cambiamenti 
	 * di qualsiasi prima posizione dei rank locali.
	 * Utilizza una MulticastSocket con porta e indirizzo specificati nel file config e si mette in attesa di pacchetti UDP.
	 * Alla ricezione di un pacchetto UDp, lo converte in stringa e lo stampa.
	 * La join e leave dal gruppo multicast vengono eseguite dal client rispettivamente a seguito di un comando di login e di uno di logout.
	 * Infine, in caso di comando exit, la socket viene chiusa.
	 */
	
	// soscket multicast
	private MulticastSocket socket;
	// address multicast socket
	private InetAddress group;

	public HotelierClientMulticastReciever(String mcastAddress, int mcastPort) {

		try {
			// instazione una nuova socket multicast con porta passata
			socket = new MulticastSocket(mcastPort);
			// ottengo address da indirizzo passato 
			group = InetAddress.getByName(mcastAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// avvio il thread
		Thread thread = new Thread(this);
		thread.start();
	}
	
	// effettua la join del gruppo multicast
	public void joinGroup() {
		try {
			socket.joinGroup(group); // Join the multicast group
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// effettua la leave dal gruppo multicast
	public void leaveGroup() {
		try {
			socket.leaveGroup(group); // Leave the multicast group
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {

		try {
			// alloco un byte array di 1024 byte
			byte[] byteArray = new byte[1024];
			// instanzio un DatagramPacket avente come array di byte byteArray e lunghezza la lunghezza di byteArray per ricever notifiche Udp
			DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length);
			
			// itero finchè il thread non viene interrotto
			while (!Thread.interrupted()) {
				
				// mi metto in attesa della notifica Udp
				socket.receive(packet);
				
				// notifica ricevuta
				// converto i byte ricevuti in una stringa risposta
				String response = new String(packet.getData(), 0, packet.getLength());
				// stampo la rispota
				System.out.println("[NOTIFICA] " + response + "\n");
				// eseguo il flush di system out
				System.out.flush();
			}
		} catch (IOException e) {
			close();
		}
	}
	
	// chiude la socket multicast se ancora aperta
	public void close() {

		if (!socket.isClosed()) {

			socket.close();

		}

	}

}
