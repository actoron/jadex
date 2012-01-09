package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 *  Test sending using a shared connection for all messages.
 */
public class SingleConSendingBenchmark	extends AbstractRelayBenchmark
{
	//-------- attributes --------
	
	/** The connection. */
	protected HttpURLConnection	con;
	
	//-------- template methods --------
	/**
	 *  Open the connection.
	 */
	protected void setUp() throws Exception
	{
		URL	url	= new URL(ADDRESS);
		con	= (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setChunkedStreamingMode(0);
		con.connect();
	}

	/**
	 *  Send a single message.
	 */
	protected void doSingleRun() throws Exception
	{
		byte[]	data	= new byte[SIZE];
		Random	rnd	= new Random();
		rnd.nextBytes(data);
		con.getOutputStream().write(SUtil.intToBytes(data.length));
		con.getOutputStream().write(data);
		con.getOutputStream().flush();
			
		// Wait for acknowledgement.
//		System.out.println("ping: "+in.read());
	}
	
	protected void tearDown() throws Exception
	{
		con.getOutputStream().close();
		byte[]	buf	= new byte[1024];
		while(con.getInputStream().read(buf)>-1);
//			System.out.println(SUtil.arrayToString(buf));
		con.getInputStream().close();	// Required, otherwise servlet will not be executed.				
		con.disconnect();
	}
}
