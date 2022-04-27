
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DicWindow {

	private static Image image;
	private static Client client;
	private static DicWindow dicWindow;
	private static SuccessfulCheckClient successfulCheckClient;
	private Font font;
	private JLabel inputWord;
	private JLabel background;
	private JLabel description;
	private Border border1;
	private Border border2;
	private JButton searchButton1;
	private JButton addButton1;
	private JButton deleteButton1;
	private JTextField inputField;
	private JTextArea descriptionArea;
	private ImageIcon picture;
	private JPanel jpanel;
	private String path;
	private String clientWord;
	private String meaningContent;
	private ReplyContent replyContent;
	private boolean error;
	JFrame jframe = new JFrame();

	public static void main(String[] args) {
		if (args.length == 2) {
			if (args[0] != null && args[1] != null) {
				successfulCheckClient = ClientInputError(args);
				String IPAddress = args[0];
				String portNumberString = args[1];
				boolean isIP = IPCheck(IPAddress);
				boolean isPortNumber = portNumberCheck(portNumberString);
				if (isIP == true && isPortNumber == true) {
					successfulCheckClient.isSuccessful = true;
				} else if (isIP == true && isPortNumber == false) {
					successfulCheckClient.isSuccessful = false;
					successfulCheckClient.errorDescription = "portnumber";
				} else if (isIP == false && isPortNumber == true) {
					successfulCheckClient.isSuccessful = false;
					successfulCheckClient.errorDescription = "IPaddress";
				} else if (isIP == false && isPortNumber == false) {
					successfulCheckClient.isSuccessful = false;
					successfulCheckClient.errorDescription = "portnumber&IPaddress";
				}
				if (successfulCheckClient.isSuccessful == true) {
					client = new Client();
					client.ClientConnection(args);
					dicWindow = new DicWindow();
					dicWindow.setFrame();
				} else {
					if (successfulCheckClient.errorDescription.equals("portnumber")) {
						System.out.println("Port number input error!");
						System.exit(0);
					} else if (successfulCheckClient.errorDescription.equals("IPaddress")) {
						System.out.println("IP address input error!");
						System.exit(0);
					} else if (successfulCheckClient.errorDescription.equals("portnumber&IPaddress")) {
						System.out.println("Port number and IP address input error!");
						System.exit(0);
					}
				}
			} else {
				System.out.println("Arguments input error!");
			}
		} else {
			System.out.println("The number of arguments are error!");
		}
	}

	private void setFrame() {
		jframe.setBounds(500, 250, 500, 350);
		jframe.setTitle("Dictionary");
		setFont();
		setHomePage();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}

	private void setFont() {
		font = new Font("FZShuTi", Font.BOLD, 13);
		UIManager.put("Label.font", font);
		UIManager.put("Button.font", font);
		UIManager.put("Panel.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("TextArea.font", font);
	}

	private void setHomePage() {
		jpanel = new JPanel();
		jframe.add(jpanel);
		jpanel.setLayout(null);
		path = "/img/pic1.jpg";
		image = setPicture(path);
		picture = new ImageIcon(image);
		background = new JLabel(picture);
		searchButton1 = new JButton("SEARCH");
		addButton1 = new JButton("ADD");
		deleteButton1 = new JButton("DELETE");
		background.setBounds(0, 0, 500, 350);
		searchButton1.setBounds(200, 160, 100, 25);
		addButton1.setBounds(200, 200, 100, 25);
		deleteButton1.setBounds(200, 240, 100, 25);
		jpanel.add(searchButton1);
		jpanel.add(addButton1);
		jpanel.add(deleteButton1);
		jpanel.add(background);
		searchButton1.addActionListener(new PageListener());
		addButton1.addActionListener(new PageListener());
		deleteButton1.addActionListener(new PageListener());
		jpanel.setVisible(true);
	}

	private void uniformLayOut(JPanel uniformJPanel) {
		uniformJPanel.setLayout(null);
		uniformJPanel.setBackground(Color.white);
		border2 = BorderFactory.createLineBorder(Color.GRAY);
		border1 = BorderFactory.createLineBorder(Color.BLACK);
		inputWord = new JLabel("input word:");
		inputField = new JTextField(10);
		description = new JLabel("description:");
		descriptionArea = new JTextArea(8, 31);
		inputWord.setBounds(10, 10, 90, 40);
		inputField.setBounds(90, 10, 230, 40);
		description.setBounds(10, 60, 90, 20);
		descriptionArea.setBounds(10, 90, 350, 210);
		inputField.setBorder(
				BorderFactory.createCompoundBorder(border1, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		descriptionArea
				.setBorder(BorderFactory.createCompoundBorder(border2, BorderFactory.createEmptyBorder(3, 5, 5, 3)));
		uniformJPanel.add(inputWord);
		uniformJPanel.add(inputField);
		uniformJPanel.add(description);
		uniformJPanel.add(descriptionArea);

	}

	private void searchPage(JPanel searchJPanel) {
		uniformLayOut(searchJPanel);
		JButton searchButton2 = new JButton("SEARCH");
		searchButton2.setBounds(350, 10, 100, 25);
		searchJPanel.add(searchButton2);
		backButton(searchJPanel);
		searchButton2.addActionListener(new WordListener());
	}

	private void addPage(JPanel addJPanel) {
		uniformLayOut(addJPanel);
		JButton addButton2 = new JButton("ADD");
		addButton2.setBounds(350, 10, 100, 25);
		addJPanel.add(addButton2);
		backButton(addJPanel);
		addButton2.addActionListener(new WordListener());
	}

	private void deletePage(JPanel deleteJPanel) {
		uniformLayOut(deleteJPanel);
		JButton deleteButton2 = new JButton("DELETE");
		deleteButton2.setBounds(350, 10, 100, 25);
		deleteJPanel.add(deleteButton2);
		backButton(deleteJPanel);
		deleteButton2.addActionListener(new WordListener());
	}

	private void backButton(JPanel jpanel) {
		jpanel.setLayout(null);
		JButton backButton = new JButton("BACK");
		backButton.setBounds(374, 250, 100, 25);
		jpanel.add(backButton);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jpanel.setVisible(false);
				JPanel backJPanel = new JPanel();
				jframe.add(backJPanel);
				setHomePage();
			}
		});
	}

	private static SuccessfulCheckClient ClientInputError(String[] args) {
		String IPAddress = args[0];
		String portNumberString = args[1];
		SuccessfulCheckClient successfulCheckClient = new SuccessfulCheckClient();
		boolean isIP = dicWindow.IPCheck(IPAddress);
		boolean isPortNumber = dicWindow.portNumberCheck(portNumberString);
		if (isIP == true && isPortNumber == true) {
			successfulCheckClient.isSuccessful = true;
		} else if (isIP == true && isPortNumber == false) {
			successfulCheckClient.isSuccessful = false;
			successfulCheckClient.errorDescription = "portnumber";
		} else if (isIP == false && isPortNumber == true) {
			successfulCheckClient.isSuccessful = false;
			successfulCheckClient.errorDescription = "IPaddress";
		} else if (isIP == false && isPortNumber == false) {
			successfulCheckClient.isSuccessful = false;
			successfulCheckClient.errorDescription = "portnumber&IPaddress";
		}
		return successfulCheckClient;
	}

	public static boolean IPCheck(String IPaddress) {
		if (IPaddress.length() < 7 || IPaddress.length() > 15 || "".equals(IPaddress)) {
			return false;
		}
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(rexp);
		Matcher matcher = pattern.matcher(IPaddress);
		boolean isIPAddress = matcher.find();
		return isIPAddress;
	}

	private static boolean portNumberCheck(String portNumberString) {
		int portNumber = Integer.parseInt(portNumberString);
		if (portNumber < 1025 || portNumber > 65534) {
			return false;
		} else {
			return true;
		}
	}

	public static Image setPicture(String path) {
		image = null;
		URL url = Server.class.getResource(path);
		try {
			InputStream input = url.openStream();
			image = ImageIO.read(input);
		} catch (IOException e) {
			System.out.println("Error path!");
		}
		return image;
	}

	private class PageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("SEARCH")) {
				jpanel.setVisible(false);
				JPanel searchJPanel = new JPanel();
				jframe.add(searchJPanel);
				searchPage(searchJPanel);
			} else if (command.equals("ADD")) {
				jpanel.setVisible(false);
				JPanel addJPanel = new JPanel();
				jframe.add(addJPanel);
				addPage(addJPanel);
			} else if (command.equals("DELETE")) {
				jpanel.setVisible(false);
				JPanel deleteJPanel = new JPanel();
				jframe.add(deleteJPanel);
				deletePage(deleteJPanel);
			}
		}
	}

	private class WordListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			clientWord = inputField.getText();
			meaningContent = descriptionArea.getText();
			boolean hasSpecialChar = hasSpecialChar(clientWord);
			if (!clientWord.equals("")) {
				if (hasSpecialChar) {
					JOptionPane.showMessageDialog(null, "Input error, please input again!", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (command.equals("SEARCH")) {
						if (meaningContent.equals("")) {
							replyContent = client.searchFunction(clientWord);
							meaningContent = replyContent.description;
							error = replyContent.isError;
							if (error == true) {
								JOptionPane.showMessageDialog(null, "Search failed, word does not exist!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else {
								descriptionArea.setText(meaningContent);
							}
						} else if (!meaningContent.equals("")) {
							JOptionPane.showMessageDialog(null, "Error! Please not input meaning!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

					} else if (command.equals("ADD")) {
						if (!meaningContent.equals("")) {
							replyContent = client.addFunction(clientWord, meaningContent);
							error = replyContent.isError;
							if (error == true) {
								JOptionPane.showMessageDialog(null, "Add failed, word already exists!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else {
								JOptionPane.showMessageDialog(null, "Add succeed!", "Response",
										JOptionPane.INFORMATION_MESSAGE);
							}
						} else if (meaningContent.equals("")) {
							JOptionPane.showMessageDialog(null, "Error! Please input meaning!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

					} else if (command.equals("DELETE")) {
						if (meaningContent.equals("")) {
							replyContent = client.deleteFunction(clientWord);
							error = replyContent.isError;
							if (error == true) {
								JOptionPane.showMessageDialog(null, "Delete failed, word does not exists!", "Error",
										JOptionPane.ERROR_MESSAGE);
							} else {
								JOptionPane.showMessageDialog(null, "Delete succeed!", "Response",
										JOptionPane.INFORMATION_MESSAGE);
							}
						} else if (!meaningContent.equals("")) {
							JOptionPane.showMessageDialog(null, "Error! Please not input meaning!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

					}
				}
			} else if (clientWord.equals("")) {
				JOptionPane.showMessageDialog(null, "Error! Please input word!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		private boolean hasSpecialChar(String input) {
			String specialChar = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~ï¼�@#ï¿¥%â€¦â€¦&*ï¼ˆï¼‰â€”â€”+|{}ã€�ã€‘â€˜ï¼›ï¼šâ€�â€œâ€™ã€‚ï¼Œã€�ï¼Ÿ]|\n|\r|\t";
			Pattern pattern = Pattern.compile(specialChar);
			Matcher matcher = pattern.matcher(input);
			return matcher.find();
		}
	}
}
