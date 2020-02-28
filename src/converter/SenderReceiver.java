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
	int port;

	public SenderReceiver(InetAddress ip, int port) {
		this.port=port;
		this.addr=ip;
	}

	@Override
	public void run() {
		try {
			
			
			// creazione socket
			socket = new Socket(addr, port);
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
				serverInfo = userInput.split(" ");
				
				if (serverInfo[0].equals("WELCOME")) {
					status = "WELCOME";
					if (serverInfo[1].equals("Black"))
						Main.blackPlayer = true;
					else
						Main.blackPlayer = false;
				}
				else if (serverInfo[0].equals("MESSAGE") && serverInfo[1].equals("All")) {
					status="MESSAGE All players connected";
					try {
						core.Main.algSem.release();
						core.Main.srSem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else if (serverInfo[0].equals("OPPONENT_MOVE")) {
					status = "OPPONENT_MOVE";
					move = serverInfo[1];
					try {
						core.Main.algSem.release();
						core.Main.srSem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else if (serverInfo[0].equals("YOUR_TURN")) {
					status="YOUR_TURN";
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
			status= serverInfo[0];
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
