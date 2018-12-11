package jadex.extension.rs.publish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import fi.iki.elonen.NanoHTTPD.CookieHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

/**
 *  Wrapper of HttpServletRequest for nano.
 */
public class NanoHttpServletRequestWrapper implements HttpServletRequest
{
	private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
    private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEX, Pattern.CASE_INSENSITIVE);
	 
	/** The nano session. */
	protected IHTTPSession session;
	
	/** The request attributes. */
	protected Map<String, Object> attributes;
	
	/** The async context. */
	protected NanoAsyncContext context;
	
	
	private String getDetailFromContentHeader(String contentTypeHeader, Pattern pattern, String defaultValue, int group) 
	{
		Matcher matcher = pattern.matcher(contentTypeHeader);
		return matcher.find() ? matcher.group(group) : defaultValue;
	}
	
	/**
	 *  Create a new wrapper.
	 */
	public NanoHttpServletRequestWrapper(IHTTPSession session)
	{
		this.session = session;
	}
	
	public Object getAttribute(String name)
	{
		return attributes!=null? attributes.get(name): null;
	}
	    
	public Enumeration<String> getAttributeNames()
	{
		return attributes!=null? new Vector<String>(attributes.keySet()).elements(): null;
	}
	    
	public String getCharacterEncoding()
	{
		return getDetailFromContentHeader(session.getHeaders().get("content-type"), CHARSET_PATTERN, null, 2);
	}
	
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException
	{
		throw new UnsupportedOperationException();
	}
	
	public int getContentLength()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	    
	public long getContentLengthLong()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	    
	public String getContentType()
	{
		return session.getHeaders().get("content-type");
	}
	    
	public ServletInputStream getInputStream() throws IOException
	{
	   return new ServletInputStream()
	    {
	        public int read() throws IOException 
	        {
	        	return session.getInputStream().read();
	        }
	        
	        @Override
	        public boolean isFinished()
	        {
	        	throw new UnsupportedOperationException();
	        }
	        
	        @Override
	        public boolean isReady()
	        {
	        	throw new UnsupportedOperationException();
	        }
	        
	        @Override
	        public void setReadListener(ReadListener readListener)
	        {
	        	throw new UnsupportedOperationException();
	        }
	    };
	}
	     
	public String getParameter(String name)
	{
		 List<String> ret = session.getParameters().get(name);
		 return ret!=null? ret.get(0): null;
	}
	    
	public Enumeration<String> getParameterNames()
	{
		return session.getParameters()!=null? new Vector<String>(session.getParameters().keySet()).elements(): null;
	}
	        
	public String[] getParameterValues(String name)
	{
		List<String> ret = session.getParameters().get(name);
		return ret!=null? ret.toArray(new String[ret.size()]): null;
	}
	 
	public Map<String, String[]> getParameterMap()
	{
		Map<String, String[]> ret = null;
		Map<String, List<String>> ps = session.getParameters();
		if(ps!=null)
		{
			ret = new HashMap<>();
			for(Map.Entry<String, List<String>> e: ps.entrySet())
			{
				ret.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
			}
		}
		
		return ret;
	}
	    
	public String getProtocol()
	{
		// todo: version
		String host = session.getHeaders().get("host");
		return host.toLowerCase().contains("https")? "HTTPS": "HTTP";
	}
	    
	public String getScheme()
	{
		String host = session.getHeaders().get("host");
		return host.toLowerCase().contains("https")? "https": "http";
	}
	    
	public String getServerName()
	{
		String host = session.getHeaders().get("host");
		if(host!=null)
		{
			int idx = host.indexOf(":");
			if(idx!=-1)
				host = host.substring(0, idx);
		}
		return host;
	}
	    
	public int getServerPort()
	{
		return getLocalPort();
	}
	    
	public BufferedReader getReader() throws IOException
	{
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}
	    
	public String getRemoteAddr()
	{
		return session.getRemoteIpAddress();
	}
	    
	public String getRemoteHost()
	{
		return session.getRemoteHostName();
	}
	    
	public void setAttribute(String name, Object o)
	{
		if(attributes==null)
			attributes = new HashMap<>();
		attributes.put(name, o);
	}
	    
	public void removeAttribute(String name)
	{
		if(attributes!=null)
			attributes.remove(name);
	}
	    
	public Locale getLocale()
	{
		throw new UnsupportedOperationException();
	}
	    
	public Enumeration<Locale> getLocales()
	{
		throw new UnsupportedOperationException();
	}
	    
	public boolean isSecure()
	{
		return session.getUri().indexOf("https")!=-1;
	}
	    
	public RequestDispatcher getRequestDispatcher(String path)
	{
		throw new UnsupportedOperationException();
	}
	    
	public String getRealPath(String path)
	{
		throw new UnsupportedOperationException();
	}
	    
	public int getRemotePort()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	
	public String getLocalName()
	{
		throw new UnsupportedOperationException();
	}
	
	public String getLocalAddr()
	{
		throw new UnsupportedOperationException();
	}
	
	public int getLocalPort()
	{
		// todo: 443 for https
		int ret = 80;
		
		String host = session.getHeaders().get("host");
		if(host!=null)
		{
			int idx = host.indexOf(":");
			if(idx!=-1)
			{
				String rest = host.substring(idx+1);
				idx = rest.indexOf("/");
				if(idx!=-1)
				{
					rest = rest.substring(0, idx);
				}
				
				try
				{
					ret = Integer.parseInt(rest);
				}
				catch(Exception e)
				{
					// nop
				}
			}
		}
		return ret;
	}
	
	public ServletContext getServletContext()
	{
		throw new UnsupportedOperationException();
	}
	
	public AsyncContext startAsync() throws IllegalStateException
	{
		if(context==null)
			context = new NanoAsyncContext(null, null);
		
		context.start(null);
		
		return context;
	}
	
	 
	public AsyncContext startAsync(ServletRequest request, ServletResponse response) 
		throws IllegalStateException
	{
		if(context==null)
			context = new NanoAsyncContext(request, response);
		return context;
	}
	   
	public boolean isAsyncStarted()
	{
		return context!=null? context.isStarted(): false;
	}
	
	public boolean isAsyncSupported()
	{
		return true;
	}
	
	public AsyncContext getAsyncContext()
	{
		return context;
	}
	
	public DispatcherType getDispatcherType()
	{
		throw new UnsupportedOperationException();
	}
	
	public String getAuthType()
	{
		throw new UnsupportedOperationException();
	}
	
	public Cookie[] getCookies()
	{
		CookieHandler ch = session.getCookies();
		
		List<Cookie> ret = new ArrayList<Cookie>();
		for(String cname: ch)
		{
			ret.add(new Cookie(cname, ch.read(cname)));
		}
		
		return ret.toArray(new Cookie[ret.size()]);
	}

	public long getDateHeader(String name)
	{
		long ret = 0;
		String h = getHeader(name);
		
		if(h!=null && h.length()>0)
		{
			ret = DateHandler.parse(h);
			if(ret==-1)
				throw new IllegalArgumentException("Date parse problem: "+h);
	    }
		
		return ret;
	}

	public String getHeader(String name)
	{
		return session.getHeaders().get(name);
	}

	public Enumeration<String> getHeaders(String name)
	{
		// todo: does nano support more than one header of same name?!
		String hs = session.getHeaders().get(name);
		Vector<String> ret = new Vector<>();
		ret.add(hs);
		return ret.elements();
	}
	    
	public Enumeration<String> getHeaderNames()
	{
		return new Vector<String>(session.getHeaders().keySet()).elements();
	}
	    
	public int getIntHeader(String name)
	{
		String h = getHeader(name);
		return Integer.parseInt(h);
	}
	    
	public String getMethod()
	{
		return session.getMethod().toString();
	}
	    
	public String getPathInfo()
	{
		throw new UnsupportedOperationException();
	}

	public String getContextPath()
	{
		// todo: how to implment?!
		return "";
	}
	    
	public String getQueryString()
	{
		return session.getQueryParameterString();
	}
	    
	public String getRemoteUser()
	{
		throw new UnsupportedOperationException();
	}
	    
	public boolean isUserInRole(String role)
	{
		throw new UnsupportedOperationException();
	}
	    
	public java.security.Principal getUserPrincipal()
	{
		throw new UnsupportedOperationException();
	}
	    
	public String getRequestedSessionId()
	{
		return null;
	}
	    
	public String getRequestURI()
	{
		return session.getUri();
	}
	    
	public StringBuffer getRequestURL()
	{
		return new StringBuffer(session.getUri());
	}

	public String getServletPath()
	{
		// todo
		return "";
	}
	    
	public HttpSession getSession(boolean create)
	{
		// todo
		throw new UnsupportedOperationException();
	}

	public HttpSession getSession()
	{
		// todo
		throw new UnsupportedOperationException();
	}

	public String changeSessionId()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	    
	public boolean isRequestedSessionIdValid()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	    
	public boolean isRequestedSessionIdFromCookie()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	    
	public boolean isRequestedSessionIdFromURL()
	{
		// todo
		throw new UnsupportedOperationException();
	}
	    
	public boolean isRequestedSessionIdFromUrl()
	{
		// todoServletRequest request, ServletResponse response
		throw new UnsupportedOperationException();
	}

	public boolean authenticate(HttpServletResponse response) 
		throws IOException,ServletException
	{
		throw new UnsupportedOperationException();
	}
	    
	public void login(String username, String password) 
		throws ServletException
	{
		throw new UnsupportedOperationException();
	}
	    
	public void logout() throws ServletException
	{
		throw new UnsupportedOperationException();
	}

	public Collection<Part> getParts() throws IOException, ServletException
	{
		throw new UnsupportedOperationException();
	}

	public Part getPart(String name) throws IOException, ServletException
	{
		throw new UnsupportedOperationException();
	}

	public <T extends HttpUpgradeHandler> T  upgrade(Class<T> handlerClass)
		throws IOException, ServletException
	{
		throw new UnsupportedOperationException();
	}

	//-------- additional methods --------
	
	public static class DateHandler
	{
	    protected static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	    final static String formats[] =
	    {
	        "EEE, dd MMM yyyy HH:mm:ss zzz",
	        "EEE, dd-MMM-yy HH:mm:ss",
	        "EEE MMM dd HH:mm:ss yyyy",
	        "EEE, dd MMM yyyy HH:mm:ss", "EEE dd MMM yyyy HH:mm:ss zzz",
	        "EEE dd MMM yyyy HH:mm:ss", "EEE MMM dd yyyy HH:mm:ss zzz", "EEE MMM dd yyyy HH:mm:ss",
	        "EEE MMM-dd-yyyy HH:mm:ss zzz", "EEE MMM-dd-yyyy HH:mm:ss", "dd MMM yyyy HH:mm:ss zzz",
	        "dd MMM yyyy HH:mm:ss", "dd-MMM-yy HH:mm:ss zzz", "dd-MMM-yy HH:mm:ss", "MMM dd HH:mm:ss yyyy zzz",
	        "MMM dd HH:mm:ss yyyy", "EEE MMM dd HH:mm:ss yyyy zzz",
	        "EEE, MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yy HH:mm:ss zzz",
	        "EEE dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss",
	    };
	    
	    protected final static SimpleDateFormat formatter[] = new SimpleDateFormat[formats.length];
	    
	    static
	    {
	       GMT.setID("GMT");
	    }

	    public static long parse(final String datestr)
	    {
	        for(int i = 0; i < formatter.length; i++)
	        {
	            if(formatter[i] == null)
	            {
	            	formatter[i] = new SimpleDateFormat(formats[i], Locale.US);
	            	formatter[i].setTimeZone(GMT);
	            }

	            try
	            {
	                Date date = (Date)formatter[i].parseObject(datestr);
	                return date.getTime();
	            }
	            catch (java.lang.Exception e)
	            {
	                // LOG.ignore(e);
	            }
	        }

	        if(datestr.endsWith(" GMT"))
	        {
	            final String val = datestr.substring(0, datestr.length() - 4);

	            for (SimpleDateFormat element : formatter)
	            {
	                try
	                {
	                    Date date = (Date) element.parseObject(val);
	                    return date.getTime();
	                }
	                catch (java.lang.Exception e)
	                {
	                    // LOG.ignore(e);
	                }
	            }
	        }
	        return -1;
	    }
	    
	    public static String format(long date)
	    {
	    	return formatter[0].format(new Date(date));
	    }
	}
	
	public static class NanoAsyncContext implements AsyncContext
	{
		protected boolean started;
		protected boolean completed;
		protected List<AsyncListener> listeners = new ArrayList<>();
		protected ServletRequest request;
		protected ServletResponse response;
		protected Timer timer;
		protected long timeout;
		
		public NanoAsyncContext(ServletRequest request, ServletResponse response)
		{
			this.request = request;
			this.response = response;
		}
		
		@Override
		public void start(Runnable run) 
		{
			started = true;
			notifyListeners(NotifyType.STARTED);
		}
		
		@Override
		public void setTimeout(long timeout) 
		{
			if(timer==null)
			{
				this.timeout = timeout;
				timer = new Timer();
				timer.schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						
					}
				}, timeout);
			}
		}
		
		@Override
		public boolean hasOriginalRequestAndResponse() 
		{
			return request!=null && response!=null;
		}
		
		@Override
		public long getTimeout() 
		{
			return timeout;
		}
		
		@Override
		public ServletResponse getResponse() 
		{
			return response;
		}
		
		@Override
		public ServletRequest getRequest() 
		{
			return request;
		}
		
		@Override
		public void dispatch(ServletContext context, String path) 
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void dispatch(String path) 
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void dispatch() 
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException 
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void complete() 
		{
			completed = true;
			notifyListeners(NotifyType.COMPLETED);
		}
		
		@Override
		public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) 
		{
			// todo: request response
			listeners.add(listener);
			
			if(started)
				notifyListeners(NotifyType.STARTED);
			if(completed)
				notifyListeners(NotifyType.COMPLETED);
		}
		
		@Override
		public void addListener(AsyncListener listener) 
		{
			listeners.add(listener);
			
			if(started)
				notifyListeners(NotifyType.STARTED);
			if(completed)
				notifyListeners(NotifyType.COMPLETED);
		}

		public boolean isStarted() 
		{
			return started;
		}
		
		public enum NotifyType
		{
			STARTED, TIMEOUT, COMPLETED, ERROR
		}
		
		protected void notifyListeners(NotifyType type)
		{
			for(AsyncListener lis: listeners)
			{
				try
				{
					if(type==NotifyType.STARTED)
						lis.onStartAsync(new AsyncEvent(this));
					else if (type==NotifyType.TIMEOUT)
						lis.onTimeout(new AsyncEvent(this));
					else if(type==NotifyType.COMPLETED)
						lis.onComplete(new AsyncEvent(this));
					else if(type==NotifyType.ERROR)
						lis.onError(new AsyncEvent(this));
				}
				catch(Exception e)
				{
					// nop
				}
			}
		}
	}

	@Override
	public String getPathTranslated() 
	{
		throw new UnsupportedOperationException();
	}
}