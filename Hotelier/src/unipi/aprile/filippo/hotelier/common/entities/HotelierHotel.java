package unipi.aprile.filippo.hotelier.common.entities;

import java.util.ArrayList;
import java.util.List;

import unipi.aprile.filippo.hotelier.common.entities.rating.HotelierHotelRating;

public class HotelierHotel {
	
	/**
	 * La classe HotelierHotel rappresenta gli hotel all' interno di Hotelier.
	 * Ogni hotel è formato dai seguenti campi IMMUTABILI:
	 * 	• id;
	 * 	• nome;
	 * 	• descrizione;
	 * 	• città;
	 * 	• numero di telefono;
	 * 	• lista di servizi (Stringhe),
	 * e dai segueni campi MUTABILI:
	 * 	• rate: media dei rate delle sue recensioni;
	 * 	• ratings: classe contenente media dei punteggi: cleaning, position, services e quality delle sue recensioni;
	 * 	• reviewCount: numero di recensioni presenti su di esso;
	 * 	• rank: punteggio globale calcolato in base a: numero, qualità e attualità delle recensioini;
	 * 	• localRank: rank locale rispetto agli altri hotel della stessa città;
	 * La classe utilizza la sincronizzazione per garantire la coerenza dei dati  per accedere/modificare i campi mutabili
	 * in ambiente multithread garantendo thread safeness.
	 */

	private final int id;
	private final String name;
	private final String description;
	private final String city;
	private final String phone;
	private final List<String> services;
	private float rate;
	private HotelierHotelRating ratings;
	private int reviewCount;
	private double rank;
	private int localRank;

	public HotelierHotel(int id, String name, String description, String city, String phone, List<String> services, int rate, HotelierHotelRating ratings) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.city = city;
		this.phone = phone;
		this.services = new ArrayList<>(services);
		this.rate = rate;
		this.ratings = new HotelierHotelRating(ratings);
	}
	
	
	// copy constructor, restitusce una copia dell' istanza hotel passata
	public HotelierHotel(HotelierHotel hotel) {
        this.id = hotel.id;
        this.name = hotel.name;
        this.description = hotel.description;
        this.city = hotel.city;
        this.phone = hotel.phone;
        this.services = new ArrayList<>(hotel.services); 
        this.rate = hotel.rate;
        this.ratings = new HotelierHotelRating(hotel.ratings); 
        this.reviewCount = hotel.reviewCount;
        this.rank = hotel.rank;
        this.localRank = hotel.localRank;
    }
	

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCity() {
		return city;
	}

	public String getPhone() {
		return phone;
	}

	public List<String> getServices() {
		return services;
	}

	public synchronized float getRate() {
		return rate;
	}

	public synchronized void setRate(float rate) {
		this.rate = rate;
	}

	public synchronized HotelierHotelRating getRating() {
		return ratings;
	}

	public synchronized void setRating(HotelierHotelRating ratings) {
		this.ratings = ratings;
	}

	public synchronized int getReviewCount() {
		return reviewCount;
	}

	public synchronized void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}
	
	public synchronized void incrementReviewCount() {
		this.reviewCount++;
	}
	
	public synchronized double getRank() {
		return rank;
	}

	public synchronized void setRank(double rank) {
		this.rank = rank;
	}
	
	public synchronized int getLocalRank() {
		return localRank;
	}

	public synchronized void setLocalRank(int localRank) {
		this.localRank = localRank;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Name: ").append(name).append("\n");
		sb.append("Description: ").append(description).append("\n");
		sb.append("City: ").append(city).append("\n");
		sb.append("Phone: ").append(phone).append("\n");
		sb.append("Services: ").append(services).append("\n");
		sb.append("Rate: ").append(rate).append("\n");
		sb.append(ratings).append("\n");
		sb.append("Review Count: ").append(reviewCount).append("\n");
		sb.append("Rank: ").append(rank).append("\n");
		sb.append("Local Rank: ").append(localRank);

		return sb.toString();
	}

}
