package converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import core.Main;

public class SenderReceiver extends Thread {
	private InetAddress addr;
	private Socket socket = null;
	private BufferedReader in = null, stdIn = null;
	private PrintWriter out = null;
	private String userInput;
	private String[] serverInfo;

	private String status;
	private String move;
	private Boolean end;

	public SenderReceiver() {
	}

	@Override
	public void run() {
		try {
			addr = InetAddress.getLocalHost();
			// creazione socket
			socket = new Socket(addr, 8080);
			// creazione stream di input da socket
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			in = new BufferedReader(isr);
			// creazione stream di output su socket
			OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
			BufferedWriter bw = new BufferedWriter(osw);
			out = new PrintWriter(bw, true);
			userInput = "";
			end = false;
		} catch (UnknownHostException e) {
			System.err.println("Don’t know about host " + addr);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn’t get I/O for the connection to: " + addr);
			System.exit(1);
		}
		while (!userInput.equals("TIMEOUT") && !userInput.equals("VICTORY") && !userInput.equals("TIE")
				&& !userInput.equals("DEFEAT")) {
			try {
				userInput = in.readLine();
				System.out.println(userInput);
				serverInfo = userInput.split(" ");

				if (serverInfo[0].equals("WELCOME")) {
					status = "WELCOME";
					if (serverInfo[1].equals("Black"))
						Main.blackPlayer = true;
					else
						Main.blackPlayer = false;
				}
				
				if (serverInfo[0].equals("OPPONENT_MOVE")) {
					status = "OPPONENT_MOVE";
					move = serverInfo[1];
					try {
						core.Main.algSem.release();
						core.Main.srSem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (serverInfo[0].equals("YOUR_TURN")) {
					status = "YOUR_TURN";
					try {
						core.Main.algSem.release();
						core.Main.srSem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					out.println(move);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Couldn’t get I/O for the connection to: " + addr);
				System.exit(1);
			}
		}

		try {
			in.close();
			out.close();
			socket.close();
			end = true;
			core.Main.algSem.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
