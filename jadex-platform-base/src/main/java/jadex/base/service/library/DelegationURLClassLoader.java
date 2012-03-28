package jadex.base.service.library;

import jadex.bridge.IResourceIdentifier;

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
		super(rid!=null && rid.getLocalIdentifier()!=null? new URL[]{rid.getLocalIdentifier().getUrl()}: new URL[0], basecl);
		this.rid = rid;
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
	
//	/**
//	 *  Get all managed urls inlcuding all subdependencies.
//	 *  @return The urls.
//	 */
//	public Set<URL> getAllURLs()
//	{
//		Set<URL> ret = new HashSet<URL>();
//		if(delegates!=null)
//		{
//			for(int i=0; i<delegates.length; i++)
//			{
//				ret.addAll(delegates[i].getAllURLs());
//			}
//		}
//		if(getURL()!=null)
//			ret.add(getURL());
//		return ret;
//	}
	
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
	 *  Load a class directly, without delegation to dependencies.
	 *  Overridden to delegate to dependencies, if not found.
	 */
	protected Class<?>	loadDirectClass(String name, boolean resolve)	throws ClassNotFoundException
	{
		return super.loadClass(name, resolve);
	}
	
	/**
	 *  Load class.
	 *  Overridden to delegate to dependencies, if not found.
	 */
	protected Class<?>	loadClass(String name, boolean resolve)	throws ClassNotFoundException
	{
		Class<?> ret = null;
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
		
		if(ret==null)
			throw new ClassNotFoundException(name);
		
		return ret;
	}

//	protected static ThreadLocal<Boolean>	RECURSE	= new ThreadLocal<Boolean>();
//	
//	/**
//	 *  Find a class using super implementation or delegates.
//	 */
//	protected Class<?>	findClass(String name) throws ClassNotFoundException
//	{
//		Class<?> ret = null;
//		if(RECURSE.get()==null || RECURSE.get().booleanValue())
//		{
//			try
//			{
//				RECURSE.set(Boolean.FALSE);
//				try
//				{
//					ret	= super.findClass(name);
//				}
//				catch(ClassNotFoundException e)
//				{
//					for(DelegationURLClassLoader dep: getFlattenedDependencies())
//					{
//	//					System.out.println("findClass: "+name+", "+dep);
//						try
//						{
//							ret = dep.loadClass(name);
//							break;
//						}
//						catch (ClassNotFoundException ex)
//						{
//						}				
//					}
//				}
//				
//				if(ret==null)
//					throw new ClassNotFoundException(name);
//			}
//			finally
//			{
//				RECURSE.set(null);
//			}
//		}
//		else
//		{
//			return super.findClass(name);
//		}
//		return ret;
//	}
	
//	/**
//	 *  Find a class.
//	 *  @param name The class name.
//	 *  @return The class.
//	 */
//	protected Class<?> findClass(String name) throws ClassNotFoundException
//	{
//		Class<?> ret = null;
//		
//		try
//		{
//			ret = super.findClass(name);
//		}
//		catch(ClassNotFoundException e)
//		{
//		}
//		catch(NullPointerException e)
//		{
//			e.printStackTrace();
//		}
//		
//		if(ret==null)
//		{
//			if(delegates!=null)
//			{
//				for(int i=0; ret==null && i<delegates.length; i++)
//				{
//					try
//					{
//						ret = delegates[i].loadClass(name);
//					}
//					catch (ClassNotFoundException e)
//					{
//					}
//				}
//			}
//		}
//		
//		if(ret==null)
//			throw new ClassNotFoundException(name);
//		
//		return ret;
//	}

	
	/**
	 *  Find the resource.
	 *  @param name The name.
	 *  @return The url.
	 */
	public URL findResource(String name)
	{
		URL ret = null;
		
		try
		{
			ret = super.findResource(name);
		}
		catch(Exception e)
		{
		}
		
		if(ret==null)
		{
			if(delegates!=null)
			{
				for(int i=0; ret==null && i<delegates.length; i++)
				{
					try
					{
						ret = delegates[i].getResource(name);
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Find the resource.
	 *  @param name The name.
	 *  @return The url.
	 */
	public Enumeration<URL> findResources(String name) throws IOException
	{
		Set<URL> res = new HashSet<URL>();
		URL ret = null;
		
		try
		{
			res.addAll(Collections.list(super.findResources(name)));
		}
		catch(Exception e)
		{
		}
		
		if(ret==null)
		{
			if(delegates!=null)
			{
				for(int i=0; ret==null && i<delegates.length; i++)
				{
					try
					{
						res.addAll(Collections.list(delegates[i].getResources(name)));
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		
		return Collections.enumeration(res);
	}
}
