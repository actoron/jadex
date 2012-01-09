package jadex.base.service.message.transport.httprelaymtp;

import jadex.commons.SUtil;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class SendingBenchmark
{
//	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS+"benchmark";
	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/benchmark";
//	public static String	ADDRESS = "http://grisougarfield.dyndns.org:52339/relay/benchmark";
//	public static String	ADDRESS = "http://77.6.184.35:52339/relay/benchmark";
	
	/**
	 *  Send a number of messages.
	 */
	public static void	send(int size, int num) throws Exception
	{
		byte[]	data	= new byte[size];
		Random	rnd	= new Random();
		
		for(int i=0; i<num; i++)
		{
			rnd.nextBytes(data);
			URL	url	= new URL(ADDRESS);
			HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/octet-stream");
			con.setFixedLengthStreamingMode(4+data.length);
			con.connect();
			OutputStream	out	= con.getOutputStream();
			out.write(SUtil.intToBytes(data.length));
			out.write(data);
			out.flush();		
			out.close();
//			con.getInputStream().close();	// Required, otherwise servlet will not be executed.				
			con.disconnect();
		}
	}
	
	/**
	 *  Run the benchmark.
	 */
	public static void	main(String[] args) throws Exception
	{
		int	size	= 1234;
		int	setup	= 10;
		int	benchmark	= 100;
		
		System.out.println("Benchmark setup...");
		runTest(size, setup);
		
		System.out.println("Running benchmark...");
		long	time	= runTest(size, benchmark);
		
		System.out.println("Benchmark took: "+(time*100/benchmark)/100.0+" ms per message");
		System.out.println("Average transfer rate: "+(size*benchmark*100L/time)/100.0+" kB/s");
	}
	
	/**
	 *  Run the benchmark.
	 */
	public static String	runBenchmark() throws Exception
	{
		int	size	= 1234;
		int	setup	= 100;
		int	benchmark	= 1000;
		
		runTest(size, setup);
		
		long	time	= runTest(size, benchmark);
		
		return "Benchmark took: "+(time*100/benchmark)/100.0+" ms per message\n"
			+ "Average transfer rate: "+(size*benchmark*100L/time)/100.0+" kB/s";
	}
	
	/**
	 *  Run a given test.
	 */
	public static long runTest(int size, int num) throws Exception
	{
		long	start	= System.currentTimeMillis();
		send(size, num);
		return System.currentTimeMillis() - start;
	}
}
