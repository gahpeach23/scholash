package app;

import components.TextField;
import components.PasswordField;
import components.Button;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import app.Database.Status;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
					System.out.println(Database.getTransactions("sup"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setResizable(false);
		setTitle("Scholash");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Login.class.getResource("/icons/dark.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 420);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(null);

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel bg = new JPanel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				Color c1 = new Color(10, 46, 127, 255); // dark
				Color c2 = new Color(29, 82, 188, 255); // light
				GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
				g2.setPaint(gp);
				g2.fill(new Rectangle(getWidth(), getHeight()));
			}
		};
		bg.setBackground(new Color(0, 64, 128));
		bg.setPreferredSize(new Dimension(324, 380));
		contentPane.add(bg, BorderLayout.WEST);
		bg.setLayout(null);

		JLabel welcome = new JLabel("Getting Started?");
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		welcome.setBounds(10, 20, 304, 37);
		welcome.setForeground(new Color(255, 255, 255));
		welcome.setFont(new Font("Arial", Font.BOLD, 32));
		bg.add(welcome);

		JLabel instructions = new JLabel("<html>" + "To create an account..." + "<ol>"
				+ "<li>Type in a username*</li>" + "<li>Type in a password*</li>" + "<li>Sign up!</li>" + "</ol>"
				+ "To log into your account..." + "<ol>" + "<li>Type in your username</li>"
				+ "<li>Type in your password</li>" + "<li>Log in!</li>" + "</ol>" + "</html>");
		instructions.setVerticalAlignment(SwingConstants.TOP);
		instructions.setHorizontalAlignment(SwingConstants.LEFT);
		instructions.setForeground(Color.WHITE);
		instructions.setFont(new Font("Arial", Font.BOLD, 20));
		instructions.setBounds(20, 90, 284, 232);
		bg.add(instructions);

		JLabel requirements = new JLabel("<html> *usernames and passwords have to be at least 5 characters and at most 45 characters. </html>");
		requirements.setVerticalAlignment(SwingConstants.BOTTOM);
		requirements.setHorizontalAlignment(SwingConstants.CENTER);
		requirements.setForeground(Color.WHITE);
		requirements.setFont(new Font("Arial", Font.BOLD, 10));
		requirements.setBounds(10, 333, 304, 37);
		bg.add(requirements);

		JSeparator separator = new JSeparator() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setPaint(Color.white);
				g2.fill(new Rectangle(getWidth(), getHeight()));
			}
		};
		separator.setBounds(0, 68, 324, 3);
		bg.add(separator);

		JPanel login = new JPanel();
		login.setBackground(Color.WHITE);
		login.setPreferredSize(new Dimension(380, 380));
		contentPane.add(login, BorderLayout.CENTER);
		login.setLayout(null);

		TextField username = new TextField((String) null, (Color) null, (Color) null, (Color) null);
		username.setTitle("Username");
		username.setBounds(65, 110, 250, 64);
		login.add(username);

		PasswordField password = new PasswordField((String) null, (Color) null, (Color) null, (Color) null);
		password.setTitle("Password");
		password.setBounds(65, 200, 250, 64);
		login.add(password);

		Button loginBtn = new Button();
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				switch (Database.login(username.getText(), password.getPassword())) {
					case Status.UNAVAILABLE: 
						JOptionPane.showMessageDialog(rootPane, 
								"Connection unavailable, try again later.", 
								"Error", 
								JOptionPane.WARNING_MESSAGE);
						break;
					case Status.SUCCESSFUL:
						App app = new App();
						app.setVisible(true);
						dispose();
						break;
					case Status.INVALID:
						JOptionPane.showMessageDialog(rootPane,
								"Invalid username or password", 
								"Error",
								JOptionPane.WARNING_MESSAGE);
						break;
				}
			}
		});

		loginBtn.setText("Log In");
		loginBtn.setColorClick(new Color(192, 192, 192));
		loginBtn.setBorderColor(new Color(0, 0, 0));
		loginBtn.setBounds(65, 300, 120, 40);
		login.add(loginBtn);

		Button signupBtn = new Button();
		signupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				switch (Database.signup(username.getText(), password.getPassword())) {
					case Status.UNAVAILABLE: 
						JOptionPane.showMessageDialog(rootPane, 
								"Connection unavailable, try again later.", 
								"Error", 
								JOptionPane.WARNING_MESSAGE);
						break;
					case Status.SUCCESSFUL:
						JOptionPane.showMessageDialog(rootPane,
								"Sign up successful! Continue to login.",
								"Success",
								JOptionPane.INFORMATION_MESSAGE);
						break;
					case Status.DUPLICATE:
						JOptionPane.showMessageDialog(rootPane, 
								"This username is taken, try a different one.", 
								"Error", 
								JOptionPane.WARNING_MESSAGE);
						break;
					case Status.INVALID:
						JOptionPane.showMessageDialog(rootPane, 
								"Your username or password doesn't meet app guidelines.", 
								"Error", 
								JOptionPane.WARNING_MESSAGE); 
						break;
				}
			}
		});

		signupBtn.setText("Sign Up");
		signupBtn.setBounds(195, 300, 120, 40);
		login.add(signupBtn);

		JPanel top = new JPanel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				Color c1 = new Color(255, 168, 0, 255); // light
				Color c2 = new Color(255, 120, 0, 255); // dark
				GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
				g2.setPaint(gp);
				g2.fill(new Rectangle(getWidth(), 68));
			}
		};
		top.setBackground(new Color(255, 168, 0));
		FlowLayout flowLayout = (FlowLayout) top.getLayout();
		flowLayout.setVgap(10);
		top.setBounds(0, 0, 380, 99);
		login.add(top);

		JPanel bgIcon = new JPanel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color c1 = new Color(255, 160, 0, 255); // light
				Color c2 = new Color(255, 140, 0, 255); // dark
				GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
				g2.setPaint(Color.white);
				g2.fillOval(0, 0, getWidth(), getHeight());
				g2.setPaint(gp);
				g2.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
			}
		};
		bgIcon.setPreferredSize(new Dimension(90, 90));
		top.add(bgIcon);

		ImageIcon imageIcon = new ImageIcon(Login.class.getResource("/icons/light.png")); // load the image to a
																							// imageIcon
		Image image = imageIcon.getImage(); // transform it
		Image newimg = image.getScaledInstance(56, 56, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
		imageIcon = new ImageIcon(newimg);
		bgIcon.setLayout(null);
		JLabel icon = new JLabel("");
		icon.setBounds(15, 11, 60, 60);
		bgIcon.add(icon);
		icon.setFont(new Font("Arial", Font.BOLD, 32));
		icon.setHorizontalAlignment(SwingConstants.CENTER);
		icon.setIcon(imageIcon);

		JLabel title = new JLabel("Scholash Login");
		title.setVerticalAlignment(SwingConstants.TOP);
		title.setPreferredSize(new Dimension(250, 70));
		top.add(title);
		title.setForeground(new Color(255, 255, 255));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 32));
	}
}
