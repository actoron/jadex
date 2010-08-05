package jadex.bridge;

import jadex.commons.Tuple;
import jadex.service.IResultSelector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *  Result selector for finding a component factory. 
 */
public class ComponentFactorySelector implements IResultSelector
{
	//-------- attributes --------
	
	/** The component type. */
	protected String	type;
	
	/** The model to be loaded. */
	protected String	model;
	
	/** The imports (if any). */
	protected String[]	imports;
	
	/** The class loader (if any). */
	protected ClassLoader	classloader;
	
	//-------- constructors --------
	
	/**
	 *  Find a matching component factory.
	 *  @param model	The model to be loaded.
	 *  @param imports	The imports (if any).
	 *  @param classloader	The class loader (if any).
	 */
	public ComponentFactorySelector(String model, String[] imports, ClassLoader classloader)
	{
		this.model	= model;
		this.imports	= imports;
		this.classloader	= classloader;
	}
	
	/**
	 *  Find a component factory for loading a specific component type.
	 *  @param type	The component type.
	 */
	public ComponentFactorySelector(String type)
	{
		this.type	= type;
	}
	
	//-------- IResultSelector interface --------
	
	/**
	 *  Called for each searched service provider node.
	 *  @param services	The provided services (class->list of services).
	 *  @param results	The collection to which results should be added.
	 */
	public void	selectServices(Map services, Collection results)
	{
		if(services!=null)
		{
			Collection	coll	= (Collection)services.get(IComponentFactory.class);
			if(coll!=null)
			{
				for(Iterator it=coll.iterator(); results.isEmpty() && it.hasNext(); )
				{
					IComponentFactory	fac	= (IComponentFactory)it.next();
					boolean	match;
					if(type!=null)
					{
						match	= Arrays.asList(fac.getComponentTypes()).contains(type);
					}
					else
					{
						match	= fac.isLoadable(model, imports, classloader);
					}
					
					if(match)
					{
						results.add(fac);
					}
				}
			}
		}
	}
	
	/**
	 *  Get the result.
	 *  Called once after search is finished.
	 *  @param results	The collection of selected services.
	 *  @return A single service or a list of services.
	 */
	public Object	getResult(Collection results)
	{
		return results.isEmpty() ? null : results.toArray()[0];
	}
	
	/**
	 *  Test if the search result is sufficient to stop the search.
	 *  @param results	The collection of selected services.
	 *  @return True, if the search should be stopped.
	 */
	public boolean	isFinished(Collection results)
	{
		return !results.isEmpty();
	}
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey()
	{
		return type!=null ? new Tuple(new Object[]{getClass(), type})
			: new Tuple(new Object[]{getClass(), model, imports!=null ? (Object)new Tuple(imports) : "null", classloader!=null ? (Object)classloader : "null"});
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ComponentFactorySelector( type=" + type + ", model=" + model
			+ ", imports="+ (imports != null ? Arrays.asList(imports) : null) + ")";
	}
}
