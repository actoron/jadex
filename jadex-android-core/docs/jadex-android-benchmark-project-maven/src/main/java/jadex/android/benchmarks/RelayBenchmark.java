package jadex.android.benchmarks;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.commons.SUtil;
import jadex.micro.benchmarks.BenchmarkMessage;
import jadex.xml.bean.JavaWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class RelayBenchmark
{
	/**
	 *  Receive a number of message.
	 */
	public static void	receive(Object id, int num, boolean decode) throws Exception
	{
		System.out.println("Connecting as: "+id);
		String	xmlid	= JavaWriter.objectToXML(id, RelayBenchmark.class.getClassLoader());
		URL	url	= new URL("http://localhost:8080/jadex-applications-web/relay/?id="+URLEncoder.encode(xmlid, "UTF-8"));
		System.out.println("Connecting to: "+url);
		URLConnection	con	= url.openConnection();
		con.setUseCaches(false);
		InputStream	in	= con.getInputStream();
		for(int i=0; i<num; i++)
		{
			if(decode)
			{
				SRelay.readObject(in);
			}
			else
			{
				byte[]	len	= SRelay.readData(in, 4);
				int	length	= SUtil.bytesToInt(len);
				SRelay.readData(in, length);
			}
			System.out.println(id+" received: "+i);
		}
	}

	/**
	 *  Send a number of messages.
	 */
	public static void	main(Object id, int num, int size, boolean encode) throws Exception
	{
		Object obj	= new BenchmarkMessage("test message", true);
		int datasize	= JavaWriter.objectToXML(obj, RelayBenchmark.class.getClassLoader()).getBytes().length;
		System.out.println("data size: "+datasize);
		byte[]	data	= new byte[size];
		new Random().nextBytes(data);
		
		for(int i=0; i<num; i++)
		{
			System.out.println("Sending to: "+id);
			if(encode)
			{
				SRelay.sendData(id, SRelay.DEFAULT_ADDRESS, obj);
			}
			else
			{
				URL	url	= new URL(SRelay.DEFAULT_ADDRESS);
				HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setUseCaches(false);
				OutputStream	out	= con.getOutputStream();
				SRelay.writeObject(id, out);
				out.write(SUtil.intToBytes(data.length));
				out.write(data);
				out.flush();		
				out.close();
				con.connect();
				con.getInputStream();	// Required, otherwise servlet will not be executed.				
			}
		}
	}
}
