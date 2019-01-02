package jadex.extension.rs.publish;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import jadex.extension.rs.publish.NanoHttpServletRequestWrapper.DateHandler;

/**
 *  Wrapper of HttpServletResponse for nano.
 */
public class NanoHttpServletResponseWrapper implements HttpServletResponse
{
	/** The nano session. */
	protected IHTTPSession session;
	
	protected String contenttype;
	protected int status = 200;
	protected long length;
	protected String charencoding;
	protected Map<String, String> headers;
	protected ServletOutputStream out;
	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();
	protected PrintWriter writer;
	
	
	public NanoHttpServletResponseWrapper(IHTTPSession session)
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
    	this.status = sc;
    	this.contenttype = "text/html";
    	getWriter().append("<html><head></head><body><h1>Error</h1>");
    	if(msg!=null)
    		getWriter().append(msg);
    	getWriter().append("</body></html>");
    }

    public void sendError(int sc) throws IOException
    {
    	sendError(sc, null);
    }

    public void sendRedirect(String location) throws IOException
    {
    	throw new UnsupportedOperationException();
    }
    
    public void setDateHeader(String name, long date)
    {
    	setHeaderInternal(name, DateHandler.format(date));
    }
    
    public void addDateHeader(String name, long date)
    {
    	addHeaderInternal(name, DateHandler.format(date));
    }
    
    public void setHeader(String name, String value)
    {
    	setHeaderInternal(name, value);
    }
    
    public void addHeader(String name, String value)
    {
    	addHeaderInternal(name, value);
    }

    public void setIntHeader(String name, int value)
    {
    	setHeaderInternal(name, ""+value);
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
    	return headers!=null? headers.get(name): null;
    }

    public Collection<String> getHeaders(String name)
    {
    	String h = getHeader(name);
    	StringTokenizer stok = new StringTokenizer(h, ",");
    	List<String> ret = new ArrayList<>();
    	while(stok.hasMoreTokens())
    		ret.add(stok.nextToken());
    	return ret;
    }
    
    public Collection<String> getHeaderNames()
    {
    	return headers==null? Collections.EMPTY_LIST: headers.keySet();
    }
    
    public void setCharacterEncoding(String charset)
    {
    	this.charencoding = charset;
    }
    
    public String getCharacterEncoding()
    {
    	return charencoding;
    }
    
    public String getContentType()
    {
    	return contenttype;
    }
    
    public ServletOutputStream getOutputStream() throws IOException
    {
    	if(out==null)
    	{
    		out = new ServletOutputStream() 
    		{
    			@Override
				public void write(int b) throws IOException 
				{
    				bos.write(b);
				}
				
				@Override
				public void setWriteListener(WriteListener writeListener) 
				{
					throw new UnsupportedOperationException();
				}
				
				@Override
				public boolean isReady() 
				{
					return true;
//					throw new UnsupportedOperationException();
				}
			};
    	}
    	return out;
    }
    
    public PrintWriter getWriter() throws IOException
    {
    	if(writer==null)
    		writer = new PrintWriter(bos);
    	
    	return writer;
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
    
    protected void setHeaderInternal(String name, String val)
    {
    	if(headers==null)
    		headers = new HashMap<>();
    	headers.put(name, val);
    }
    
    protected void addHeaderInternal(String name, String val)
    {
    	if(headers==null)
    		headers = new HashMap<>();
    	String oval = headers.get(name);
    	if(oval!=null)
    		val = oval+","+val;
    	headers.put(name, val);
    }

    public ByteArrayOutputStream getOutput()
    {
    	try{ getWriter().flush(); } catch(Exception e) {e.printStackTrace();}
    	return bos;
    }
}
