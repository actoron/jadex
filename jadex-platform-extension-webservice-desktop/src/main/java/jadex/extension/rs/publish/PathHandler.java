package jadex.extension.rs.publish;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.grizzly.http.server.Request;

import jadex.commons.Tuple2;

/**
 *  Main handler dealing with incoming request.
 */
public class PathHandler implements IRequestHandler
{
	/** 
	 *  Published subhandlers.
	 *  vhost+path -> path+httphandler
	 *  Path needs to be preserved in the value since the cache does not preserve it.
	 */
	protected Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> subhandlers;
	
	/** Published subhandler matching cache. */
	protected Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> subhandlercache;
	
	/**
	 *  Create the handler.
	 */
	public PathHandler()
	{
		subhandlers = new HashMap<Tuple2<String, String>, Tuple2<String, IRequestHandler>>();
		subhandlercache = new HashMap<Tuple2<String, String>, Tuple2<String, IRequestHandler>>();
	}
	
	/**
	 *  Handle the request.
	 *  @param request The request.
	 *  @param response The response.
	 *  @param args Container specific args.
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response, Object args) throws Exception
	{
		String path = request.getRequestURI();//request.getRequest().getRequestURIRef().getURI();
		String host = request.getHeader("host");
		int	idx	= host.indexOf(":");
		if(idx!=-1)
			host	= host.substring(0, idx);
		
		Tuple2<String, IRequestHandler> subhandlertuple = subhandlercache.get(new Tuple2<String, String>(host, path));
		if(subhandlertuple == null)
			subhandlertuple = subhandlercache.get(new Tuple2<String, String>(null, path));
		
		int pidx = path.lastIndexOf('/');
		if(subhandlertuple == null && pidx > 0 && pidx <= path.length() - 1)
		{
			String cpath = path.substring(0, pidx);
			subhandlertuple = subhandlercache.get(new Tuple2<String, String>(host, cpath));
		}
		
		if(subhandlertuple == null)
		{
			subhandlertuple = findSubhandler(host, path);
			if(subhandlertuple == null)
			{
				subhandlertuple = findSubhandler(null, path);
			}
			
			if(subhandlertuple != null)
			{
				subhandlercache.put(new Tuple2<String, String>(host, path), subhandlertuple);
			}
		}
		
		if(subhandlertuple == null)
			throw new RuntimeException("No handler found for path: " + path);
		
		Method setcontextpath = Request.class.getDeclaredMethod("setContextPath", new Class<?>[]{String.class});
		setcontextpath.setAccessible(true);
		setcontextpath.invoke(request, subhandlertuple.getFirstEntity());
		
		subhandlertuple.getSecondEntity().handleRequest(request, response, args);
	}
	
	/**
	 *  Adds a new subhandler.
	 *  
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 *  @param subhandler The subhandler.
	 */
	public void addSubhandler(String vhost, String path, IRequestHandler subhandler)
	{
		subhandlers.put(new Tuple2<String, String>(vhost, path), new Tuple2<String, IRequestHandler>(path, subhandler));
		synchronized (subhandlers)
		{
			subhandlercache = Collections.synchronizedMap(new HashMap<Tuple2<String,String>, Tuple2<String, IRequestHandler>>(subhandlers));
		}
	}
	
	/**
	 *  Tests if a handler for the exact URI is currently published.
	 * 
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 *  @return True, if a handler was found.
	 */
	public boolean containsSubhandlerForExactUri(String vhost, String path)
	{
		return subhandlers.containsKey(new Tuple2<String, String>(vhost, path));
	}
	
	/**
	 *  Tests if the handler contains no subhandlers.
	 *  
	 *  @return True, if no subhandlers remain.
	 */
	public boolean isEmpty()
	{
		return subhandlers.isEmpty();
	}
	
	/**
	 * 
	 * @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 */
	public void removeSubhandler(String vhost, String path)
	{
		subhandlers.remove(new Tuple2<String, String>(vhost, path));
		synchronized (subhandlers)
		{
			subhandlercache = new HashMap<Tuple2<String,String>, Tuple2<String, IRequestHandler>>(subhandlers);
		}			
	}
	
	/**
	 *  Locates an appropriate subhandler that matches the requested resource closely.
	 *  
	 *  @param host The requested virtual host.
	 *  @param path The requested path
	 *  @return The subhandler or null if none is found for the host.
	 */
	protected Tuple2<String, IRequestHandler> findSubhandler(String host, String path)
	{
		Tuple2<String, IRequestHandler> ret = null;
		do
		{
			int pidx = path.lastIndexOf('/');
			if (pidx >= 0)
			{
				path = path.substring(0, pidx);
				ret = subhandlercache.get(new Tuple2<String, String>(host, path));
			}
			else
			{
				path = null;
			}
		}
		while (ret == null && path != null && path.length() > 0);
		return ret;
	}
}