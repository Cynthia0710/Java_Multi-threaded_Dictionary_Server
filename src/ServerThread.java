
import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread implements Runnable {

	private Server server;
	private Socket socket = null;
	private String clientInput;
	private String commandInfo;
	private String replyMessage;
	private String reply;
	private InputStream inputStream;
	private BufferedReader bufferedReader;
	private OutputStream outputStream;
	private PrintWriter printWriter;
	private String[] clientInputInfo = new String[3];

	public ServerThread(Socket socket, Server server) {
		super();
		this.socket = socket;
		this.server = server;
	}

	public void run() {
		try {
			inputStream = socket.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			outputStream = socket.getOutputStream();
			printWriter = new PrintWriter(outputStream);
			while (true) {
				clientInput = bufferedReader.readLine();
				if (clientInput != null) {
					clientInputInfo = clientInput.split("#");
					commandInfo = clientInputInfo[0];
					if (commandInfo.equals("search")) {
						replyMessage = server.searchWord(clientInputInfo[1]);
						if (replyMessage.equals("not exist")) {
							reply = "search#" + clientInputInfo[1] + "#null";
							printWriter.write(reply + "\n");
							printWriter.flush();
						} else {
							reply = "search#" + clientInputInfo[1] + "#" + replyMessage;
							printWriter.write(reply + "\n");
							printWriter.flush();
						}
					} else if (commandInfo.equals("add")) {
						replyMessage = server.addWord(clientInputInfo[1], clientInputInfo[2]);
						if (replyMessage.equals("already exist")) {
							reply = "already exist";
							printWriter.write(reply + "\n");
							printWriter.flush();
						} else if (replyMessage.equals("successful")) {
							reply = "successful";
							printWriter.write(reply + "\n");
							printWriter.flush();
						}
					} else if (commandInfo.equals("delete")) {
						replyMessage = server.deleteWord(clientInputInfo[1]);
						if (replyMessage.equals("not exist")) {
							reply = "not exist";
							printWriter.write(reply + "\n");
							printWriter.flush();
						} else if (replyMessage.equals("successful")) {
							reply = "successful";
							printWriter.write(reply + "\n");
							printWriter.flush();
						}
					}
				}
			}

		} catch (IOException ex) {
			// ex.printStackTrace();
		}
		try {
			if (printWriter != null)
				printWriter.close();
			if (outputStream != null)
				outputStream.close();
			if (bufferedReader != null)
				bufferedReader.close();
			if (inputStream != null)
				inputStream.close();
			if (socket != null)
				socket.close();
		} catch (IOException ex) {
			// System.out.println("I/O error!");
			ex.printStackTrace();
		}
	}

}
