package jadex.base.service.library;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *  The delegation url classloader is responsible for managing
 *  a own resource plus a set of fixed delegate loaders.
 */
public class DelegationURLClassLoader extends URLClassLoader
{
	//-------- attributes --------
	
	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** The base class loader. */
	protected ClassLoader basecl;
	
	/** The delegation classloader. */
	protected DelegationURLClassLoader[] delegates;
	
	/** The flattened transitive dependencies without duplicates
	 *  (created lazy from delegates list). */
	protected Set<DelegationURLClassLoader>	dependencies;
	
	//-------- constructors --------
	
	/**
	 *  Create a new classloader.
	 */
	public DelegationURLClassLoader(ClassLoader basecl, DelegationURLClassLoader[] delegates)
	{
		this(null, basecl, delegates);
	}
	
	/**
	 *  Create a new classloader.
	 */
	public DelegationURLClassLoader(IResourceIdentifier rid, ClassLoader basecl, DelegationURLClassLoader[] delegates)
	{
		/* $if !android $ */
		super(rid!=null && rid.getLocalIdentifier()!=null? new URL[]{rid.getLocalIdentifier().getUrl()}: new URL[0], null);
		/* $else $
		super(rid!=null && rid.getLocalIdentifier()!=null? new URL[]{rid.getLocalIdentifier().getUrl()}: new URL[0], basecl);
		$endif $ */
		this.rid = rid;
		this.basecl	= basecl;
		this.delegates = delegates;
//		System.out.println("d1 : "+url+" "+SUtil.arrayToString(delegates));
	}

	//-------- methods --------
	
	/**
	 *  Get the delegates.
	 *  @return The delegates.
	 */
	public DelegationURLClassLoader[] getDelegateClassLoaders()
	{
		return delegates;
	}
	
	/**
	 *  Get transitive dependencies as flattened set (without duplicates).
	 */
	public Set<DelegationURLClassLoader>	getFlattenedDependencies()
	{
		if(dependencies==null)
		{
			synchronized(this)
			{
				if(dependencies==null)
				{
					dependencies	= new LinkedHashSet<DelegationURLClassLoader>();
					for(int i=0; i<delegates.length; i++)
					{
						dependencies.add(delegates[i]);
						dependencies.addAll(delegates[i].getFlattenedDependencies());
					}
					
//					System.out.println("Dependencies: "+rid+", "+dependencies.size());
//					for(DelegationURLClassLoader dep: dependencies)
//					{
//						System.out.println("\t"+dep.getResourceIdentifier());
//					}
				}
			}
		}
		return dependencies;
	}
	
	/**
	 *  Get the own url.
	 */
	public URL getURL()
	{
		URL[] urls = getURLs();
		return urls!=null && urls.length>0? urls[0]: null; 
	}
	
	/**
	 *  Get the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return rid;
	}
	
	/**
	 *  Get all managed resource identifiers inlcuding all subdependencies.
	 *  @return The resource identifiers.
	 */
	public Set<IResourceIdentifier> getAllResourceIdentifiers()
	{
		Set<IResourceIdentifier> ret = new LinkedHashSet<IResourceIdentifier>();
		if(delegates!=null)
		{
			for(int i=0; i<delegates.length; i++)
			{
				ret.addAll(delegates[i].getAllResourceIdentifiers());
			}
		}
		if(getResourceIdentifier()!=null)
			ret.add(getResourceIdentifier());
		return ret;
	}
	
	/**
	 *  Load a class directly, without delegation to dependencies or base class loader
	 */
	protected Class<?>	loadDirectClass(String name, boolean resolve)	throws ClassNotFoundException
	{
//		System.out.println("loadClass: "+name+", "+rid);
		return super.loadClass(name, resolve);
	}
	
	/**
	 *  Load class.
	 *  Overridden to delegate to dependencies, if not found.
	 */
	protected Class<?>	loadClass(String name, boolean resolve)	throws ClassNotFoundException
	{
		Class<?> ret = null;
		
		if(basecl!=null)
		{
			try
			{
				ret	=  basecl.loadClass(name);
				if(resolve)
				{
					// Todo: should resolve in other class loader?
					resolveClass(ret);
				}
			}
			catch(Exception e)
			{
			}
		}
		
		if(ret==null)
		{
			try
			{
				ret	= super.loadClass(name, resolve);
			}
			catch(ClassNotFoundException e)
			{
				for(DelegationURLClassLoader dep: getFlattenedDependencies())
				{
	//				System.out.println("findClass: "+name+", "+dep);
					try
					{
						ret = dep.loadDirectClass(name, resolve);
						break;
					}
					catch (ClassNotFoundException ex)
					{
					}				
				}
			}
		}
		
		if(ret==null)
			throw new ClassNotFoundException(name);
		
		return ret;
	}

	/**
	 *  Find the resource.
	 *  @param name The name.
	 *  @return The url.
	 */
	protected URL findDirectResource(String name)
	{
		return super.findResource(name);
	}
	
	/**
	 *  Find the resource.
	 *  @param name The name.
	 *  @return The url.
	 */
	public URL findResource(String name)
	{
		URL ret = null;
		if(basecl!=null)
		{
			ret	= basecl.getResource(name);
		}
		if(ret==null)
		{
			ret = super.findResource(name);
		}
		if(ret==null)
		{
			for(DelegationURLClassLoader dep: getFlattenedDependencies())
			{
				ret = dep.findDirectResource(name);
				if(ret!=null)
					break;
			}
		}
		
		return ret;
	}

	/**
	 *  Find the resource.
	 *  @param name The name.
	 *  @return The url.
	 */
	protected Enumeration<URL> findDirectResources(String name) throws IOException
	{
		return super.findResources(name);
	}
	
	/**
	 *  Find the resource.
	 *  @param name The name.
	 *  @return The url.
	 */
	public Enumeration<URL> findResources(String name) throws IOException
	{
		Set<URL> res = new HashSet<URL>();
		if(basecl!=null)
		{
			res.addAll(Collections.list(basecl.getResources(name)));
		}
		res.addAll(Collections.list(super.findResources(name)));
		for(DelegationURLClassLoader dep: getFlattenedDependencies())
		{
			res.addAll(Collections.list(dep.findDirectResources(name)));
		}
		
		return Collections.enumeration(res);
	}

	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+"("+rid+", "+SUtil.arrayToString(delegates)+")";
	}
}
