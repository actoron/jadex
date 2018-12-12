package jadex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Properties;
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

import jadex.commons.SUtil;
import jadex.commons.collection.LRU;

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
 *  /(displayInfo)				: Display info
 *  /displayUsers				: Show the current users
 *  /displayMappings			: Show the current mappings in html
 *  
 *  /addMapping?name=a&target=b : Add a new mapping
 *  /removeMapping?name=a       : Remove an existing mapping
 *  /refreshMapping?name=a      : Update time stamp of mapping
 *  /getLeasetime				: Get the lease time
 *  /setLeasetime?leasetime=a	: Set the lease time	
 *  /login?user=a&pass=b		: Login	
 *  /logout						: Logout
 *  /addUser?user=a&pass=b		: Add a new user
 *  /removeUser?user=a&pass=b	: Remove an existing user
 *  
 *  Parameters in filter init:
 *  
 *  leasetime: [mins], default 5 mins   	leasetime for refreshs
 *  https: true/false default=false         must use https? 
 *  authentication: true/false default=true must service calls be authenticated?
 *  adminpass: default="admin"				admin password
 *  httpsport: default=8443					https port
 *  filepath: default=tomcat home			directory for storing files
 */
public class ForwardFilter implements Filter
{
	/** The mapping infos. */
	protected static Map<String, ForwardInfo> infos = Collections.synchronizedMap(new LinkedHashMap<String, ForwardInfo>());
		
	/** Supported commands. */
	public static final String addmapping = "addMapping";
	public static final String remmapping = "removeMapping";
	public static final String refreshmapping = "refreshMapping";
	public static final String getleasetime = "getLeasetime";
	public static final String setleasetime = "setLeasetime";
	public static final String login = "login";
	public static final String logout = "logout";
	public static final String adduser = "addUser";
	public static final String remuser = "removeUser";
	
	public static final String displayinfo = "displayInfo";
	public static final String displayusers = "displayUsers";
	public static final String displaymappings = "displayMappings";
		
	public static final String authenticated = "authenticated";
	
	public static final Set<String> commands = Collections.synchronizedSet(new HashSet<String>());

//	protected static String stylecss;
	
	//-------- attributes --------
	
	/** Flag if authentication is required. */
	protected boolean authentication;
	
	/** Flag if https should be enforced. */
	protected boolean https;
	
	/** The https port of the server. */
	protected String httpsport;
	
	/** The lease time in millis. */
	protected long leasetime;
	
	/** The file path. */
	protected String filepath;
	
	/** The emitted nonces. */
	protected LRU<String, String> nonces;
	
	/** The known users and passwords. */
	protected Map<String, String> users = Collections.synchronizedMap(new LinkedHashMap<String, String>());
	
