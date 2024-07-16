package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketHotel extends HotelierPacket {
	
	/**
	 * La classe HotelierPacketHotel rappresenta il pacchetto di richiesta inviato a seguito del comando "searchHotel".
	 * Contiene:
	 * • hotelName : nome hotel richiesto; 
	 * • city : città hotel richiesto; 
	 */

	private final String hotelName;

	private final String city;
	
	public HotelierPacketHotel(String hotelName, String city) {
		this.hotelName = hotelName;
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public String getHotelName() {
		return hotelName;
	}
}
