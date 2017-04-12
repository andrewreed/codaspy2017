import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class ServerThread implements Runnable {

	private Socket clientSocket;
	private KDTree windowDB;

	public static final int WINDOW_SIZE = 30;

	public ServerThread(Socket clientSocket, KDTree windowDB) {
		this.clientSocket = clientSocket;
		this.windowDB = windowDB;
	}

	@Override
	public void run() {
		PearsonsCorrelation correlator = new PearsonsCorrelation();

		String ipAddr = clientSocket.getInetAddress().toString();

		System.out.println(ipAddr + "\tconnected");

		PrintWriter out = null;
		Scanner in = null;

		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true); 
			in = new Scanner(clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println(ipAddr + "\treader/writer failed"); 
			return; 
		} 

		while (in.hasNextLine()) {
			String inputLine = in.nextLine();

			if (inputLine.equals("complete")) {
				System.out.println(ipAddr + "\tcomplete");
				break;
			}

			Movie dummyMovie = new Movie("dummyTitle\t0\t" + inputLine, WINDOW_SIZE);
			Window currentWindow = new Window(dummyMovie, (short)0);
			
			float[] key = currentWindow.getKey();

			float[] lowerKey = new float[]{(key[0]/1.0019f) - (30*525), //first test "default settings"
			                               //key[0] - 500000,
			                               //(key[0]/1.0019f) - (30*540),
			                               key[1] - 0.0001f,
			                               key[2] - 0.0001f,
			                               key[3] - 0.0001f,
			                               key[4] - 0.0001f,
			                               key[5] - 0.0001f};

			float[] upperKey = new float[]{(key[0]/1.0017f) - (30*515), //first test "default settings"
			                               //key[0],
			                               //(key[0]/1.0017f) - (30*500),
			                               key[1] + 0.0001f,
			                               key[2] + 0.0001f,
			                               key[3] + 0.0001f,
			                               key[4] + 0.0001f,
			                               key[5] + 0.0001f};

			Object[] shortList = windowDB.getRange(lowerKey, upperKey);

			int[] currentSegments = currentWindow.getSegments();

			String result = "none";
			for (int i = 0; i < shortList.length; i++) {
				Window compareWindow = (Window)shortList[i];
				int compareStart = compareWindow.getStartIndex();
				int[] compareSegments = compareWindow.getSegments();

				double[] currentDoubles = new double[30];
				double[] compareDoubles = new double[30];

				for (int y = 0; y < currentSegments.length; y++) {
					currentDoubles[y] = (double)currentSegments[y];
					compareDoubles[y] = (double)compareSegments[compareStart + y];
				}

				double segmentCorrel = correlator.correlation(currentDoubles, compareDoubles);

				if (segmentCorrel > 0.9999) {
					result = compareWindow.getTitle() + "\t" + 
					         compareWindow.getStartIndex() + "\t" +
 					         (currentWindow.getKey()[0] - compareWindow.getKey()[0]) + "\t" +
					         segmentCorrel;
					break;
				}
			}
			out.println(result);
		}
		try {
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) { 
			System.err.println(ipAddr + "\tclean-up failed"); 
		} 
		System.out.println(ipAddr + "\tdisconnected");
	}
}
