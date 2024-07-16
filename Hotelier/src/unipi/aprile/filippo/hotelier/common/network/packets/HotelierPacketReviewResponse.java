package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketReviewResponse extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketReviewResponse rappresenta il pacchetto di risposta relativo al comando "insertReview".
	 * Contiene risposta (esito) della richiesta inserimento della recensione.
	 * Inviato se la richiesta viene gestita con successo.
	 */
	
	private final String response;
	
	public HotelierPacketReviewResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}
}
