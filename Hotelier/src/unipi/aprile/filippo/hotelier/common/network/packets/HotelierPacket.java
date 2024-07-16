package unipi.aprile.filippo.hotelier.common.network.packets;

public abstract class HotelierPacket{
	
	/**
	 * La classe HotelierPacket rappresenta la classe base astratta per tutti i pacchetti di rete 
	 * inviati nel protocollo di comunicazione TCP di Hotelier.
	 * Hotelier utilizza un protocollo richiesta/risposta per la comunicazione TCP, dove ogni messaggio è formato da:
	 * 	• payloadSize (int): lunghezza del pacchetto serializzato; 
	 * 	• id (int): identificatore univoco del pacchetto (login, logout, loginResponse ...); 
	 * 	• pacchetto serializzato in JSON (String): pacchetto serializzato.
	 * Ogni tipo di richiesta e la relativa risposta utilizza un pacchetto specifico che estende HotelierPacket, i quali si dividono in:
	 * 	• pacchetti di richiesta : inviati dal client a server (HotelierPacketLogin, HotelierPacketLogout ...); 
	 * 	• pacchetti di risposta : inviati dal server al client se la richiesta ha avuto successo (HotelierPacketLoginResponse, 
	 * 	  HotelierPacketLogoutResponse ...);
	 * 	• pacchetti di errore (HotelierPacketErrorResponse) : inviato dal server al client se la richiesta ha sollevato un errore 
	 * 	  (mancanza di permessi, risorsa non trovata ...)
	 * Questo approccio consente di filtrare facilmente i pacchetti in base al loro tipo utilizzando l'operatore `instanceof`.
	 * Ogni pacchetto viene convertito in JSON e  i byte risultanti vengono inviati sulla socket.
	 */
	
}
