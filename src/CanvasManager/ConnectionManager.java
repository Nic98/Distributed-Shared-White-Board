package CanvasManager;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public class ConnectionManager {
	public static synchronized int checkin(String name) {
		Integer ans = CanvasManagerLoginGUI.canvasManagerGUI.connectRequest(name);
		return ans;
	}
	@SuppressWarnings("unchecked")
	public static synchronized void updateGuestList(ArrayList<String> guests) {
		CanvasManagerLoginGUI.canvasManagerGUI.user_list.setListData(guests.toArray());
	}
	
	// Send message to all guests
	public static synchronized void broadcastMsg(JSONObject msg) {
		for (ManagerSideConnection cnt : CanvasManager.lanucher.guests) {

			if (cnt.socket.isConnected()) {
				if (msg.get("resType").equals("Chat")) {
					if (!cnt.username.equals(msg.get("Sender"))) {
						try {
							cnt.output.writeUTF(msg.toJSONString());
							cnt.output.flush();
						} catch (IOException e) {
//							System.out.println(e);
//							System.out.println("Error broadcast");
						}
					}
				} else {
					try {
						cnt.output.writeUTF(msg.toJSONString());
						cnt.output.flush();
					} catch (IOException e) {
//						System.out.println(e);
//						System.out.println("Error broadcast");
					}
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	public static synchronized void broadcastDraw(ArrayList<JSONObject> contents) {
		for (JSONObject content : contents) {
			for (ManagerSideConnection cnt : CanvasManager.lanucher.guests) {
				if (cnt.socket.isConnected()) {
					try {
						JSONObject res = new JSONObject();
						res.put("resType", "Paint");
						res.put("resBody", content);
						cnt.output.writeUTF(res.toJSONString());
						cnt.output.flush();
					} catch (IOException e) {
//						System.out.println("Error broadcast");
					}
				}
			}
		}
	}
}
