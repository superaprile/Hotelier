package unipi.aprile.filippo.hotelier.common.network.packets;

import unipi.aprile.filippo.hotelier.common.entities.rating.HotelierHotelRating;

public class HotelierPacketReview extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketReview rappresenta il pacchetto di richiesta inviato a seguito del comando "insertReview".
	 * Contiene:
	 * • hotelName : nome hotel da recensire; 
	 * • city : città hotel da recensire; 
	 * • rate : valore int compreso tra 0 e 5; 
	 * • rating : classe contentente i valori int compreso tra 0 e 5 dei punteggi: cleaning, position, services e quality; 
	 */

	private final String hotelName;
	private final String city;
	private final int rate;
	private final HotelierHotelRating rating;

	public HotelierPacketReview(String hotelName, String city, int rate, HotelierHotelRating rating) {
		
		this.hotelName = hotelName;
		this.city = city;
		this.rate = rate;
		this.rating = rating;
	}

	public String getHotelName() {
		return hotelName;
	}

	public String getCity() {
		return city;
	}

	public int getRate() {
		return rate;
	}

	public HotelierHotelRating getRatings() {
		return rating;
	}
}