	static
	{
		commands.add(addmapping);
		commands.add(remmapping);
		commands.add(refreshmapping);
		commands.add(getleasetime);
		commands.add(setleasetime);
		commands.add(login);
		commands.add(logout);
		commands.add(adduser);
		commands.add(remuser);

		commands.add(displayinfo);
		commands.add(displayusers);
		commands.add(displaymappings);
		
//		Scanner sc = null;
//		try
//		{
//			InputStream is = SUtil.getResource0("jadex/style.css", 
//				Thread.currentThread().getContextClassLoader());
//			sc = new Scanner(is);
//			stylecss = sc.useDelimiter("\\A").next();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//		finally
//		{
//			if(sc!=null)
//			{
//				sc.close();
//			}
//		}
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
		
		val = conf.getInitParameter("authentication");
		if(val!=null)
		{
			authentication = Boolean.getBoolean(val);
		}
		else
		{
			authentication = true;
		}
		
		val = conf.getInitParameter("adminpass");
		users.put("admin", val!=null? val: "admin");
		
		val = conf.getInitParameter("httpsport");
		if(val!=null)
		{
			httpsport = val;
		}
		else
		{
			httpsport = "8443";
		}
		
		val = conf.getInitParameter("filepath");
		if(val!=null)
		{
			filepath = val;
		}
		else
		{
			filepath = System.getProperty("catalina.base");
		}
		
		readUsersFromFile();
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
			String requri = cutUrl(req.getRequestURI().substring(req.getContextPath().length()));
			List<String> mimetypes = null;
			
			if(commands.contains(requri))
			{
				removeDueMappings();
				
				mimetypes = parseMimetypes(req);
				
				boolean userops = displayusers.equals(requri) || adduser.equals(requri) || remuser.equals(requri);
				
				// check if https is used or security critical operations 
				if(https || userops)
				{
					fini = checkSecure(req, res); 
				}
				
				boolean neednoauth = login.equals(requri) || displayinfo.equals(requri);
				
				// check if user has logged in
				if(!fini && authentication && !neednoauth) 
				{
					fini = checkAuthentication(req, res, mimetypes, userops? "admin": null);
				}
			}

			if(!fini)
			{
				// redirect to info
				if(requri.length()==0)// && req.getContextPath().length()>0)
				{
					res.sendRedirect("displayInfo");
					fini = true;
				}
				if(displayinfo.equals(requri))
				{
					sendDisplayInfo(req,res);
					fini = true;
				}
				else if(displayusers.equals(requri))
				{
					sendDisplayUsers(req, res);
					fini = true;
				}
				else if(adduser.equals(requri))
				{
					String user = request.getParameter("user");
					String pass = request.getParameter("pass");
					if(user!=null && pass!=null)
					{
						addUser(user, pass);
					}
					if(isBrowserClient(mimetypes))
					{
						res.sendRedirect("displayUsers");
					}
					fini = true;
				}
				else if(remuser.equals(requri))
				{
					String user = request.getParameter("user");
					removeUser(user);
					if(isBrowserClient(mimetypes))
					{
						res.sendRedirect("displayUsers");
					}
					fini = true;
				}
				else if(login.equals(requri))
				{
					String auth = req.getHeader("Authorization");
					if(auth!=null)
					{			
						fini = checkDigestAuthentication(req, res, mimetypes);
					}
					else
					{
						checkUrlParameterAuthentication(req, res, mimetypes);
						fini = true;
					}
				}
				else if(logout.equals(requri))
				{
					HttpSession sess = req.getSession(false);
					if(sess!=null)
					{
						sess.removeAttribute(authenticated);
					}
					if(isBrowserClient(mimetypes))
					{
						res.sendRedirect("");
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
					else
					{
						res.setContentType("application/json");
						res.getWriter().write("{\"leasetime\":"+getLeasetime()/1000/60+"}");
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
						res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mapping not found: "+apppath);
					}
					fini = true;
				}
				else if(displaymappings.equals(requri))
				{
					sendDisplayMappings(req, res);
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
							res.getWriter().write("{\"leasetime\":"+fi.getTime()+getLeasetime()+"}");
						}
						else
						{
							res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mapping not found: "+apppath);
						}
					}
					else
					{
						res.getWriter().write("{\"leasetime\":"+getLeasetime()+"}");
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

							if(!req.getRequestURI().endsWith("/") && cutUrl(requri).equals(cutUrl(fi.getAppPath())))
							{
								String redir = requri+"/";
								if(req.getQueryString()!=null)
									redir += "?"+req.getQueryString();
								res.sendRedirect(redir);
								fini = true;
							}
							else
							{
								// Cannot use request dispatcher as it only allows for sending server internal forwards :-((
								sendForward(fi, req, res);
								fini = true;
							}
							break;
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
	public String cutUrl(String url)
	{
		String ret = url;
		if(ret.startsWith("/"))
			ret = ret.substring(1);
		if(ret.endsWith("/"))
			ret = ret.substring(0, ret.length()-1);
		return ret;
	}
	
	/**
	 * 
	 */
	protected List<String> parseMimetypes(HttpServletRequest request)
	{
		List<String> mimetypes = null;
		String mts = request.getHeader("Accept");
		if(mts!=null)
		{
			mimetypes = new ArrayList<String>();
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
		return mimetypes;
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
			int port = request.getServerPort();
			url = url.replaceFirst(""+port, httpsport);
			if(request.getQueryString()!=null)
			{
				url += "?"+request.getQueryString();
			}
			
			response.sendRedirect(url);
			fini = true;
		}
		return fini;
	}
	
	/**
	 * 
	 */
	protected boolean checkAuthentication(HttpServletRequest request, HttpServletResponse response, List<String> mimetypes, String username) throws IOException
	{
		boolean fini = false;
		String user = isAuthenticated(request, response);
		if(user==null || (username!=null && !user.equals(username)))
		{
			fini = true;
			String next = request.getRequestURI();
			if(isBrowserClient(mimetypes))
			{
				if(request.getQueryString()==null)
				{
					response.sendRedirect("login?next="+next);
				}
				else
				{
					response.sendRedirect("login?next="+next+"?"+request.getQueryString());
				}
			}
			else
			{
				sendAuthorizationRequest(request.getServerName(), response);
			}
		}
		return fini;
	}
	
	/**
	 * 
	 */
	protected boolean checkDigestAuthentication(HttpServletRequest request, HttpServletResponse response, 
		Collection<String> mimetypes) throws IOException
	{
		boolean ret = true;
		
		String auth = request.getHeader("Authorization");
		if(auth.startsWith("Digest"))
		{
			Map<String, String> vals = parseHeader(auth);
			
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
					
					if(qop!=null && qop.equals("auth-int"))
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
						session.setAttribute(authenticated, user);
						
						if(isBrowserClient(mimetypes))
						{
							// todo: redirect to next
							response.sendRedirect("displayMappings");
						}
//						else
//						{
//							ret = false;
//						}
					}
				}
				else
				{
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User unknown.");
				}
			}
			else
			{
				sendAuthorizationRequest(request.getServerName(), response);
			}
		}
		else
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Only digest authentication supported.");
		}
		
