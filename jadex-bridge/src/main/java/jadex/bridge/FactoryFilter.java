package jadex.bridge;

import jadex.commons.IFilter;
import jadex.commons.SUtil;

import java.util.Arrays;

/**
 * 
 */
public class FactoryFilter implements IFilter
{
	//-------- attributes --------
	
	/**
	 * Returns a hash code value for the array
	 * @param array the array to create a hash code value for
	 * @return a hash code value for the array
	 */
	private static int hashCode(Object[] array)
	{
		int prime = 31;
		if(array == null)
			return 0;
		int result = 1;
		for(int index = 0; index < array.length; index++)
		{
			result = prime * result
					+ (array[index] == null ? 0 : array[index].hashCode());
		}
		return result;
	}

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
	public FactoryFilter(String model, String[] imports, ClassLoader classloader)
	{
		this.model	= model;
		this.imports	= imports;
		this.classloader	= classloader;
	}
	
	/**
	 *  Find a component factory for loading a specific component type.
	 *  @param type	The component type.
	 */
	public FactoryFilter(String type)
	{
		this.type	= type;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if an object passes the filter.
	 *  @return True, if passes the filter.
	 */
	public boolean filter(Object obj)
	{
		boolean	match = false;
		
		if(obj instanceof IComponentFactory)
		{
			IComponentFactory fac = (IComponentFactory)obj;
			
			if(type!=null)
			{
				match	= Arrays.asList(fac.getComponentTypes()).contains(type);
			}
			else
			{
				match	= fac.isLoadable(model, imports, classloader);
			}
		}
		return match;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classloader == null) ? 0 : classloader.hashCode());
		result = prime * result + FactoryFilter.hashCode(imports);
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 *  Test if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof FactoryFilter)
		{
			FactoryFilter other = (FactoryFilter)obj;
		
			ret = SUtil.equals(classloader, other.classloader) && Arrays.equals(imports, other.imports) &&
				SUtil.equals(model, other.model) && SUtil.equals(type, other.type);
		}
		return ret;		
	}

}
