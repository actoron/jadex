package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.commons.SUtil;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

/**
 * 
 */
public class ComponentFeatureFactory implements IComponentFeatureFactory
{
	/** The type. */
	protected Class<?> type;
	
	/** The presdecessors. */
	protected Set<Class<?>> pres;
	
	/** The successors. */
	protected Set<Class<?>> sucs;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor for type level.
	 */
	public ComponentFeatureFactory(Class<?> type)
	{
		this.type = type;
	}
	
	/**
	 *  Bean constructor for type level.
	 */
	public ComponentFeatureFactory(Class<?> type, Class<?>[] pres, Class<?>[] sucs)
	{
		this.type = type;
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
		return pres!=null? pres: (Set)Collections.emptySet();
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
			Constructor<?> con = type.getConstructor(new Class<?>[]{IInternalAccess.class, ComponentCreationInfo.class});
			return (IComponentFeature)con.newInstance(new Object[]{access, info});
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
