package unipi.aprile.filippo.hotelier.common.network.packets;

import unipi.aprile.filippo.hotelier.common.entities.HotelierUser.HotelierBadge;

public class HotelierPacketBadgeResponse extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketBadgeResponse rappresenta il pacchetto di risposta relativo al comando "showMyBadges".
	 * Contiene il distintivo dell'utente che ha effettuato la richiesta.
	 * Inviato se la richiesta viene gestita con successo.
	 */
	
	private final HotelierBadge badge;
	
	public HotelierPacketBadgeResponse(HotelierBadge badge) {
		this.badge = badge;
	}

	public HotelierBadge getBadge() {
		return badge;
	}	
}