		return ret;
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
					sendLoginPage(request, response, next);
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
				session.setAttribute(authenticated, user);
				
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
	protected String isAuthenticated(HttpServletRequest request, HttpServletResponse response)
	{
		String ret = null;
		HttpSession session = request.getSession(false);
		if(session!=null)
		{
			ret = (String)session.getAttribute(authenticated);
		}
		return ret;
	}
	
	/**
	 *  Remove all mappings with too old timestamps.
	 */
	protected void removeDueMappings()
	{
		if(getLeasetime()>0)
		{
			long now = System.currentTimeMillis();
			for(ForwardInfo fi: getForwardInfos())
			{
				if(now>fi.getTime()+getLeasetime())
				{
					infos.remove(fi.getAppPath());
				}
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
	 *  Get the lease time.
	 *  @return The lease time.
	 */
	public long getLeasetime()
	{
		return leasetime;
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
	protected Map.Entry<String, String>[] getUsers()
	{
		return users.entrySet().toArray(new Map.Entry[users.size()]);
	}
	
	/**
	 * 
	 */
	protected void sendForward(ForwardInfo fi, HttpServletRequest request, HttpServletResponse response)
	{
		// forward to other server
		StringBuffer buf = new StringBuffer();
		buf.append(fi.getForwardPath());
		String url = cutUrl(request.getRequestURI().substring(request.getContextPath().length()));
		if(url.length()>fi.getAppPath().length())
		{
			String add = url.substring(fi.getAppPath().length());
			boolean sep = fi.getForwardPath().endsWith("/") || add.startsWith("/");
			buf.append(sep? add: "/"+add);
		}
		if(request.getQueryString()!=null)
		{
			buf.append("?").append(request.getQueryString());
		}
		String fowurl = buf.toString();
		
		HttpURLConnection con = null;
		try 
		{
			// Open connection with copied header
			URL urlc = new URL(fowurl);
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
				SUtil.copyStream(request.getInputStream(), con.getOutputStream());
				con.getOutputStream().flush();
			}
			
			// URL rewriting needed
			// The internal absolute urls point to the internal server 
			// and must be rewritten to point on the external one
			
		    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    // Replace content if is html with possibly wrong links
		    
		    Map<String,List<String>> hd = con.getHeaderFields();
		    if(hd!=null)
		    {
		    	for(Map.Entry<String, List<String>> entry: hd.entrySet())
		    	{
		    		if(!"Content-Length".equals(entry.getKey()))
	    			{
			    		for(String val: entry.getValue())
			    		{
			    			response.addHeader(entry.getKey(), val);
			    		}
	    			}
		    	}
		    }
		    response.setContentType(con.getContentType());
		    
		    if(con.getContentType()==null || con.getContentType().toLowerCase().indexOf("text/html")!=-1
		    	|| con.getContentType().toLowerCase().indexOf("text/css")!=-1)
		    {
			    String line;
//			    String internal = urlc.getProtocol()+"://"+urlc.getHost()+":"+urlc.getPort(); 
			    String internal = fi.getForwardPath();
			    String external = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
			    String wppath = request.getContextPath();
			    if(wppath.length()>0)
			    {
			    	boolean sep = wppath.startsWith("/") || external.endsWith("/");
			    	external += sep? wppath: "/"+wppath;
			    }
			    String appath = fi.getAppPath();
			    if(appath.length()>0)
			    {
			    	boolean sep = appath.startsWith("/") || external.endsWith("/");
			    	external += sep? appath: "/"+appath;
			    }
//			    String fapp = cutUrl(urlc.getPath());
			    URL iurl = new URL(fi.getForwardPath());
			    String fapp = cutUrl(iurl.getPath());
			    while((line = rd.readLine()) != null) 
			    { 
			    	// Replace absolute links, i.e. http://www.myserver.com
			        String rep = line.replace(internal, external);
			        // Replace document root related links to external ones
			        rep = rep.replace("href=\"/"+fapp, "href=\""+external);
			        rep = rep.replace("href='/"+fapp, "href='"+external);
			        rep = rep.replace("src=\"/"+fapp, "src=\""+external);
			        rep = rep.replace("src='/"+fapp, "src='"+external);
			        rep = rep.replace("@import \"/"+fapp, "@import \""+external);
			        rep = rep.replace("url(/"+fapp, "url("+external);
			        rep = rep.replace("url('/"+fapp, "url('"+external);
			        rep = rep.replace("url(\"/"+fapp, "url(\""+external);
			        rep += "\n";
			        response.getOutputStream().write(rep.getBytes());
			    }
		    }
		    else
		    {
		    	// Copy without modifications
		    	SUtil.copyStream(con.getInputStream(), response.getOutputStream());
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
	protected void sendDisplayMappings(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head>\n");
//			pw.write(stylecss);
			pw.write("</head><body>\n");
			pw.write("<h1>Current Mappings</h1>\n");
			ForwardInfo[] fis = getForwardInfos();
			if(fis.length==0)
			{
				pw.write("No mappings available.");
			}
			else
			{
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				pw.write("<table cellspacing=\"0\">\n");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Local Name</th>\n");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Remote Address</th>\n");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Timestamp</th>\n");
				pw.write("<th style=\"border-bottom:solid 1px black; padding:10px 10px\">Actions</th>\n");
				for(ForwardInfo fi: fis)
				{
					pw.write("<tr>\n");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+fi.getAppPath()+"</td>\n");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+fi.getForwardPath()+"</td>\n");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+df.format(new Date(fi.getTime()))+"</td>\n");
					pw.write("<td style=\"padding:0px 10px\">\n");
					pw.write("<a href=\"removeMapping?name="+fi.getAppPath()+"\">Remove</a>\n");
					pw.write("  ");
					pw.write("<a href=\"refreshMapping?name="+fi.getAppPath()+"\">Refresh</a>\n");
					pw.write("</td>\n");
					pw.write("</tr>\n");
				}
				pw.write("</table>\n");
			}
				
			pw.write("<h2>Add a Mapping</h2>\n");
			pw.write("<form name=\"input\" action=\"addMapping\" method=\"get\">\n");
			pw.write("<table cellspacing=\"0\">\n");
			pw.write("<tr><td>Application name:</td><td><input type=\"text\" name=\"name\"/></td></tr>\n");
			pw.write("<tr><td>Remote server address:</td><td><input type=\"text\" name=\"target\"/></td></tr>\n");
			pw.write("<tr><td><input type=\"submit\" value=\"Add\"/></td></tr>\n");
			pw.write("</table>\n");
			pw.write("</form>\n");
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			pw.write("<h2>Leasetime</h2>\n");
//			pw.write("Leasetime [mins]: ");
			String lt = getLeasetime()==0? "0": ""+(getLeasetime()/1000/60);
//			pw.write(lt);
//			pw.write(sdf.format(new Date(leasetime-TimeZone.getDefault().getRawOffset())));
			pw.write("<form name=\"input\" action=\"setLeasetime\" method=\"get\">\n");
			pw.write("<table cellspacing=\"0\">\n");
			pw.write("<tr><td>Leasetime [mins]:</td><td><input type=\"text\" name=\"leasetime\" value=\""+lt+"\"/></td></tr>\n");
			pw.write("<tr><td><input type=\"submit\" value=\"Set\"/></td></tr>\n");
			pw.write("</table>\n");
			pw.write("</form>\n");
			
			pw.write("<body></html>\n");
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
	protected void sendLoginPage(HttpServletRequest request, HttpServletResponse response, String next)
	{
		try
		{
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head>\n");
//			pw.write(stylecss);
			pw.write("</head><body>\n");
			pw.write("<h1>Login</h1>\n");
			
			pw.write("<form name=\"input\" action=\"login\" method=\"get\">\n");
			pw.write("<table cellspacing=\"0\">\n");
			pw.write("<tr><td>User name:</td><td><input type=\"text\" name=\"user\"/></td></tr>\n");
			pw.write("<tr><td>Password:</td><td><input type=\"text\" name=\"pass\"/></td></tr>\n");
			pw.write("<input type=\"hidden\" name=\"next\"/ value=\""+next+"\">\n");
			pw.write("<tr><td><input type=\"submit\" value=\"Login\"/></td></tr>\n");
			pw.write("</table>\n");
			pw.write("</form>\n");
			
			pw.write("</body></html>\n");
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
	protected void sendDisplayUsers(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head>\n");
//			pw.write(stylecss);
			pw.write("</head><body>\n");
			pw.write("<h1>Current Users</h1>\n");
			if(users.isEmpty())
			{
				pw.write("No users available.");
			}
			else
			{
				pw.write("<table cellspacing=\"0\">\n");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">User Name</th>\n");
				pw.write("<th style=\"border-right:solid 1px black; border-bottom:solid 1px black; padding:10px 10px\">Password</th>\n");
				pw.write("<th style=\"border-bottom:solid 1px black; padding:10px 10px\">Actions</th>\n");
				Map.Entry<String, String>[] entries = users.entrySet().toArray(new Map.Entry[users.size()]);
				for(Map.Entry<String, String> user: entries)
				{
					pw.write("<tr>\n");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+user.getKey()+"</td>\n");
					pw.write("<td style=\"border-right:solid 1px black; padding:0px 10px\">"+user.getValue()+"</td>\n");
					pw.write("<td style=\"padding:0px 10px\">\n");
					if(!user.getKey().equals("admin"))
					{
						pw.write("<form name=\"input\" action=\"addUser\" method=\"get\">\n");
						pw.write("<a href=\"removeUser?user="+user.getKey()+"\">Remove</a>\n");
						pw.write(" ");
						pw.write("<input type=\"hidden\" name=\"user\"/ value=\""+user.getKey()+"\"/>\n");
						pw.write("<input type=\"text\" name=\"pass\"/>\n");
						pw.write("<input type=\"submit\" value=\"Change Pass\"/>\n");
						pw.write("</form>");
					}
					pw.write("</td>\n");
					pw.write("</tr>\n");
				}
				pw.write("</table>\n");
			}
				
			pw.write("<h2>Add a User</h2>\n");
			pw.write("<form name=\"input\" action=\"addUser\" method=\"get\">\n");
			pw.write("<table cellspacing=\"0\">\n");
			pw.write("<tr><td>User name:</td><td><input type=\"text\" name=\"user\"/></td></tr>\n");
			pw.write("<tr><td>User password:</td><td><input type=\"text\" name=\"pass\"/></td></tr>\n");
			pw.write("<tr><td><input type=\"submit\" value=\"Add\"/></td></tr>\n");
			pw.write("</table>\n");
			pw.write("</form>\n");
			
			pw.write("</body></html>\n");
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
	protected void sendDisplayInfo(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			pw.write("<html><head>\n");
//			pw.write(stylecss);
			pw.write("</head><body>\n");
			pw.write("<h1>Web Proxy Menu</h1>\n");
			
			pw.write("<a href=\"displayUsers\">Manage Users</a>\n");
			pw.write("<br/>\n");
			pw.write("<a href=\"displayMappings\">Manage Mappings</a>\n");
			
			pw.write("</body></html>\n");
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
	 * 
	 */
	protected boolean isBrowserClient(Collection<String> mimetypes)
	{
		return mimetypes==null || mimetypes.contains("text/html") || mimetypes.contains("*/*");
	}
	
	/**
	 *  Convert header to key value pairs.
	 */
	protected Map<String, String> parseHeader(String header)
	{
		String h = header.substring(header.indexOf(" ")+1).trim();
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
		return hex(digest((fmtDate+randomInt.toString()).getBytes()));
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
	 * Returns the request body as string.
	 */
	protected String readRequestBody(HttpServletRequest request) throws IOException
	{
		StringBuilder buf = new StringBuilder();
		BufferedReader reader = null;
		try
		{
			InputStream is = request.getInputStream();
			if(is!=null)
			{
				reader = new BufferedReader(new InputStreamReader(is));
				char[] charBuffer = new char[256];
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
				}
			}
		}
		return buf.toString();
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
	protected void addUser(String user, String pass)
	{
		users.put(user, pass);
		saveUserstoFile();
	}
	
	/**
	 * 
	 */
	protected void removeUser(String user)
	{
		users.remove(user);
		saveUserstoFile();
	}
	
	/**
	 * 
	 */
	protected void saveUserstoFile()
	{
		synchronized(users)
		{
			FileOutputStream fos = null;
			try
			{
				File f = new File(filepath+File.separator+"users.txt");
				fos = new FileOutputStream(f);
				Properties p = new Properties();
				p.putAll(users);
				p.store(fos, null);
			}
			catch(Exception e)
			{
				System.out.println("Could not access users.txt file.");
			}
			finally
			{
				if(fos!=null)
				{
					try
					{
						fos.close();
					}
					catch(IOException e)
					{
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected void readUsersFromFile()
	{
		synchronized(users)
		{
			FileInputStream fis = null;
			try
			{
				File f = new File(filepath+File.separator+"users.txt");
				fis = new FileInputStream(f);
				Properties p = new Properties();
				p.load(fis);
				users.clear();
				for(Map.Entry<Object, Object> entry: p.entrySet())
				{
					users.put((String)entry.getKey(), (String)entry.getValue());
				}
			}
			catch(Exception e)
			{
				System.out.println("Could not read user file.");
			}
			finally
			{
				if(fis!=null)
				{
					try
					{
						fis.close();
					}
					catch(IOException e)
					{
					}
				}
			}
		}
	}
	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args)
	{
//		Map<String, String> m = new HashMap<String, String>();
//		m.put("a", "a");
//		m.put("b", "b");
//		JSONObject js = new JSONObject(m);
//		System.out.println(js);
//		
//		String o2 = "{\"movielist\": [\"Friday the 13th\", \"Friday the 13th Part 2\", \"Friday the 13th Part III\", \"Friday the 13th: The Final Chapter\", \"Friday the 13th: A New Beginning\"]}";
//		Object o = JSONValue.parse(o2);
//		o = JSONValue.parse("{\"name\":\"hans\"}");
//		System.out.println("o: "+o);
		
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
