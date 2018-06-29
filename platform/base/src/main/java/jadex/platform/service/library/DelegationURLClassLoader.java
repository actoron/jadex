package jadex.platform.service.library;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.IResourceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;


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
	protected List<DelegationURLClassLoader> delegates;
	
	/** The parent classloaders (i.e. the support). */
	protected List<DelegationURLClassLoader> parents;
	
	/** The flattened transitive dependencies without duplicates
	    (created lazy from delegates list). */
	protected volatile Set<DelegationURLClassLoader>	dependencies;
	
	//-------- constructors --------
	
	/**
	 *  Create a new classloader.
	 */
	public DelegationURLClassLoader(ClassLoader basecl, DelegationURLClassLoader[] delegates)
	{
		this(null, null, basecl, delegates);
	}
	
	/**
	 *  Create a new classloader.
	 */
	public DelegationURLClassLoader(IResourceIdentifier rid, URL url, ClassLoader basecl, DelegationURLClassLoader[] delegates)
	{
		super(url!=null? new URL[]{url}: new URL[0],
			// No parent class loader to avoid multiple lookups of unavailable classes (not supported on android)
			SReflect.isAndroid()? basecl : null);

		this.rid = rid;
		this.basecl	= basecl;
		this.delegates = delegates==null? new ArrayList(): SUtil.arrayToList(delegates);
		this.parents = new ArrayList<DelegationURLClassLoader>();
	
//		addParentClassLoader(parent);
//		if(rid!=null && rid.getLocalIdentifier()!=null)
//			System.out.println("delclassloader : "+rid.getLocalIdentifier().getUrl()+" "+SUtil.arrayToString(delegates)+" "+hashCode());
	}

	//-------- methods --------
	
	/**
	 *  Get the delegates.
	 *  @return The delegates.
	 */
	public List<IResourceIdentifier> getDelegateResourceIdentifiers()
	{
		List<IResourceIdentifier> ret = new ArrayList<IResourceIdentifier>();
		for(int i=0; i<delegates.size(); i++)
		{
			ret.add(delegates.get(i).getResourceIdentifier());
		}
		return ret;
	}
	
	/**
	 *  Get the delegates.
	 *  @return The delegates.
	 */
	public DelegationURLClassLoader[] getDelegateClassLoaders()
	{
		return delegates.toArray(new DelegationURLClassLoader[delegates.size()]);
	}
	
	/**
	 *  Add a new delegate loader.
	 *  @param classloader The delegate classloader.
	 */
	public synchronized boolean addDelegateClassLoader(DelegationURLClassLoader classloader)
	{
		if(classloader==null)
			throw new IllegalArgumentException("Must not null.");

//		if(rid==null)
//			System.out.println("adding:: "+classloader);
		
		if(delegates.contains(classloader))
			return false;
		delegates.add(classloader);
		dependencies = null;
		return true;
	}
	
	/**
	 *  Remove a new delegate loader.
	 *  @param classloader The delegate classloader.
	 *  @return True, if classloader was removed.
	 */
	public synchronized boolean removeDelegateClassLoader(DelegationURLClassLoader classloader)
	{
		if(classloader==null)
			throw new IllegalArgumentException("Must not null.");

		boolean ret = delegates.remove(classloader);
		if(ret)
			dependencies = null;
		return ret;
	}
	
	/**
	 *  Add a parent loader.
	 *  @param classloader The parent loader.
	 */
	public synchronized boolean addParentClassLoader(DelegationURLClassLoader parent)
	{
		if(parent==null)
			throw new IllegalArgumentException("Must not null.");
		
		if(parents.contains(parent))
			return false;
		this.parents.add(parent);
		return true;
	}
	
	/**
	 *  Remove a parent classloader.
	 */
	public synchronized boolean removeParentClassLoader(DelegationURLClassLoader parent)
	{
		if(parent==null)
			throw new IllegalArgumentException("Must not null.");

		return parents.remove(parent);
	}
	
	/**
	 *  Test if has parent classloader.
	 */
	public boolean hasParentClassLoader()
	{
		return !parents.isEmpty();
	}
	
	/**
	 *  Get the parent class loaders.
	 */
	public synchronized List<DelegationURLClassLoader> getParentClassLoaders()
	{
		return (List<DelegationURLClassLoader>)(((ArrayList)parents).clone());
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
					dependencies = computeFlattenedDependencies(getDelegates());
				}
			}
		}
		return dependencies;
	}
	
	/**
	 *  Get transitive dependencies as flattened set (without duplicates).
	 */
	public static Set<DelegationURLClassLoader>	computeFlattenedDependencies(List<DelegationURLClassLoader> deps)
	{
		Set<DelegationURLClassLoader> ret = new LinkedHashSet<DelegationURLClassLoader>();
		for(int i=0; i<deps.size(); i++)
		{
			ret.add(deps.get(i));
			ret.addAll(deps.get(i).getFlattenedDependencies());
		}
					
		return ret;
	}
	
	/**
	 *  Get the delegates.
	 */
	public List<DelegationURLClassLoader> getDelegates()
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
	 *  Set the rid.
	 *  @param rid The rid to set.
	 */
	public void setResourceIdentifier(IResourceIdentifier rid)
	{
		this.rid = rid;
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
			for(int i=0; i<delegates.size(); i++)
			{
				ret.addAll(delegates.get(i).getAllResourceIdentifiers());
			}
		}
		if(getResourceIdentifier()!=null)
			ret.add(getResourceIdentifier());
		return ret;
	}

