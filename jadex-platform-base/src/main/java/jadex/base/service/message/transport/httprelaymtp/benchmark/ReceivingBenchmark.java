package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.commons.SUtil;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *  Test receiving speed from relay servlet.
 */
public class ReceivingBenchmark	extends AbstractRelayBenchmark
{
	//-------- attributes --------
	
	/** The input stream for receiving messages. */
	protected InputStream	in;
	
	//-------- template methods --------
	
	/**
	 *  Open the connection.
	 */
	protected void	setUp() throws Exception
	{
		String	xmlid	= JavaWriter.objectToXML("benchmark", ReceivingBenchmark.class.getClassLoader());
		URL	url	= new URL(ADDRESS+"?id="+URLEncoder.encode(xmlid, "UTF-8")+"&size="+SIZE);
		URLConnection	con	= url.openConnection();
		con.setUseCaches(false);
		in	= con.getInputStream();
	}

	/**
	 *  Receive a message.
	 */
	protected void doSingleRun() throws Exception
	{
		// Wait until message becomes available (skip pings).
		int	read	= in.read();
		while(read!=-1 && read!=SRelay.MSGTYPE_DEFAULT)
		{
			// Skip awareness content.
			if(read!=SRelay.MSGTYPE_PING)
			{
				byte[]	len	= readData(in, 4);
				int	length	= SUtil.bytesToInt(len);
				readData(in, length);				
			}
			
			read	= in.read();
		}
		
		if(read==-1)
		{
			throw new IOException("Stream closed.");
		}
		
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		readData(in, length);
	}	
	
	//-------- helper methods --------
	
	/**
	 *  Read data into a byte array.
	 */
	public static byte[] readData(InputStream is, int length) throws IOException
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
