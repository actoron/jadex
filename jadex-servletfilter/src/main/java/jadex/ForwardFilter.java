package jadex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class ForwardFilter implements Filter
{
	protected static Set<ForwardInfo> infos = Collections.synchronizedSet(new TreeSet<ForwardInfo>(new Comparator<ForwardInfo>()
	{
		public int compare(ForwardInfo o1, ForwardInfo o2) 
		{
			return o1.getTime()<o2.getTime()? -1: o1.getTime()>o2.getTime()? 1: o1.hashCode()-o2.hashCode();
		}
	}));
	
	static
	{
		infos.add(new ForwardInfo("/banking1", "http://0.0.0.0:8081/banking1"));
	}
	
	/**
	 * 
	 */
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}
	
	/**
	 * 
	 */
	public void destroy()
	{
	}
	
	/**
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		boolean forwarded = false;
		
		if(request instanceof HttpServletRequest)
		{
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse res = (HttpServletResponse)response;
//			String path = req.getRequestURI().substring(req.getContextPath().length());
			
			String requri = req.getRequestURI();
			
			for(ForwardInfo fi: getForwardInfos())
			{
				if(requri.startsWith(fi.getAppPath()))
				{
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
					forwarded = true;
				}
			}
		}
		
		if(!forwarded)
		{
			chain.doFilter(request, response); // Goes to default servlet.
		}
	}
	
	/**
	 * 
	 */
	protected ForwardInfo[] getForwardInfos()
	{
		return infos.toArray(new ForwardInfo[0]);
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
			
			// Transfer back to client
			copyStream(con.getInputStream(), response.getOutputStream());
			response.getOutputStream().flush();
		} 
		catch(Exception e) 
		{
			// todo: handle output in error case
		    System.out.println("Exception: " + e);
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
			this.forwardpath = forwardpath;
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
}
