package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketLogoutResponse extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketLogoutResponse rappresenta il pacchetto di risposta relativo al comando "logout".
	 * Contiene risposta (esito) della richiesta di logout.
	 * Inviato se la richiesta viene gestita con successo.
	 */

	private final String response;

	public HotelierPacketLogoutResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}
}