//	protected Class<?> findClass(String name) throws ClassNotFoundException
//	{
//		System.out.println("find: "+name);
//		return super.findClass(name);
//	}
	
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
//				e.printStackTrace();
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
	 * 
	 */
	protected ClassLoader getBaseClassLoader()
	{
		return basecl;
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
	 * 
	 */
	public boolean isClassLoaderCompatible(Class<?> clazz)
	{
		ClassLoader clcl = clazz.getClassLoader();
		boolean ret = clcl.equals(this);
		if(!ret)
		{
			Set<ClassLoader> res = new HashSet<ClassLoader>();
			getAllParentLoaders(this, res);
			ret = res.contains(clcl);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public static void getAllParentLoaders(ClassLoader cl, Set<ClassLoader> cls)
	{
		cls.add(cl);
		if(cl.getParent()!=null && !cls.contains(cl.getParent()))
		{
			cls.add(cl.getParent());
			getAllParentLoaders(cl.getParent(), cls);
		}
		if(cl instanceof DelegationURLClassLoader)
		{
			DelegationURLClassLoader dcl = (DelegationURLClassLoader)cl;
			ClassLoader bcl = dcl.getBaseClassLoader();
			if(bcl!=null && !cls.contains(bcl))
			{
				cls.add(bcl);
				getAllParentLoaders(bcl, cls);
			}
			for(DelegationURLClassLoader tst: dcl.getParentClassLoaders())
			{
				if(!cls.contains(tst))
				{
					cls.add(tst);
					getAllParentLoaders(tst, cls);
				}
			}
		}
	}
	
	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
//		return SReflect.getInnerClassName(getClass())+"("+rid+", "+SUtil.arrayToString(delegates)+")";
		return SReflect.getInnerClassName(getClass())+"("+rid+", "+delegates.size()+", "+basecl+")";
	}
	
//	/**
//	 *  Get a string representation.
//	 */
//	public String toString()
//	{
//		int num = 0;
//		try
//		{
//			Field f = ClassLoader.class.getDeclaredField("classes");
//			f.setAccessible(true);
//			Vector<Class> classes = (Vector<Class>)f.get(this);
//			num = classes.size();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		return SReflect.getInnerClassName(getClass())+"("+rid+", loaded classes="+num;
//	}
		
}