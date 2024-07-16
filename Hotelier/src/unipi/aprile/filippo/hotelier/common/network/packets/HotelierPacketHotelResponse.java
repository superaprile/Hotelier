package unipi.aprile.filippo.hotelier.common.network.packets;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;

public class HotelierPacketHotelResponse extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketHotelResponse rappresenta il pacchetto di risposta relativo al comando "searchHotel".
	 * Contiene hotel richiesto.
	 * Inviato se la richiesta viene gestita con successo.
	 */

	private final HotelierHotel hotel;

	public HotelierPacketHotelResponse(HotelierHotel hotel) {
		this.hotel = hotel;

	}

	public HotelierHotel getHotel() {
		return hotel;
	}
}
