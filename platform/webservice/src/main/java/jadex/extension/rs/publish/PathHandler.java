package jadex.extension.rs.publish;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.commons.Tuple2;

/**
 *  Main handler dealing with incoming request.
 */
public class PathHandler implements IPathHandler
{
	/** 
	 *  Published subhandlers.
	 *  vhost+path -> path+httphandler
	 *  Path needs to be preserved in the value since the cache does not preserve it.
	 */
	protected Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> subhandlers;
	
	/** Published subhandler matching cache. Adds already resolved handlers for requested paths. */
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
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		String host = request.getHeader("host");
		if(host!=null)
		{
			int	idx	= host.indexOf(":");
			if(idx!=-1)
				host = host.substring(0, idx);
		}
		
		String pathcp = null; 
		String ctx = request.getContextPath();
		if(ctx!=null && ctx.length()>0)
			pathcp = path.replaceFirst(ctx, "/"+ExternalRestPublishService.DEFAULT_APP);
		
		Tuple2<String, IRequestHandler> tup = subhandlercache.get(new Tuple2<String, String>(host, path));
		if(tup==null)
			tup = subhandlercache.get(new Tuple2<String, String>(null, path));
		if(tup==null && pathcp!=null)
			tup = subhandlercache.get(new Tuple2<String, String>(null, pathcp));
		
		int pidx = path.lastIndexOf('/');
		if(tup == null && pidx > 0 && pidx <= path.length() - 1)
		{
			String cpath = path.substring(0, pidx);
			tup = subhandlercache.get(new Tuple2<String, String>(host, cpath));
		}
		
		if(tup==null)
		{
			tup = findSubhandler(host, path);
			if(tup==null)
				tup = findSubhandler(null, path);
			
			if(tup!=null)
				subhandlercache.put(new Tuple2<String, String>(host, path), tup);
		}
		
		if(tup==null)
		{
			if(pathcp!=null)
				tup = findSubhandler(null, pathcp);
			
			if(tup!=null)
				subhandlercache.put(new Tuple2<String, String>(host, pathcp), tup);
		}
		
		if(tup==null)
			throw new RuntimeException("No handler found for path: " + path);
		
//		Method setcontextpath = request.getClass().getDeclaredMethod("setContextPath", new Class<?>[]{String.class});
//		setcontextpath.setAccessible(true);
//		setcontextpath.invoke(request, subhandlertuple.getFirstEntity());
		
		HttpServletRequestWrapper wr = new HttpServletRequestWrapper(request);
		String cp = tup.getFirstEntity();
		if(cp.startsWith("/"+ExternalRestPublishService.DEFAULT_APP))
			cp = cp.replaceFirst("/"+ExternalRestPublishService.DEFAULT_APP, request.getContextPath());
		wr.setContextPath(cp);
		String full = request.getRequestURI();
		int idx = full.indexOf(cp)+cp.length();
		String npi = full.substring(idx);
		wr.setPathInfo(npi);
		
		tup.getSecondEntity().handleRequest(wr, response, args);
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
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		subhandlers.put(new Tuple2<String, String>(vhost, path), new Tuple2<String, IRequestHandler>(path, subhandler));
		subhandlercache = new HashMap<Tuple2<String,String>, Tuple2<String, IRequestHandler>>(subhandlers);
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
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
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
	 *  Remove a subhandler.
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 */
	public void removeSubhandler(String vhost, String path)
	{
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		subhandlers.remove(new Tuple2<String, String>(vhost, path));
		subhandlercache = new HashMap<Tuple2<String,String>, Tuple2<String, IRequestHandler>>(subhandlers);
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
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		Tuple2<String, IRequestHandler> ret = null;
		do
		{
			int pidx = path.lastIndexOf('/');
			if(pidx >= 0)
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

	/**
	 *  Get the subhandlers. 
	 *  @return The subhandlers
	 */
	public Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> getSubhandlers()
	{
		return subhandlers;
	}
}