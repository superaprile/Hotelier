package unipi.aprile.filippo.hotelier.common.entities;

import java.util.ArrayList;
import java.util.List;

public class HotelierLocalRank {

	/**
	 * La classe HotelierLocalRank rappresenta i rank locali all' interno di Hotelier.
	 * Ogni rank locale è formato dai seguenti campi MUTABILI:
	 *  • città; 
	 * e dai segueni campi MUTABILI: 
	 * 	• hotels: lista di hotel aventi quella città;
	 */

	private final String city;
	private List<HotelierHotel> hotels;

	public HotelierLocalRank(String city) {
		this.city = city;
		this.hotels = new ArrayList<>();

	}

	public void add(HotelierHotel hotel) {
		hotels.add(hotel);
	}

	public String getCity() {
		return city;
	}

	public List<HotelierHotel> getHotels() {
		return hotels;
	}

	public void setHotels(List<HotelierHotel> hotels) {

		this.hotels = hotels;

	}

}
