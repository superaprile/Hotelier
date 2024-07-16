package unipi.aprile.filippo.hotelier.common.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HotelierClientInterface extends Remote {
	
	// metodo per invocazione remote callback notifica cambiamento rank locale città di interesse
	public void notifyInterest(String serializedLocalRank) throws RemoteException;	
}
