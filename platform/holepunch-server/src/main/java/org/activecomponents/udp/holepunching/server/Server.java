package org.activecomponents.udp.holepunching.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.activecomponents.udp.holepunching.server.commands.IServerCommand;
import org.activecomponents.udp.holepunching.server.webcommands.WebConnectedHost;

public class Server
{
	public static Charset UTF8;
	static
	{
		UTF8 = Charset.forName("UTF-8");
	}
	
	/** Server socket for command line access */
	protected ServerSocket serversocket;
	
	/** Server socket for web access */
	protected ServerSocket webserversocket;
	
	/** UDP socket */
	protected DatagramSocket dgsocket;
	
	protected Map<String, IRegisteredHost> registeredhosts; 
	
	protected boolean running;
	
	public Server(String host, int port, int wport, int uport)
	{
		System.out.println(Arrays.toString(ServerConnection.COMMAND_CLASSNAMES.toArray()));
		registeredhosts = Collections.synchronizedMap(new HashMap<String, IRegisteredHost>());
		try
		{
			serversocket = new ServerSocket(port);
			if (uport < 0)
				uport = 12000;
				//uport = (int)(Math.random() * 30000 + 5000);
			if (host != null)
			{
				System.out.println("Binding UDP to: " + host + " " + uport);
				dgsocket = new DatagramSocket(null);
				dgsocket.bind(new InetSocketAddress(host, uport));
//				dgsocket = new DatagramSocket(uport, iaddr);
				System.out.println("Bound UDP to: " + dgsocket.getLocalAddress().getHostAddress() + " " + dgsocket.getLocalPort());
			}
			else
			{
				System.out.println("Binding UDP to: " + uport);
				dgsocket = new DatagramSocket(uport);
				System.out.println("Bound UDP to: " + dgsocket.getLocalAddress().getHostAddress() + " " + dgsocket.getLocalPort());
			}
			if (wport > 0)
			{
				webserversocket = new ServerSocket(wport);	
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void start()
	{
		running = true;
		if (webserversocket != null)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					while (running)
					{
						Socket tmpclient = null;
						try
						{
							tmpclient = webserversocket.accept();
						}
						catch (IOException e2)
						{
							e2.printStackTrace();
						}
						final Socket client = tmpclient;
						new Thread(new Runnable()
						{
							public void run()
							{
								try
								{
									BufferedInputStream	bin	= new BufferedInputStream(client.getInputStream());
									String	line	= readLine(bin);
									boolean	get	= line.toUpperCase().startsWith("GET");
									String	path	= line.substring(line.indexOf(' ')+1, line.lastIndexOf(' '));	// request path
		//							System.out.println("Path: '"+path+"'");
									String	host	= client.getInetAddress().getHostAddress();	// Remote host.
									
									while(!"".equals(line=readLine(bin)))
									{
//										System.out.println(line);
										if(line.toLowerCase().startsWith("x-forwarded-for:"))
										{
											host = line.substring(17).trim();
											if (host.contains(","))
											{
												host = host.substring(0, host.indexOf(','));
											}
										}
									}
//									System.out.println(host);
									int argstart = path.indexOf('?');
									String result = "Path incorrect: " + path;
									if(get && path != null && path.startsWith("/holepunch/HpServlet") && argstart > 0)
									{
										String argstr = path.substring(argstart + 1);
										StringTokenizer tok = new StringTokenizer(argstr, "&");
										String cmd = null;
										List<String> cmdparams = new ArrayList<String>();
										while (tok.hasMoreTokens())
										{
											String token = tok.nextToken();
											int ind = token.indexOf('=');
											if (ind > 0)
											{
												String t = token.substring(0, ind);
												String v = token.substring(ind + 1);
												if ("cmd".equals(t))
												{
													cmd = v;
												}
												else if (t.startsWith("p"))
												{
													cmdparams.add(v);
												}
											}
										}
//										System.out.println(cmd);
//										System.out.println(Arrays.toString(cmdparams.toArray()));
										result = "Command not found";
										IServerCommand[] commands = ServerConnection.getCommands(ServerConnection.COMMAND_CLASSNAMES_WEB, getClass().getClassLoader());
										for (int i = 0; i < commands.length; ++i)
										{
											if (commands[i] != null && commands[i].isApplicable(cmd))
											{
												IConnectedHost chost = new WebConnectedHost(host, registeredhosts, dgsocket);
												result = commands[i].execute(cmd, cmdparams.toArray(new String[cmdparams.size()]), chost);
												break;
											}
										}
										
										
									}
//									System.out.println(result);
									PrintWriter	pw	= new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")));
									pw.print("HTTP/1.0 200 OK\r\n");
									pw.print("\r\n");
									pw.append(result);
									pw.flush();
									client.close();
								}
								catch(Exception e)
								{
									e.printStackTrace();
									try
									{
										client.close();
									}
									catch (IOException e1)
									{
									}
								}
							}
						}).start();
					}
				}
			}).start();
		}
		
		
		try
		{
			while (running)
			{
				Socket socket = serversocket.accept();
				new ServerConnection(socket, dgsocket, registeredhosts);
			}
		}
		catch (Exception e)
		{
		}
	}
	
	public static void main(String[] args)
	{
		Map<String, String> params = new HashMap<String, String>();
		String pname = null;
		for (int i = 0; i < args.length; ++i)
		{
			if (pname == null)
			{
				pname = args[i];
			}
			else
			{
				params.put(pname.substring(1), args[i]);
				pname = null;
			}
		}
		
		int cport = 10000;
		try
		{
			cport = Integer.parseInt(params.get("cport"));
		}
		catch (Exception e)
		{
		}
		
		int uport = -1;
		try
		{
			uport = Integer.parseInt(params.get("cport"));
		}
		catch (Exception e)
		{
		}
		
		int wport = -1;
		try
		{
			wport = Integer.parseInt(params.get("wport"));
		}
		catch (Exception e)
		{
		}
		
		Server server = null;
//		if (args.length > 1)
//		{
//			server = new Server(args[1], port);
//		}
//		else
		{
			server = new Server(null, cport, wport, uport);
		}
		server.start();
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
}
