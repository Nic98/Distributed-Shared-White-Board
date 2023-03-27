package CanvasGuest;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CanvasTechs implements MouseMotionListener, MouseListener, ActionListener{
	
	
	JFrame frame;
	String drawmode = "";
	Color curr_color = Color.black; // Record the current pencil color.
	Color prev_color = Color.black; // Record the previous pencil color.
	JFrame colorFrame;  // Color frame for selected colors.
	
	Point firstPoint = new Point(0,0); // Record the location of mouse when pressed.
	Point secondPoint = new Point(0,0); // Record the location of mouse when released.
	Point midPoint = new Point(0,0);
	
	Graphics2D g2d; // Graphic drawing tool.
	Canvas canvas;
	Stroke stroke = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	
	JSONParser parser = new JSONParser();
	JSONObject content;
	
	
	public CanvasTechs(JFrame frame) {
		this.frame = frame;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		drawmode = e.getActionCommand();
		g2d.setStroke(stroke);
		
		if (drawmode.equals("Colors")) {
			colorFrame = new JFrame("Colors");
			colorFrame.setVisible(false);
			colorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			Color selectedColor = JColorChooser.showDialog(colorFrame, "Choose your color", null);
			if (selectedColor == null) {
				curr_color = prev_color;
			} else {
				curr_color = selectedColor;
				prev_color = curr_color;
			}
			g2d.setColor(curr_color);
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		firstPoint.setLocation(e.getX(), e.getY());
		
		if(drawmode == null) {
			return;
		}
		
		if (drawmode.equals("Draw")) {
			g2d.drawLine(firstPoint.x, firstPoint.y, firstPoint.x, firstPoint.y);
			transToJSON("Draw");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void mouseReleased(MouseEvent e) {
		secondPoint.setLocation(e.getX(),e.getY());

		switch(drawmode) {
			case("Line"):
				g2d.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
				transToJSON("Line");
				break;
				
			case("Circle"):
				int px = Math.min(firstPoint.x, secondPoint.x);
			    int py = Math.min(firstPoint.y, secondPoint.y);
			    int pw = Math.abs(firstPoint.x - secondPoint.x);
			    g2d.drawOval(px, py, pw, pw);
			    transToJSON("Circle");
			    break;
			    
			case("Rectangle"):
				Point topleft = getTopLeft();
				int width = Math.abs(secondPoint.x - firstPoint.x);
				int height = Math.abs(secondPoint.y - firstPoint.y);
				g2d.drawRect(topleft.x, topleft.y, width, height);
				transToJSON("Rectangle");
				break;
				
			case("Triangle"):
				if (firstPoint.x > secondPoint.x) {
		            midPoint = new Point((secondPoint.x +(Math.abs(firstPoint.x - secondPoint.x)/2)),e.getY());
				} else {
				    midPoint = new Point((secondPoint.x -(Math.abs(firstPoint.x - secondPoint.x)/2)),e.getY()); 
				}
				int[] xs = { firstPoint.x, secondPoint.x, midPoint.x };
				int[] ys = { firstPoint.y, secondPoint.y, midPoint.y };   
				g2d.drawPolygon(xs, ys, 3);
				transToJSON("Triangle");
				break;
				
			case("Text"):
				String textString = JOptionPane.showInputDialog("Your Text:");
				if(textString != null) {
					g2d.drawString(textString, secondPoint.x, secondPoint.y);
					content = new JSONObject();
					content.put("method", "Text");
					content.put("fx", firstPoint.x);
					content.put("fy", firstPoint.y);
					content.put("sx", secondPoint.x);
					content.put("sy", secondPoint.y);
					content.put("info", textString);
					content.put("color", Integer.toString(curr_color.getRGB()));
					canvas.addContent(content);
					
					JSONObject req = new JSONObject();
					req.put("reqType", "Paint");
					req.put("reqBody", content);
					try {
						GuestLanucher.output.writeUTF(req.toJSONString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;
		}
	
	}
	

	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		secondPoint.setLocation(e.getX(),e.getY());
		
		if (drawmode.equals("Draw")) {
			g2d.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
			transToJSON("Line");
			firstPoint.x = secondPoint.x;
			firstPoint.y = secondPoint.y;
		}
		
		// Draw Line Rubber-Banding
//		if (drawmode.equals("Line")) {
//        	Graphics graphics = canvas.getGraphics();
//        	graphics.setColor(Color.LIGHT_GRAY);
//        	graphics.setXORMode(canvas.getBackground());
//        	graphics.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
//			secondPoint.setLocation(e.getX(), e.getY());
//
//		}
//		
//        if(drawmode.equals("Rectangle")) {
//        	Graphics graphics = canvas.getGraphics();
//        	graphics.setColor(Color.LIGHT_GRAY);
//        	graphics.setXORMode(canvas.getBackground());
//            Point TopLeft = getTopLeft();
//            int width = Math.abs(secondPoint.x - firstPoint.x);
//            int height = Math.abs(secondPoint.y - firstPoint.y);
//            graphics.drawRect(TopLeft.x, TopLeft.y, width, height);
//        	secondPoint.setLocation(e.getX(), e.getY());
//        	canvas.paint(graphics);
//        }
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	// Find the TopLeft Point
	Point getTopLeft() {
		Point toleft = new Point();
		toleft.x = Math.min(firstPoint.x, secondPoint.x);
		toleft.y = Math.min(firstPoint.y, secondPoint.y);
		return toleft;
	} 
	
	@SuppressWarnings("unchecked")
	private void transToJSON(String drawmode) {
		content = new JSONObject();
		content.put("method", drawmode);
		content.put("fx", firstPoint.x);
		content.put("fy", firstPoint.y);
		content.put("sx", secondPoint.x);
		content.put("sy", secondPoint.y);
		content.put("color", Integer.toString(curr_color.getRGB()));
		canvas.addContent(content);
	
		JSONObject req = new JSONObject();
		req.put("reqType", "Paint");
		req.put("reqBody", content);
		try {
			GuestLanucher.output.writeUTF(req.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
