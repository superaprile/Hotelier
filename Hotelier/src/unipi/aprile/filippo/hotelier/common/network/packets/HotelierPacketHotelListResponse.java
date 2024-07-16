package unipi.aprile.filippo.hotelier.common.network.packets;

import java.util.List;

import unipi.aprile.filippo.hotelier.common.entities.HotelierHotel;

public class HotelierPacketHotelListResponse extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketHotelListResponse rappresenta il pacchetto di risposta relativo al comando "searchAllHotels".
	 * Contiene la lista di hotel aventi città richiesta.
	 * Inviato se la richiesta viene gestita con successo.
	 */

	private final List<HotelierHotel> hotels;
	
	public HotelierPacketHotelListResponse(List<HotelierHotel> hotels) {
		this.hotels = hotels;
	}

	public List<HotelierHotel> getHotels() {
		return hotels;
	}
}
