package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

/**
 * 
 */
public class ComponentFeatureFactory implements IComponentFeatureFactory
{
	/** The interface type. */
	protected Class<?> type;
	
	/** The implementation type. */
	protected Class<?> impl;
	
	/** The presdecessors. */
	protected Set<Class<?>> pres;
	
	/** The successors. */
	protected Set<Class<?>> sucs;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor for type level.
	 */
	public ComponentFeatureFactory(Class<?> type, Class<?> impl)
	{
		this(type, impl, null, null);
	}
	
	/**
	 *  Bean constructor for type level.
	 */
	public ComponentFeatureFactory(Class<?> type, Class<?> impl, Class<?>[] pres, Class<?>[] sucs)
	{
		this.type = type;
		this.impl = impl;
		this.pres = pres==null? null: (Set)SUtil.arrayToSet(pres);
		this.sucs = sucs==null? null: (Set)SUtil.arrayToSet(sucs);
	}
	
	//-------- IComponentFeature interface / type level --------
	
	/**
	 *  Get the predecessors, i.e. features that should be inited first.
	 */
	public Set<Class<?>> getPredecessors()
	{
		return pres!=null? pres: (Set)Collections.emptySet();
	}
	
	/**
	 *  Get the successors, i.e. features that should be inited after this feature.
	 */
	public Set<Class<?>>	getSuccessors()
	{
		return sucs!=null? sucs: (Set)Collections.emptySet();
	}
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?> getType()
	{
		return type;
	}
	
	/**
	 *  Create an instance of the feature.
	 *  @param access	The access of the component.
	 *  @param info	The creation info.
	 */
	public IComponentFeature	createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		try
		{
			Constructor<?> con = impl.getConstructor(new Class<?>[]{IInternalAccess.class, ComponentCreationInfo.class});
			return (IComponentFeature)con.newInstance(new Object[]{access, info});
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Create a string representation.
	 */
	public String	toString()
	{
		return "ComponentFeatureFactory("+SReflect.getUnqualifiedClassName(type)+")";
	}
}
