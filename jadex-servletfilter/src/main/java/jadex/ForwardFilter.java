package jadex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *  The forward filter is a kind of web proxy that offers rest methods to adjust its mappings.
 *  A mapping expresses a source path and a target address on another web server server.
 *  Incoming requests are checked if the begin with one of the paths in the mapping and if yes
 *  are forwarded to the corresponding remote server.
 *  
 *  Answers are directly passed back as result of the web request with one exception. In
 *  case the mime type is "text/html" the proxy will inspect the result and replace absolute
 *  urls with its own server address. This allows links to work from the remote server.
 *  
 *  Supported methods are:
 *  
 *  /addMapping?name=a&target=b : Add a new mapping
 *  /removeMapping?name=a       : Remove an existing mapping
 *  /refreshMapping?name=a      : Update time stamp of mapping
 *  /displayMappings			: Show the current mappings in html
 *  /getLeasetime				: Get the lease time
 *  /setLeasetime?leasetime=a	: Set the lease time	
 *  /login?user=a&pass=b		: Login		
 */
public class ForwardFilter implements Filter
{
	/** The mapping infos. */
	protected static Map<String, ForwardInfo> infos = Collections.synchronizedMap(new LinkedHashMap<String, ForwardInfo>());
	
	/** The known users and passwords. */
	protected static Map<String, String> users = Collections.synchronizedMap(new HashMap<String, String>());
	
	public static final String addmapping = "addMapping";
	public static final String remmapping = "removeMapping";
	public static final String refreshmapping = "refreshMapping";
	public static final String displaymappings = "displayMappings";
	public static final String getleasetime = "getLeasetime";
	public static final String setleasetime = "setLeasetime";
	public static final String login = "login";
	
	public static final Set<String> commands = Collections.synchronizedSet(new HashSet<String>());
	
	public static final String authenticated = "authenticated";
	
	/** The lease time in millis. */
	protected long leasetime;
	
	static
	{
		users.put("admin", "admin");
		
		commands.add(addmapping);
		commands.add(remmapping);
		commands.add(refreshmapping);
		commands.add(displaymappings);
		commands.add(getleasetime);
		commands.add(setleasetime);
		commands.add(login);
	}
	
	/**
	 *  Init the filter.
	 */
	public void init(FilterConfig conf) throws ServletException
	{
		String val = conf.getInitParameter("leasetime");
		if(val!=null)
		{
			setLeaseTime(val);
		}
		else
		{
			setLeaseTime(5*60*1000);
		}
	}
	
	/**
	 *  Destroy the filter.
	 */
	public void destroy()
	{
	}
	
