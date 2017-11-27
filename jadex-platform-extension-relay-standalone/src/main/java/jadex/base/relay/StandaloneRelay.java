package jadex.base.relay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.X509KeyManager;

import jadex.bridge.VersionInfo;
import jadex.commons.security.SSecurity;

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
		// Hack!!! Let relay know we are running standalone.
		System.setProperty("relay.standalone", "true");
		
		int	port	= -1;
		boolean	ssl	= false;
		String	keystore	= new File(RelayHandler.SYSTEMDIR, "keystore.jks").getAbsolutePath();
		String	storepass	= "jadexrelay";
		String	keyalias		= "jadexrelaykey";
		String	keypass		= "jadexrelaypass";
		String  ownUrl = null;
		boolean	status	= true;
		
		for(int i=0; args!=null && i<args.length; i++)
		{
			if("-port".equals(args[i]) && i+1<args.length)
			{
				port	= Integer.parseInt(args[i+1]);
			}
			else if("-ssl".equals(args[i]) && i+1<args.length)
			{
				ssl	= Boolean.parseBoolean(args[i+1]);
			}
			else if("-keystore".equals(args[i]) && i+1<args.length)
			{
				keystore	= args[i+1];
			}
			else if("-storepass".equals(args[i]) && i+1<args.length)
			{
				storepass	= args[i+1];
			}
			else if("-keyalias".equals(args[i]) && i+1<args.length)
			{
				keyalias	= args[i+1];
			}
			else if("-keypass".equals(args[i]) && i+1<args.length)
			{
				keypass	= args[i+1];
			}
			else if("-status".equals(args[i]) && i+1<args.length)
			{
				status	= Boolean.parseBoolean(args[i+1]);
			}
			else if("-ownurl".equals(args[i]) && i+1<args.length)
			{
				ownUrl	= args[i+1];
			}
			else if("-help".equals(args[i]))
			{
				System.out.println("Jadex Standalone Relay Server Version "+VersionInfo.getInstance().getVersion()+" ("+VersionInfo.getInstance().getTextDateString()+")");
				System.out.println("Supported args:");
				System.out.println("-port <portnumber>\t(default 80 or 443 when ssl=true)");
				System.out.println("-ssl <flag>\t\t(enable https, default false)");
				System.out.println("-keystore <filename>\t(keystore for ssl certificate, default $RELAY_HOME/keystore.jks, will be generated, if not found)");
				System.out.println("-storepass <password>\t(keystore password)");
				System.out.println("-keyalias <alias name>\t(name of the certificate)");
				System.out.println("-keypass <password>\t(password for the key)");
				System.out.println("-status <flag>\t\t(show status page on web access, default true)");
				System.out.println("-ownurl <url>\t\t(url that points to this relay. May also be specified using $RELAY_OWNURL environment variable.)");
				return;
			}
		}
		
		// Set default port if not specified in args.
		if(port==-1)
		{
			port	= ssl ? 443 : 80;
		}
		
		ServerSocket	server;
		
		if(ssl)
		{
			final KeyStore	ks	= SSecurity.getKeystore(keystore, storepass, keypass, keyalias);
			
	    	final String	alias	= keyalias;
	    	final String	pass	= keypass;
			SSLContext	sc	= SSLContext.getInstance("TLS");
			sc.init(new X509KeyManager[]
			{ 
			    new X509KeyManager()
			    {
			    	public String[] getServerAliases(String arg0, Principal[] arg1)
			    	{
			    		return new String[]{alias};
			    	}
			    	
			    	public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2)
			    	{
			    		return alias;
			    	}
			    	
			    	public X509Certificate[] getCertificateChain(String arg0)
			    	{
		    			X509Certificate[]	ret	= null;
			    		try
			    		{
							Certificate[] certs = ks.getCertificateChain(alias);
							if(certs!=null && certs.length>=0)
							{
								ret = new X509Certificate[certs.length];
								System.arraycopy(certs, 0, ret, 0, certs.length);
							}
						}
						catch(Exception e)
						{
						}
			    		
			    		return ret;
			    	}
			    	
			    	public PrivateKey getPrivateKey(String arg0)
			    	{
						try
						{
							return (PrivateKey)ks.getKey(alias, pass.toCharArray());
						}
						catch(Exception e)
						{
							return null;
						}
			    	}
			    	
			    	public String[] getClientAliases(String arg0, Principal[] arg1)
			    	{
			    		throw new UnsupportedOperationException();
			    	}
			    	public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2)
			    	{
			    		throw new UnsupportedOperationException();
			    	}
			    }
			}, null, null);
			
			SSLServerSocketFactory	ssf	= sc.getServerSocketFactory();
			server	= ssf.createServerSocket(port);
		}
		else
		{
			server	= new ServerSocket(port);			
		}
		
		final RelayHandler	handler	= new RelayHandler();
		if (ownUrl == null) ownUrl = System.getenv("RELAY_OWNURL");
		if (ownUrl != null) {
			// hacks to allow pure http urls as parameter
			if (!ownUrl.startsWith("relay-")) {
				ownUrl = "relay-" + ownUrl;
			}
			if (!ownUrl.endsWith("/")) {
				ownUrl = ownUrl + "/";
			}
			handler.setUrl(ownUrl);
		}
		RelayHandler.getLogger().info("Relay own URL is: " + handler.settings.getUrl() != null ? handler.settings.getUrl() : " not set explicitly.");
		RelayHandler.getLogger().info("Jadex Relay listening on port "+port);
		while(true)
		{
			final Socket	client	= server.accept();
			final boolean	fstatus	= status;
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						String	hostip	= ((InetSocketAddress)client.getRemoteSocketAddress()).getAddress().getHostAddress();
						String	hostname	= ((InetSocketAddress)client.getRemoteSocketAddress()).getHostName();
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
							if(line.toLowerCase().startsWith("x-forwarded-for:"))
							{
								hostip = line.substring(17).trim();
								if (host.contains(","))
								{
									host = host.substring(0, host.indexOf(','));
								}
								// Reverse DNS done automatically if needed.
								hostname = null;
							}
//							System.out.println("'"+line+"'");
						}
						
						if(get && "/servers".equals(path))
						{
							// Todo: handle peer url and initial flag.
							String	serverurls	= handler.handleServersRequest("http://"+host+path, null, null, -1, false);
							
							PrintWriter	pw	= new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")));
							pw.print("HTTP/1.0 200 OK\r\n");
							pw.print("Content-type: text/plain\r\n");
							pw.print("\r\n");
							pw.println(serverurls);
							pw.flush();
							client.close();
						}
						else if(get && path.startsWith("/ping"))
						{
							if(get && path.startsWith("/ping?id="))
							{
								String	id	= URLDecoder.decode(path.substring(path.indexOf('=')+1), "UTF-8");
								handler.handlePing(id);
							}
							
							PrintWriter	pw	= new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")));
							pw.print("HTTP/1.0 200 OK\r\n");
							pw.println("\r\n");
							pw.flush();
							client.close();
						}
						else if(get && path.startsWith("/?id="))
						{
//							client.setTcpNoDelay(true);
							String	id	= URLDecoder.decode(path.substring(path.indexOf('=')+1), "UTF-8");
							handler.initConnection(id, hostip, hostname, "http");	// Hack!!! https?
//							System.out.println("id: '"+id+"'");
//							System.out.println("hostip: '"+hostip+"'");
//							System.out.println("hostname: '"+hostname+"'");
							OutputStream	out	= new BufferedOutputStream(client.getOutputStream(), client.getSendBufferSize());
							out.write("HTTP/1.0 200 OK\r\n\r\n".getBytes("UTF-8"));
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
								else if(path.startsWith("/offline"))
								{
									handler.handleOffline(hostip, new CounterInputStream(bin, contentlength));
								}
								else if(path.startsWith("/platforminfos"))
								{
									handler.handlePlatforms(new CounterInputStream(bin, contentlength));
								}
								else if(path.startsWith("/platforminfo"))
								{
									handler.handlePlatform(new CounterInputStream(bin, contentlength));
								}
								else
								{
									handler.handleMessage(new CounterInputStream(bin, contentlength), "http");	// Hack!!! https?									
								}
								PrintWriter	pw	= new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")));
								pw.print("HTTP/1.0 200 OK\r\n");
								pw.println("\r\n");
								pw.flush();
								client.close();
							}
							catch(Exception e)
							{
								PrintWriter	pw	= new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")));
								pw.print("HTTP/1.0 404 Not Found\r\n");
								pw.println("\r\n");
								pw.flush();
								client.close();								
							}
						}
						else
						{
							// Default page
							PrintWriter	pw	= new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")));
							handleStatusPage(handler, pw, fstatus);
							client.close();
						}
					}
					catch(Exception e)
					{
						RelayHandler.getLogger().warning(e.toString());
//						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create a status page.
	 */
	protected static void	handleStatusPage(RelayHandler handler, PrintWriter pw, boolean status)
	{
		pw.print("HTTP/1.0 200 OK\r\n");
		pw.print("Content-type: text/html\r\n");
		pw.println("\r\n");
		
		if(status)
		{
			PeerHandler[]	peers	= handler.getCurrentPeers();
			PlatformInfo[]	platforms	= handler.getCurrentPlatforms();
			
			StringBuffer	buf	= new StringBuffer();
			buf.append("<html><head><title>Jadex Relay Status Page</title></head><body>\n");
			buf.append("<h1>Jadex Relay</h1>\n");
			if(peers!=null && peers.length>0)
			{
				buf.append("<h2>Connected Peers</h2>\n<ul>\n");
				for(PeerHandler pe: peers)
				{
					buf.append("<li><a href=\"");
					buf.append(pe.getUrl());
					buf.append("\">");
					buf.append(pe.getUrl());
					buf.append("</a></li>");
				}
				buf.append("</ul>\n");
			}
			if(platforms!=null && platforms.length>0)
			{
				buf.append("<h2>Connected Platforms</h2>\n<ul>\n");
				for(PlatformInfo pi: platforms)
				{
					buf.append("<li>");
					buf.append(pi.getId());
					buf.append("</li>");
				}
				buf.append("</ul>\n");
			}
			buf.append("</body></html>");
			
			pw.println(buf.toString());
		}
		
		pw.flush();
	}
	
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
