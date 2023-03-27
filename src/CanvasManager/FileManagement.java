package CanvasManager;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FileManagement {
	
	JSONParser parser = new JSONParser();
	JFrame frame;
	Canvas canvas;
	public FileManagement(JFrame frame, Canvas canvas) {
		this.frame = frame;
		this.canvas = canvas;
	}
	
	
	@SuppressWarnings("unchecked")
	public void save(File save_file) {
		PrintWriter outputStream = null;
		ArrayList<JSONObject> contents = canvas.getContents();
		JSONArray contents_jsonArray = new JSONArray();
		for (JSONObject c : contents) {
			contents_jsonArray.add(c);
		}

		try {
			outputStream = new PrintWriter(new FileWriter(save_file));
			outputStream.write(contents_jsonArray.toJSONString());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Error occuring when saving file");
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void open(File selectedFile, File save_file) {
        try (FileReader reader = new FileReader(selectedFile)) {
    		canvas.removeAll();
    		canvas.clearAll();

			JSONObject clear_msg = new JSONObject();
			clear_msg.put("resType", "Clear");
			ConnectionManager.broadcastMsg(clear_msg);
			
            //Read JSON file
            Object obj = parser.parse(reader);
            JSONArray contents = (JSONArray) obj;
            for (Object content : contents) {
            	canvas.addContent((JSONObject) content);
            }
            
            ConnectionManager.broadcastDraw(contents);
        } catch (FileNotFoundException e) {
        	JOptionPane.showMessageDialog(frame, "There is no such file exist!" );
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(frame, "Error occuring when openning the file!" );
        } catch (org.json.simple.parser.ParseException e) {
        	JOptionPane.showMessageDialog(frame, "Error occuring when parsing the file!" );
		}
        canvas.paint((Graphics2D) canvas.getGraphics());
    } 
}
