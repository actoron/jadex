package jadex.commons.service.library;

import jadex.commons.ThreadSuspendable;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 */
public class LibraryClassLoader extends URLClassLoader
{
	//-------- attributes --------
	
	/** The service provider.*/
	protected IServiceProvider provider;
	
	/** The map of loaded classes. */
	protected Map classes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new classloader.
	 */
    public LibraryClassLoader(URL[] urls, ClassLoader parent, IServiceProvider provider) 
    {
    	super(urls, parent);
    	this.provider = provider;
    	this.classes = new HashMap();
    }

    //-------- methods --------
	
    /**
     * 
     */
    public Class findClass(String name) throws ClassNotFoundException
    {
    	Class ret = null;
    	ret = (Class)classes.get(name);
    	
    	if(ret==null)
    	{
	    	try
	    	{
	    		ret = super.findClass(name);
	    	}
	    	catch(Exception e)
	    	{
	    		// Try to find classes via library services.
	    		System.out.println("in: "+Thread.currentThread().getName());
				Collection libservices = (Collection)SServiceProvider.getServices(provider, ILibraryService.class, true, true).get(new ThreadSuspendable());
				System.out.println("libs: "+Thread.currentThread().getName()+" "+libservices);
				byte[] data = null;
				for(Iterator it=libservices.iterator(); it.hasNext() && data==null; )
				{
					ILibraryService libser = (ILibraryService)it.next();
					
					// Do not ask own library service
					if(!libser.getServiceIdentifier().getProviderId().equals(provider.getId()))
					{
						try
						{
							data = (byte[])libser.getClassDefinition(name).get(new ThreadSuspendable());
							ret = defineClass(name, data, 0, data.length);
							classes.put(name, ret);
						}
						catch(Exception ex)
						{
							throw new ClassNotFoundException(name);
						}
					}
				}
				System.out.println("out: "+Thread.currentThread().getName());
	    	}
    	}
    	
    	return ret;
    }

}