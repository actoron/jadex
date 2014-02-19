package jadex;

import jadex.commons.SUtil;
import jadex.commons.collection.LRU;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
	
	//-------- attributes --------
	
	/** Flag if https should be enforced. */
	protected boolean https;
	
	/** The lease time in millis. */
	protected long leasetime;
	
	/** The emitted nonces. */
	protected LRU<String, String> nonces;
	
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
		nonces = new LRU<String, String>(5000);
		String val = conf.getInitParameter("leasetime");
		if(val!=null)
		{
			setLeaseTime(val);
		}
		else
		{
			setLeaseTime(5*60*1000);
		}
		
		val = conf.getInitParameter("https");
		if(val!=null)
		{
			https = Boolean.getBoolean(val);
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
			
			if(commands.contains(requri))
			{
				removeDueMappings();
				
				// check if https is used
				if(https)
				{
					fini = checkSecure(req, res); 
				}
				
				// check if user has logged in
				if(!fini && !login.equals(requri)) 
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
					}
				}
			}

			if(!fini)
			{
				if(login.equals(requri))
				{
					String auth = req.getHeader("Authorization");
					if(auth!=null)
					{			
						checkDigestAuthentication(req, res, mimetypes);
					}
					else
					{
						checkUrlParameterAuthentication(req, res, mimetypes);
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
					if(isBrowserClient(mimetypes))
					{
						res.sendRedirect("displayMappings");
					}
					fini = true;
				}
				else if(remmapping.equals(requri))
				{
					String apppath = request.getParameter("name");
					ForwardInfo old = infos.remove(apppath);
					if(isBrowserClient(mimetypes))
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
						if(isBrowserClient(mimetypes))
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
					if(isBrowserClient(mimetypes))
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
	protected void checkDigestAuthentication(HttpServletRequest request, HttpServletResponse response, 
		Collection<String> mimetypes) throws IOException
	{
		String auth = request.getHeader("Authorization");
		if(auth.startsWith("Digest"))
		{
			HashMap<String, String> vals = parseHeader(auth);
			
			String digest = vals.get("response");
			String check = null;
			
			String method = request.getMethod();
			String user = vals.get("username");
			String nonce = vals.get("nonce");
			
			// is nonce contained in nonces, i.e fresh
			if(nonces.remove(nonce)!=null)
			{
				// if user exists
				if(users.containsKey(user))
				{
					String qop = vals.get("qop");
					String realm = vals.get("realm");
					String pass = users.get(user);
					String uri = vals.get("uri");
					
					String ha1 = hex(digest(user+":"+realm+":"+pass));
					String ha2;
					
					if(qop.equals("auth-int"))
					{
						String body = readRequestBody(request);
						ha2 = hex(digest(method+":"+uri+":"+SUtil.hex(digest(body))));
					}
					else
					{
						ha2 = hex(digest(method+":"+uri));
					}
					
					if(qop==null || qop.length()==0)
					{
						check = hex(digest(ha1+":"+nonce+":"+ha2));
					}
					else
					{
						String noncecnt = vals.get("nc");
						String conce = vals.get("cnonce");
						check = hex(digest(ha1+":"+nonce+":"+noncecnt+":"+conce+":"+qop+":"+ha2));
					}

					if(!digest.equals(check))
					{
						sendAuthorizationRequest(request.getServerName(), response);
					}
					else
					{
						HttpSession session = request.getSession();
						session.setAttribute(authenticated, Boolean.TRUE);
						
						if(isBrowserClient(mimetypes))
						{
							// todo: redirect to next
							response.sendRedirect("displayMappings");
						}
					}
				}
				else
				{
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User unknown.");
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Nonce unknown.");
			}
		}
		else
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only digest authentication supported.");
		}
	}
	
	/**
	 * 
	 */
	protected void checkUrlParameterAuthentication(HttpServletRequest request, HttpServletResponse response, 
		Collection<String> mimetypes) throws IOException
	{
		String user = request.getParameter("user");
		String pass = request.getParameter("pass");
		String next = request.getParameter("next");
		
		if(user==null)
		{
			if(isBrowserClient(mimetypes))
			{
				if(request.isSecure())
				{
					sendLoginPage(response);
				}
				else
				{
					sendAuthorizationRequest(request.getServerName(), response);
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "No username/password.");
			}
		}
		else
		{
			if(pass.equals(users.get(user)))
			{
				HttpSession session = request.getSession();
				session.setAttribute(authenticated, Boolean.TRUE);
				
				if(isBrowserClient(mimetypes))
				{
					if(next!=null)
					{
						response.sendRedirect(next);
					}
					else
					{
						response.sendRedirect("displayMappings");
					}
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Wrong username/password.");
			}						
		}
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
	 * 
	 */
	protected void sendAuthorizationRequest(String realm, HttpServletResponse response) throws IOException
	{
		StringBuffer buf = new StringBuffer();
		String nonce = createNonce();
		nonces.put(nonce, nonce);
		buf.append("Digest realm=\"").append(realm).append("\",");
		buf.append("qop=").append("auth").append(",");
		buf.append("nonce=\"").append(nonce).append("\",");
//		buf.append("opaque=\"").append(getOpaque(req.getRemoteHost(), getNonce()).append("\"");
		buf.append("algorithm=\"MD5\"");
		response.addHeader("WWW-Authenticate", buf.toString());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
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
	 * 
	 */
	protected boolean isBrowserClient(Collection<String> mimetypes)
	{
		return mimetypes==null || mimetypes.contains("text/html") || mimetypes.contains("*/*");
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
	
//	 private String authMethod = "auth";
//	 private String userName = "usm";
//	 private String password = "password";
//	 private String realm = "example.com";
	  
//	 public String nonce;
	  
	// nonce = calculateNonce();

//	protected void checkAuthenticationResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//	{
//		response.setContentType("text/html;charset=UTF-8");
//		PrintWriter out = response.getWriter();
//
//		String requestBody = readRequestBody(request);
//
//		try
//		{
//			String authHeader = request.getHeader("Authorization");
//			if(StringUtils.isBlank(authHeader))
//			{
//				response.addHeader("WWW-Authenticate", getAuthenticateHeader());
//				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//			}
//			else
//			{
//				if(authHeader.startsWith("Digest"))
//				{
//					HashMap<String, String> vals = parseHeader(authHeader);
//					String method = request.getMethod();
//					String ha1 = DigestUtils.md5Hex(userName + ":" + realm + ":" + password);
//					String qop = vals.get("qop");
//					String ha2;
//					String reqURI = vals.get("uri");
//					if(!StringUtils.isBlank(qop) && qop.equals("auth-int"))
//					{
//						String entityBodyMd5 = DigestUtils.md5Hex(requestBody);
//						ha2 = DigestUtils.md5Hex(method + ":" + reqURI + ":" + entityBodyMd5);
//					}
//					else
//					{
//						ha2 = DigestUtils.md5Hex(method + ":" + reqURI);
//					}
//
//					String serverResponse;
//
//					if(StringUtils.isBlank(qop))
//					{
//						serverResponse = DigestUtils.md5Hex(ha1 + ":" + nonce + ":" + ha2);
//					}
//					else
//					{
//						String domain = vals.get("realm");
//						String nonceCount = vals.get("nc");
//						String clientNonce = vals.get("cnonce");
//						serverResponse = DigestUtils.md5Hex(ha1 + ":" + nonce + ":" + nonceCount + ":" + clientNonce + ":" + qop + ":" + ha2);
//					}
//					String clientResponse = vals.get("response");
//
//					if(!serverResponse.equals(clientResponse))
//					{
//						response.addHeader("WWW-Authenticate", getAuthenticateHeader());
//						response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//					}
//				}
//				else
//				{
//					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, " This Servlet only supports Digest Authorization");
//				}
//			}
//		}
//		finally
//		{
//			out.close();
//		}
//	}
//
	/**
	 *  Convert header to key value pairs.
	 */
	private HashMap<String, String> parseHeader(String header)
	{
		String h = header.substring(header.indexOf(" ") + 1).trim();
		HashMap<String, String> values = new HashMap<String, String>();
		for(String keyval :  h.split(","))
		{
			if(keyval.contains("="))
			{
				String key = keyval.substring(0, keyval.indexOf("="));
				String value = keyval.substring(keyval.indexOf("=") + 1);
				values.put(key.trim(), value.replaceAll("\"", "").trim());
			}
		}
		return values;
	}

	/**
	 * 
	 */
	public String createNonce()
	{
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
		String fmtDate = f.format(d);
		Random rand = new Random(100000);
		Integer randomInt = rand.nextInt();
		return SUtil.hex(digest((fmtDate+randomInt.toString()).getBytes()));
	}
	
	/**
	 *  Convert to hex value.
	 */ 
	public static String hex(byte[] data)
	{
		return SUtil.hex(data, false);
	}
	
	
	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[] digest(String input)
	{
		return digest(input.getBytes());
	}
	
	/**
	 *  Build the digest given the timestamp and password.
	 */
	public static byte[] digest(byte[] input)
	{
//		System.out.println("build digest: "+timestamp+" "+secret);
		try
		{
			MessageDigest	md	= MessageDigest.getInstance("MD5");
			byte[]	output	= md.digest(input);
			return output;
		}
		catch(NoSuchAlgorithmException e)
		{
			// Shouldn't happen?
			throw new RuntimeException(e);
		}
	}

//	private String getOpaque(String domain, String nonce)
//	{
//		return DigestUtils.md5Hex(domain + nonce);
//	}
//
	/**
	 * Returns the request body as String
	 */
	private String readRequestBody(HttpServletRequest request) throws IOException
	{
		StringBuilder buf = new StringBuilder();
		BufferedReader reader = null;
		try
		{
			InputStream is = request.getInputStream();
			if(is != null)
			{
				reader = new BufferedReader(new InputStreamReader(is));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while((bytesRead = reader.read(charBuffer)) > 0)
				{
					buf.append(charBuffer, 0, bytesRead);
				}
			}
			else
			{
				buf.append("");
			}
		}
		catch(IOException ex)
		{
			throw ex;
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(IOException ex)
				{
					throw ex;
				}
			}
		}
		String body = buf.toString();
		return body;
	}
	
//	public static class HttpDigestAuth
//	{
//		public HttpURLConnection tryAuth(HttpURLConnection connection, String username, String password) throws IOException
//		{
//			int responseCode = connection.getResponseCode();
//			if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
//			{
//				connection = tryDigestAuthentication(connection, username, password);
//				if(connection == null)
//				{
//					throw new AuthenticationException();
//				}
//			}
//			return connection;
//		}
//
//		public static HttpURLConnection tryDigestAuthentication(HttpURLConnection input, String username, String password)
//		{
//			String auth = input.getHeaderField("WWW-Authenticate");
//			if(auth == null || !auth.startsWith("Digest "))
//			{
//				return null;
//			}
//			final HashMap<String, String> authFields = splitAuthFields(auth.substring(7));
//			MessageDigest md5 = null;
//			try
//			{
//				md5 = MessageDigest.getInstance("MD5");
//			}
//			catch(NoSuchAlgorithmException e)
//			{
//				return null;
//			}
//			Joiner colonJoiner = Joiner.on(':');
//			String HA1 = null;
//			try
//			{
//				md5.reset();
//				String ha1str = colonJoiner.join(username, authFields.get("realm"), password);
//				md5.update(ha1str.getBytes("ISO-8859-1"));
//				byte[] ha1bytes = md5.digest();
//				HA1 = bytesToHexString(ha1bytes);
//			}
//			catch(UnsupportedEncodingException e)
//			{
//				return null;
//			}
//			String HA2 = null;
//			try
//			{
//				md5.reset();
//				String ha2str = colonJoiner.join(input.getRequestMethod(), input.getURL().getPath());
//				md5.update(ha2str.getBytes("ISO-8859-1"));
//				HA2 = bytesToHexString(md5.digest());
//			}
//			catch(UnsupportedEncodingException e)
//			{
//				return null;
//			}
//			String HA3 = null;
//			try
//			{
//				md5.reset();
//				String ha3str = colonJoiner.join(HA1, authFields.get("nonce"), HA2);
//				md5.update(ha3str.getBytes("ISO-8859-1"));
//				HA3 = bytesToHexString(md5.digest());
//			}
//			catch(UnsupportedEncodingException e)
//			{
//				return null;
//			}
//			StringBuilder sb = new StringBuilder(128);
//			sb.append("Digest ");
//			sb.append("username").append("=\"").append(username).append("\",");
//			sb.append("realm").append("=\"").append(authFields.get("realm")).append("\",");
//			sb.append("nonce").append("=\"").append(authFields.get("nonce")).append("\",");
//			sb.append("uri").append("=\"").append(input.getURL().getPath()).append("\",");
//			// sb.append("qop" ).append('=' ).append("auth" ).append(",");
//			sb.append("response").append("=\"").append(HA3).append("\"");
//			try
//			{
//				final HttpURLConnection result = (HttpURLConnection)input.getURL().openConnection();
//				result.addRequestProperty("Authorization", sb.toString());
//				return result;
//			}
//			catch(IOException e)
//			{
//				return null;
//			}
//		}
//
//		private static HashMap<String, String> splitAuthFields(String authString)
//		{
//			final HashMap<String, String> fields = Maps.newHashMap();
//			final CharMatcher trimmer = CharMatcher.anyOf("\"\t ");
//			final Splitter commas = Splitter.on(',').trimResults().omitEmptyStrings();
//			final Splitter equals = Splitter.on('=').trimResults(trimmer).limit(2);
//			String[] valuePair;
//			for(String keyPair : commas.split(authString))
//			{
//				valuePair = Iterables.toArray(equals.split(keyPair), String.class);
//				fields.put(valuePair[0], valuePair[1]);
//			}
//			return fields;
//		}
//
//		private static final String	HEX_LOOKUP	= "0123456789abcdef";
//
//		private static String bytesToHexString(byte[] bytes)
//		{
//			StringBuilder sb = new StringBuilder(bytes.length * 2);
//			for(int i = 0; i < bytes.length; i++)
//			{
//				sb.append(HEX_LOOKUP.charAt((bytes[i] & 0xF0) >> 4));
//				sb.append(HEX_LOOKUP.charAt((bytes[i] & 0x0F) >> 0));
//			}
//			return sb.toString();
//		}
//
//		public static class AuthenticationException extends IOException
//		{
//			private static final long	serialVersionUID	= 1L;
//
//			public AuthenticationException()
//			{
//				super("Problems authenticating");
//			}
//		}
//	}
}
