package jadex.base.relay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

/**
 *  Relay as a simple java application.
 */
public class StandaloneRelay
{
	/**
	 *  Start the relay application.
	 */
	public static void main(String[] args) throws Exception
	{
		int	port	= 80;
		for(int i=0; args!=null && i<args.length; i++)
		{
			if("-port".equals(args[i]) && i+1<args.length)
			{
				port	= Integer.parseInt(args[i+1]);
			}
		}
		
		final RelayHandler	handler	= new RelayHandler();
		ServerSocket	server	= new ServerSocket(port);
		while(true)
		{
			final Socket	client	= server.accept();
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						BufferedInputStream	bin	= new BufferedInputStream(client.getInputStream());
						String	line	= readLine(bin);
						boolean	get	= line.startsWith("GET");
						String	path	= line.substring(line.indexOf(' ')+1, line.lastIndexOf(' '));	// request path
//						System.out.println("Path: '"+path+"'");
						String	host	= null;	// server name as known from the outside.
						int	contentlength	= 0;
						
						while(!"".equals(line=readLine(bin)))
						{
							if(line.toLowerCase().startsWith("host:"))
							{
								host	= line.substring(line.indexOf(' ')+1);
//								System.out.println("Host: '"+host+"'");
							}
							if(line.toLowerCase().startsWith("content-length:"))
							{
								contentlength	= Integer.parseInt(line.substring(line.indexOf(' ')+1));
//								System.out.println("Content-Length: "+contentlength+"");
							}
//							System.out.println("'"+line+"'");
						}
						
						if(get && "/servers".equals(path))
						{
							PrintStream	out	= new PrintStream(client.getOutputStream());
							out.print("HTTP/1.0 200 OK\r\n");
							out.print("Content-type: text/plain\r\n");
							out.println("\r\n");
							out.println("http://"+host+"/");
							out.flush();
							client.close();
						}
						else if(get && "/ping".equals(path))
						{
							PrintStream	out	= new PrintStream(client.getOutputStream());
							out.print("HTTP/1.0 200 OK\r\n");
							out.println("\r\n");
							out.flush();
							client.close();
						}
						else if(get && path.startsWith("/?id="))
						{
//							client.setTcpNoDelay(true);
							
							String	id	= URLDecoder.decode(path.substring(path.indexOf('=')+1), "UTF-8");
							String	hostip	= ((InetSocketAddress)client.getRemoteSocketAddress()).getAddress().getHostAddress();
							String	hostname	= ((InetSocketAddress)client.getRemoteSocketAddress()).getHostName();
							handler.initConnection(id, hostip, hostname, "http");	// Hack!!! https?
//							System.out.println("id: '"+id+"'");
//							System.out.println("hostip: '"+hostip+"'");
//							System.out.println("hostname: '"+hostname+"'");
							OutputStream	out	= new BufferedOutputStream(client.getOutputStream(), client.getSendBufferSize());
							out.write("HTTP/1.0 200 OK\r\n\r\n".getBytes());
							System.out.println("Client connected: '"+id+"'");//, "+client.getSendBufferSize());
							handler.handleConnection(id, out);	// Hack!!! https?
						}
						else if(!get)
						{
							try
							{
								if(path.startsWith("/awareness"))
								{
									handler.handleAwareness(new CounterInputStream(bin, contentlength));
								}
								else
								{
									handler.handleMessage(new CounterInputStream(bin, contentlength), "http");	// Hack!!! https?									
								}
								PrintStream	out	= new PrintStream(client.getOutputStream());
								out.print("HTTP/1.0 200 OK\r\n");
								out.println("\r\n");
								out.flush();
								client.close();
							}
							catch(Exception e)
							{
								PrintStream	out	= new PrintStream(client.getOutputStream());
								out.print("HTTP/1.0 404 Not Found\r\n");
								out.println("\r\n");
								out.flush();
								client.close();								
							}
						}
					}
					catch(Exception e)
					{
						System.out.println("Client disconnected: "+e);
//						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Read a line from an input stream.
	 */
	public static String	readLine(InputStream in)	throws Exception
	{
		StringBuffer	ret	= new StringBuffer();
		boolean	r	= false;
		boolean	n	= false;
		int	read;
		while(!(r&&n) && (read=in.read())!=-1)
		{
			if(!r && read=='\r')
			{
				r	= true;
			}
			else if(r && read=='\n')
			{
				n	= true;
			}
			else
			{
				r	= false;
				ret.append((char)read);
			}
		}
		return ret.toString();
	}
	
	//-------- helper classes --------
	
	/**
	 *  Notify end of stream after a given number of bytes. 
	 */
	static class CounterInputStream	extends FilterInputStream
	{
		//-------- attributes --------
		
		/** The remaining expected input length. */
		protected int	remaining;
		
		//-------- constructors --------
		
		/**
		 *  Create a counter input stream.
		 */
		public CounterInputStream(InputStream in, int expected)
		{
			super(in);
			this.remaining	= expected;
		}
		
		//-------- methods --------
		
		/**
		 *  Read a byte.
		 */
		public int read() throws IOException
		{
			int	read	= -1;
			if(remaining>0)
			{
				read	= super.read();
				if(read>-1)
				{
					remaining--;
				}
			}
			return read;
		}
		
		/**
		 *  Read some bytes.
		 */
		public int read(byte[] b, int off, int len) throws IOException
		{
			int	read	= -1;
			if(remaining>0)
			{
				read	= super.read(b, off, len);
				if(read>-1)
				{
					remaining-=read;
				}
			}
			return read;
		}
	}
}
