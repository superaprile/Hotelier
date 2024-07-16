package unipi.aprile.filippo.hotelier.common.network.packets;

public class HotelierPacketHotelList extends HotelierPacket{
	
	/**
	 * La classe HotelierPacketHotelList rappresenta il pacchetto di richiesta inviato a seguito del comando "searchAllHotels".
	 * Contiene:
	 * • city : città hotels richiesti; 
	 */
	
	private final String city;
	
	public HotelierPacketHotelList(String city) {
		this.city = city;
	}
	
	public String getCity() {
		return city;
	} 
}
