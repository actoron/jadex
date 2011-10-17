package maventest;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.SUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 *  The delegation url classloader is responsible for managing
 *  a own resource plus a set of fixed delegate loaders.
 */
public class DelegationURLClassLoader extends URLClassLoader
{
	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** The delegation classloader. */
	protected DelegationURLClassLoader[] delegates;
	
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
		super(rid!=null && rid.getLocalIdentifier()!=null? new URL[]{rid.getLocalIdentifier().getSecondEntity()}: new URL[0], basecl);
		this.rid = rid;
		this.delegates = delegates;
//		System.out.println("d1 : "+url+" "+SUtil.arrayToString(delegates));
	}

	
	
	/**
	 *  Get the delegates.
	 *  @return The delegates.
	 */
	public DelegationURLClassLoader[] getDelegateClassLoaders()
	{
		return delegates;
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
	 *  Get all managed urls inlcuding all subdependencies.
	 *  @return The urls.
	 */
	public Set<URL> getAllURLs()
	{
		Set<URL> ret = new HashSet<URL>();
		if(delegates!=null)
		{
			for(int i=0; i<delegates.length; i++)
			{
				ret.addAll(delegates[i].getAllURLs());
			}
		}
		if(getURL()!=null)
			ret.add(getURL());
		return ret;
	}
	
	/**
	 *  Find a class.
	 *  @param name The class name.
	 *  @return The class.
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		Class<?> ret = null;
		
		try
		{
			ret = super.findClass(name);
		}
		catch(ClassNotFoundException e)
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
						ret = delegates[i].loadClass(name);
					}
					catch (ClassNotFoundException e)
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
