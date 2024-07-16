package unipi.aprile.filippo.hotelier.common.entities;

import java.time.LocalDateTime;

import unipi.aprile.filippo.hotelier.common.entities.rating.HotelierHotelRating;

public class HotelierReview {
	
	/**
	 * La classe HotelierReview rappresenta le recensioni all' interno di Hotelier.
	 * Ogni recensione è formata dai seguenti campi IMMUTABILI:
	 * 	• username: username utente che ha inserito la recensione;
	 * 	• hotelID: id hotel a cui è riferita la recensione;
	 * 	• rate: rate hotel;
	 * 	• rating: instanza classe contente punteggi: cleaning, position, services e quality;
	 * 	• timestamp: timestamp di pubblicazione della recensione;
	 */

	private final String username;
	private final int hotelID;
	private final int rate;
	private final HotelierHotelRating rating;
	private final String timestamp;

	public HotelierReview(String username, int hotelID, int rate, HotelierHotelRating rating) {
		this.username = username;
		this.hotelID = hotelID;
		this.rate = rate;
		this.rating = new HotelierHotelRating(rating);

		timestamp = LocalDateTime.now().toString();
	}

	public String getUsername() {
		return username;
	}

	public int gethotelID() {
		return hotelID;
	}

	public int getRate() {
		return rate;
	}

	public HotelierHotelRating getRating() {
		return new HotelierHotelRating(rating);
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Username: ").append(username).append("\n");
		builder.append("HotelID: ").append(hotelID).append("\n");
		builder.append("Rate: ").append(rate).append("\n");
		builder.append("Review: ").append(rating).append("\n");
		builder.append("Timestamp: ").append(timestamp);

		return builder.toString();
	}

}
