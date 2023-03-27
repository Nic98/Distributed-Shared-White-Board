package CanvasManager;

public class CanvasManager {

	static String IPaddress;
	static int portNumber;
	static String username;
	static CanvasManagerLoginGUI managerLoginWindow;
	static ManagerLanucher lanucher;
	
	public static void main(String args[]) {
		
		// Checking command input, if there's no command input use the default option.
		if(args.length >= 3) {
			try {
				IPaddress = args[0];
				portNumber = Integer.parseInt(args[1]);
				username = args[2];
			} catch (Exception e) {
				System.out.println("Wrong launch arguments, exit now.");
				System.exit(1);
			}
		} else {
			IPaddress = "localhost";
			portNumber = 8888;
			username = "Manager";
			System.out.println("Default launch option.");
		}
		
		// Initialize a Login GUI for Manager to login.
		try {
			managerLoginWindow = new CanvasManagerLoginGUI(username);
		} catch (Exception e) {
			System.out.println("Error occuring when LoginGUI initialisation");
		}
		// Launch the server waiting for guest connections.
		lanucher = new ManagerLanucher(portNumber, IPaddress);
		lanucher.lanuch();	
	}
}
