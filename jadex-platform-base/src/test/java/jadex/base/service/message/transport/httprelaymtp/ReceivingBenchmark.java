package jadex.base.service.message.transport.httprelaymtp;

import jadex.commons.SUtil;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class ReceivingBenchmark
{
//	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS+"benchmark";
//	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/benchmark";
	public static String	ADDRESS = "http://grisougarfield.dyndns.org:52339/relay/benchmark";
	
	/**
	 *  Open the connection.
	 */
	public static InputStream	startReceiving(int size) throws Exception
	{
		URL	url	= new URL(ADDRESS+"?size="+size);
		URLConnection	con	= url.openConnection();
		con.setUseCaches(false);
		InputStream	is	= con.getInputStream();
		return is;
	}

	/**
	 *  Receive a number of messages.
	 */
	public static void	receive(InputStream in, int num) throws Exception
	{
		for(int i=0; i<num; i++)
		{
			if(in.read()==SRelay.MSGTYPE_DEFAULT)
			{
				byte[]	len	= SRelay.readData(in, 4);
				int	length	= SUtil.bytesToInt(len);
				SRelay.readData(in, length);
			}
		}
	}
	
	/**
	 *  Run the benchmark.
	 */
	public static void	main(String[] args) throws Exception
	{
		int	size	= 1234;
		int	setup	= 100;
		int	benchmark	= 1000;
		
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
		InputStream	in	= startReceiving(size);
		long	start	= System.currentTimeMillis();
		receive(in, num);
		return System.currentTimeMillis() - start;
	}
}