	/**
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		boolean fini = false;
		
		if(request instanceof HttpServletRequest)
		{
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;
			String requri = req.getRequestURI().substring(req.getContextPath().length()).replace("/","");
			List<String> mimetypes = null;
			boolean json = false;
			
			if(commands.contains(requri))
			{
				removeDueMappings();
				fini = checkSecure(req, res); // check if https is used
				if(!fini && !login.equals(requri)) // check if user has logged in
				{
					fini = checkAuthentication(req, res);
				}
				if(!fini)
				{
					String mts = req.getHeader("Accept");
					if(mts!=null)
					{
						StringTokenizer stok = new StringTokenizer(mts, ",");
						while(stok.hasMoreTokens())
						{
							String tok = stok.nextToken();
							StringTokenizer substok = new StringTokenizer(tok, ";");
							String mt = substok.nextToken();
							if(mimetypes==null)
							{
								mimetypes = new ArrayList<String>();
							}
							mimetypes.add(mt);
						}
						if(mimetypes.contains("application/json"))
						{
							json = true;
						}
					}
				}
			}

			if(!fini)
			{
				if(login.equals(requri))
				{
					String user = request.getParameter("user");
					String pass = request.getParameter("pass");
					String next = request.getParameter("next");
					
					if(user==null)
					{
						if(json)
						{
							res.getWriter().write("{authenticated: false, reason=\"no username/password given\"}");
						}
						else
						{
							sendLoginPage(res);
						}
					}
					else
					{
						if(pass.equals(users.get(user)))
						{
							HttpSession session = req.getSession();
							session.setAttribute(authenticated, Boolean.TRUE);
							
							if(json)
							{
								res.getWriter().write("{authenticated: true}");
							}
							else
							{
								if(next!=null)
								{
									res.sendRedirect(next);
								}
								else
								{
									res.sendRedirect("displayMappings");
								}
							}
						}
						else
						{
							if(json)
							{
								res.getWriter().write("{authenticated: false, reason=\"username/password incorrect\"}");
							}
							else
							{
								res.sendError(401);
							}
						}
					}
					fini = true;
				}
				else if(addmapping.equals(requri))
				{
					String apppath = request.getParameter("name");
					String target = request.getParameter("target");
					ForwardInfo fi = infos.get(apppath);
					if(fi!=null)
					{
						fi.setForwardPath(target);
						fi.setTime(System.currentTimeMillis());
					}
					else
					{
						infos.put(apppath, new ForwardInfo(apppath, target));
					}
					if(!json)
					{
						res.sendRedirect("displayMappings");
					}
					fini = true;
				}
				else if(remmapping.equals(requri))
				{
					String apppath = request.getParameter("name");
					ForwardInfo old = infos.remove(apppath);
					if(!json)
					{
						res.sendRedirect("displayMappings");
					}
					fini = true;
				}
				else if(refreshmapping.equals(requri))
				{
					String apppath = request.getParameter("name");
					ForwardInfo fi = infos.get(apppath);
					if(fi!=null)
					{
						fi.setTime(System.currentTimeMillis());
						if(!json)
						{
							res.sendRedirect("displayMappings");
						}
					}
					else
					{
						res.sendError(500, "Mapping not found: "+apppath);
					}
					fini = true;
				}
				else if(displaymappings.equals(requri))
				{
					// if(!json) ???
					sendDisplayMappings(res);
					fini = true;
				}
				else if(getleasetime.equals(requri))
				{
					String apppath = request.getParameter("name");
					if(apppath!=null)
					{
						ForwardInfo fi = infos.get(apppath);
						if(fi!=null)
						{
							res.getWriter().write("{leasetime="+fi.getTime()+leasetime+"}");
						}
						else
						{
							res.sendError(500, "Mapping not found: "+apppath);
						}
					}
					else
					{
						res.getWriter().write("{leasetime="+leasetime+"}");
					}
					fini = true;
				}
				else if(setleasetime.equals(requri))
				{
					String lt = request.getParameter("leasetime");
					setLeaseTime(lt);
					if(!json)
					{
						res.sendRedirect("displayMappings");
					}
					fini = true;
				}
				else
				{
					for(ForwardInfo fi: getForwardInfos())
					{
						if(requri.startsWith(fi.getAppPath()))
						{
							removeDueMappings();
							
							// forward to other server
							StringBuffer buf = new StringBuffer();
							buf.append(fi.forwardpath);
							if(requri.length()>fi.getAppPath().length())
							{
								String add = requri.substring(fi.getAppPath().length());
								buf.append(add);
							}
							if(req.getQueryString()!=null)
							{
								buf.append(req.getQueryString());
							}
							
							// Cannot use request dispatcher as it only allows for server internal forwards :-((
							sendForward(buf.toString(), req, res);
							fini = true;
						}
					}
				}
			}
		}
		
		if(!fini)
		{
			chain.doFilter(request, response); // Goes to default servlet.
		}
	}
	
	/**
	 * 
	 */
	protected boolean checkSecure(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		boolean fini = false;
		if(!request.isSecure())
		{
			String url = request.getRequestURL().toString().replaceFirst("http", "https");
			if(request.getServerPort()==8080)
			{
				url = url.replaceFirst("8080", "8443");
			}
			if(request.getQueryString()!=null)
			{
				url += request.getQueryString();
			}
			
			response.sendRedirect(url);
			fini = true;
		}
		return fini;
	}
	
