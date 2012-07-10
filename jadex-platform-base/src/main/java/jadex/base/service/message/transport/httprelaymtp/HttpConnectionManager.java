package jadex.base.service.message.transport.httprelaymtp;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

/**
 *  The connection manager performs http requests and further
 *  allows asynchronously terminating open connections
 *  to avoid hanging on e.g. platform shutdown.
 */
public class HttpConnectionManager
{
	//-------- constants --------
	
	/** A buffer for reading response data (ignored but needs to be read for connection to be reusable). */
	protected static final byte[]	RESPONSE_BUF	= new byte[8192];
	
	//-------- attributes --------
	
	/** The open connections. */
	protected Collection<HttpURLConnection>	connections;
	
	/** The flag indicating shutdown. */
	protected boolean	shutdown;
	
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
	public void	dispose()
	{
		this.shutdown	= true;
		HttpURLConnection[]	acon	= connections.toArray(new HttpURLConnection[0]);
		for(int i=0; i<acon.length; i++)
		{
			// Use sun.net.www.http.HttpClient.closeServer()
			// as con.disconnect() just blocks for sun default implementation :-(
			if(acon[i].getClass().getName().equals("sun.net.www.protocol.http.HttpURLConnection"))
			{
				try
				{
					Field	f	= acon[i].getClass().getDeclaredField("http");
					f.setAccessible(true);
					Object	client	= f.get(acon[i]);
					if(client!=null)
					{
						client.getClass().getMethod("closeServer", new Class[0]).invoke(client, new Object[0]);
					}
					else
					{
						acon[i].disconnect();	// Connection not open?			
					}
				}
				catch(Exception e)
				{
					acon[i].disconnect();	// Hangs until next ping :-(
				}
			}
			// Special treatment for android impl not needed, because disconnect() works fine. 
//			else if()
//			{
//				// org.apache.harmony.luni.internal.net.www.protocol.http.HttpURLConnectionImpl	con;
//				// org.apache.harmony.luni.internal.net.www.protocol.http.HttpConnection	connection = con.connection;
//				// Socket socket	= connection.socket;
//				Field	f	= con.getClass().getDeclaredField("connection");
//				f.setAccessible(true);
//				Object	connection	= f.get(con);
//				f	= connection.getClass().getDeclaredField("socket");
//				f.setAccessible(true);
//				Socket	socket	= (Socket)f.get(connection);
//				socket.close();				
//			}
			else
			{
				acon[i].disconnect();
			}			
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Ping a relay server.
	 *  @throws IOException on connection failures
	 */
	public void	ping(String address)	throws IOException
	{
		if(shutdown)
			throw new IOException("No new connections allowed after shutdown.");
		
		address	= httpAddress(address);
		HttpURLConnection	con	= null;
		try
		{
			URL	url	= new URL(address + "ping");
			con	= (HttpURLConnection)url.openConnection();
			connections.add(con);
			con.connect();
			int	code	= con.getResponseCode();
			if(code!=HttpURLConnection.HTTP_OK)
				throw new IOException("HTTP code "+code+": "+con.getResponseMessage());
			while(con.getInputStream().read(RESPONSE_BUF)!=-1)
			{
			}
			con.getInputStream().close();
		}
		finally
		{
			if(con!=null)
			{
				connections.remove(con);
			}
		}
	}
	
	/**
	 *  Open a receiving connection.
	 *  The connection should be removed when it is closed to avoid memory leaks. 
	 *  @return The connection.
	 *  @throws IOException on connection failures
	 */
	public HttpURLConnection	openReceiverConnection(String address, IComponentIdentifier receiver)	throws IOException
	{
		if(shutdown)
			throw new IOException("No new connections allowed after shutdown.");
		
		address	= httpAddress(address);
		HttpURLConnection	con	= null;
		String	xmlid	= receiver.getRoot().getName();
		URL	url	= new URL(address+"?id="+URLEncoder.encode(xmlid, "UTF-8"));
		con	= (HttpURLConnection)url.openConnection();
		con.setUseCaches(false);
//		con.setRequestProperty("User-Agent", "Jadex4Android 2.1-SNAPSHOT");
//		con.setRequestProperty("Cache-Control", "no-cache, no-transform");
//		con.setRequestProperty("Pragma", "no-cache");
		connections.add(con);
		
		//						// Hack!!! Do not validate server (todo: enable/disable by platform argument).
		//						if(con instanceof HttpsURLConnection)
		//						{
		//							HttpsURLConnection httpscon = (HttpsURLConnection) con;  
		//					        httpscon.setHostnameVerifier(new HostnameVerifier()  
		//					        {        
		//					            public boolean verify(String hostname, SSLSession session)  
		//					            {  
		//					                return true;  
		//					            }  
		//					        });												
		//						}

		return con;
	}
	
	/**
	 *  Remove a connection.
	 */
	public void	remove(HttpURLConnection con)
	{
		connections.remove(con);
	}
	
	/**
	 *  Get known servers from a server.
	 *  @param address	The remote server address.
	 *  @return The comma separated server list.
	 *  @throws IOException on connection failures
	 */
	public String	getServers(String address)	throws IOException
	{
		return getPeerServers(address, null);
	}

	/**
	 *  Get known servers from a peer server.
	 *  @param peeraddress	The remote server address.
	 *  @param ownaddress	The local server address supplied for mutual connection (may be null if not connecting to peer).
	 *  @return The comma separated server list.
	 *  @throws IOException on connection failures
	 */
	public String	getPeerServers(String peeraddress, String ownaddress)	throws IOException
	{
		if(shutdown)
			throw new IOException("No new connections allowed after shutdown.");
		
		String	ret;
		HttpURLConnection	con	= null;
		peeraddress	= httpAddress(peeraddress);
		ownaddress	= ownaddress!=null ? httpAddress(ownaddress) : null;
		try
		{
			URL	url	= new URL(peeraddress+"servers"
				+(ownaddress!=null ? "?peerurl="+URLEncoder.encode(ownaddress, "UTF-8") : ""));
			con	= (HttpURLConnection)url.openConnection();
			connections.add(con);
			if(con.getContentType()!=null && con.getContentType().startsWith("text/plain"))
			{
				ret	= new Scanner(con.getInputStream()).useDelimiter("\\A").next();
			}
			else
			{
				throw new IOException("Unexpected content type: "+con.getContentType());
			}
		}
		finally
		{
			if(con!=null)
			{
				connections.remove(con);
			}
		}
		return ret;
	}
	
	/**
	 *  Post a message.
	 *  @throws IOException on connection failures
	 */
	public void postMessage(String address, IComponentIdentifier targetid, byte[][] data)	throws IOException
	{
		if(shutdown)
			throw new IOException("No new connections allowed after shutdown.");
		
		address	= httpAddress(address);
		byte[]	iddata	= targetid.getName().getBytes("UTF-8");
		
		HttpURLConnection	con;
		OutputStream	out;
		int	code;
		URL	url	= new URL(address);
		con	= (HttpURLConnection)url.openConnection();
		int	datalength	= 0;
		for(int i=0; i<data.length; i++)
		{
			datalength	+= data[i].length;
		}

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type", "application/octet-stream");
		con.setRequestProperty("Content-Length", ""+(4+iddata.length+4+datalength));

//		synchronized(POOL_LOCK)
		{
			con.connect();
	
			out	= con.getOutputStream();

			out.write(SUtil.intToBytes(iddata.length));
			out.write(iddata);
			out.write(SUtil.intToBytes(datalength));
			for(int i=0; i<data.length; i++)
			{
				out.write(data[i]);
			}
			out.flush();
//								out.close();
	
			code	= con.getResponseCode();
		}
		
		if(code!=HttpURLConnection.HTTP_OK)
		{
			throw new IOException("HTTP code "+code+": "+con.getResponseMessage()+" target="+targetid);
		}
		while(con.getInputStream().read(RESPONSE_BUF)!=-1)
		{
		}
//		con.getInputStream().close();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Convert a potential 'relay-' address to normal http(s) address.
	 *  Also makes sure that addresses end with '/'.
	 */
	public static String	httpAddress(String address)
	{
		if(address.startsWith("relay-"))
		{
			address	= address.substring(6);
		}
		if(!address.endsWith("/") && !address.endsWith("/awareness"))	// For compatibility with old servers: don't add slash (Todo: remove)
		{
			address	+= "/";
		}
		return address;
	}
	
	/**
	 *  Convert a potential non 'relay-' address to relay address.
	 *  Also makes sure that addresses end with '/'.
	 */
	public static String	relayAddress(String address)
	{
		if(!address.startsWith("relay-"))
		{
			address	= "relay-"+address;
		}
		if(!address.endsWith("/") && !address.endsWith("/awareness"))	// For compatibility with old servers: don't add slash (Todo: remove)
		{
			address	+= "/";
		}
		return address;
	}
}
