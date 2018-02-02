package jadex.micro.features.impl;

import java.util.Collections;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.SimpleParameterGuesser;
import jadex.micro.MicroModel;

/**
 *  Feature that makes pojo accessible.
 */
public class MicroPojoComponentFeature extends	AbstractComponentFeature implements IPojoComponentFeature, IValueFetcher
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IPojoComponentFeature.class, MicroPojoComponentFeature.class,
		null, null);
	
	//-------- attributes --------
	
	/** The pojo agent. */
	protected Object pojoagent;
	
	/** The parameter guesser (cached for speed). */
	protected IParameterGuesser	guesser; 
	
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroPojoComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
		try
		{
			// Create the pojo agent
			MicroModel model = (MicroModel)getComponent().getModel().getRawModel();
			this.pojoagent = model.getPojoClass().getType(model.getClassloader()).newInstance();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Get the pojoagent.
	 *  @return The pojoagent
	 */
	public Object getPojoAgent()
	{
		return pojoagent;
	}
	
	/**
	 *  Get the POJO agent object casted to the pojo class.
	 *  @return The pojo agent.
	 */
	public <T> T getPojoAgent(Class<T> pojoclass)
	{
		return (T)pojoagent;
	}
	
	/**
	 *  The feature can inject parameters for expression evaluation
	 *  by providing an optional value fetcher. The fetch order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IValueFetcher	getValueFetcher()
	{
		return this;
	}
	
	/**
	 *  Add $pojoagent to fetcher.
	 */
	public Object fetchValue(String name)
	{
		if("$pojoagent".equals(name))
		{
			return getPojoAgent();
		}
		else if(Starter.hasPlatformValue(getComponent().getComponentIdentifier(), name))
		{
			return Starter.getPlatformValue(getComponent().getComponentIdentifier(), name);
		}
		else
		{
			throw new RuntimeException("Value not found: "+name);
		}
	}
	
	/**
	 *  The feature can add objects for field or method injections
	 *  by providing an optional parameter guesser. The selection order is the reverse
	 *  init order, i.e., later features can override values from earlier features.
	 */
	public IParameterGuesser	getParameterGuesser()
	{
		if(guesser==null)
			guesser	= new SimpleParameterGuesser(super.getParameterGuesser(), Collections.singleton(pojoagent));
		return guesser;
	}
}
