package unipi.aprile.filippo.hotelier.common.network.packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.google.gson.GsonBuilder;

public class HotelierPacketRegistry {
	
	/**
	 * HotelierPacketRegistry è una classe statica che gestisce la deserilizzazione e l'identificazione dei pacchetti di rete
	 * utilizzati nella comunicazione Tcp all' interno di Hotelier. 
	 */
	
	
	// restuisce il pacchetto deserializzato rispetto a id pacchetto passato, null se pacchetto non supportato
	public static HotelierPacket getPacketFromID(int packetID, ByteBuffer packetPayload) {
		
		// istanzio un gson per deserializzare il pacchetto
		var gson = new GsonBuilder().serializeNulls().create();
		// converto byteBuffer passto a stringa
		String serializedPacket = new String(packetPayload.array(), StandardCharsets.UTF_8);
		
		// filtro rispetto a id pacchetto
		return switch (packetID) {
			// id 0: pacchetto di login
			case 0 -> gson.fromJson(serializedPacket, HotelierPacketLogin.class); 
			// id 1: pacchetto di risposta login
			case 1 -> gson.fromJson(serializedPacket, HotelierPacketLoginResponse.class);
			// id 2: pacchetto di logout
			case 2 -> gson.fromJson(serializedPacket, HotelierPacketLogout.class);
			// id 3: pacchetto di risposta logout
			case 3 -> gson.fromJson(serializedPacket, HotelierPacketLogoutResponse.class);
			// id 4: pacchetto hotel
			case 4 -> gson.fromJson(serializedPacket, HotelierPacketHotel.class);
			// id 5: pacchetto di risposta hotel
			case 5 -> gson.fromJson(serializedPacket, HotelierPacketHotelResponse.class);
			// id 6: pacchetto di hotelList
			case 6 -> gson.fromJson(serializedPacket, HotelierPacketHotelList.class);
			// id 7: pacchetto di risposta hotelList
			case 7 -> gson.fromJson(serializedPacket, HotelierPacketHotelListResponse.class);
			// id 8: pacchetto di review
			case 8 -> gson.fromJson(serializedPacket, HotelierPacketReview.class);
			// id 9: pacchetto di risposta review
			case 9 -> gson.fromJson(serializedPacket, HotelierPacketReviewResponse.class);
			// id 10: pacchetto di badge
			case 10 -> gson.fromJson(serializedPacket, HotelierPacketBadge.class);
			// id 11: pacchetto di risposta badge
			case 11 -> gson.fromJson(serializedPacket, HotelierPacketBadgeResponse.class);
			// id 12: pacchetto di risposta erroe
			case 12 -> gson.fromJson(serializedPacket, HotelierPacketErrorResponse.class);
			// default: pacchetto non supportato
			default -> null;
		};
	}
	
	// restuisce id pacchetto relativo al pacchetto passato, -1 se pacchetto non supportato
	public static int getIDFromPacket(HotelierPacket packet) {
		// filtro rispetto a istanza del pacchetto
		return switch (packet) {
			case HotelierPacketLogin loginPacket -> 0;
			case HotelierPacketLoginResponse loginResponsePacket -> 1;
			case HotelierPacketLogout logoutPacket -> 2;
			case HotelierPacketLogoutResponse logoutResponsePacket -> 3;
			case HotelierPacketHotel hotelPacket -> 4;
			case HotelierPacketHotelResponse hotelResponsePacket -> 5;
			case HotelierPacketHotelList hotelListPacket -> 6;
			case HotelierPacketHotelListResponse hotelListResponsePacket -> 7;
			case HotelierPacketReview reviewPacket -> 8;
			case HotelierPacketReviewResponse reviewResponsePacket -> 9;
			case HotelierPacketBadge badgePacket -> 10;
			case HotelierPacketBadgeResponse badgeResponsePacket -> 11;
			case HotelierPacketErrorResponse errorResponseRequest -> 12;
			default -> -1;
		};
	}
}
