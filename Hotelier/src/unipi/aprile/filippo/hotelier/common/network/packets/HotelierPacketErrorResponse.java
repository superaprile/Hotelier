package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketErrorResponse extends HotelierPacket{
	
	/**
	 * La classe `HotelierPacketErrorResponse` rappresenta i pacchetti di risposta relativi a "showMyBadges" all' interno di Hotelier.
	 * Contiene il badge relativo all' utente che ha effettuato la risposta.
	 */
	
	private final String response;
	
	public HotelierPacketErrorResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	} 
}
