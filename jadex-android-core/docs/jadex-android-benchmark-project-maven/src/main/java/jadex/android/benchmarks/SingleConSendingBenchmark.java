package jadex.android.benchmarks;

import jadex.commons.SUtil;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class SingleConSendingBenchmark
{
//	public static String	ADDRESS = SRelay.DEFAULT_ADDRESS+"benchmark";
//	public static String	ADDRESS = "http://localhost:8080/jadex-platform-relay-web/benchmark";
	public static String	ADDRESS = "http://grisougarfield.dyndns.org:52339/relay/benchmark";
	
	/**
	 *  Send a number of messages.
	 */
	public static void	send(int size, int num) throws Exception
	{
		byte[]	data	= new byte[size];
		Random	rnd	= new Random();
		
		URL	url	= new URL(ADDRESS);
		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setChunkedStreamingMode(0);
		con.connect();
		OutputStream	out	= con.getOutputStream();
//		InputStream	in	= con.getInputStream();	// Required, otherwise servlet will not be executed.				
		
		for(int i=0; i<num; i++)
		{
			rnd.nextBytes(data);
			out.write(SUtil.intToBytes(data.length));
			out.write(data);
			out.flush();
			
			// Wait for acknowledgement.
//			System.out.println("ping: "+in.read());
		}
		out.close();
		byte[]	buf	= new byte[1024];
		while(con.getInputStream().read(buf)>-1);
//			System.out.println(SUtil.arrayToString(buf));
		con.getInputStream().close();	// Required, otherwise servlet will not be executed.				
		con.disconnect();
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
		long	start	= System.currentTimeMillis();
		send(size, num);
		return System.currentTimeMillis() - start;
	}
}
