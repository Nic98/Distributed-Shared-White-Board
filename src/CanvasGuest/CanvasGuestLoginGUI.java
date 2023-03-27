package CanvasGuest;


import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CanvasGuestLoginGUI {

	JFrame frame;
	private JTextField userNameInput;
	private String username;
	public static CanvasGuestGUI canvasGuestGUI;
	protected static JSONParser parser = new JSONParser();
	
	/**
	 * Create the application.
	 */
	public CanvasGuestLoginGUI(String username) {
		this.username = username;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
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
		
		
		final JOptionPane optionPane = new JOptionPane("Waitting for response", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		final JDialog dialog = new JDialog();
		dialog.setLocationRelativeTo(frame);
		dialog.setTitle("Message");
		dialog.setModal(true);
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
		//create timer to dispose of dialog after 5 seconds
		Timer timer = new Timer(5000, (ActionListener) new AbstractAction() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = -8377963149965903035L;
			@Override
		    public void actionPerformed(ActionEvent ae) {
		        dialog.dispose();
		    }
		});
		timer.setRepeats(false);//the timer should only go off once


		
		
		// Once the Login button has been clicked it will automatically open a canvas window
		loginButton.addActionListener(e -> {
			if (e.getActionCommand().equals("Login")) {
				username = userNameInput.getText();
				
				if(username.isBlank()) {
					JOptionPane.showMessageDialog(frame, "Plase enter your username.");
				} else {
					JSONObject request = new JSONObject();
					request.put("reqType", "Connect");
					request.put("username", username);
					try {
						GuestLanucher.output.writeUTF(request.toJSONString());
						GuestLanucher.output.flush();
						String permission = CanvasGuest.lanucher.cnt.isPermitted();

						//start timer to close JDialog as dialog modal we must start the timer before its visible
						timer.start();
						dialog.setVisible(true);
						
						permission = CanvasGuest.lanucher.cnt.isPermitted();
						if (permission.equals("Dulplicate")) {
							JOptionPane.showMessageDialog(frame, "Dulplicate username.");
							CanvasGuest.lanucher.cnt.reset();
						} 
						
						else if (permission.equals("Rejected")) {
							frame.dispose();
							JOptionPane.showMessageDialog(frame, "Rejected by Manager");
							CanvasGuest.lanucher.socket.close();
							System.exit(1);
						} 
						
						else if (permission.equals("Accept")) {
							frame.dispose();
							try {
								if (canvasGuestGUI == null) {
									canvasGuestGUI = new CanvasGuestGUI(username);
								}
							} catch (Exception e1) {

								System.out.println("Error occuring when initialising Manager Canvas.");
							}
						} else {
							JOptionPane.showMessageDialog(frame, "Connection Timeout, exit automatically...");
							CanvasGuest.lanucher.socket.close();
							System.exit(1);
							frame.dispose();
						}
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(frame, "Error occuring when sending request to manager");
					} catch (NullPointerException e2) {
						JOptionPane.showMessageDialog(frame, "The Manager is not openned yet.");
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
