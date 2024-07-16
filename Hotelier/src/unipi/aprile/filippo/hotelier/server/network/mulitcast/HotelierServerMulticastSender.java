package unipi.aprile.filippo.hotelier.server.network.mulitcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;

public class HotelierServerMulticastSender {
	
	/**
	 * La classe HotelierServerMulticastSender ha il compito di inviare pacchetti UDP per notificare cambiamento prima posizione 
	 * rank locale  a tutti gli utenti loggati.
	 * Utilizza una MultiCastSocket con porta assegnata dal sistema operativo e address, porta passati da costruttore per la send.
	 */
	
	
	// multicast address
	private final String mcastAddress;
	// porta multiCast a cui inviare i pacchetti UDP
	private final int mcastPort;
	// multicast socket UDP
	private MulticastSocket socket;
	// indirizzo gruppo multicast client loggati
	private InetAddress group;

	public HotelierServerMulticastSender(String mcastAddress, int mcastPort) throws IOException {
		this.mcastAddress = mcastAddress;
		this.mcastPort = mcastPort;
		socket = new MulticastSocket();
		group = InetAddress.getByName(mcastAddress);
	}
	
	// invia pacchetto UDP contenente nuovo hotel in prima posizione per il rank locale
	public void notifyFirstPosition(HotelierHotel hotel) {
		try {
			
			// setto la stringa da inviare per la notifica
			String response = "Prima posizione cambiata per la città " + hotel.getCity() + " : " + hotel.getName();
			// converto la stringa in bytes
			byte[] responseBytes = response.getBytes();
			// creo il pacchetto UDP contenente stringa serializzata in byte inserendo gruppo e porta multicast socket client
			DatagramPacket messagePacket = new DatagramPacket(responseBytes, responseBytes.length, group, mcastPort);
			// invio il pacchetto
			socket.send(messagePacket);

		} catch (IOException e) {
			close();
		}
	}
	
	// chiude la socket se era stata creata ed era ancora aperta
	private void close() {

		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}
}
