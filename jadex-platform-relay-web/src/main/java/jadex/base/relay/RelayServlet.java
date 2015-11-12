package jadex.base.relay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			
			// ..or disable platform connection
			if(handler.getSettings().isNoConnections())
			{
				// Set content length to avoid error page being sent.
				response.setStatus(403);
				response.setContentLength(0);
			}
		}
		else if(request.getServletPath().startsWith("/resources") || request.getServletPath().equals("/robots.txt"))
		{
			// serve images etc. (hack? url mapping doesn't support excludes and we want the relay servlet to react to the wepapp root url.
			serveResource(request, response);
		}
		else if(request.getServletPath().startsWith("/map"))
		{
			// serve maps from google.
			serveMap(request, response);
		}
		else
		{
			String	id	= request.getParameter("id");
			// Render status page.
			if(id==null)
			{
				String	view	= null;
				// todo: add request property
				if("/history".equals(request.getServletPath()) && handler.getStatisticsDB()!=null)
				{
					int	cnt	= -1;	// 20
					try
					{
						cnt	= Integer.parseInt(request.getParameter("cnt"));
					}
					catch(RuntimeException e)
					{
					}
					request.setAttribute("platforms", handler.getStatisticsDB().getPlatformInfos(cnt));
					view	= "/WEB-INF/jsp/history.jsp";
				}
				else if("/history.json".equals(request.getServletPath()) && handler.getStatisticsDB()!=null)
				{
					int	cnt	= -1;
					try
					{
						cnt	= Integer.parseInt(request.getParameter("cnt"));
					}
					catch(RuntimeException e)
					{
					}
					
					handler.getStatisticsDB().writePlatformInfos(response.getOutputStream(), cnt);
				}
				else if("/history_all".equals(request.getServletPath()) && handler.getStatisticsDB()!=null)
				{
					request.setAttribute("platforms", handler.getStatisticsDB().getPlatformInfos(-1));
					view	= "/WEB-INF/jsp/history.jsp";
				}
				else if("/export".equals(request.getServletPath()) && handler.getStatisticsDB()!=null)
				{
					// Todo: properties
					request.setAttribute("platforms", handler.getStatisticsDB().getAllPlatformInfos(false));
					view	= "/WEB-INF/jsp/csv.jsp";
				}
				else if("/sync".equals(request.getServletPath()))
				{
					String	peerid	= request.getParameter("peerid");
					String	sstartid	= request.getParameter("startid");
					String	scnt	= request.getParameter("cnt");
					int	startid	= -1;
					int	cnt	= -1;
					try
					{
						startid	= Integer.valueOf(sstartid);
						cnt	= Integer.valueOf(scnt);
						response.setHeader("Content-Disposition", "attachment; filename=relay_dbentries.ser");
						handler.handleSyncRequest(peerid, startid, cnt, response.getOutputStream());
					}
					catch(Exception e)
					{
						e.printStackTrace();
						// Set content length to avoid error page being sent.
						response.setStatus(400);
						response.setContentLength(0);
					}
				}
				else if("/servers".equals(request.getServletPath()))
				{
					String	requesturl	= request.getRequestURL().toString();
					String	peerurl	= request.getParameter("peerurl");
					String	peerid	= request.getParameter("peerid");
					String	sdbstate	= request.getParameter("peerstate");
					String	initial	= request.getParameter("initial");
					
					int	dbstate	= -1;
					if(sdbstate!=null)
					{
						try
						{
							dbstate	= Integer.valueOf(sdbstate);
						}
						catch(NumberFormatException e)
						{
						}
					}
					
					String	serverurls	= handler.handleServersRequest(requesturl, peerurl, peerid, dbstate, "true".equals(initial));
					request.setAttribute("peers", serverurls);					
					view	= "/WEB-INF/jsp/servers.jsp";
				}
				else
				{
					List<PlatformInfo>	infos	= new ArrayList<PlatformInfo>();
					infos.addAll(Arrays.asList(handler.getCurrentPlatforms()));
					request.setAttribute("platforms", infos.toArray(new PlatformInfo[0]));
					request.setAttribute("peers", handler.getCurrentPeers());
					request.setAttribute("url", handler.getSettings().isUrlSpecified() ? handler.getSettings().getUrl() : request.getRequestURL().toString());
					request.setAttribute("refresh", "30");
					view	= "/WEB-INF/jsp/status.jsp";
				}
				
				if(view!=null)
				{
					RequestDispatcher	rd	= getServletContext().getRequestDispatcher(view);
					rd.forward(request, response);
				}
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
//		System.out.println("post: "+request.getServletPath()+", "+request.getRemoteHost());
		
//		String	s;
//		s	= request.getContextPath();
//		s	= request.getPathInfo();
//		s	= request.getQueryString();
//		s	= request.getServletPath();
//		s	= request.getRequestURL().toString();
		try
		{
			if(request.getServletPath().startsWith("/awareness"))
			{
				handler.handleAwareness(request.getInputStream());
			}
			else if(request.getServletPath().startsWith("/offline"))
			{
				handler.handleOffline(request.getRemoteAddr(), request.getInputStream());
			}
			else if(request.getServletPath().startsWith("/platforminfos"))
			{
				handler.handlePlatforms(request.getInputStream());
			}
			else if(request.getServletPath().startsWith("/platforminfo"))
			{
				handler.handlePlatform(request.getInputStream());
			}
			else
			{
				handler.handleMessage(request.getInputStream(), request.getScheme());
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			// Set content length to avoid error page being sent.
			response.setStatus(404);
			response.setContentLength(0);
		}
	}
	
	//-------- helper methods --------
	
	/** Time in seconds before browsers should refresh static resources (7 days). */
	protected static final long	MAXAGE	= 60*60*24*7;
	
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
        	else	// see e.g. https://github.com/bertramdev/karman/issues/3
        	{
//                css: 'text/css',
//                gz: 'application/x-compressed',
//                js: 'application/javascript',
//                pdf: 'application/pdf',
//                eot: 'application/vnd.ms-fontobject',
//                otf: 'font/opentype',
//                svg: 'image/svg+xml',
//                ttf: 'application/x-font-ttf',
//                woff: 'application/x-font-woff'
        		if(file.getName().toLowerCase().endsWith(".css"))
        		{
        			response.setContentType("text/css");
        		}
        		else if(file.getName().toLowerCase().endsWith(".js"))
            	{
            		response.setContentType("application/javascript");
            	}
        	}
        	
			// Copy file content to output stream.
        	FileInputStream	in	= null;
        	try
        	{
	        	in	= new FileInputStream(file);
				byte[]	buf	= new byte[8192];  
				int	len;
				while((len=in.read(buf)) != -1)
				{
					response.getOutputStream().write(buf, 0, len);
				}
        	}
        	finally
        	{
        		if(in!=null)
        		{
        			in.close();
        		}
        	}
        }
	}

	/**
	 *  Serve a map from google
	 */
	protected void serveMap(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String[]	colors	= new String[]{"black", "brown", "green", "purple", "yellow", "blue", "gray", "orange", "red", "white"};
		StringBuffer markers	= new StringBuffer();
		Set<String> positions	= new HashSet<String>();
		int	cnt	= 0;
		
		// Add markers for locally connected platforms
		List<PlatformInfo>	pinfos	= new ArrayList<PlatformInfo>();
		pinfos.addAll(Arrays.asList(handler.getCurrentPlatforms()));
		PlatformInfo[]	infos	= pinfos.toArray(new PlatformInfo[0]);
		cnt = addMarkers(infos, markers, colors, positions, cnt);

		// Add markers for remotely connected platforms
		PeerHandler[]	peers	= handler.getCurrentPeers();
		if(peers.length>0)
		{
			for(int j=0; j<peers.length && markers.length()+250<2048; j++)	// hack!!! make sure url length stays below 2048 character limit. 
			{
				PlatformInfo[]	infos2	= peers[j].getPlatformInfos();
				cnt	= addMarkers(infos2, markers, colors, positions, cnt);
			}
		}

		// Use scale=2 for larger pictures in spite of 640 pixel limitS
		int	width	= Integer.valueOf(request.getParameter("width"))/2;
		int	height	= Integer.valueOf(request.getParameter("height"))/2;
		String	url	= "https://maps.googleapis.com/maps/api/staticmap?scale=2&size="+width+"x"+height+"&sensor=false"+markers;
		
		// Copy content to output stream. (against google policies?)
//		HttpURLConnection	con	= (HttpURLConnection)new URL(url).openConnection();
//    	response.setContentType(con.getContentType());
//    	response.setContentLength(con.getContentLength());
//    	InputStream	in	= con.getInputStream();
//		byte[]	buf	= new byte[8192];  
//		int	len;
//		while((len=in.read(buf)) != -1)
//		{
//			response.getOutputStream().write(buf, 0, len);
//		}
//		in.close();
		
		// Redirect (allowed by google?)
		response.sendRedirect(url);
	}
	
	/**
	 *  Add markers for given platform infos.
	 */
	protected int addMarkers(PlatformInfo[] infos, StringBuffer markers, String[] colors, Set<String> positions, int cnt)
	{
		if(infos.length>0)
		{
			for(int i=0; i<infos.length && markers.length()+250<2048; i++)	// hack!!! make sure url length stays below 2048 character limit. 
			{
				if(infos[i].getPosition()!=null && !positions.contains(infos[i].getPosition()))
				{
					if(cnt<9)
					{
						// Add labeled markers for first 1..9 entries
						markers.append("&markers=size:mid|label:");
						markers.append(cnt+1);
						markers.append("|color:");
						markers.append(colors[Math.abs(infos[i].getId().hashCode())%colors.length]);
						markers.append("|");
						markers.append(infos[i].getPosition());
						positions.add(infos[i].getPosition());
					}
					else if(cnt==9)
					{
						// Add unlabeled markers for each unique position of remaining entries
						markers.append("&markers=size:mid|color:");
						markers.append(colors[Math.abs(infos[i].getId().hashCode())%colors.length]);
						markers.append("|");
						markers.append(infos[i].getPosition());
						positions.add(infos[i].getPosition());
					}
					else
					{
						// Add unlabeled markers for each unique position of remaining entries
						markers.append("|");
						markers.append(infos[i].getPosition());
						positions.add(infos[i].getPosition());
					}
					cnt++;
				}
			}
		}
		return cnt;
	}
}
