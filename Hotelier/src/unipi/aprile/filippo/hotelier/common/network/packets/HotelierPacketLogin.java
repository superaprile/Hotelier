package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketLogin extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketLogin rappresenta il pacchetto di richiesta inviato a seguito del comando "login".
	 * Contiene:
	 * • username : username utente; 
	 * • username : password utente; 
	 */

	private final String username;
	private final String password;
	
	public HotelierPacketLogin(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
