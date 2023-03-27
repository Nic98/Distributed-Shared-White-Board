package CanvasGuest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.parser.JSONParser;



public class GuestLanucher {

	String IPaddress;
	int portNumber;
	String username;
	Socket socket;
	protected static JSONParser parser = new JSONParser();
	protected static DataInputStream input;
	protected static DataOutputStream output;
	protected static GuestSideConnection cnt;
	
	public GuestLanucher(int portNumber, String username) {
		this.portNumber = portNumber;
		this.username = username;
	}
	
	
	public void lanuch() {
		try {
			
			socket = new Socket(IPaddress, portNumber);
			input = new DataInputStream(socket.getInputStream());
		    output = new DataOutputStream(socket.getOutputStream());
		    cnt = new GuestSideConnection(socket,input,output);
		    cnt.run();
			
		} catch (UnknownHostException e) {
			System.out.println("Canvas not found");
		} catch (IOException e) {
			System.out.println("Canvas closed");
		} finally {
			try {
				socket.close();
				input.close();
				output.close();
			} catch (IOException | NullPointerException e) {
				System.out.println("Canvas closed");
			}
		}
		
	}
	
}
