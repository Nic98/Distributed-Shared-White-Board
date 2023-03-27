package CanvasGuest;

public class CanvasGuest {
	
	static int portNumber;
	static String username;
	protected static CanvasGuestLoginGUI loginWindow;
	protected static GuestLanucher lanucher;
	
	public static void main(String args[]) {
		
		// Checking command input, if there's no command input use the default option.
		if(args.length >= 2) {
			try {
				portNumber = Integer.parseInt(args[0]);
				username = args[1];
			} catch (Exception e) {
				System.out.println("Wrong launch arguments, exit now.");
				System.exit(1);
			}
		} else {
			System.err.println("Correct format <host name> <port number> <user name>");
			System.exit(1);
		}
		
		// Initialize a Login GUI for Manager to login.
		try {
			loginWindow = new CanvasGuestLoginGUI(username);
		} catch (Exception e) {
			System.out.println("Error occuring when LoginGUI initialisation");
		}	
		
		lanucher = new GuestLanucher(portNumber, username);
		lanucher.lanuch();
	}
	
	
}
