package http;

import java.io.*;

/**
 * Provides the ability to read from a stream. Returns its value as String or binarydata or
 * either downloads it to a file
 * 
 * @author Marvin Huber
 * 
 * @version 1.00
 */
public class StreamReader {

	private InputStream stream = null;
	
	/**
	 * Initialises a new StreamReader with an InputStream to read from
	 * 
	 * @param stream - Stream to read from lateron
	 * @param connection - The connection the stream is established to, to determine the datas
	 * 						MIME-type
	 */
	public StreamReader (InputStream stream) {
		this.stream = stream;
	}
	
	/**
	 * Reads the whole stream and returns it as a String
	 * 
	 * @return - The content of the inputstream as a String
	 */
	public String read () {
		StringBuilder result = new StringBuilder();
		byte buffer[] = new byte[1024];
		int size = 0;
			
		while (size != -1) {
			result.append(new String(buffer, 0, size));
			try {
				size = stream.read(buffer);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
	
	/**
	 * Reads a part of the stream and returns it as a String
	 * 
	 * @param begin - The position to begin reading
	 * @param end - The position to stop reading
	 * 
	 * @return A part of the String read form stream that goes from begin to end. If the stream
	 * 			is shorter than end or begin < 0 NULL is returned
	 */
	public String read (int begin, int end) {
		String result = read();
		try {
			return result.substring(begin,end);
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Reads the whole stream and returns it as a bytearray
	 * 
	 * @return Bytearray containing the streamdata
	 */
	public byte[] readBinary () {
		return read().getBytes();
	}
	
	/**
	 * Reads a part of the stream and returns it as bytearray
	 * 
	 * @param begin - The position to begin reading
	 * @param end - The position to stop reading
	 * 
	 * @return A part of the String read form stream that goes from begin to end converted to a
	 * 			bytearray. If the stream is shorter than end, the whole stream from begin on is 
	 * 			read. If the stream is shortet than begin, NULL is returned
	 */
	public byte[] readBinary (int begin, int end) {
		return read(begin, end).getBytes();
	}
	
	/**
	 * Save the streamdata to a file located at filepath
	 * 
	 * @param filepath - The location to save the file with file extension
	 */
	public void download (String filepath) {
		BufferedInputStream in = new BufferedInputStream(stream);
		FileOutputStream file;
		try {
			file = new FileOutputStream(filepath);
			BufferedOutputStream out = new BufferedOutputStream(file);
			int i;
			while ((i = in.read()) != -1) {
			    out.write(i);
			}
			out.flush();
			out.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
