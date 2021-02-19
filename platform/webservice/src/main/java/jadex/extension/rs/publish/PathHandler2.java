package jadex.extension.rs.publish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;

/**
 *  Main handler dealing with incoming request.
 */
public class PathHandler2 implements IPathHandler
{
	protected Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> subhandlersold;

	protected List<Map<String, Collection<SubHandler>>> subhandlers;

	/**
	 *  Create the handler.
	 */
	public PathHandler2()
	{
		subhandlers = new ArrayList<Map<String, Collection<SubHandler>>>();
		subhandlersold = new HashMap<Tuple2<String, String>, Tuple2<String, IRequestHandler>>();
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
		
		SubHandler sh = findSubhandler(host, path);
		SubHandler sh2 = findSubhandler(host, pathcp);
		SubHandler subhandler = sh==null && sh2==null? null: 
			sh!=null && sh2==null? sh: 
			sh2!=null && sh==null? sh2: 
			sh.getSpecifity()>sh2.getSpecifity()? sh: sh2;

		if(subhandler==null)
			throw new RuntimeException("No handler found for path: " + path);
		
//		Method setcontextpath = request.getClass().getDeclaredMethod("setContextPath", new Class<?>[]{String.class});
//		setcontextpath.setAccessible(true);
//		setcontextpath.invoke(request, subhandlertuple.getFirstEntity());
		
		// tweak the context path and path info
		HttpServletRequestWrapper wr = new HttpServletRequestWrapper(request);
		String cp = subhandler.getPath();
		if(cp.startsWith("/"+ExternalRestPublishService.DEFAULT_APP))
			cp = cp.replaceFirst("/"+ExternalRestPublishService.DEFAULT_APP, request.getContextPath());
		wr.setContextPath(cp);
		String full = request.getRequestURI();
		int idx = full.indexOf(cp)+cp.length();
		String npi = full.substring(idx);
		wr.setPathInfo(npi);
		
		subhandler.getHandler().handleRequest(wr, response, args);
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
		
		SubHandler sh = new SubHandler(vhost, path, subhandler);
		StringTokenizer stok = new StringTokenizer(path, "/");
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String pe = stok.nextToken();
			if(pe.startsWith("{") && pe.endsWith("}"))
				pe = "*";

			Map<String, Collection<SubHandler>> hmap = subhandlers.get(i);
			if(hmap==null)
				hmap = new HashMap<>();
			
			Collection<SubHandler> handlers = hmap.get(pe);
			if(handlers==null)
			{
				handlers = new ArrayList<SubHandler>();
				hmap.put(pe, handlers);
			}	
			handlers.add(sh);
		}
		
		subhandlersold.put(new Tuple2<String, String>(vhost, path), new Tuple2<String, IRequestHandler>(path, subhandler));
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
		
		SubHandler sh = findSubhandler(vhost, path);
		
		// Using specifity to avoid problems with variables in path {part}
		return sh.getSpecifity()==path.length() - path.replace("/", "").length();
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
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 */
	public void removeSubhandler(String vhost, String path)
	{
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		// fetch target handler
		SubHandler sh = findSubhandler(vhost, path);
		
		// remnove that handler from the whole path
		StringTokenizer stok = new StringTokenizer(path, "/");
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String pe = stok.nextToken();
			if(pe.startsWith("{") && pe.endsWith("}"))
				pe = "*";

			Map<String, Collection<SubHandler>> hmap = subhandlers.get(i);
			if(hmap!=null)
			{
				Collection<SubHandler> col = hmap.get(pe);
				if(col!=null)
				{
					for(SubHandler tmp: col)
					{
						if(tmp.getHandler().equals(sh.getHandler()))
							col.remove(tmp);
						if(col.size()==0)
							hmap.remove(pe);
					}
				}
			}
		}
		
		subhandlersold.remove(new Tuple2<String, String>(vhost, path));
	}
	
	/**
	 *  Get the subhandlers. 
	 *  @return The subhandlers
	 */
	public Map<Tuple2<String, String>, Tuple2<String, IRequestHandler>> getSubhandlers()
	{
		return subhandlersold;
	}
	
	/**
	 *  Locates an appropriate subhandler that matches the requested resource closely.
	 *  
	 *  @param host The requested virtual host.
	 *  @param path The requested path
	 *  @return The subhandler or null if none is found for the host.
	 */
	protected SubHandler findSubhandler(String host, String path)
	{
		SubHandler ret = null;

		StringTokenizer stok = new StringTokenizer(path, "/");
		
		Collection<SubHandler> res = null;
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String pe = stok.nextToken();
			Map<String, Collection<SubHandler>> hmap = subhandlers.get(i);
			Collection<SubHandler> shs = SUtil.notNull(hmap.get(pe));
			shs.addAll(SUtil.notNull(hmap.get("*")));
			
			if(i==0)
			{
				res = shs;
			}
			else
			{
				Collection<SubHandler> newres = new HashSet<SubHandler>(res);
				newres.retainAll(shs);
				if(newres.size()<=1)
				{
					break;
				}
				else
				{
					res = newres;
				}
			}
		}
		
		// Select most specific one
		if(res.size()==1)
		{
			ret = res.iterator().next();
		}
		if(res.size()>1)
		{
			int spec = -1;
			for(SubHandler tmp: res)
			{
				if(tmp.getSpecifity()>spec)
				{
					ret = tmp;
					spec = tmp.getSpecifity();
				}
			}
		}
		
		return ret;
	}
	

	/**
	 * 
	 */
	public static class SubHandler
	{
		protected String vhost;
		protected String path;
		protected IRequestHandler handler;
		
		public SubHandler(String vhost, String path, IRequestHandler handler)
		{
			this.vhost = vhost;
			this.path = path;
			this.handler = handler;
		}

		/**
		 * @return the vhost
		 */
		public String getVhost()
		{
			return vhost;
		}

		/**
		 * @param vhost the vhost to set
		 */
		public void setVhost(String vhost)
		{
			this.vhost = vhost;
		}

		/**
		 * @return the path
		 */
		public String getPath()
		{
			return path;
		}

		/**
		 * @param path the path to set
		 */
		public void setPath(String path)
		{
			this.path = path;
		}

		/**
		 * @return the handler
		 */
		public IRequestHandler getHandler()
		{
			return handler;
		}

		/**
		 * @param handler the handler to set
		 */
		public void setHandler(IRequestHandler handler)
		{
			this.handler = handler;
		}
		
		/**
		 * 
		 */
		public int getSpecifity()
		{
			return path.length() - path.replace("/", "").length();
		}
	}

}