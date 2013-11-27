package jadex.base.relay;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;


/**
 *  Example showing how to connect to Jadex relay server.
 */
public class JadexRelayExample
{
	/**
	 *  Main method for testing.
	 */
	public static void	main(String[] args)	throws Exception
	{
		// Create a random id, which is used for sending message to.
		String	id	= "RelayExample_"+new Random().nextInt(1000);
		
		// Connect to server.
		connect(id);
		
		// Send a message to self.
		send(id, "Hello Relay World!".getBytes());

		send(id, "some more testing...".getBytes());

		// Wait while received message is printed on receiver thread.
		Thread.sleep(1000);
		
		// Exit
		System.exit(0);
	}
	
	/**
	 *  Called whenever a message is received.
	 */
	public static void	deliverMessage(byte[] rawmsg)
	{
		System.out.println("Message received: "+new String(rawmsg));
	}
	
	/** The relay server address. */
//	public static final String	ADDRESS	= "http://jadex.informatik.uni-hamburg.de/relay/";
	public static final String	ADDRESS	= "http://localhost/";
	
	/** The default message type: followed by length (int as 4 bytes) and arbitrary message content from some sender. */
	public static final byte	MSGTYPE_DEFAULT	= 1;
	
	/** The ping message type sent by server to verify if connection is alive (just the type byte and no content). */
	public static final byte	MSGTYPE_PING	= 2;
	
	/**
	 *  Start a new thread and connect to the relay server. 
	 */
	public static void	connect(String id)	throws Exception
	{
		// Connect to server.
		URL	url	= new URL(ADDRESS+"?id="+URLEncoder.encode(id, "UTF-8"));
		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		con.setUseCaches(false);
		final InputStream	in	= con.getInputStream();
		int read;
		while((read=in.read())!=-1 && read!=MSGTYPE_PING)
		{
			// wait for first ping.
			System.out.println(read);
		}
		
		// Start receiver thread.
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						// Read message type.
						int	b	= in.read();
						if(b==-1)
						{
							throw new IOException("Stream closed");
						}
						else if(b==MSGTYPE_PING)
						{
							System.out.println("Received server ping");
						}
						else if(b==MSGTYPE_DEFAULT)
						{
							byte[] rawmsg	= null;
							
							// Read message header (size)
							int msg_size;
							byte[] asize = new byte[4];
							for(int i=0; i<asize.length; i++)
							{
								b	= in.read();
								if(b==-1)
									throw new IOException("Stream closed");
								asize[i] = (byte)b;
							}
							msg_size = bytesToInt(asize);
	
							// Read all bytes for message.
							if(msg_size>0)
							{
								rawmsg = new byte[msg_size];
								int count = 0;
								while(count<msg_size) 
								{
									int bytes_read = in.read(rawmsg, count, msg_size-count);
									if(bytes_read==-1) 
										throw new IOException("Stream closed");
									count += bytes_read;
								}
								
								deliverMessage(rawmsg);
							}
						}
					}
				}
				catch(Exception e)
				{
					System.out.println("Disconnected: "+e);
				}
			}
		}).start();
	}
	
	/** A buffer for reading response data (ignored but needs to be read for connection to be reusable). */
	protected static final byte[]	RESPONSE_BUF	= new byte[8192];

	/**
	 *  Send a message.
	 */
	public static void send(String id, byte[] data)	throws IOException
	{
		// target id needs to be sent as bytes.
		byte[]	iddata	= id.getBytes("UTF-8");
		
		// Open connection
		URL	url	= new URL(ADDRESS);
		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setRequestProperty("Content-Length", ""+(4+iddata.length+4+data.length));	
		con.connect();

		// Send target id and message content
		OutputStream	out	= con.getOutputStream();
		out.write(intToBytes(iddata.length));
		out.write(iddata);
		out.write(intToBytes(data.length));
		out.write(data);
		out.flush();

		// Receive server answer to check if message could be delivered.
		int	code	= con.getResponseCode();
		if(code!=HttpURLConnection.HTTP_OK)
		{
			throw new IOException("Message not sent. HTTP code "+code+": "+con.getResponseMessage()+" target="+id);
		}
		
		// Read all of response from stream
		// otherwise data will get in the way on next request due to HTTP keep-alive.
		while(con.getInputStream().read(RESPONSE_BUF)!=-1)
		{
		}
	}

	/**
	 *  Convert bytes to an integer.
	 */
	public static int bytesToInt(byte[] buffer)
	{
		if(buffer.length != 4)
		{
			throw new IllegalArgumentException("buffer length must be 4 bytes!");
		}

		int value = (0xFF & buffer[0]) << 24;
		value |= (0xFF & buffer[1]) << 16;
		value |= (0xFF & buffer[2]) << 8;
		value |= (0xFF & buffer[3]);

		return value;
	}

	/**
	 *  Convert an integer to bytes.
	 */
	public static byte[] intToBytes(int val)
	{
		byte[] buffer = new byte[4];

		buffer[0] = (byte)((val >>> 24) & 0xFF);
		buffer[1] = (byte)((val >>> 16) & 0xFF);
		buffer[2] = (byte)((val >>> 8) & 0xFF);
		buffer[3] = (byte)(val & 0xFF);

		return buffer;
	}
}
