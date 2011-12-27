package jadex.base.service.message.transport.httprelaymtp;

import jadex.bridge.ComponentIdentifier;
import jadex.commons.SUtil;
import jadex.xml.bean.JavaWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class RelayBenchmark
{
	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS;
//	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/";
	
	/**
	 *  Open the connection.
	 */
	public static InputStream	startReceiving(Object id) throws Exception
	{
//		System.out.println("Connecting as: "+id);
		String	xmlid	= JavaWriter.objectToXML(id, RelayBenchmark.class.getClassLoader());
		URL	url	= new URL(ADDRESS+"?id="+URLEncoder.encode(xmlid, "UTF-8"));
//		System.out.println("Connecting to: "+url);
		URLConnection	con	= url.openConnection();
		con.setUseCaches(false);
		return con.getInputStream();
	}

	/**
	 *  Receive a number of message.
	 */
	public static void	receive(Object id, InputStream in, int num, boolean decode) throws Exception
	{
		for(int i=0; i<num; i++)
		{
			if(in.read()!=SRelay.MSGTYPE_DEFAULT)
			{
				throw new RuntimeException("no messages to receive.");
			}
			
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
			
			if(i%100==50)
				System.out.print(".");
		}
	}

	/**
	 *  Send a number of messages.
	 */
	public static void	send(Object id, int num, boolean encode, boolean binary) throws Exception
	{
		Map<String, Object>	msg	= new LinkedHashMap<String, Object>();
		Object obj	= new BenchmarkMessage("test message", true);
		msg.put("content", obj);
		msg.put("sender", new ComponentIdentifier(""+id));
		msg.put("receiver", new ComponentIdentifier(""+id));
		byte[]	data	= JavaWriter.objectToXML(msg, RelayBenchmark.class.getClassLoader()).getBytes();
//		System.out.println("data size: "+datasize);
		if(binary)
		{
			new Random().nextBytes(data);
		}
		
		for(int i=0; i<num; i++)
		{
//			System.out.println("Sending to: "+id);
			if(encode)
			{
				SRelay.sendData(id, ADDRESS, msg);
			}
			else
			{
				URL	url	= new URL(ADDRESS);
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
	
	/**
	 *  Run the benchmark.
	 */
	public static void	main(String[] args) throws Exception
	{
		int	setup	= 100;
		int	benchmark	= 2000;
		
		System.out.println("Benchmark setup:");
		System.out.print("simple");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, false, false, false);
		System.out.print("\nencoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, true, false, false);
		System.out.print("\ndecoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, false, true, false);
		System.out.print("\nen/decoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, true, true, false);
		System.out.print("\nbinary");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, false, false, true);
		
		System.out.print("\n\nRunning simple benchmark.");
		long	simple	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, false, false, false);
		System.out.print("\nRunning encoding benchmark.");
		long	encoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, true, false, false);
		System.out.print("\nRunning decoding benchmark.");
		long	decoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, false, true, false);
		System.out.print("\nRunning encoding/decoding benchmark.");
		long	endecoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, true, true, false);
		System.out.print("\nRunning binary benchmark.");
		long	binary	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, true, true, false);
		
		System.out.println("\nSimple benchmark took: "+(simple*100/benchmark)/100.0+" ms per message");
		System.out.println("Encoding benchmark took: "+(encoding*100/benchmark)/100.0+" ms per message ("+(encoding*100/simple)+"%)");
		System.out.println("Decoding benchmark took: "+(decoding*100/benchmark)/100.0+" ms per message ("+(decoding*100/simple)+"%)");
		System.out.println("En/decoding benchmark took: "+(decoding*100/benchmark)/100.0+" ms per message ("+(endecoding*100/simple)+"%)");
		System.out.println("Binary benchmark took: "+(decoding*100/benchmark)/100.0+" ms per message ("+(binary*100/simple)+"%)");
	}
	
	/**
	 *  Run a given test.
	 */
	public static long runTest(final Object id, final int num, final boolean encode, boolean decode, final boolean binary) throws Exception
	{
		InputStream	in	= startReceiving(id);
		long	start	= System.currentTimeMillis();
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					send(id, num, encode, binary);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		receive(id, in, num, decode);
		return System.currentTimeMillis() - start;
	}
}
