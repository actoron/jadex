package jadex.base.relay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  The relay servlet allows distributing Jadex messages through firewall/NAT settings
 *  by acting as a central public relay.
 */
public class RelayServlet extends HttpServlet
{
	//-------- attributes --------
	
	/** The relay handler. */
	protected RelayHandler	handler;
	
	//-------- constructors --------

	/**
	 *  Initialize the servlet.
	 */
	public void init() throws ServletException
	{
		this.handler	= new RelayHandler();
	}
	
	/**
	 *  Cleanup on servlet shutdown.
	 */
	public void destroy()
	{
		this.handler.dispose();
	}
	
	//-------- methods --------
	
	/**
	 *  Provide last modified time for static content
	 *  to aid browser caches.
	 */
	protected long getLastModified(HttpServletRequest request)
	{
		long	ret;
		if(request.getServletPath().startsWith("/resources"))
		{
			ret	= new File(getServletContext().getRealPath(request.getServletPath())).lastModified();
		}
		else
		{
			ret	= super.getLastModified(request);
		}
		return ret;
	}
	
	/**
	 *  Called when a platform registers itself at the relay. 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if("/ping".equals(request.getServletPath()))
		{
			// somebody is checking, if the server is available, just return an empty http ok.
		}
		else if(request.getServletPath().startsWith("/resources"))
		{
			// serve images etc. (hack? url mapping doesn't support excludes and we want the relay servlet to react to the wepapp root url.
			serveResource(request, response);
		}
		else
		{
	
			String	id	= request.getParameter("id");
			// Render status page.
			if(id==null)
			{
				String	view;
				// todo: add request property
				if("/history".equals(request.getServletPath()))
				{
					request.setAttribute("platforms", StatsDB.getDB().getPlatformInfos(20));
					view	= "/WEB-INF/jsp/history.jsp";
				}
				else if("/history_all".equals(request.getServletPath()))
				{
					request.setAttribute("platforms", StatsDB.getDB().getPlatformInfos(-1));
					view	= "/WEB-INF/jsp/history.jsp";
				}
				else if("/export".equals(request.getServletPath()))
				{
					request.setAttribute("platforms", StatsDB.getDB().getAllPlatformInfos());
					view	= "/WEB-INF/jsp/csv.jsp";
				}
				else if("/servers".equals(request.getServletPath()))
				{
					String	peerurl	= request.getParameter("peerurl");
					String	requesturl	= request.getRequestURL().toString();
					String	serverurls	= handler.handleServersRequest(requesturl, peerurl);
					request.setAttribute("peers", serverurls);					
					view	= "/WEB-INF/jsp/servers.jsp";
				}
				else
				{
					// Fetch array to avoid concurrency problems
					request.setAttribute("platforms", handler.getCurrentPlatforms());
					request.setAttribute("refresh", "30");
					view	= "/WEB-INF/jsp/status.jsp";
				}
				RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
				rd.forward(request, response);
			}
			
			// Handle platform connection.
			else
			{
				// Set cache header to avoid interference of proxies (e.g. vodafone umts)
				response.setHeader("Cache-Control", "no-cache, no-transform");
				response.setHeader("Pragma", "no-cache");
				
				handler.initConnection(id, request.getRemoteAddr(), request.getRemoteHost(), request.getScheme());
				handler.handleConnection(id, response.getOutputStream());
			}
		}
	}

	/**
	 *  Called when a message should be sent.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("post: "+request.getServletPath()+", "+request.getRemoteHost());
		
//		String	s;
//		s	= request.getContextPath();
//		s	= request.getPathInfo();
//		s	= request.getQueryString();
//		s	= request.getServletPath();
//		s	= request.getRequestURL().toString();
		try
		{
			if("/awareness".equals(request.getServletPath()) || "/awareness/".equals(request.getServletPath()))	// new code always adds slash.
			{
				handler.handleAwareness(request.getInputStream());
			}
			else
			{
				handler.handleMessage(request.getInputStream(), request.getScheme());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// Set content length to avoid error page being sent.
			response.setStatus(404);
			response.setContentLength(0);
		}
	}
	
	//-------- helper methods --------
	
	/** Time in seconds before browsers should refresh static resources (7 days). */
	protected static long	MAXAGE	= 60*60*24*7;
	
	/**
	 *  Serve a static resource from the file system.
	 */
	protected void serveResource(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
        File	file	= new File(getServletContext().getRealPath(request.getServletPath()));
        if(!file.canRead())
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
        }
        else
        {
        	response.setContentLength((int)file.length());
            response.setDateHeader("Last-Modified", file.lastModified());
            response.setDateHeader("Expires", System.currentTimeMillis() + MAXAGE*1000);
            response.addHeader("Cache-Control", "max-age="+MAXAGE);
            String	mimetype	= URLConnection.guessContentTypeFromName(file.getName());
        	if(mimetype!=null)
        	{
        		response.setContentType(mimetype);
        	}
        	
			// Copy file content to output stream.
        	FileInputStream	in	= new FileInputStream(file);
			byte[]	buf	= new byte[8192];  
			int	len;
			while((len=in.read(buf)) != -1)
			{
				response.getOutputStream().write(buf, 0, len);
			}
			in.close();
        }
	}
}
