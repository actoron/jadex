package jadex.bridge.service.component;

import jadex.bridge.FactoryFilter;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.BasicResultSelector;
import jadex.bridge.service.types.factory.IComponentFactory;

import java.util.Collection;
import java.util.Map;

/**
 *  Result selector for finding a component factory. 
 */
public class ComponentFactorySelector<T> extends BasicResultSelector<T>
{	
	//-------- constructors --------
	
	/**
	 *  Find a matching component factory.
	 *  @param model	The model to be loaded.
	 *  @param imports	The imports (if any).
	 *  @param classloader	The class loader (if any).
	 */
	public ComponentFactorySelector(String model, String[] imports, IResourceIdentifier rid)
	{
		super(new FactoryFilter(model, imports, rid));
	}
	
	/**
	 *  Find a component factory for loading a specific component type.
	 *  @param type	The component type.
	 */
	public ComponentFactorySelector(String type)
	{
		super(new FactoryFilter(type));
	}
	
	//-------- IResultSelector interface --------
	
	/**
	 *  Get all services of the map as linear collection.
	 */
	public IService[] generateServiceArray(Map<Class<?>, Collection<IService>> servicemap)
	{
		Collection<IService> tmp = servicemap.get(IComponentFactory.class);
		return tmp==null? IService.EMPTY_SERVICES: (IService[])tmp.toArray(new IService[tmp.size()]);
	}
}
