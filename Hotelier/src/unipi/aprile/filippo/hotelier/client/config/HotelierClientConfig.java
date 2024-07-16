package unipi.aprile.filippo.hotelier.client.config;

public class HotelierClientConfig {
	
	/**
	 * La classe HotelierClientConfig contiene i config del client, ovvero:
	 * • tcpPort, porta per socket Tcp
	 * • rmiPort, porta per registro Rmi
	 * • mcastPort, porta per socket mutlicast
	 * • serverAddress, indirizzo HotelierServer utilizzato dalle socket
	 * • rmiRemoteReference, nome per reperire stub server dal registro rmi
	 * • mcastAddress, indirizzo ip socket multicast
	 */

    private final int tcpPort;
    private final int rmiPort;
    private final int mcastPort;
    private final String serverAddress;
    private final String rmiRemoteReference;
    private final String mcastAddress;

    public HotelierClientConfig(int tcpPort, int rmiPort, int mcastPort, String serverAddress, String rmiRemoteReference, String mcastAddress) {
        this.tcpPort = tcpPort;
        this.rmiPort = rmiPort;
        this.mcastPort = mcastPort;
        this.serverAddress = serverAddress;
        this.rmiRemoteReference = rmiRemoteReference;
        this.mcastAddress = mcastAddress;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public int getMcastPort() {
        return mcastPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getRmiRemoteReference() {
        return rmiRemoteReference;
    }

    public String getMcastAddress() {
        return mcastAddress;
    }
}