	/**
	 * 
	 */
	protected boolean checkAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		boolean fini = false;
		if(!isAuthenticated(request, response))
		{
			fini = true;
			String next = request.getRequestURI();
			if(request.getQueryString()==null)
			{
				response.sendRedirect("login?next="+next);
			}
			else
			{
				response.sendRedirect("login?next="+next+request.getQueryString());
			}
		}
		return fini;
	}
	
	/**
	 * 
	 */
	protected boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response)
	{
		boolean ret = false;
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
			Boolean auth = (Boolean)session.getAttribute(authenticated);
			ret = auth==null? false: auth.booleanValue();
		}
		return ret;
	}
	
	/**
	 *  Remove all mappings with too old timestamps.
	 */
	protected void removeDueMappings()
	{
		long now = System.currentTimeMillis();
		for(ForwardInfo fi: getForwardInfos())
		{
			if(now>fi.getTime()+leasetime)
			{
				infos.remove(fi.getAppPath());
			}
		}
	}
	
	/**
	 *  Set the lease time.
	 *  @param leasetime The lease time.
	 */
	public void setLeaseTime(long leasetime) 
	{
		this.leasetime = leasetime;
	}
	
	/**
	 *  Set the lease time.
	 *  @param leasetime The lease time.
	 */
	public void setLeaseTime(String val) 
	{
		if(val!=null)
		{
			int mins = Integer.valueOf(val);
			leasetime = mins*60*1000;
		}
	}

	/**
	 * 
	 */
	protected ForwardInfo[] getForwardInfos()
	{
		return infos.values().toArray(new ForwardInfo[0]);
	}
	
	/**
	 * 
	 */
	protected void sendForward(String url, HttpServletRequest request, HttpServletResponse response)
	{
		HttpURLConnection con = null;
		try 
		{
			// Open connection with copied header
			URL urlc = new URL(url);
			con = (HttpURLConnection)urlc.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod(request.getMethod());
			con.setRequestProperty("Content-Type", request.getContentType());
			con.setRequestProperty("Content-Length", ""+request.getContentLength());	
			con.connect();
			
			// Write body data from request input
			if("POST".equals(request.getMethod()))
			{
				copyStream(request.getInputStream(), con.getOutputStream());
				con.getOutputStream().flush();
			}
			
			// URL rewriting needed
			// The internal absolute urls point to the internal server 
			// and must be rewritten to point on the external one
			
		    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    // Replace content if is html with possibly wrong links
		    if(request.getContentType()==null || ("text/html").equals(request.getContentType().toLowerCase()))
		    {
		    	// todo: also replace different subpath!?
			    String line;
			    String internal = urlc.getProtocol()+"://"+urlc.getHost()+":"+urlc.getPort(); 
			    String external = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
			    while((line = rd.readLine()) != null) 
			    { 
			        String rep = line.replace(internal, external);
			        response.getOutputStream().write(rep.getBytes());
			    }
		    }
		    else
		    {
		    	// Copy without modifications
		    	copyStream(con.getInputStream(), response.getOutputStream());
		    }
		    response.getOutputStream().flush();
		} 
		catch(Exception e) 
		{
			// todo: handle output in error case
		    System.out.println("Exception: " + e);
		    try
		    {
		    	response.sendError(500, "Exception occurred: "+e.getMessage());
		    }
		    catch(Exception ex)
		    {
		    	// ignore
		    }
		}
		finally
		{
			if(con!=null)
			{
				try{con.disconnect();}catch(Exception e){}
			}
		}
	}
	
	/**
	 * 
	 */
	protected void sendDisplayMappings(HttpServletResponse response)
	{
		try
		{
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head></head><body>");
			pw.write("<h1>Current Mappings</h1>");
			ForwardInfo[] fis = getForwardInfos();
			if(fis.length==0)
			{
				pw.write("No mappings available.");
			}
			else
			{
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				pw.write("<table cellspacing=\"0\">");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Local Name</th>");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Remote Address</th>");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Timestamp</th>");
				pw.write("<th style=\"border-bottom:solid 1px black; padding:10px 10px\">Actions</th>");
				for(ForwardInfo fi: fis)
				{
					pw.write("<tr>");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+fi.getAppPath()+"</td>");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+fi.getForwardPath()+"</td>");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+df.format(new Date(fi.getTime()))+"</td>");
					pw.write("<td style=\"padding:0px 10px\">");
					pw.write("<a href=\"removeMapping?name="+fi.getAppPath()+"\">Remove</a>");
					pw.write("  ");
					pw.write("<a href=\"refreshMapping?name="+fi.getAppPath()+"\">Refresh</a>");
					pw.write("</td>");
					pw.write("</tr>");
				}
				pw.write("</table>");
			}
				
			pw.write("<h2>Add a Mapping</h2>");
			pw.write("<form name=\"input\" action=\"addMapping\" method=\"get\">");
			pw.write("<table cellspacing=\"0\">");
			pw.write("<tr><td>Application name:</td><td><input type=\"text\" name=\"name\"/></td></tr>");
			pw.write("<tr><td>Remote server address:</td><td><input type=\"text\" name=\"target\"/></td></tr>");
			pw.write("<tr><td><input type=\"submit\" value=\"Add\"/></td></tr>");
			pw.write("</table>");
			pw.write("</form>");
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			pw.write("<h2>Leasetime</h2>");
//			pw.write("Leasetime [mins]: ");
			String lt = leasetime==0? "0": ""+(leasetime/1000/60);
