package jadex.extension.rs.publish;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import jadex.commons.SUtil;

/**
 *  The path manager helps resolving elements (handlers) for path with variables, e.g. a/{varb}/c.
 *  
 *  Using getBindings() one can get the concrete variable bindings for a given path/handler.
 */
public class PathManager<T>
{
	/** The list with maps per level. Each level has a map for looking up suitable elements. The results must be cut set. */
	protected List<Map<String, Collection<T>>> pathparts;
	
	/** The exactly added paths of elements. */
	protected Map<T, String> addedpaths;
	
	/**
	 *  Create a new info.
	 */
	public PathManager()
	{
		this.pathparts = new ArrayList<>();
		this.addedpaths = new HashMap<>();
	}
	
	/**
	 *  Adds a new info.
	 *  @param path The path.
	 *  @param elem The element.
	 */
	public void addPathElement(String path, T elem)
	{
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		addedpaths.put(elem, path);
		
		StringTokenizer stok = new StringTokenizer(path, "/");
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String pe = stok.nextToken();
			if(pe.startsWith("{") && pe.endsWith("}"))
				pe = "*";

			Map<String, Collection<T>> hmap = getPathPartMap(i, true);
			if(hmap==null)
				hmap = new HashMap<>();
			
			Collection<T> handlers = hmap.get(pe);
			if(handlers==null)
			{
				handlers = new ArrayList<T>();
				hmap.put(pe, handlers);
			}	
			handlers.add(elem);
		}
	}
	
	/**
	 *  Get the element for a path.
	 *  @param path The path.
	 *  @return The element.
	 */
	public T getElementForPath(String path)
	{
		Collection<T> res = getElementsForPath(path);
		return res.size()>0? res.iterator().next(): null;
	}
	
	/**
	 *  Get the element for a path.
	 *  @param path The path.
	 *  @return The element.
	 */
	public Collection<T> getElementsForPath(String path)
	{
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);

		StringTokenizer stok = new StringTokenizer(path, "/");
		
		Collection<T> res = null;
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String pe = stok.nextToken();
			Map<String, Collection<T>> hmap = getPathPartMap(i, false);
			Collection<T> shs = hmap.get(pe);
			if(shs==null)
				shs = new HashSet<>();
			shs.addAll(SUtil.notNull(hmap.get("*")));
			
			if(i==0)
			{
				res = shs;
			}
			else
			{
				Collection<T> newres = new HashSet<T>(res);
				newres.retainAll(shs);
				if(newres.size()==0)
				{
					break;
				}
				else
				{
					res = newres;
				}
			}
		}
		
		// remove handler with more path parts
		int maxdepth = getPathDepth(path);
		res = res.stream().filter(x -> getPathDepth(x)<=maxdepth).collect(Collectors.toList());
		
		Collections.sort(new ArrayList<T>(res), (x,y) -> getPathDepth(x)-getPathDepth(y));
		
		return res;
	}
	
	// todo: remove per element?!
	/**
	 *  @param vhost Virtual host specification.
	 *  @param path Path being handled.
	 */
	public void removeElementForPath(String path)
	{
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		
		// fetch target handler
		T elem = getElementForPath(path);
		
		addedpaths.remove(elem);
		
		// remnove that handler from the whole path
		StringTokenizer stok = new StringTokenizer(path, "/");
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String pe = stok.nextToken();
			if(pe.startsWith("{") && pe.endsWith("}"))
				pe = "*";

			Map<String, Collection<T>> hmap = getPathPartMap(i, false);
			if(hmap!=null)
			{
				Collection<T> col = hmap.get(pe);
				if(col!=null)
				{
					for(T tmp: col)
					{
						if(tmp.equals(elem))
							col.remove(tmp);
						if(col.size()==0)
							hmap.remove(pe);
					}
				}
			}
		}
	}
	
	/**
	 *  Get the variable bindings for path variables.
	 *  @param path The path.
	 *  @return The variables for the path.
	 */
	public Map<String, String> getBindingsForPath(String path)
	{
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
	
		Map<String, String> ret = new HashMap<>();
		
		T elem = getElementForPath(path);
		if(elem!=null)
		{
			// concrete path without variables
			StringTokenizer stok1 = new StringTokenizer(path, "/");
			// added path with variables
			StringTokenizer stok2 = new StringTokenizer(addedpaths.get(elem), "/");
			
			while(stok1.hasMoreTokens() && stok2.hasMoreTokens())
			{
				String cpe = stok1.nextToken();
				String ape = stok2.nextToken();
				
				if(ape.startsWith("{") && ape.endsWith("}"))
				{
					String varname = ape.substring(1, ape.length()-1);
					ret.put(varname, cpe);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the variable bindings for path variables.
	 *  @param elem The element.
	 *  @return The variables for the element.
	 * /
	public Map<String, String> getBindingsForElement(T elem)
	{	
		Map<String, String> ret = new HashMap<>();
	
		String path = getPathForElement(elem);
		
		if(path!=null)
		{
			if(path.endsWith("/"))
				path = path.substring(0, path.length()-1);
		
			// concrete path without variables
			StringTokenizer stok1 = new StringTokenizer(path, "/");
			// added path with variables
			StringTokenizer stok2 = new StringTokenizer(addedpaths.get(elem), "/");
			
			while(stok1.hasMoreTokens() && stok2.hasMoreTokens())
			{
				String cpe = stok1.nextToken();
				String ape = stok2.nextToken();
				
				if(ape.startsWith("{") && ape.endsWith("}"))
				{
					String varname = ape.substring(1, ape.length()-1);
					ret.put(varname, cpe);
				}
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Get a path part map for a level.
	 *  @param level The level.
	 *  @return The map or null.
	 */
	protected Map<String, Collection<T>> getPathPartMap(int level, boolean add)
	{
		if(add && level>=pathparts.size())
		{
			for(int i=pathparts.size()-1; i<=level; i++)
			{
				Map<String, Collection<T>> map = new HashMap<>();
				pathparts.add(map);
			}
		}
		return level<pathparts.size()? pathparts.get(level): null;
	}
	
	/**
	 *  Get the path depth of an element.
	 *  @param path The path.
	 *  @return The path depth.
	 */
	public int getPathDepth(String path)
	{
		return path==null? 0: path.length() - path.replace("/", "").length();
	}
	
	/**
	 *  Get the path depth of an element.
	 *  @param elem The element.
	 *  @return The path depth.
	 */
	public int getPathDepth(T elem)
	{
		String path = addedpaths.get(elem);
		return getPathDepth(path);
	}
	
	/**
	 *  Get the path for an element.
	 *  @param elem The element.
	 *  @return The added path.
	 */
	public String getPathForElement(T elem)
	{
		return addedpaths.get(elem);
	}
	
	/**
	 *  Get the number of added paths.
	 *  @return The number of added paths.
	 */
	public int size()
	{
		return addedpaths.size();
	}
	
	/**
	 *  Get the added elements.
	 */
	public Collection<T> getElements()
	{
		return addedpaths.keySet();
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		PathManager<String> pm = new PathManager<String>();
		
		pm.addPathElement("a/b/c", "a/b/c");
		pm.addPathElement("a/{varb}/c", "a/{varb}/c");
		pm.addPathElement("a/b/{varc}", "a/b/{varc}");
		pm.addPathElement("a/{varb}/{varc}", "a/{varb}/{varc}");
		pm.addPathElement("a/b/c/d", "a/b/c/d");
		pm.addPathElement("aa/bb/{cc}", "aa/bb/{cc}");
		
		System.out.println("a/b/c: "+pm.getElementForPath("a/b/c")+" "+pm.getBindingsForPath("a/b/c"));
		System.out.println("a/bbb/c: "+pm.getElementForPath("a/bbb/c")+" "+pm.getBindingsForPath("a/bbb/c"));
		System.out.println("a/dummy/c: "+pm.getElementForPath("a/dummy/c")+" "+pm.getBindingsForPath("a/dummy/c"));
		System.out.println("a/b/c/d: "+pm.getElementForPath("a/b/c/d")+" "+pm.getBindingsForPath("a/b/c/d"));
		System.out.println("d/b/c: "+pm.getElementForPath("d/b/c")+" "+pm.getBindingsForPath("d/b/c"));
		System.out.println("a/lok/doc: "+pm.getElementForPath("a/lok/doc")+" "+pm.getBindingsForPath("a/lok/doc"));
	}
}
