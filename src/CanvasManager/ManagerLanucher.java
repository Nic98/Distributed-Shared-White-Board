package CanvasManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ManagerLanucher {

	ArrayList<ManagerSideConnection> guests = new ArrayList<ManagerSideConnection>();
	ArrayList<String> guest_names = new ArrayList<String>();
	int portNumber;
	String IPaddress;
	ServerSocket listeningSocket;
	Socket clientSocket;
	
	public ManagerLanucher(int portNumber, String IPaddress) {
		this.portNumber = portNumber;
		this.IPaddress = IPaddress;
	}
	
	public void addName(String name) {
		this.guest_names.add(name);
	}
	
	public ArrayList<String> getNames() {
		return this.guest_names;
	}
	
	public void lanuch() {
		try {
			listeningSocket = new ServerSocket(portNumber);
			while (true) {
				clientSocket = listeningSocket.accept();
				ManagerSideConnection mcnt = new ManagerSideConnection(clientSocket);
				guests.add(mcnt);
				mcnt.start();
			}
		} catch (Exception e) {
			System.out.println("Canvas Manager Rebooted.");
			System.exit(1);
		} finally {
			if(listeningSocket != null) {
				try {
					listeningSocket.close();
				} catch (Exception e) {
					System.out.println("Unable to close the Canvas Manager.");
				}
			}
		}
	}
	
}
