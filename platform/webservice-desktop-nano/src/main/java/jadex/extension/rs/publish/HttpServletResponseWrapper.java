package jadex.extension.rs.publish;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import jadex.extension.rs.publish.HttpServletRequestWrapper.DateHandler;

/**
 *  Wrapper of HttpServletResponse for nano.
 */
public class HttpServletResponseWrapper implements HttpServletResponse
{
	/** The nano session. */
	protected IHTTPSession session;
	
	/** The response. */
//	protected Response response;
	
	protected String contenttype;
	protected int status;
	protected long length;
	
	public HttpServletResponseWrapper(IHTTPSession session)
	{
		this.session = session;
	}
	
    public void addCookie(Cookie cookie)
    {
    	session.getCookies().set(cookie.getName(), cookie.getValue(), cookie.getMaxAge());
    }

    public boolean containsHeader(String name)
    {
    	return session.getHeaders().containsKey(name);
    }

    public String encodeURL(String url)
    {
    	throw new UnsupportedOperationException();
    }

    public String encodeRedirectURL(String url)
    {
    	throw new UnsupportedOperationException();
    }

    public String encodeUrl(String url)
    {
    	throw new UnsupportedOperationException();
    }
    
    public String encodeRedirectUrl(String url)
    {
    	throw new UnsupportedOperationException();
    }

    public void sendError(int sc, String msg) throws IOException
    {
    	throw new UnsupportedOperationException();
    }

    public void sendError(int sc) throws IOException
    {
    	throw new UnsupportedOperationException();
    }

    public void sendRedirect(String location) throws IOException
    {
    	throw new UnsupportedOperationException();
    }
    
    public void setDateHeader(String name, long date)
    {
    	session.getHeaders().put(name, DateHandler.format(date));
    }
    
    public void addDateHeader(String name, long date)
    {
    	addHeaderInternal(name, DateHandler.format(date));
    }
    
    public void setHeader(String name, String value)
    {
    	session.getHeaders().put(name, value);
    }
    
    public void addHeader(String name, String value)
    {
    	addHeaderInternal(name, value);
    }

    public void setIntHeader(String name, int value)
    {
    	session.getHeaders().put(name, ""+value);
    }

    public void addIntHeader(String name, int value)
    {
    	addHeaderInternal(name, ""+value);
    }

    public void setStatus(int sc)
    {
    	this.status = sc;
//    	response.setStatus(Response.Status.lookup(sc));
    }
  
    public void setStatus(int sc, String sm)
    {
    	this.status = sc;
    	// todo: sm
//    	response.setStatus(Response.Status.lookup(sc));
    }

    public int getStatus()
    {
    	return status;
//    	return response.getStatus().getRequestStatus();
    }

    public String getHeader(String name)
    {
    	return session.getHeaders().get(name);
    }

    public Collection<String> getHeaders(String name)
    {
    	String h = session.getHeaders().get(name);
    	StringTokenizer stok = new StringTokenizer(h, ",");
    	List<String> ret = new ArrayList<>();
    	while(stok.hasMoreTokens())
    		ret.add(stok.nextToken());
    	return ret;
    }
    
    public Collection<String> getHeaderNames()
    {
    	return session.getHeaders().keySet();
    }
    
    public void setCharacterEncoding(String charset)
    {
    	throw new UnsupportedOperationException();
    }
    
    public String getCharacterEncoding()
    {
    	throw new UnsupportedOperationException();
    }
    
    public String getContentType()
    {
    	return contenttype;
    }
    
    public ServletOutputStream getOutputStream() throws IOException
    {
    	throw new UnsupportedOperationException();
    }
    
    public PrintWriter getWriter() throws IOException
    {
    	throw new UnsupportedOperationException();
    }
    
    public void setContentLength(int len)
    {
    	this.length = len;
    }
    
    public void setContentLengthLong(long len)
    {
    	this.length = len;
    }

    public void setContentType(String type)
    {
    	this.contenttype = type;
    }

    public void setBufferSize(int size)
    {
    	throw new UnsupportedOperationException();
    }
   
    public int getBufferSize()
    {
    	throw new UnsupportedOperationException();
    }
    
    public void flushBuffer() throws IOException
    {
    	throw new UnsupportedOperationException();
    }
    
    public void resetBuffer()
    {
    	throw new UnsupportedOperationException();	
    }
    
    public boolean isCommitted()
    {
    	throw new UnsupportedOperationException();
    }
    
    public void reset()
    {
    	throw new UnsupportedOperationException();
    }
    
    public void setLocale(Locale loc)
    {
    	throw new UnsupportedOperationException();
    }
    
    public Locale getLocale()
    {
    	throw new UnsupportedOperationException();
    }
    
    protected void addHeaderInternal(String name, String val)
    {
    	Map<String, String> hs = session.getHeaders();
    	String oval = hs.get(name);
    	if(oval!=null)
    		val = oval+","+val;
    	hs.put(name, val);
    }
}
