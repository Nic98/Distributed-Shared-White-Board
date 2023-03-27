package CanvasManager;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.SystemColor;

public class CanvasManagerLoginGUI{

	private JFrame frame;
	private JTextField userNameInput;
	private String username;
	public static CanvasManagerGUI canvasManagerGUI;
	static ManagerLanucher lanucher;
	
	
	/**
	 * Create the application.
	 */
	public CanvasManagerLoginGUI(String username) {
		this.username = username;
		initialize();
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(UIManager.getColor("activeCaption"));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		
		// Login button
		JButton loginButton = new JButton("Login");
		loginButton.setFont(new Font("SimSun", Font.PLAIN, 16));
		loginButton.setBounds(74, 147, 291, 39);
		loginButton.setBorderPainted(false);
		loginButton.setOpaque(true);
		loginButton.setBackground(Color.BLACK);
		loginButton.setForeground(Color.WHITE);
		loginButton.setRolloverEnabled(false);
		loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	loginButton.setBackground(new Color(0, 0, 51));
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	loginButton.setBackground(Color.BLACK);
		    }
		});
		frame.getContentPane().add(loginButton);
		
		// Once the Login button has been clicked it will automatically open a canvas window
		loginButton.addActionListener(e -> {
			if (e.getActionCommand().equals("Login")) {
				username = userNameInput.getText();
				if(username.isBlank()) {
					JOptionPane.showMessageDialog(frame, "Plase enter your username.");
				} else {
					frame.dispose();
					try {
						canvasManagerGUI = new CanvasManagerGUI(username);
					} catch (Exception e1) {
						System.out.println("Error occuring when initialising Manager Canvas.");
					}
				}
			}
		});

		
		
		JLabel Username = new JLabel("Username");
		Username.setFont(new Font("SimSun", Font.BOLD, 16));
		Username.setBounds(74, 68, 70, 20);
		frame.getContentPane().add(Username);
		
		userNameInput = new JTextField();
		userNameInput.setBackground(SystemColor.menu);
		userNameInput.setFont(new Font("SimSun", Font.PLAIN, 14));
		userNameInput.setBounds(74, 98, 162, 39);
		frame.getContentPane().add(userNameInput);
		userNameInput.setColumns(10);
		userNameInput.setText(username);
		
	}
}