//			pw.write(lt);
//			pw.write(sdf.format(new Date(leasetime-TimeZone.getDefault().getRawOffset())));
			pw.write("<form name=\"input\" action=\"setLeasetime\" method=\"get\">");
			pw.write("<table cellspacing=\"0\">");
			pw.write("<tr><td>Leasetime [mins]:</td><td><input type=\"text\" name=\"leasetime\" value=\""+lt+"\"/></input></td></tr>");
			pw.write("<tr><td><input type=\"submit\" value=\"Set\"/></td></tr>");
			pw.write("</table>");
			pw.write("</form>");
			
			pw.write("<body></html>");
		}
		catch(Exception e)
		{
			try
		    {
		    	response.sendError(500, "Exception occurred: "+e.getMessage());
		    }
		    catch(Exception ex)
		    {
		    	// ignore
		    }
		}
	}
	
	/**
	 * 
	 */
	public void sendLoginPage(HttpServletResponse response)
	{
		try
		{
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head></head><body>");
			pw.write("<h1>Login</h1>");
			
			pw.write("<form name=\"input\" action=\"login\" method=\"get\">");
			pw.write("<table cellspacing=\"0\">");
			pw.write("<tr><td>User name:</td><td><input type=\"text\" name=\"user\"/></td></tr>");
			pw.write("<tr><td>Password:</td><td><input type=\"text\" name=\"pass\"/></td></tr>");
			pw.write("<tr><td><input type=\"submit\" value=\"Login\"/></td></tr>");
			pw.write("</table>");
			pw.write("</form>");
			
			pw.write("<body></html>");
		}
		catch(Exception e)
		{
			try
		    {
		    	response.sendError(500, "Exception occurred: "+e.getMessage());
		    }
		    catch(Exception ex)
		    {
		    	// ignore
		    }
		}
	}
	
	/**
	 *  Copy all data from input to output stream.
	 */
	public static void copyStream(InputStream is, OutputStream os) 
	{
		try
		{
	        byte[] buf = new byte[10 * 1024];
	        int len = 0;
	        while((len = is.read(buf)) != -1) 
	        {
	            os.write(buf, 0, len);
	        }
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public static class ForwardInfo 
	{
		/** The application url path. */
		protected String apppath;
		
		/** The forward url base path. */
		protected String forwardpath;
		
		/** The timestamp. */
		protected long time;

		/**
		 *  Create a new ForwardInfo. 
		 */
		public ForwardInfo(String apppath, String forwardpath)
		{
			this.apppath = apppath;
//			this.apppath = apppath.startsWith("/")? apppath: "/"+apppath;
			this.forwardpath = forwardpath.startsWith("http")? forwardpath: "http://"+forwardpath;
			this.time = System.currentTimeMillis();
		}

		/**
		 *  Get the apppath.
		 *  @return The apppath.
		 */
		public String getAppPath()
		{
			return apppath;
		}

		/**
		 *  Set the apppath.
		 *  @param apppath The apppath to set.
		 */
		public void setAppPath(String apppath)
		{
			this.apppath = apppath;
		}

		/**
		 *  Get the forwardpath.
		 *  @return The forwardpath.
		 */
		public String getForwardPath()
		{
			return forwardpath;
		}

		/**
		 *  Set the forwardpath.
		 *  @param forwardpath The forwardpath to set.
		 */
		public void setForwardPath(String forwardpath)
		{
			this.forwardpath = forwardpath;
		}

		/**
		 *  Get the time.
		 *  @return The time.
		 */
		public long getTime()
		{
			return time;
		}

		/**
		 *  Set the time.
		 *  @param time The time to set.
		 */
		public void setTime(long time)
		{
			this.time = time;
		}
		
		/** 
		 * 
		 */
		public int hashCode()
		{
			return 31*apppath.hashCode();
		}

		/** 
		 * 
		 */
		public boolean equals(Object obj)
		{
			boolean ret = false;
			if(obj instanceof ForwardInfo)
			{
				ForwardInfo other = (ForwardInfo)obj;
				ret = other.getAppPath().equals(getAppPath());
			}
			return ret;
		}
	}
	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args)
	{
		try
		{
			URL url = new URL("http://localhost:8080/test/addMapping?name=a&target=a");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");

			if(con.getResponseCode() != 200)
			{
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while((output = br.readLine()) != null)
			{
				System.out.println(output);
			}

			con.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
