import java.util.*;
import java.io.*;
import java.net.*;

public class Netflid {

	public static final int WINDOW_SIZE = 30;

	public static void main(String[] args) {
		KDTree windowDB = new KDTree(6, 48);

		FileInputStream movieListIS = null;

		try {
			movieListIS = new FileInputStream(args[0]);
		} catch (Exception e) {
			System.out.println("ERROR: Unable to open the fingerprint file.");
			System.exit(0);
		}

		Scanner dataInput = new Scanner(movieListIS);

		while (dataInput.hasNextLine()) {
			Movie currentMovie = new Movie(dataInput.nextLine(), WINDOW_SIZE);

			for (short i = 0; i < currentMovie.getNumWindows(); i++) {
				Window currentWindow = new Window(currentMovie, i);
				windowDB.add(currentWindow.getKey(), currentWindow);
			}
		}

		dataInput.close();

		ServerSocket serverSocket = null; 

		try { 
			serverSocket = new ServerSocket(10007); 
		} catch (IOException e) { 
			System.err.println("Could not listen on port: 10007."); 
			System.exit(1); 
		} 

		System.out.println("Server started");

		while (true) {
			Socket clientSocket = null; 

			try { 
				clientSocket = serverSocket.accept(); 

				ServerThread serverThread = new ServerThread(clientSocket, windowDB);
				Thread serverThread1 = new Thread(serverThread, "server");

				serverThread1.start();
			} catch (IOException e) { 
				System.err.println("Accept failed."); 
			}
		}
	}
}
