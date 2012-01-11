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
public class AsyncSendingBenchmark	extends AbstractRelayBenchmark
{
	protected long[]	times;
	protected Socket	sock;
	protected InputStream	is;
	protected StringBuffer	inbuf;
	protected OutputStream	out;
	protected String	host;
	protected String	path;
	protected int	port;
	boolean	first;
	
	//-------- template methods --------
		
	/**
	 *  Init the time array and
	 *  open the connection.
	 */
	protected void setUp() throws Exception
	{
		times	= new long[6];

		// Create Connection.
		if(!ADDRESS.startsWith("http://"))
			throw new IOException("Unknown URL scheme: "+ADDRESS);
		path	= "";
		port	= 80;
		host	= ADDRESS.substring(7);
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
	}
	
	/**
	 *  Close the socket.
	 */
	protected void tearDown() throws Exception
	{
		if(sock!=null)
		{
			sock.close();
		}
		sock	= null;
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
		
		// Step 2: Create socket.
		if(sock==null)
		{
//			System.out.println("(re)connect");
			sock	= new Socket(host, port);
			sock.setSoTimeout(30000);
			out	= sock.getOutputStream();
			is	= sock.getInputStream();
			inbuf	= new StringBuffer();
			first	= true;
			System.out.println("reconnect1");
		}
		times[step++]	+= System.nanoTime() - start;
		
		// Step 3: Write data.		
		for(int i=0; i<(first?2:1); i++)
		{	// Always have one extra msg on the wire.
			
			try
			{
//				System.out.println("before");
				out.write(("POST "+path+" HTTP/1.1\r\n").getBytes());
//				System.out.println("after");
				out.write(("Content-Type: application/octet-stream\r\n").getBytes());
				out.write(("Host: "+host+":"+port+"\r\n").getBytes());
				out.write(("Content-Length: "+(4+id.length+4+data.length)+"\r\n").getBytes());
				out.write(("\r\n").getBytes());
				
				out.write(SUtil.intToBytes(id.length));
				out.write(id);
				out.write(SUtil.intToBytes(data.length));
				out.write(data);
			}
			catch(IOException e)
			{
				i--;
				sock	= new Socket(host, port);
				out	= sock.getOutputStream();
				is	= sock.getInputStream();
				inbuf	= new StringBuffer();
				System.out.println("reconnect2");
			}
		}
		times[step++]	+= System.nanoTime() - start;
		
		// Step 4: Send data.
		out.flush();
		times[step++]	+= System.nanoTime() - start;
		
		// Step 5: Get response.
		byte[]	buf	= new byte[256];
		int	read, idx;
		while((idx=inbuf.indexOf("\r\n\r\n"))==-1 && (read=is.read(buf))!=-1)
		{
			inbuf.append(new String(buf, 0, read));
		}
		String	response	= idx!=-1 ? inbuf.substring(0, idx) : inbuf.toString();
		if(idx!=-1)
		{
			inbuf.delete(0, idx+4);
		}
		boolean	close	= response.indexOf("Connection: close")!=-1;
		if(close)
			System.out.print(response);
		if(response.indexOf("\r\n")!=-1)
		{
			response	= response.substring(0, response.indexOf("\r\n"));
		}
		if(!"HTTP/1.1 200 OK".equals(response))
			throw new IOException("HTTP response: "+response);
		times[step++]	+= System.nanoTime() - start;
		
		// Step 6: Close connection.
		if(close)
		{
			sock.close();
			sock	= null;
		}
		times[step++]	+= System.nanoTime() - start;
	}
}
