package CanvasManager;

import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class ManagerSideConnection extends Thread {
	protected Socket socket = null;
	protected DataInputStream input;
	protected DataOutputStream output;
	protected JSONParser parser = new JSONParser();
	protected ManagerLanucher lanucher = CanvasManager.lanucher;
	protected String username;
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	public void run() {
		try {
			while (true) {
				JSONObject request = null;
				try {
					request = (JSONObject) parser.parse(input.readUTF());
				} catch (ParseException | IOException e) {
					JSONObject res = new JSONObject();
					res.put("resType", "Remove");
					res.put("resBody", username);
					ConnectionManager.broadcastMsg(res);
					socket.close();
					lanucher.guests.remove(this);
					lanucher.guest_names.remove(username);
					ConnectionManager.updateGuestList(lanucher.getNames());
					break;
				}
				
				if (request!=null) {
					
					String reqType = (String) request.get("reqType");
					
					switch(reqType) {
						case "Connect":
							username = (String) request.get("username");
							if (lanucher.guest_names.contains(username)) {
								JSONObject res = new JSONObject();
								res.put("resType", "Connect");
								res.put("resBody", "Dulplicate");
								output.writeUTF(res.toJSONString());
								output.flush();
								username = "Dul";
							} else {
								Integer confirmation = ConnectionManager.checkin(username);
								if (confirmation == 0) {
									if (lanucher.guest_names.contains(username)) {
										JSONObject res = new JSONObject();
										res.put("resType", "Connect");
										res.put("resBody", "Dulplicate");
										output.writeUTF(res.toJSONString());
										output.flush();
										lanucher.guests.remove(this);
										socket.close();
										break;
									} else {
										if (socket.isConnected()) {
											JSONObject res = new JSONObject();
											res.put("resType", "Connect");
											res.put("resBody", "Accept");
											output.writeUTF(res.toJSONString());
											output.flush();
											lanucher.addName(username);
											ConnectionManager.updateGuestList(lanucher.getNames());
										}
									}
								} else {
									JSONObject res = new JSONObject();
									res.put("resType", "Connect");
									res.put("resBody", "Rejected");
									output.writeUTF(res.toJSONString());
									output.flush();
									lanucher.guests.remove(this);
									lanucher.guest_names.remove(username);
									ConnectionManager.updateGuestList(lanucher.getNames());
								}
							}
							break;
						case "Sync":
							ArrayList<JSONObject> contents = CanvasManagerGUI.canvas.getContents();
							ArrayList<String> usernames = CanvasManager.lanucher.guest_names;
							JSONObject res = new JSONObject();
							res.put("resType", "Sync");
							res.put("resBody", usernames);
							ConnectionManager.broadcastDraw(contents);
							ConnectionManager.broadcastMsg(res);
							break;
						case "Paint":
							JSONObject content = (JSONObject) request.get("reqBody");
							ArrayList<JSONObject> new_contents = CanvasManagerGUI.canvas.getContents();
							new_contents.add(content);
							ConnectionManager.broadcastDraw(new_contents);
							CanvasManagerGUI.canvas.paint((Graphics2D) CanvasManagerGUI.canvas.getGraphics());
							break;
						case "Ready":
							contents = CanvasManagerGUI.canvas.getContents();
							ConnectionManager.broadcastDraw(contents);
							break;
							
						case "Chat":
							String msg = (String) request.get("reqBody");
							String who = (String) request.get("Sender");
							CanvasManagerLoginGUI.canvasManagerGUI.addText(msg, who);
							JSONObject res2 = new JSONObject();
							res2.put("resType", "Chat");
							res2.put("resBody", msg);
							res2.put("Sender", who);
							output.writeUTF(res2.toJSONString());
							output.flush();
							ConnectionManager.broadcastMsg(res2);
							break;
					}
				}
			}
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(CanvasManager.managerLoginWindow.canvasManagerGUI.frame, "Time out");
		} catch (IOException e) {
			System.out.println("The guest has left.");
		}
	}
	
	
	
	public ManagerSideConnection(Socket socket) {
		this.socket = socket;
		try {
			// The JSON Parser
			parser = new JSONParser();
			// Input stream
			input = new DataInputStream(socket.getInputStream());
			// Output Stream
		    output = new DataOutputStream(socket.getOutputStream());
		    
		} catch(SocketException e) {
			System.out.println("Illegal socket!");
		} catch (IOException e) {
			System.out.println("Error occuring when creating buffers");
		}
	}
}
