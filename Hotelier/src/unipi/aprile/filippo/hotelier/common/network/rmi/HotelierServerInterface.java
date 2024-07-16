package unipi.aprile.filippo.hotelier.common.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface HotelierServerInterface extends Remote {
	
	// Metodo per invocazione remote registrazione nuovo utente
	public String registerUser(String username, String password) throws RemoteException;
	// Metodo per invocazione remote registrazione callbackRmi client e relaltive città di interesse
	public void registerCallback(HotelierClientInterface callbackClient, List<String> cities) throws RemoteException;	
	// Metodo per invocazione remote deregistrazione callbackRmi client
	public void unregisterCallback(HotelierClientInterface callbackClient) throws RemoteException;	
}
