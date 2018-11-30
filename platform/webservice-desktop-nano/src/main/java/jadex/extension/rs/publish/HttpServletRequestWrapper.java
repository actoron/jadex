package jadex.extension.rs.publish;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
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

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

/**
 *  Wrapper of HttpServletRequest interface to selectivly
 *  override methods and introduce setters.
 */
public class HttpServletRequestWrapper //implements HttpServletRequest
{
//	private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
//    private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEX, Pattern.CASE_INSENSITIVE);
//	 
//	/** The nano session. */
//	protected IHTTPSession session;
//	
//	/** The contextpath. */
//	protected String contextpath;
//	
//	/** The path info. */
//	protected String pathinfo;
//	
//	/** The servlet path. */
//	protected String servletpath;
//	
//	/** The request attributes. */
//	protected Map<String, Object> attributes;
//	
//	private String getDetailFromContentHeader(String contentTypeHeader, Pattern pattern, String defaultValue, int group) 
//	{
//		Matcher matcher = pattern.matcher(contentTypeHeader);
//		return matcher.find() ? matcher.group(group) : defaultValue;
//	}
//	
//	/**
//	 *  Create a new wrapper.
//	 */
//	public HttpServletRequestWrapper(IHTTPSession session)
//	{
//		this.session = session;
//	}
//	
//	public Object getAttribute(String name)
//	{
//		return attributes!=null? attributes.get(name): null;
//	}
//	    
//	public Enumeration<String> getAttributeNames()
//	{
//		return attributes!=null? new Vector<String>(attributes.keySet()).elements(): null;
//	}
//	    
//	public String getCharacterEncoding()
//	{
//		return getDetailFromContentHeader(session.getHeaders().get("content-type"), CHARSET_PATTERN, null, 2);
//	}
//	
//	public void setCharacterEncoding(String env) throws UnsupportedEncodingException
//	{
//		throw new UnsupportedOperationException();
//	}
//	
//	public int getContentLength()
//	{
//		return request.getContentLength();
//	}
//	    
//	public long getContentLengthLong()
//	{
//		return request.getContentLengthLong();
//	}
//	    
//	public String getContentType()
//	{
//		return session.getHeaders().get("content-type");
//	}
//	    
//	public ServletInputStream getInputStream() throws IOException
//	{
//	    ServletInputStream servletInputStream = new ServletInputStream()
//	    {
//	        public int read() throws IOException 
//	        {
//	        	return session.getInputStream().read();
//	        }
//	        
//	        @Override
//	        public boolean isFinished()
//	        {
//	        	return session.getInputStream().available()
//	        }
//	        
//	        @Override
//	        public boolean isReady()
//	        {
//	        	// TODO Auto-generated method stub
//	        	return false;
//	        }
//	    }
//	}
//	     
//	public String getParameter(String name)
//	{
//		 List<String> ret = session.getParameters().get(name);
//		 return ret!=null? ret.get(0): null;
//	}
//	    
//	public Enumeration<String> getParameterNames()
//	{
//		return session.getParameters()!=null? new Vector<String>(session.getParameters().keySet()).elements(): null;
//	}
//	        
//	public String[] getParameterValues(String name)
//	{
//		List<String> ret = session.getParameters().get(name);
//		return ret!=null? ret.toArray(new String[ret.size()]): null;
//	}
//	 
//	public Map<String, String[]> getParameterMap()
//	{
//		Map<String, String[]> ret = null;
//		Map<String, List<String>> ps = session.getParameters();
//		if(ps!=null)
//		{
//			ret = new HashMap<>();
//			for(Map.Entry<String, List<String>> e: ps.entrySet())
//			{
//				ret.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
//			}
//		}
//		
//		return ret;
//	}
//	    
//	public String getProtocol()
//	{
//		String uri = session.getUri();
//		// todo:
//		return uri;
//	}
//	    
//	public String getScheme()
//	{
//		String uri = session.getUri();
//		// todo:
//		return uri;
//	}
//	    
//	public String getServerName()
//	{
//		String uri = session.getUri();
//		// todo:
//		return uri;
//	}
//	    
//	public int getServerPort()
//	{
//		String uri = session.getUri();
//		// todo:
//		return 80;
//	}
//	    
//	public BufferedReader getReader() throws IOException
//	{
//		return new BufferedReader(new InputStreamReader(getInputStream()));
//	}
//	    
//	public String getRemoteAddr()
//	{
//		String uri = session.getUri();
//		// todo:
//		return uri;
//	}
//	    
//	public String getRemoteHost()
//	{
//		String uri = session.getUri();
//		// todo:
//		return uri;
//	}
//	    
//	public void setAttribute(String name, Object o)
//	{
//		if(attributes==null)
//			attributes = new HashMap<>();
//		attributes.put(name, o);
//	}
//	    
//	public void removeAttribute(String name)
//	{
//		if(attributes!=null)
//			attributes.remove(name);
//	}
//	    
//	public Locale getLocale()
//	{
//		return request.getLocale();
//	}
//	    
//	public Enumeration<Locale> getLocales()
//	{
//		return request.getLocales();
//	}
//	    
//	public boolean isSecure()
//	{
//		return session.getUri().indexOf("https")!=-1;
//	}
//	    
//	public RequestDispatcher getRequestDispatcher(String path)
//	{
//		return request.getRequestDispatcher(path);
//	}
//	    
//	public String getRealPath(String path)
//	{
//		return request.getRealPath(path);
//	}
//	    
//	public int getRemotePort()
//	{
//		return request.getRemotePort();
//	}
//	
//	public String getLocalName()
//	{
//		return request.getLocalName();
//	}
//	
//	public String getLocalAddr()
//	{
//		return request.getLocalAddr();
//	}
//	
//	public int getLocalPort()
//	{
//		return request.getLocalPort();
//	}
//	
//	public ServletContext getServletContext()
//	{
//		return request.getServletContext();
//	}
//	
//	public AsyncContext startAsync() throws IllegalStateException
//	{
//		return request.startAsync();
//	}
//	 
//	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) 
//		throws IllegalStateException
//	{
//		return request.startAsync(servletRequest, servletResponse);
//	}
//	   
//	public boolean isAsyncStarted()
//	{
//		return request.isAsyncStarted();
//	}
//	
//	public boolean isAsyncSupported()
//	{
//		return request.isAsyncSupported();
//	}
//	
//	public AsyncContext getAsyncContext()
//	{
//		return request.getAsyncContext();
//	}
//	
//	public DispatcherType getDispatcherType()
//	{
//		return request.getDispatcherType();
//	}
//	
//	public String getAuthType()
//	{
//		return request.getAuthType();
//	}
//	
//	public Cookie[] getCookies()
//	{
//		return request.getCookies();
//	}
//
//	public long getDateHeader(String name)
//	{
//		return request.getDateHeader(name);
//	}
//
//	public String getHeader(String name)
//	{
//		return request.getHeader(name);
//	}
//
//	public Enumeration<String> getHeaders(String name)
//	{
//		return request.getHeaders(name);
//	}
//	    
//	public Enumeration<String> getHeaderNames()
//	{
//		return request.getHeaderNames();
//	}
//	    
//	public int getIntHeader(String name)
//	{
//		return request.getIntHeader(name);
//	}
//	    
//	public String getMethod()
//	{
//		return request.getMethod();
//	}
//	    
//	public String getPathInfo()
//	{
//		return pathinfo!=null? pathinfo: request.getPathInfo();
//	}
//
//	public String getPathTranslated()
//	{
//		return request.getPathTranslated();
//	}
//
//	public String getContextPath()
//	{
//		return contextpath!=null? contextpath: request.getContextPath();
//	}
//	    
//	public String getQueryString()
//	{
//		return session.getQueryParameterString();
//	}
//	    
//	public String getRemoteUser()
//	{
//		return null;
//	}
//	    
//	public boolean isUserInRole(String role)
//	{
//		return false;
//	}
//	    
//	public java.security.Principal getUserPrincipal()
//	{
//		return null;
//	}
//	    
//	public String getRequestedSessionId()
//	{
//		return null;
//	}
//	    
//	public String getRequestURI()
//	{
//		return session.getUri();
//	}
//	    
//	public StringBuffer getRequestURL()
//	{
//		return 
//	}
//
//	public String getServletPath()
//	{
//		return servletpath!=null? servletpath: request.getServletPath();
//	}
//	    
//	public HttpSession getSession(boolean create)
//	{
//		return request.getSession(create);
//	}
//
//	public HttpSession getSession()
//	{
//		return request.getSession();
//	}
//
//	public String changeSessionId()
//	{
//		return request.changeSessionId();
//	}
//	    
//	public boolean isRequestedSessionIdValid()
//	{
//		return request.isRequestedSessionIdValid();
//	}
//	    
//	public boolean isRequestedSessionIdFromCookie()
//	{
//		return request.isRequestedSessionIdFromCookie();
//	}
//	    
//	public boolean isRequestedSessionIdFromURL()
//	{
//		return request.isRequestedSessionIdFromURL();
//	}
//	    
//	public boolean isRequestedSessionIdFromUrl()
//	{
//		return request.isRequestedSessionIdFromUrl();
//	}
//
//	public boolean authenticate(HttpServletResponse response) 
//		throws IOException,ServletException
//	{
//		// nop?!
//	}
//	    
//	public void login(String username, String password) 
//		throws ServletException
//	{
//		// nop ?!
//	}
//	    
//	public void logout() throws ServletException
//	{
//		// nop ?!
//	}
//
//	public Collection<Part> getParts() throws IOException, ServletException
//	{
//		return request.getParts();
//	}
//
//	public Part getPart(String name) throws IOException, ServletException
//	{
//		return request.getPart(name);
//	}
//
//	public <T extends HttpUpgradeHandler> T  upgrade(Class<T> handlerClass)
//		throws IOException, ServletException
//	{
//		return request.upgrade(handlerClass);
//	}
//
//
//	//-------- additional methods --------
//	
//	/**
//	 *  Set the contextpath.
//	 *  @param contextpath The contextpath to set
//	 */
//	public void setContextPath(String contextpath)
//	{
//		this.contextpath = contextpath;
//	}
//
//	/**
//	 *  Set the pathinfo.
//	 *  @param pathinfo The pathinfo to set
//	 */
//	public void setPathInfo(String pathinfo)
//	{
//		this.pathinfo = pathinfo;
//	}
//
//	/**
//	 *  Set the servletpath.
//	 *  @param servletpath The servletpath to set
//	 */
//	public void setServletPath(String servletpath)
//	{
//		this.servletpath = servletpath;
//	}
//	
}
