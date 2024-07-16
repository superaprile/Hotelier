package unipi.aprile.filippo.hotelier.common.entities.rating;

public class HotelierHotelRating {
	/**
	 * La classe HotelierHotelRating rappresenta i rating degli hotel all' interno di Hotelier.
	 * Ogni rating è formato dai seguenti campi MUTABILI:
	 * 	• cleaning: valora compreso tra 0 e 5;
	 * 	• position: valora compreso tra 0 e 5;
	 * 	• services: valora compreso tra 0 e 5;
	 * 	• quality: valora compreso tra 0 e 5;
	 * La classe utilizza la sincronizzazione per garantire la coerenza dei dati  per accedere/modificare i campi mutabili
	 * in ambiente multithread garantendo thread safeness.
	 */

	private float cleaning;
	private float position;
	private float services;
	private float quality;

	public HotelierHotelRating(float cleaning, float position, float services, float quality) {
		this.cleaning = cleaning;
		this.position = position;
		this.services = services;
		this.quality = quality;
	}
	
	public HotelierHotelRating(HotelierHotelRating rating) {
        this.cleaning = rating.cleaning;
        this.position = rating.position;
        this.services = rating.services;
        this.quality = rating.quality;
    }

	public synchronized float getCleaning() {
		return cleaning;
	}

	public synchronized void setCleaning(float cleaning) {
		this.cleaning = cleaning;
	}

	public synchronized float getPosition() {
		return position;
	}

	public synchronized void setPosition(float position) {
		this.position = position;
	}

	public synchronized float getServices() {
		return services;
	}

	public synchronized void setServices(float services) {
		this.services = services;
	}

	public synchronized float getQuality() {
		return quality;
	}

	public synchronized void setQuality(float quality) {
		this.quality = quality;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Cleaning: ").append(cleaning).append("\n");
		builder.append("Position: ").append(position).append("\n");
		builder.append("Services: ").append(services).append("\n");
		builder.append("Quality: ").append(quality);

		return builder.toString();
	}

}
