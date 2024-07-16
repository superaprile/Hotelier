package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketLoginResponse extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketLoginResponse rappresenta il pacchetto di risposta relativo al comando "login".
	 * Contiene risposta (esito) della richiesta di login.
	 * Inviato se la richiesta viene gestita con successo.
	 */
	
	private final String response;
	
	public HotelierPacketLoginResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}
}
