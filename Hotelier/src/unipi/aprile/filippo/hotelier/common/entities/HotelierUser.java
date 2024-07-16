package unipi.aprile.filippo.hotelier.common.entities;

public class HotelierUser {

	/**
	 * La classe HotelierUser rappresenta gli utenti all' interno di Hotelier.
	 * Ogni utente è formato dai seguenti campi IMMUTABILI: 
	 * 	• username: username dell' utente; 
	 * 	• password: password dell' utente; 
	 * e dai seguenti campi MUTABILI: 
	 * • badge: distintivo dell' utente; 
	 * • reviewCount: numero di recensioni effettuate dall' utente; 
	 * La classe utilizza la sincronizzazione per garantire la coerenza dei dati  per accedere/modificare i campi mutabili
	 * in ambiente multithread garantendo thread safeness.
	 */

	private final String username;
	private final String password;
	private HotelierBadge badge;
	private int reviewCount;

	public HotelierUser(String username, String password) {
		this.username = username;
		this.password = password;
		this.badge = HotelierBadge.RECENSORE;
		reviewCount = 0;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public synchronized HotelierBadge getBadge() {
		return badge;
	}

	public synchronized void setBadge(HotelierBadge badge) {
		this.badge = badge;
	}

	// Esegue update del badge una volta raggiunta la soglia prestabilita
	public synchronized void updateBadge() {

		switch (reviewCount) {
			case 2:
				badge = HotelierBadge.RECENSORE_ESPERTO;

				break;
			case 3:
				badge = HotelierBadge.CONTRIBUENTE;

				break;
			case 4:
				badge = HotelierBadge.CONTRIBUENTE_ESPERTO;

				break;
			case 5:
				badge = HotelierBadge.SUPER_CONTRIBUENTE;

				break;

			default:

				break;
		}
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Username: ").append(username).append("\n");
		builder.append("Password: ").append(password).append("\n");
		builder.append("Review Count: ").append(reviewCount).append("\n");
		builder.append("Badge: ").append(badge);

		return builder.toString();
	}

	public enum HotelierBadge {
		RECENSORE, RECENSORE_ESPERTO, CONTRIBUENTE, CONTRIBUENTE_ESPERTO, SUPER_CONTRIBUENTE;
	}

}
