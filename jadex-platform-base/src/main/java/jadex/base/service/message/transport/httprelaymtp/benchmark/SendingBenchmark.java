package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 *  Test sending using new connections.
 */
public class SendingBenchmark	extends AbstractRelayBenchmark
{
	protected long[]	times;
	
	//-------- template methods --------
		
	/**
	 *  Init the time array.
	 */
	protected void setUp() throws Exception
	{
		times	= new long[8];
	}
	
	/**
	 *  Also print time array.
	 */
	protected void printResults(long time, int count)
	{
		super.printResults(time, count);
		for(int i=0; i<times.length; i++)
		{
			double	val	= ((i>0 ? times[i]-times[i-1] : times[i])*100/count/1000000)/100.0;
			System.out.println("Step "+(i+1)+" took: "+val+" ms per item");
		}
	}

	/**
	 *  Receive a message.
	 */
	protected void doSingleRun() throws Exception
	{
		int	step	= 0;
		long	start	= System.nanoTime();
		
		// Step 1: prepare data.
		byte[]	id	= JavaWriter.objectToByteArray("benchmark", getClass().getClassLoader());
		byte[]	data	= new byte[SIZE];
		Random	rnd	= new Random();
		rnd.nextBytes(data);
		times[step++]	+= System.nanoTime() - start;
		
		//======== HTTPUrlConnection ========
		
		// Step 2: Create connection.
		URL	url	= new URL(ADDRESS);
		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 3: Configure connection.
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setRequestProperty("Connection", "Close");
		con.setFixedLengthStreamingMode(4+id.length+4+data.length);
		times[step++]	+= System.nanoTime() - start;

		// Step 4: Connect.
		con.connect();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 5: Connect pt. 2
		OutputStream	out	= con.getOutputStream();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 6: Write data.
		out.write(SUtil.intToBytes(id.length));
		out.write(id);
		out.write(SUtil.intToBytes(data.length));
		out.write(data);
		times[step++]	+= System.nanoTime() - start;
		
		// Step 7: Send data.
		out.flush();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 8: Get response. 
		int	code	= con.getResponseCode();
		if(code!=HttpURLConnection.HTTP_OK)
			throw new IOException("HTTP code "+code+": "+con.getResponseMessage());
		times[step++]	+= System.nanoTime() - start;
	}
}
