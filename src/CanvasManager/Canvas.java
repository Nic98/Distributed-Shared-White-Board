package CanvasManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class Canvas extends JPanel {
	
	// An array list to store current canvas contents.
	ArrayList<JSONObject> contents = new ArrayList<JSONObject>();
	Stroke stroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

	
	public void paint(Graphics2D graphics) {
		super.paint(graphics);
		draw(graphics, contents);
	}
	
	public void draw(Graphics2D graphic, ArrayList<JSONObject> contents) {
		ArrayList<JSONObject> conccArrayList = contents;
		for (JSONObject content : conccArrayList) {
			String method = (String) content.get("method");
			int fx;
			int fy;
			int sx;
			int sy;
			
			if ((content.get("fx") instanceof Integer && content.get("fy") instanceof Integer 
					&& content.get("sx") instanceof Integer && content.get("sy") instanceof Integer)) {
				fx = (int) content.get("fx");
				fy = (int) content.get("fy");
				sx = (int) content.get("sx");
				sy = (int) content.get("sy");
			} else {
				fx = (int)(long) content.get("fx");
				fy = (int)(long) content.get("fy");
				sx = (int)(long) content.get("sx");
				sy = (int)(long) content.get("sy");
			}
			String color = (String) content.get("color");
		    graphic.setColor(new Color(Integer.parseInt(color)));
		    graphic.setStroke(stroke);
			switch(method) {
				case "Draw":
					graphic.drawLine(fx, fy, fx, fy);
					break;
				case "Line":
					graphic.drawLine(fx, fy, sx, sy);
					break;
				case "Circle":
					int px = Math.min(fx, sx);
				    int py = Math.min(fy, sy);
				    int pw = Math.abs(fx - sx);
				    graphic.drawOval(px, py, pw, pw);
					break;
				case "Triangle":
					Point midPoint;
					if (fx > sx) {
			            midPoint = new Point((sx +(Math.abs(fx - sx)/2)),sy);
					} else {
					    midPoint = new Point((sx -(Math.abs(fx - sx)/2)),sy); 
					}
					int[] xs = { fx, sx, midPoint.x };
					int[] ys = { fy, sy, midPoint.y };   
					graphic.drawPolygon(xs, ys, 3);
					break;
				case "Rectangle":
					Point topleft = new Point();
					topleft.x = Math.min(fx, sx);
					topleft.y = Math.min(fy, sy);
					int width = Math.abs(sx - fx);
					int height = Math.abs(sy - fy);
					graphic.drawRect(topleft.x, topleft.y, width, height);
					break;
				case "Text":
					String textString = (String) content.get("info");
					graphic.drawString(textString, sx, sy);
					break;
			}
		}
	}
	
	public void addContent(JSONObject content) {
		contents.add(content);
	}
	
	public ArrayList<JSONObject> getContents() {
		return this.contents;
	}
	
	public void clearAll() {
		this.contents.clear();
	}
	
}
