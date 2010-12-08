package jadex.simulation.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author Handle files
 */
public class FileHandler {

	/**
	 * This method reads contents of a file and print it out
	 */
	public static BufferedInputStream readFromFile(String filename) {

		BufferedInputStream bufferedInput = null;
		byte[] buffer = new byte[1024];

		try {

			// Construct the BufferedInputStream object
			bufferedInput = new BufferedInputStream(new FileInputStream(
					filename));

			int bytesRead = 0;

			// Keep reading from the file while there is any content
			// when the end of the stream has been reached, -1 is returned
			while ((bytesRead = bufferedInput.read(buffer)) != -1) {

				// Process the chunk of bytes read
				// in this case we just construct a String and print it out
				String chunk = new String(buffer, 0, bytesRead);
				System.out.print(chunk);
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedInputStream
			try {
				if (bufferedInput != null)
					bufferedInput.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return bufferedInput;
	}

	public static void writeToFile(String filename, String input) {

		BufferedOutputStream bufferedOutput = null;

		try {

			// Construct the BufferedOutputStream object
			bufferedOutput = new BufferedOutputStream(new FileOutputStream(
					filename));

			// Start writing to the output stream
			bufferedOutput.write(input.getBytes());
			// bufferedOutput.write("Line one".getBytes());
			// bufferedOutput.write("\n".getBytes()); //new line, you might want
			// to use \r\n if you're on Windows
			// bufferedOutput.write("Line two".getBytes());
			// bufferedOutput.write("\n".getBytes());

			// prints the character that has the decimal value of 65
			// bufferedOutput.write(65);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedOutputStream
			try {
				if (bufferedOutput != null) {
					bufferedOutput.flush();
					bufferedOutput.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFileAsString(String filePath) {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));

			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				fileData.append(buf, 0, numRead);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileData.toString();
	}

}