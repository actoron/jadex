package jadex.bridge;

import java.util.Arrays;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.IAsyncFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Filtering specific component factories.
 *  a) per model type (bdi, mirco, etc.)
 *  b) per model filename
 */
public class FactoryFilter implements IAsyncFilter<IComponentFactory>
{
	//-------- attributes --------

	/** The component type. */
	protected String	type;
	
	/** The model to be loaded. */
	protected String	model;
	
	/** The imports (if any). */
	protected String[]	imports;
	
	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	//-------- constructors --------
	
	/**
	 *  Find a matching component factory.
	 *  @param model	The model to be loaded.
	 *  @param imports	The imports (if any).
	 *  @param classloader	The class loader (if any).
	 */
	public FactoryFilter(String model, String[] imports, IResourceIdentifier rid)
	{
		this.model	= model;
		this.imports	= imports;
		this.rid = rid;
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
	public IFuture<Boolean> filter(IComponentFactory obj)
	{
		Future<Boolean> ret =  new Future<Boolean>();
		
		IComponentFactory fac = (IComponentFactory)obj;
		
		if(type!=null)
		{
			ret.setResult(Arrays.asList(fac.getComponentTypes()).contains(type));
		}
		else
		{
			fac.isLoadable(model, imports, rid)
				.addResultListener(new DelegationResultListener<Boolean>(ret));
		}
		
		return ret;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rid == null) ? 0 : rid.hashCode());
		result = prime * result + FactoryFilter.hashCode(imports);
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

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
	
	/**
	 *  Test if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof FactoryFilter)
		{
			FactoryFilter other = (FactoryFilter)obj;
		
			ret = SUtil.equals(rid, other.rid) && Arrays.equals(imports, other.imports) &&
				SUtil.equals(model, other.model) && SUtil.equals(type, other.type);
		}
		return ret;		
	}

}
