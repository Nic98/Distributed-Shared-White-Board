package CanvasManager;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.UIManager;
import org.json.simple.JSONObject;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JTextField;
import javax.swing.JTextArea;


public class CanvasManagerGUI {

	protected JFrame frame;
	private String managername;
	static CanvasManagerGUI canvasManagerGUI;
	static Canvas canvas;
	static CanvasTechs canvasTechs;
	@SuppressWarnings("rawtypes")
	JList user_list;
	
//	JSONParser parser = new JSONParser();
	
	FileManagement fileManager;
	File selected_file;
	File save_file;
	private JTextField textInput;
	private JTextArea chatHistory;
	/**
	 * Create the application.
	 */
	public CanvasManagerGUI(String managername) {
		this.managername = managername;
		initialize();
		// Launch the server waiting for guest connections.
	}
	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		
		
		

		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("activeCaption"));
		frame.setTitle("CanvasManger: " + managername);
		frame.setBounds(100, 100, 905, 644);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
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
		
		this.fileManager = new FileManagement(frame, canvas);
		
		JComboBox canvasFunctionBox = new JComboBox();
		canvasFunctionBox.setModel(new DefaultComboBoxModel(new String[] {"New frame", "Open frame", "Save frame", "Save frame as", "Clear frame", "Exit frame"}));
		canvasFunctionBox.addActionListener(e -> {
			String func = canvasFunctionBox.getSelectedItem().toString();
			switch (func) {
				case "New frame":
					canvas.removeAll();
					canvas.updateUI();
					canvas.clearAll();
					JSONObject clear_msg = new JSONObject();
					clear_msg.put("resType", "Clear");
					ConnectionManager.broadcastMsg(clear_msg);
					break;
				case "Open frame":
					JFileChooser fileChooser1 = new JFileChooser();
					if (fileChooser1.showOpenDialog(fileChooser1) == JFileChooser.APPROVE_OPTION) {
						String filename = fileChooser1.getSelectedFile().toString();
						if (!filename.endsWith(".json")) {
							JOptionPane.showMessageDialog(null, "You should open the file with a .json extension!");
						} else {
							selected_file = fileChooser1.getSelectedFile();
							fileManager.open(selected_file, save_file);
				            save_file = selected_file;
				            canvas.paint((Graphics2D) canvas.getGraphics());
						}
					}
					break;
				case "Save frame":
					if(save_file == null) {
						JFileChooser fileChooser2 = new JFileChooser();
						if (fileChooser2.showSaveDialog(fileChooser2) == JFileChooser.APPROVE_OPTION) {
							String filename = fileChooser2.getSelectedFile().toString();
							if (!filename.endsWith(".json")) {
								JOptionPane.showMessageDialog(null, "You should save the file with a .json extension!");
							} else {
								save_file = fileChooser2.getSelectedFile();
								fileManager.save(save_file);
							}
						}
					} else {
						fileManager.save(save_file);
					}

					break;
				case "Save frame as":
					JFileChooser fileChooser2 = new JFileChooser();
					if (fileChooser2.showSaveDialog(fileChooser2) == JFileChooser.APPROVE_OPTION) {
						String filename = fileChooser2.getSelectedFile().toString();
						if (!filename.endsWith(".json")) {
							JOptionPane.showMessageDialog(null, "You should save the file with a .json extension!");
						} else {
							save_file = fileChooser2.getSelectedFile();
							fileManager.save(save_file);
						}
					}
					break;
				case "Clear frame":
					canvas.removeAll();
					canvas.updateUI();
					canvas.clearAll();
					clear_msg = new JSONObject();
					clear_msg.put("resType", "Clear");
					ConnectionManager.broadcastMsg(clear_msg);
					break;
				case "Exit frame":
					System.exit(1);
					break;
			}
		});
		
		canvasFunctionBox.setBounds(10, 10, 127, 27);
		toolPanel.add(canvasFunctionBox);
		
		JLabel lblNewLabel = new JLabel("ToolPanel");
		lblNewLabel.setBounds(10, 10, 91, 16);
		lblNewLabel.setForeground(UIManager.getColor("Tree.selectionBackground"));
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 16));
		frame.getContentPane().add(lblNewLabel);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(769, 75, 112, 363);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollPane);

		user_list = new JList();
		user_list.setBackground(SystemColor.controlHighlight);
		ArrayList<String>  list_names = CanvasManager.lanucher.guest_names;
		list_names.add(managername);
		scrollPane.setViewportView(user_list);
		user_list.setListData(list_names.toArray());
		
		JButton kickButton = new JButton("Kick");
		kickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (user_list.getSelectedValue() == null) {
					return;
				}
				String selected_user = user_list.getSelectedValue().toString();
				
				if(selected_user.equals(managername)) {
					JOptionPane.showMessageDialog(frame, "Illegal Action: You are not permitted "
							+ "to kick yourself out of the Canvas, ExitFrame if you want to leave." );
					return;
				} else {
					ManagerSideConnection selected_cnt = null;
					for (ManagerSideConnection cnt : CanvasManager.lanucher.guests) {
						if (selected_user.equals(cnt.username)) {
							// cnt.kicked = true;
							selected_cnt = cnt;
							JSONObject res = new JSONObject();
							res.put("resType", "Kick");
							res.put("resBody", selected_user);
							try {
								cnt.output.writeUTF(res.toJSONString());
								cnt.output.flush();
								cnt.socket.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					}
					if (selected_cnt.socket.isConnected()) {
						CanvasManager.lanucher.guest_names.remove(selected_user);
						CanvasManager.lanucher.guests.remove(selected_cnt);
						JOptionPane.showMessageDialog(frame, selected_user + " has been kicked out from the Canvas!");
					}
					JSONObject res = new JSONObject();
					res.put("resType", "Remove");
					res.put("resBody", selected_user);
					ConnectionManager.broadcastMsg(res);
				}
			}
		});
		
		kickButton.setBounds(772, 448, 102, 23);
		frame.getContentPane().add(kickButton);
		
		
		textInput = new JTextField();
		textInput.setBounds(10, 573, 745, 23);
		frame.getContentPane().add(textInput);
		textInput.setColumns(10);
		
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
	
		
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = textInput.getText();
				if (!input.isBlank()) {
					JSONObject textJsonObject = new JSONObject();
					textJsonObject.put("resType", "Chat");
					textJsonObject.put("resBody", input);
					textJsonObject.put("Sender", managername);
					ConnectionManager.broadcastMsg(textJsonObject);
					textInput.setText("");
					addText(input,managername);
				}
			}
		});
		
		btnNewButton.setBounds(769, 574, 102, 23);
		frame.getContentPane().add(btnNewButton);
		

		
		
		
		
		// Adding drawing functional buttons 
		String[] draw_tools_names = {"Line", "Circle", "Triangle", "Rectangle", "Text", "Colors"};
		for (int i = 0; i < draw_tools_names.length; i++) {
			JButton btn = new JButton(draw_tools_names[i]);
			btn.setActionCommand(draw_tools_names[i]);
			btn.setPreferredSize(new Dimension(100,25));
			btn.addActionListener(canvasTechs);
			toolPanel.add(btn);
		}
	}
	
	
	public Integer connectRequest(String name) {
		final JOptionPane optionPane = new JOptionPane("The user "+name + " wants to join",
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		JDialog dialog = optionPane.createDialog("Select Yes or No");
		dialog.setLocationRelativeTo(frame);
		dialog.setTitle("Message");
		dialog.setModal(true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
        Timer timer = new Timer(5000, e -> dialog.setVisible(false));
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
        
		if (optionPane.getValue() instanceof Integer) {
			return (Integer) optionPane.getValue();
		}
		return null;
        
	}
	
	public void addText(String text, String who) {
		this.chatHistory.append(who + ": " + text + "\n");
	}
}
