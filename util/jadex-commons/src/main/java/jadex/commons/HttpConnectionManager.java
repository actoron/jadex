package jadex.commons;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 *  The connection manager allows asynchronously terminating
 *  open connections to avoid hanging on e.g. platform shutdown.
 */
public class HttpConnectionManager
{
	//-------- attributes --------
	
	/** The open connections. */
	private Collection<HttpURLConnection>	connections;
	
	/** The flag indicating shutdown. */
	private boolean	shutdown;
	
	//-------- constructors --------
	
	/**
	 *  Create a new connection manager.
	 */
	public HttpConnectionManager()
	{
		this.connections	= Collections.synchronizedSet(new HashSet<HttpURLConnection>());
	}
	
	/**
	 *  Dispose the connection manager and close all open connections.
	 */
	public synchronized void	dispose()
	{
		this.shutdown	= true;
		HttpURLConnection[]	acon	= connections.toArray(new HttpURLConnection[0]);
		for(int i=0; i<acon.length; i++)
		{
			closeConnection(acon[i]);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Open a receiving connection.
	 *  The connection should be removed when it is closed to avoid memory leaks.
	 *  @param address	The address to connect to. 
	 *  @return The connection.
	 *  @throws IOException on connection failures
	 */
	public synchronized HttpURLConnection	openConnection(String address)	throws IOException
	{
		if(shutdown)
			throw new IOException("No new connections allowed after shutdown.");
		
		HttpURLConnection	con	= null;
		URL	url	= new URL(address);
		con	= (HttpURLConnection)url.openConnection();
		connections.add(con);

		return con;
	}
	
	/**
	 *  Remove a connection.
	 *  @param con	The previously opened connection.
	 */
	public synchronized void	remove(HttpURLConnection con)
	{
		connections.remove(con);
	}

	/**
	 *  Close a connection
	 */
	public static	void	closeConnection(HttpURLConnection con)
	{
//		System.out.println("close connection: "+con);
		// Use sun.net.www.http.HttpClient.closeServer()
		// as con.disconnect() just blocks for sun default implementation :-(
		if(con.getClass().getName().startsWith("sun.net.www.protocol."))	// http+https impls.
		{
			// Get delegate from https wrapper.
			if(con.getClass().getName().equals("sun.net.www.protocol.https.HttpsURLConnectionImpl"))
			{
				try
				{
					Field	f	= SReflect.getField(con.getClass(), "delegate");
					f.setAccessible(true);
					con	= (HttpURLConnection)f.get(con);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}				
			}
			
			// closeServer() causes leak of jadex objects on terminated http receiver thread!? see HttpRelayTransport.Worker.run()
			try
			{
				Field	f	= SReflect.getField(con.getClass(), "http");
				f.setAccessible(true);
				Object	client	= f.get(con);
				if(client!=null)
				{
					client.getClass().getMethod("closeServer", new Class[0]).invoke(client, new Object[0]);
				}
				else
				{
					con.disconnect();	// Connection not open?			
				}
			}
			catch(Exception e)
			{
//				con.disconnect();	// Hangs until next ping :-(
				final HttpURLConnection	fcon	= con;
				Thread	t	= new Thread(new Runnable()
				{
					public void run()
					{
						fcon.disconnect();
					}
				});
				t.setDaemon(true);
				t.start();
			}
		}
		// Special treatment for android impl not needed, because disconnect() works fine. 
//		else if()
//		{
//			// org.apache.harmony.luni.internal.net.www.protocol.http.HttpURLConnectionImpl	con;
//			// org.apache.harmony.luni.internal.net.www.protocol.http.HttpConnection	connection = con.connection;
//			// Socket socket	= connection.socket;
//			Field	f	= con.getClass().getDeclaredField("connection");
//			f.setAccessible(true);
//			Object	connection	= f.get(con);
//			f	= connection.getClass().getDeclaredField("socket");
//			f.setAccessible(true);
//			Socket	socket	= (Socket)f.get(connection);
//			socket.close();				
//		}
		else
		{
			con.disconnect();
		}			

	}
}
