package CanvasGuest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.json.simple.JSONObject;
import javax.swing.JTextField;

public class CanvasGuestGUI {

	JFrame frame;
	private String guestname;
	static CanvasGuestGUI canvasGuestGUI;
	static Canvas canvas;
	static CanvasTechs canvasTechs;
	@SuppressWarnings("rawtypes") 
	JList user_list;
	private JTextArea chatHistory;
	private JTextField textInput;
	/**
	 * Create the application.
	 */
	public CanvasGuestGUI(String username) {
		this.guestname = username;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("activeCaption"));
		frame.setTitle("CanvasGuest: " + guestname);
		frame.setBounds(100, 100, 905, 644);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		frame.setResizable(false);
		
		// Create a canvas for drawing
		canvas = new Canvas();
		canvas.setBackground(Color.WHITE);
		canvas.setBounds(10, 71, 745, 400);
		canvas.setFont(new Font("Arial", Font.BOLD, 16));
		frame.getContentPane().add(canvas);
		
		// Add Canvas drawing techniques in to the canvas.
		canvasTechs = new CanvasTechs(frame);
		canvas.addMouseListener(canvasTechs);
		canvas.addMouseMotionListener(canvasTechs);
		canvasTechs.g2d = (Graphics2D) canvas.getGraphics();
		canvasTechs.canvas = canvas;
		
		
		JPanel toolPanel = new JPanel();
		toolPanel.setBackground(UIManager.getColor("activeCaptionBorder"));
		toolPanel.setBounds(10, 25, 871, 36);
		toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.getContentPane().add(toolPanel);
		
		
		JLabel lblNewLabel = new JLabel("ToolPanel");
		lblNewLabel.setBounds(10, 10, 91, 16);
		lblNewLabel.setForeground(UIManager.getColor("Tree.selectionBackground"));
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 16));
		frame.getContentPane().add(lblNewLabel);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(769, 75, 112, 397);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollPane);

		user_list = new JList();
		user_list.setBackground(SystemColor.controlHighlight);
		@SuppressWarnings("static-access")
		ArrayList<String>  list_names = CanvasGuest.lanucher.cnt.userList;
		scrollPane.setViewportView(user_list);
		user_list.setListData(list_names.toArray());
		
		chatHistory = new JTextArea();
		chatHistory.setBounds(10, 481, 745, 82);
		chatHistory.setEditable(false);
		chatHistory.setWrapStyleWord(true);
		chatHistory.setLineWrap(true);
		chatHistory.setFont(new Font("Arial", Font.BOLD, 14));
		
		JScrollPane sp = new JScrollPane(chatHistory, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setFont(new Font("Arial", Font.BOLD, 18));
		sp.setBounds(10, 481, 745, 82);
		frame.getContentPane().add(sp);
		
		textInput = new JTextField();
		textInput.setBounds(10, 576, 745, 21);
		frame.getContentPane().add(textInput);
		textInput.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = textInput.getText();
				if (!input.isBlank()) {
					JSONObject textJsonObject = new JSONObject();
					textJsonObject.put("reqType", "Chat");
					textJsonObject.put("reqBody", input);
					textJsonObject.put("Sender", guestname);
					try {
						GuestLanucher.output.writeUTF(textJsonObject.toJSONString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					textInput.setText("");
				}
			}
		});
		
		btnNewButton.setBounds(769, 575, 112, 23);
		frame.getContentPane().add(btnNewButton);
		
		String[] draw_tools_names = {"Line", "Circle", "Triangle", "Rectangle", "Text", "Colors"};
		for (int i = 0; i < draw_tools_names.length; i++) {
			JButton btn = new JButton(draw_tools_names[i]);
			btn.setActionCommand(draw_tools_names[i]);
			btn.setPreferredSize(new Dimension(100,25));
			btn.addActionListener(canvasTechs);
			toolPanel.add(btn);
		}

		JSONObject request = new JSONObject();
		request.put("reqType", "Sync");
		try {
			GuestLanucher.output.writeUTF(request.toJSONString());
			GuestLanucher.output.flush();
		} catch (IOException e) {
			System.out.println("Error sending request");
		}
	}
	
	public void addText(String text, String who) {
		this.chatHistory.append(who + ": " + text + "\n");
	}
}
