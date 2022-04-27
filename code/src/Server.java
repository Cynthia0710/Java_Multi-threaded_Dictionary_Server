
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.net.URL;

public class Server {

	private static JFrame jframe;
	private static Server server = new Server();
	private static String filePath;
	private static int portNumber;
	private static ServerSocket serverSocket;
	public static Hashtable<String, String> dictionaryHashTable = new Hashtable<String, String>();
	private static int count;
	private static Socket socket;
	private static Image image;
	private static SuccessfulCheckServer successfulCheck;
	private JTextArea descriptionArea;
	private JPanel jpanel;
	private Font font;
	private Border border;
	private JButton writeToFile;
	private JButton infoButton;
	private JButton numberButton;
	private JLabel welcomeLabel;
	private JLabel background;
	private ImageIcon picture;
	private JScrollPane jscrollPane;
	private String serverReply;
	private String path;

	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			if (args[0] != null && args[1] != null) {
				successfulCheck = ServerInputError(args);
				if (successfulCheck.isSuccessful == true) {
					filePath = args[1];
					portNumber = Integer.parseInt(args[0]);
					dictionaryHashTable = server.readDictionaryInfo(filePath);
					jframe = new JFrame();
					jframe.setTitle("DictionaryServer");
					jframe.setBounds(500, 250, 500, 350);
					jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					server.setFont();
					server.setServerPage();
					jframe.setVisible(true);
					serverSocket = new ServerSocket(portNumber);
					boolean wait = true;
					while (wait) {
						socket = serverSocket.accept();
						count++;
						Thread serverThread = new ServerThread(socket, server);
						serverThread.start();
					}
				} else if (successfulCheck.isSuccessful == false) {
					System.out.println("Port number input error!");
				}
			} else {
				System.out.println("Arguments input error!");
			}
		} else {
			System.out.println("The number of arguments are error!");
		}

	}

	private static SuccessfulCheckServer ServerInputError(String[] args) {
		int portNumberServer = Integer.parseInt(args[0]);
		SuccessfulCheckServer successfulCheckServer = new SuccessfulCheckServer();
		boolean isPortNumber = server.portNumberCheck(portNumberServer);
		if (isPortNumber == true) {
			successfulCheckServer.isSuccessful = true;
		} else if (isPortNumber == false) {
			successfulCheckServer.isSuccessful = false;
		}
		return successfulCheckServer;
	}

	private boolean portNumberCheck(int portNumberServer) {
		if (portNumberServer < 1025 || portNumberServer > 65534) {
			return false;
		} else {
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, String> readDictionaryInfo(String filePath) {
		ObjectInputStream readDictionaryInfo = null;
		try {
			readDictionaryInfo = new ObjectInputStream(new FileInputStream(filePath));
			dictionaryHashTable = (Hashtable<String, String>) readDictionaryInfo.readObject();
			readDictionaryInfo.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Error! Class not found!");
		} catch (FileNotFoundException e) {
			System.out.println("Error! File " + filePath + " can not found!");
			System.out.println("File " + filePath + " will be created!");
		} catch (IOException e) {
			System.out.println("Problems with dictionary input");
		}
		return dictionaryHashTable;
	}

	private void writeDictionaryInfo(Hashtable<String, String> dictionaryHashTable, String filePath) {
		ObjectOutputStream writeDictionaryInfo = null;
		try {
			writeDictionaryInfo = new ObjectOutputStream(new FileOutputStream(filePath));
			writeDictionaryInfo.writeObject(dictionaryHashTable);
			writeDictionaryInfo.close();
		} catch (IOException e) {
			System.out.println("Problem with file output.");
		}
	}

	private void setServerPage() {
		jpanel = new JPanel();
		jframe.add(jpanel);
		jpanel.setLayout(null);
		path = "/img/pic2.jpg";
		image = setPicture(path);
		picture = new ImageIcon(image);
		background = new JLabel(picture);
		descriptionArea = new JTextArea();
		writeToFile = new JButton("WRITE");
		infoButton = new JButton("Show Details");
		welcomeLabel = new JLabel("Welcome to Server!");
		numberButton = new JButton("Show Client Number");
		jscrollPane = new JScrollPane(descriptionArea);
		border = BorderFactory.createLineBorder(Color.BLACK);
		background.setBounds(0, 0, 500, 350);
		descriptionArea.setBounds(20, 50, 280, 230);
		writeToFile.setBounds(335, 100, 100, 25);
		welcomeLabel.setBounds(20, 10, 200, 40);
		numberButton.setBounds(305, 200, 170, 25);
		infoButton.setBounds(315, 150, 150, 25);
		jscrollPane.setBounds(20, 50, 280, 230);
		jscrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		descriptionArea
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		jpanel.add(jscrollPane);
		jpanel.add(writeToFile);
		jpanel.add(numberButton);
		jpanel.add(infoButton);
		jpanel.add(welcomeLabel);
		jpanel.add(background);
		jpanel.setVisible(true);
		numberButton.addActionListener(new infoButtonListener());
		infoButton.addActionListener(new infoButtonListener());
		writeToFile.addActionListener(new writeToDictionaryButtonListener());
		jframe.addWindowListener(new windowListener());
	}

	private class writeToDictionaryButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("e")) {
				jframe.dispose();
				writeDictionaryInfo(dictionaryHashTable, filePath);
				System.exit(0);
			}
		}
	}

	private class windowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			writeDictionaryInfo(dictionaryHashTable, filePath);
		}
	}

	private class infoButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			if (actionCommand.equals("Show Client Number"))
				descriptionArea.setText(count + " clients have connected!");
			if (actionCommand.equals("Show Details")) {
				if (socket == null) {
					descriptionArea.setText("Error! Please connect client!");
				} else {
					descriptionArea.setText("Remote Port: " + socket.getPort() + "\n" + "Remote Hostname: "
							+ socket.getInetAddress().getHostName() + "\n" + "Local Port: " + socket.getLocalPort());
				}
			}

		}
	}

	private static Image setPicture(String path) {
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

	private void setFont() {
		font = new Font("FZShuTi", Font.BOLD, 13);
		UIManager.put("Label.font", font);
		UIManager.put("Button.font", font);
		UIManager.put("Panel.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("TextArea.font", font);
	}

	synchronized String searchWord(String word) {
		if (dictionaryHashTable.containsKey(word)) {
			serverReply = dictionaryHashTable.get(word);
		} else {
			serverReply = "not exist";
		}
		return serverReply;
	}

	synchronized String addWord(String word, String meaning) {
		if (!dictionaryHashTable.containsKey(word)) {
			dictionaryHashTable.put(word, meaning);
			serverReply = "successful";
		} else {
			serverReply = "already exist";
		}
		return serverReply;
	}

	synchronized String deleteWord(String word) {
		if (dictionaryHashTable.containsKey(word)) {
			dictionaryHashTable.remove(word);
			serverReply = "successful";
		} else {
			serverReply = "not exist";
		}
		return serverReply;
	}
}
