
import java.io.*;
import java.net.*;
import java.io.IOException;

public class Client {
	private Socket client;
	private OutputStream outputStream;
	private PrintWriter printWriter;
	private InputStream inputStream;
	private BufferedReader bufferedReader;
	private String serverReply;
	private String searchWord;
	private String addWord;
	private String deleteWord;
	private ReplyContent replyContent = new ReplyContent();
	private String[] serverReplyInfo = new String[4];
	private String ipAddress;
	private int portNumberClient;

	public Client() {
		ipAddress = null;
		portNumberClient = 0;
	}

	public void ClientConnection(String[] args) {
		try {
			ipAddress = args[0];
			portNumberClient = Integer.parseInt(args[1]);
			client = new Socket(ipAddress, portNumberClient);
			System.out.println("Welcome to dictionary!");
			outputStream = client.getOutputStream();
			printWriter = new PrintWriter(outputStream);
			inputStream = client.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			System.out.println("Successful connection!");
		} catch (UnknownHostException e) {
			System.out.println("Host error!");
		} catch (IOException e) {
			System.out.println("Socket connection error!");
		}
	}

	public ReplyContent searchFunction(String clientWord) {
		try {
			searchWord = "search#" + clientWord + "#" + null;
			if (printWriter != null) {
				printWriter.write(searchWord + "\n");
				printWriter.flush();
			}
			if (bufferedReader != null) {
				serverReply = bufferedReader.readLine();
				if (serverReply != null) {
					serverReplyInfo = serverReply.split("#");
					if (serverReplyInfo[2].equals("null")) {
						replyContent.isError = true;
					} else {
						replyContent.isError = false;
						replyContent.description = serverReplyInfo[2];
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Search:I/O error!");
		}
		return replyContent;
	}

	public ReplyContent addFunction(String clientWord, String clientDescription) {
		try {
			addWord = "add#" + clientWord + "#" + clientDescription;
			if (printWriter != null) {
				printWriter.write(addWord + "\n");
				printWriter.flush();
			}
			if (bufferedReader != null) {
				serverReply = bufferedReader.readLine();
				if (serverReply.equals("successful")) {
					replyContent.isError = false;
				} else if (serverReply.equals("already exist")) {
					replyContent.isError = true;
				}
			}
		} catch (IOException e) {
			System.out.println("Add: I/O error!");
		}
		return replyContent;
	}

	public ReplyContent deleteFunction(String clientWord) {
		try {
			deleteWord = "delete#" + clientWord + "#" + null;
			if (printWriter != null) {
				printWriter.write(deleteWord + "\n");
				printWriter.flush();
			}
			if (bufferedReader != null) {
				serverReply = bufferedReader.readLine();
				if (serverReply.equals("successful")) {
					replyContent.isError = false;
				} else if (serverReply.equals("not exist")) {
					replyContent.isError = true;
				}
			}
		} catch (IOException e) {
			System.out.println("Delete: I/O error!");
		}
		return replyContent;
	}
}
