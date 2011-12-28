package jadex.android.benchmarks;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.ComponentIdentifier;
import jadex.commons.SUtil;
import jadex.xml.bean.JavaWriter;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class RelayBenchmark
{
//	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS;
//	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/";
	public static String	ADDRESS = "http://grisougarfield.dyndns.org:52339/relay/";
	
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
		InputStream	is	= con.getInputStream();
//		System.out.println("Got stream.");
//		is.read();	// Read first ping.
//		System.out.println("Connected.");
		return is;
	}

	/**
	 *  Receive a number of message.
	 */
	public static void	receive(Object id, InputStream in, int num, boolean decode) throws Exception
	{
		for(int i=0; i<num; i++)
		{
			if(in.read()==SRelay.MSGTYPE_DEFAULT)
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
				
//				if(i%100==50)
//					System.out.print(".");
			}
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
//		System.out.println("data size: "+data.length);
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
//				URL	url	= new URL(ADDRESS);
//				HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
//				con.setRequestMethod("POST");
//				con.setDoOutput(true);
//				con.setUseCaches(false);
//				con.setRequestProperty("Connection", "keep-alive");
//				con.setRequestProperty("Content-Type", "application/octet-stream");
//				byte[]	iddata	= JavaWriter.objectToByteArray(id, SRelay.class.getClassLoader());
//				con.setFixedLengthStreamingMode(4+iddata.length+4+data.length);
//				try
//				{
//					con.connect();
//					OutputStream	out	= con.getOutputStream();
//					out.write(SUtil.intToBytes(iddata.length));
//					out.write(iddata);
//					out.write(SUtil.intToBytes(data.length));
//					out.write(data);
//					out.flush();		
////					out.close();
//					
//					// Read response body required for connection keep alive
//					InputStream	is = con.getInputStream();
//					byte[]	buf	= new byte[256];
//					while(is.read(buf) > 0)
//					{
//					}
//					is.close();
//				}
//				catch (IOException e)
//				{
//					try
//					{
//						// Read error body required for connection keep alive
//						InputStream	es = ((HttpURLConnection)con).getErrorStream();
//						byte[]	buf	= new byte[256];
//						while(es.read(buf) > 0)
//						{
//						}
//						es.close();
//					}
//					catch(IOException ex)
//					{
//						
//						e.printStackTrace();
//					}
//				}
				
				byte[]	iddata	= JavaWriter.objectToByteArray(id, SRelay.class.getClassLoader());
				byte[]	entity	= new byte[4+iddata.length+4+data.length];
				System.arraycopy(SUtil.intToBytes(iddata.length), 0, entity, 0, 4);
				System.arraycopy(iddata, 0, entity, 4, iddata.length);
				System.arraycopy(SUtil.intToBytes(data.length), 0, entity, 4+iddata.length, 4);
				System.arraycopy(data, 0, entity, 4+iddata.length+4, data.length);
				HttpClient	client	= new DefaultHttpClient();
				HttpPost	request	= new HttpPost(ADDRESS);
				request.setEntity(new ByteArrayEntity(entity));
				client.execute(request);
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
		
		System.out.println("Benchmark setup:");
		System.out.println("simple");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, false, false, false);
		System.out.println("\nencoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, true, false, false);
		System.out.println("\ndecoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, false, true, false);
		System.out.println("\nen/decoding");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, true, true, false);
		System.out.println("\nbinary");
		runTest(SUtil.createUniqueId("relay_benchmark", 3), setup, false, false, true);
		
		System.out.println("\n\nRunning simple benchmark.");
		long	simple	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, false, false, false);
		System.out.println("\nRunning encoding benchmark.");
		long	encoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, true, false, false);
		System.out.println("\nRunning decoding benchmark.");
		long	decoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, false, true, false);
		System.out.println("\nRunning encoding/decoding benchmark.");
		long	endecoding	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, true, true, false);
		System.out.println("\nRunning binary benchmark.");
		long	binary	= runTest(SUtil.createUniqueId("relay_benchmark", 3), benchmark, true, true, false);
		
		System.out.println("\nSimple benchmark took: "+(simple*100/benchmark)/100.0+" ms per message");
		System.out.println("Encoding benchmark took: "+(encoding*100/benchmark)/100.0+" ms per message ("+(encoding*100/simple)+"%)");
		System.out.println("Decoding benchmark took: "+(decoding*100/benchmark)/100.0+" ms per message ("+(decoding*100/simple)+"%)");
		System.out.println("En/decoding benchmark took: "+(endecoding*100/benchmark)/100.0+" ms per message ("+(endecoding*100/simple)+"%)");
		System.out.println("Binary benchmark took: "+(binary*100/benchmark)/100.0+" ms per message ("+(binary*100/simple)+"%)");
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
