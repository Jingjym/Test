package exam1;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Class <em>Client</em> is a class representing a simple HTTP client.
 *
 * @author wben
 */

public class Client {

	/**
	 * default HTTP port is port 8000
	 */
	private static int port = 8000;

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * The end of line character sequence.
	 */
	private static String CRLF = "\r\n";

	/**
	 * Input is taken from the keyboard
	 */
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(
			System.in));

	/**
	 * Output is written to the screen (standard out)
	 */
	static PrintWriter screen = new PrintWriter(System.out, true);

	public static void main(String[] args) throws Exception {
		try {
			/**
			 * Create a new HttpClient object.
			 */
			HttpClient myClient = new HttpClient();

			/**
			 * Parse the input arguments.
			 */
			if (args.length != 1) {
				System.err.println("Usage: Client <server>");
				System.exit(0);
			}

			/**
			 * Connect to the input server
			 */
			myClient.connect(args[0]);

			/**
			 * Read the get request from the terminal.
			 */
			screen.println(args[0] + " provxy server is listening to your request:");
			String request = keyboard.readLine();

			if (request.startsWith("GET")) {
				/**
				 * Ask the client to process the GET request.
				 */
				myClient.processGetRequest(request,args[0]);
				
			}else {
				/**
				 * Do not process other request.
				 */
				screen.println("501 Not Implemented \n");
				myClient.close();
				return;
			}

			/**
			 * Get the headers and display them.
			 */
			screen.println("Header: \n");
			screen.print(myClient.getHeader() + "\n");
			screen.flush();
			if (request.startsWith("GET")) {
				/**
				 * Ask the user to input a name to save the GET resultant web page.
				 */
				screen.println();
				screen.print("Enter the name of the file to save: ");
				screen.flush();
				String filename = keyboard.readLine();
				FileOutputStream outfile = new FileOutputStream(filename);
	
				/**
				 * Save the response to the specified file.
				 */
				String response = myClient.getResponse();
				outfile.write(response.getBytes("iso-8859-1"));
				outfile.flush();
				outfile.close();
			}

			/**
			 * Close the connection client.
			 */
			myClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
