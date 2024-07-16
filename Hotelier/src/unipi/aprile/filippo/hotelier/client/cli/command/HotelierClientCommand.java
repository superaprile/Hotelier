package unipi.aprile.filippo.hotelier.client.cli.command;


public class HotelierClientCommand {
	
	/**
	 * 	La classe HotelierClientCommand rappresenta i comandi disponibili in Hotelier Client.
	 *	Ogni comando è formato da:
	 *	• un nome;
	 * 	• un array di argomenti (stringhe);
	 * 	• un tipo, il quale può essere: 
	 *		• TCP (gestiti da clientTcpHandler);
	 *		• RMI (gestiti da clientRmiHandler);
	 *		• LOCAL (gestiti da clientCommandHandler).
	 */
	
    private final String name;
    private final String[] arguments;
    private final CommandType commandType;

    public HotelierClientCommand(String name, String[] arguments, CommandType commandType) {
    	this.name = name;
        this.arguments = arguments;
        this.commandType = commandType;
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments;
    }
    
    public CommandType getCommandType() {
        return commandType;
    }
    
    public enum CommandType {
        TCP,
        RMI,
        LOCAL
    }
}
