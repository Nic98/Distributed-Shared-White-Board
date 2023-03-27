package CanvasGuest;

import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class GuestSideConnection extends Thread{
	protected static JSONParser parser = new JSONParser();
	protected DataInputStream input;
	protected DataOutputStream output;
	protected String permission = "Pending";
	protected static Socket socket;
	protected ArrayList<String> userList = new ArrayList<String>();;
	protected ArrayList<JSONObject> contentsBuffer = new ArrayList<JSONObject>();;

	
	public GuestSideConnection(Socket socket, DataInputStream input, DataOutputStream output) {
		GuestSideConnection.socket = socket;
		this.input = input;
		this.output = output;
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public void run() {
		while(true) {
			JSONObject response = null;
			try {
				response = (JSONObject) parser.parse(input.readUTF());
				response.toJSONString();
			} catch (ParseException | IOException e) {
				JOptionPane.showMessageDialog(CanvasGuest.loginWindow.frame, "The Manager is closed.");
				System.exit(1);
				break;
			}
			String resType = (String) response.get("resType");
			switch(resType) {
				case "Paint":
					JSONObject content = (JSONObject) response.get("resBody");
					if(CanvasGuest.loginWindow.canvasGuestGUI.canvas !=null) {
						Canvas canvas = CanvasGuest.loginWindow.canvasGuestGUI.canvas;
						canvas.addContent(content);
						canvas.paint((Graphics2D) canvas.getGraphics());
					}
					break;
					
				case "Clear":
					Canvas canvas = CanvasGuest.loginWindow.canvasGuestGUI.canvas;
					canvas.removeAll();
					canvas.updateUI();
					canvas.clearAll();
					break;
					
				case "Sync":
					userList = (ArrayList<String>) response.get("resBody");
					if (CanvasGuest.loginWindow.canvasGuestGUI!=null) {
						CanvasGuest.loginWindow.canvasGuestGUI.user_list.setListData(userList.toArray());
					}
					JSONObject res = new JSONObject();
					res.put("reqType", "Ready");
					try {
						GuestLanucher.output.writeUTF(res.toJSONString());
						GuestLanucher.output.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
					
				case "Connect":
					permission = (String) response.get("resBody");
					break;
				
				case "Kick":
					JOptionPane.showMessageDialog(CanvasGuestLoginGUI.canvasGuestGUI.frame, "You have been kicked out.");
					try {
						GuestSideConnection.socket.close();
					} catch (IOException e) {
						System.out.println("Unable to close socket");
					}
					System.exit(1);
					break;
					
				case "Remove":
					String removed_user = (String) response.get("resBody");
					userList.remove(removed_user);
					CanvasGuestLoginGUI.canvasGuestGUI.user_list.setListData(userList.toArray());
					break;
					
				case "Chat":
					String msg = (String) response.get("resBody");
					String who = (String) response.get("Sender");
					CanvasGuestLoginGUI.canvasGuestGUI.addText(msg, who);
					break;
			}
		}
	}
	
	public String isPermitted() {
		return this.permission;
	}
	public void reset() {
		this.permission = "Pending";
	} 
}
