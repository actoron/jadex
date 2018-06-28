package jadex.extension.rs.publish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

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

/**
 *  Wrapper of HttpServletRequest interface to selectivly
 *  override methods and introduce setters.
 */
public class HttpServletRequestWrapper implements HttpServletRequest
{
	/** The original request. */
	protected HttpServletRequest request;
	
	/** The contextpath. */
	protected String contextpath;
	
	/** The path info. */
	protected String pathinfo;
	
	/** The servlet path. */
	protected String servletpath;
	
	/**
	 *  Create a new wrapper.
	 */
	public HttpServletRequestWrapper(HttpServletRequest request)
	{
		this.request = request;
	}
	
	public Object getAttribute(String name)
	{
		return request.getAttribute(name);
	}
	    
	public Enumeration<String> getAttributeNames()
	{
		return request.getAttributeNames();
	}
	    
	public String getCharacterEncoding()
	{
		return request.getCharacterEncoding();
	}
	
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException
	{
		request.setCharacterEncoding(env);
	}
	
	public int getContentLength()
	{
		return request.getContentLength();
	}
	    
	public long getContentLengthLong()
	{
		return request.getContentLengthLong();
	}
	    
	public String getContentType()
	{
		return request.getContentType();
	}
	    
	public ServletInputStream getInputStream() throws IOException
	{
		return request.getInputStream();
	}
	     
	public String getParameter(String name)
	{
		return request.getParameter(name);
	}
	    
	public Enumeration<String> getParameterNames()
	{
		return request.getParameterNames();
	}
	        
	public String[] getParameterValues(String name)
	{
		return request.getParameterValues(name);
	}
	 
	public Map<String, String[]> getParameterMap()
	{
		return request.getParameterMap();
	}
	    
	public String getProtocol()
	{
		return request.getProtocol();
	}
	    
	public String getScheme()
	{
		return request.getScheme();
	}
	    
	public String getServerName()
	{
		return request.getServerName();
	}
	    
	public int getServerPort()
	{
		return request.getServerPort();
	}
	    
	public BufferedReader getReader() throws IOException
	{
		return request.getReader();
	}
	    
	public String getRemoteAddr()
	{
		return request.getRemoteAddr();
	}
	    
	public String getRemoteHost()
	{
		return request.getRemoteHost();
	}
	    
	public void setAttribute(String name, Object o)
	{
		request.setAttribute(name, o);
	}
	    
	public void removeAttribute(String name)
	{
		request.removeAttribute(name);
	}
	    
	public Locale getLocale()
	{
		return request.getLocale();
	}
	    
	public Enumeration<Locale> getLocales()
	{
		return request.getLocales();
	}
	    
	public boolean isSecure()
	{
		return request.isSecure();
	}
	    
	public RequestDispatcher getRequestDispatcher(String path)
	{
		return request.getRequestDispatcher(path);
	}
	    
	public String getRealPath(String path)
	{
		return request.getRealPath(path);
	}
	    
	public int getRemotePort()
	{
		return request.getRemotePort();
	}
	
	public String getLocalName()
	{
		return request.getLocalName();
	}
	
	public String getLocalAddr()
	{
		return request.getLocalAddr();
	}
	
	public int getLocalPort()
	{
		return request.getLocalPort();
	}
	
	public ServletContext getServletContext()
	{
		return request.getServletContext();
	}
	
	public AsyncContext startAsync() throws IllegalStateException
	{
		return request.startAsync();
	}
	 
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) 
		throws IllegalStateException
	{
		return request.startAsync(servletRequest, servletResponse);
	}
	   
	public boolean isAsyncStarted()
	{
		return request.isAsyncStarted();
	}
	
	public boolean isAsyncSupported()
	{
		return request.isAsyncSupported();
	}
	
	public AsyncContext getAsyncContext()
	{
		return request.getAsyncContext();
	}
	
	public DispatcherType getDispatcherType()
	{
		return request.getDispatcherType();
	}
	
	public String getAuthType()
	{
		return request.getAuthType();
	}
	
	public Cookie[] getCookies()
	{
		return request.getCookies();
	}

	public long getDateHeader(String name)
	{
		return request.getDateHeader(name);
	}

	public String getHeader(String name)
	{
		return request.getHeader(name);
	}

	public Enumeration<String> getHeaders(String name)
	{
		return request.getHeaders(name);
	}
	    
	public Enumeration<String> getHeaderNames()
	{
		return request.getHeaderNames();
	}
	    
	public int getIntHeader(String name)
	{
		return request.getIntHeader(name);
	}
	    
	public String getMethod()
	{
		return request.getMethod();
	}
	    
	public String getPathInfo()
	{
		return pathinfo!=null? pathinfo: request.getPathInfo();
	}

	public String getPathTranslated()
	{
		return request.getPathTranslated();
	}

	public String getContextPath()
	{
		return contextpath!=null? contextpath: request.getContextPath();
	}
	    
	public String getQueryString()
	{
		return request.getQueryString();
	}
	    
	public String getRemoteUser()
	{
		return request.getRemoteUser();
	}
	    
	public boolean isUserInRole(String role)
	{
		return request.isUserInRole(role);
	}
	    
	public java.security.Principal getUserPrincipal()
	{
		return request.getUserPrincipal();
	}
	    
	public String getRequestedSessionId()
	{
		return request.getRequestedSessionId();
	}
	    
	public String getRequestURI()
	{
		return request.getRequestURI();
	}
	    
	public StringBuffer getRequestURL()
	{
		return request.getRequestURL();
	}

	public String getServletPath()
	{
		return servletpath!=null? servletpath: request.getServletPath();
	}
	    
	public HttpSession getSession(boolean create)
	{
		return request.getSession(create);
	}

	public HttpSession getSession()
	{
		return request.getSession();
	}

	public String changeSessionId()
	{
		return request.changeSessionId();
	}
	    
	public boolean isRequestedSessionIdValid()
	{
		return request.isRequestedSessionIdValid();
	}
	    
	public boolean isRequestedSessionIdFromCookie()
	{
		return request.isRequestedSessionIdFromCookie();
	}
	    
	public boolean isRequestedSessionIdFromURL()
	{
		return request.isRequestedSessionIdFromURL();
	}
	    
	public boolean isRequestedSessionIdFromUrl()
	{
		return request.isRequestedSessionIdFromUrl();
	}

	public boolean authenticate(HttpServletResponse response) 
		throws IOException,ServletException
	{
		return request.authenticate(response);
	}
	    
	public void login(String username, String password) 
		throws ServletException
	{
		request.login(username, password);
	}
	    
	public void logout() throws ServletException
	{
		request.logout();
	}

	public Collection<Part> getParts() throws IOException, ServletException
	{
		return request.getParts();
	}

	public Part getPart(String name) throws IOException, ServletException
	{
		return request.getPart(name);
	}

	public <T extends HttpUpgradeHandler> T  upgrade(Class<T> handlerClass)
		throws IOException, ServletException
	{
		return request.upgrade(handlerClass);
	}


	//-------- additional methods --------
	
	/**
	 *  Set the contextpath.
	 *  @param contextpath The contextpath to set
	 */
	public void setContextPath(String contextpath)
	{
		this.contextpath = contextpath;
	}

	/**
	 *  Set the pathinfo.
	 *  @param pathinfo The pathinfo to set
	 */
	public void setPathInfo(String pathinfo)
	{
		this.pathinfo = pathinfo;
	}

	/**
	 *  Set the servletpath.
	 *  @param servletpath The servletpath to set
	 */
	public void setServletPath(String servletpath)
	{
		this.servletpath = servletpath;
	}
	
}
