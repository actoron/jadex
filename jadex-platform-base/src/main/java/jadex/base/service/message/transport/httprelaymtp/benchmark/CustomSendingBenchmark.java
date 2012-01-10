package jadex.base.service.message.transport.httprelaymtp.benchmark;

import jadex.commons.SUtil;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

/**
 *  Test sending using new connections.
 */
public class CustomSendingBenchmark	extends AbstractRelayBenchmark
{
	protected long[]	times;
	
	//-------- template methods --------
		
	/**
	 *  Init the time array.
	 */
	protected void setUp() throws Exception
	{
		times	= new long[12];
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
		
		// ======== Custom http impl. ========
		
		// Step 2: Get host info.
		if(!ADDRESS.startsWith("http://"))
			throw new IOException("Unknown URL scheme: "+ADDRESS);
		String	path	= "";
		int	port	= 80;
		String	host	= ADDRESS.substring(7);
		if(host.indexOf('/')!=-1)
		{
			path	= host.substring(host.indexOf('/'));
			host	= host.substring(0, host.indexOf('/'));
		}
		if(host.indexOf(':')!=-1)
		{
			port	= Integer.parseInt(host.substring(host.indexOf(':')+1));
			host	= host.substring(0, host.indexOf(':'));			
		}
		times[step++]	+= System.nanoTime() - start;
		
		// Step 3: Create connection.
		Socket	sock	= new Socket(host, port);
		times[step++]	+= System.nanoTime() - start;
		
		// Step 4: Get stream.
		OutputStream	out	= sock.getOutputStream();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 5: Send header.
		out.write(("POST "+path+" HTTP/1.1\r\n").getBytes());
		out.write(("Content-Type: application/octet-stream\r\n").getBytes());
		out.write(("Connection: Close\r\n").getBytes());
		out.write(("Host: "+host+":"+port+"\r\n").getBytes());
		out.write(("Content-Length: "+(4+id.length+4+data.length)+"\r\n").getBytes());
		out.write(("\r\n").getBytes());
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
		
		// Step 9: Get response stream.
		InputStream	is	= sock.getInputStream();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 10: Get response.
		byte[]	buf	= new byte[256];
		String	response	= "";
		for(int read; (read=is.read(buf))!=-1; )
		{
			response	+= new String(buf, 0, read);
			if(response.indexOf("\r\n")!=-1)
			{
				response	= response.substring(0, response.indexOf("\r\n"));
				break;
			}
		}
		if(!"HTTP/1.1 200 OK".equals(response))
			throw new IOException("HTTP response: "+response);
		times[step++]	+= System.nanoTime() - start;
		
		// Step 8: Close stream.
		out.close();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 11: Close stream pt. 2
		is.close();
		times[step++]	+= System.nanoTime() - start;

		// Step 12: Disconnect.
		sock.close();
		times[step++]	+= System.nanoTime() - start;

		
		//======== HTTPUrlConnection ========
		
//		// Step 2: Create connection.
//		URL	url	= new URL(ADDRESS);
//		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
//		times[step++]	+= System.nanoTime() - start;
//		
//		// Step 3: Configure connection.
//		con.setRequestMethod("POST");
//		con.setDoOutput(true);
////		con.setDoInput(false);
//		con.setUseCaches(false);
//		con.setRequestProperty("Content-Type", "application/octet-stream");
////		con.setRequestProperty("Connection", "Close");
//		con.setFixedLengthStreamingMode(4+id.length+4+data.length);
//		times[step++]	+= System.nanoTime() - start;
//
//		// Step 4: Connect.
//		con.connect();
//		times[step++]	+= System.nanoTime() - start;
//		
//		// Step 5: Connect pt. 2
//		OutputStream	out	= con.getOutputStream();
//		times[step++]	+= System.nanoTime() - start;
//		
//		// Step 6: Write data.
//		out.write(SUtil.intToBytes(id.length));
//		out.write(id);
//		out.write(SUtil.intToBytes(data.length));
//		out.write(data);
//		times[step++]	+= System.nanoTime() - start;
//		
//		// Step 7: Send data.
//		out.flush();
//		times[step++]	+= System.nanoTime() - start;
//		
//		// Step 9: Get response. 
//		int	code	= con.getResponseCode();
//		if(code!=HttpURLConnection.HTTP_OK)
//			throw new IOException("HTTP code "+code+": "+con.getResponseMessage());
////		InputStream	is	= con.getInputStream(); // Required, otherwise servlet will not be executed.
//		times[step++]	+= System.nanoTime() - start;
//		
////		// Step 8: Close stream.
////		out.close();
////		times[step++]	+= System.nanoTime() - start;
//		
////		// Step 10: Close stream pt. 2
////		is.close();
////		times[step++]	+= System.nanoTime() - start;
//
////		// Step 11: Disconnect.
////		con.disconnect();
////		times[step++]	+= System.nanoTime() - start;
	}
}
