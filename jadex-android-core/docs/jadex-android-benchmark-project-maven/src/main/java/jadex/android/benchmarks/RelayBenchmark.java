package jadex.android.benchmarks;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.ComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.xml.bean.JavaWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
//	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS;
//	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/";
	public static String	ADDRESS = "http://134.100.11.200:8080/jadex-platform-relay-web/";
	
	/**
	 *  Open the connection.
	 */
	public static Tuple2<InputStream, URLConnection>	startReceiving(Object id) throws Exception
	{
//		System.out.println("Connecting as: "+id);
		String	xmlid	= JavaWriter.objectToXML(id, RelayBenchmark.class.getClassLoader());
		URL	url	= new URL(ADDRESS+"?id="+URLEncoder.encode(xmlid, "UTF-8"));
//		System.out.println("Connecting to: "+url);
		URLConnection	con	= url.openConnection();
		con.setUseCaches(false);
		InputStream in = new BufferedInputStream(con.getInputStream());
		return new Tuple2<InputStream, URLConnection>(in, con);
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
	public static void	send(Object id, int num, int size, boolean encode) throws Exception
	{
		Map<String, Object>	msg	= new LinkedHashMap<String, Object>();
		Object obj	= new BenchmarkMessage("test message", true);
		msg.put("content", obj);
		msg.put("sender", new ComponentIdentifier(""+id));
		msg.put("receiver", new ComponentIdentifier(""+id));
//		int datasize	= JavaWriter.objectToXML(msg, RelayBenchmark.class.getClassLoader()).getBytes().length;
//		System.out.println("data size: "+datasize);
		byte[]	data	= new byte[size];
		new Random().nextBytes(data);
		
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
				con.setChunkedStreamingMode(0);
				con.setRequestProperty("Accept-Encoding", "identity");
				con.setUseCaches(false);
				OutputStream	out	= new BufferedOutputStream(con.getOutputStream());
				SRelay.writeObject(id, out);
				out.write(SUtil.intToBytes(data.length));
				out.write(data);
				out.flush();		
				out.close();
				con.connect();
				con.getInputStream();	// Required, otherwise servlet will not be executed.
				con.disconnect();
			}
		}
	}
	
	/**
	 *  Run the benchmark.
	 */
	public static void	main(String[] args) throws Exception
	{
		int	setup	= 10;
		int	benchmark	= 100;
		int	data	= 1242;	// 1308;
		
		System.out.println("Benchmark setup:");
		System.out.print("simple");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, data, false, false);
		System.out.print("\nencoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, data, true, false);
		System.out.print("\ndecoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, data, true, true);
		
		System.out.print("\n\nRunning simple benchmark.");
		long	simple	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, data, false, false);
		System.out.print("\nRunning encoding benchmark.");
		long	encoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, data, true, false);
		System.out.print("\nRunning encoding/decoding benchmark.");
		long	decoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, data, true, true);
		
		System.out.println("\nSimple benchmark took: "+(simple*100/benchmark)/100.0+" ms per message");
		System.out.println("Encoding benchmark took: "+(encoding*100/benchmark)/100.0+" ms per message ("+(encoding*100/simple)+"%)");
		System.out.println("En/decoding benchmark took: "+(decoding*100/benchmark)/100.0+" ms per message ("+(decoding*100/simple)+"%)");
	}
	
	/**
	 *  Run a given test.
	 */
	public static long runTest(final Object id, final int num, final int size, final boolean encode, boolean decode) throws Exception
	{
		Tuple2<InputStream, URLConnection>	con	= startReceiving(id);
		long	start	= System.currentTimeMillis();
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					send(id, num, size, encode);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		receive(id, con.getFirstEntity(), num, decode);
		((HttpURLConnection)con.getSecondEntity()).disconnect();
		return System.currentTimeMillis() - start;
	}
}
