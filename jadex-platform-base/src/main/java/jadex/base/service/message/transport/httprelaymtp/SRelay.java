package jadex.base.service.message.transport.httprelaymtp;

import jadex.commons.SUtil;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  Helper methods for relay service.
 */
public class SRelay
{
	//-------- constants --------
	
	/** Default relay address. */
	public static final String	DEFAULT_ADDRESS	= "http://jadex.informatik.uni-hamburg.de/jadex-applications-web/relay/";
	
	//-------- methods --------
	
	/**
	 *  Send data to the given target using the specified relay server address.
	 *  @param targetid	The target id.
	 *  @param address	The relay server address (actually the servlet address).
	 *  @param data	The data object to be transferred.	
	 */
	public static void sendData(Object targetid, String address, Object data) throws Exception
	{
		URL	url	= new URL(address);
		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setUseCaches(false);
		//		con.setRequestProperty("Content-Length", "12");
		OutputStream	out	= con.getOutputStream();
		SRelay.writeObject(targetid, out);
		SRelay.writeObject(data, out);
		out.close();
		con.connect();
		con.getInputStream();	// Required, otherwise servlet will not be executed.
	}
	
	/**
	 * 	Read an object from the given stream.
	 *  @param in	The input stream.
	 *  @return The object.
	 *  @throws	IOException when the stream is closed.
	 */
	public static Object	readObject(InputStream in) throws IOException
	{
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		byte[] buffer = readData(in, length);
		return JavaReader.objectFromByteArray(buffer, SRelay.class.getClassLoader());
	}
	
	/**
	 *  Write an object to the given stream.
	 *  @param obj	The object.
	 *  @param out	The output stream.
	 *  @throws	IOException when the stream is closed.
	 */
	public static void	writeObject(Object obj, OutputStream out) throws IOException
	{
		byte[]	data	= JavaWriter.objectToByteArray(obj, SRelay.class.getClassLoader());
		out.write(SUtil.intToBytes(data.length));
		out.write(data);
		out.flush();		
	}
	
	//-------- internal helper methods --------
	
	/**
	 *  Read data into a byte array.
	 */
	protected static byte[] readData(InputStream is, int length) throws IOException
	{
		int num	= 0;
		byte[]	buffer	= new byte[length];
		while(num<length)
		{
			int read	= is.read(buffer, num, length-num);
			if(read==-1)
			{
				throw new IOException("Stream closed.");
			}
			num	= num + read;
		}
		return buffer;
	}

}
