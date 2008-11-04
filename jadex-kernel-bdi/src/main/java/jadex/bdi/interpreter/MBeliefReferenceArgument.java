package jadex.bdi.interpreter;

import jadex.bridge.IArgument;
import jadex.commons.SReflect;
import jadex.rules.state.IOAVState;


public class MBeliefReferenceArgument	implements IArgument
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The object handle for the element. */
	protected Object	handle;

	/** The object handle for the element's scope. */
	protected Object	scope;

	//-------- constructors --------
	
	/**
	 *  Create a new belief model flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	public MBeliefReferenceArgument(IOAVState state, Object scope, Object handle)
	{
		this.state	= state;
		this.handle	= handle;
		this.scope	= scope;
	}

	//-------- IArgument --------
	
	/**
	 *  Get the element name.
	 *  @return The element name.
	 */
	public String	getName()
	{
		return (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
	}

	/**
	 *  Get the description (i.e. a natural language text)
	 *  of the element.
	 *  @return The description text.
	 */
	public String	getDescription()
	{
		return (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_description);
	}
	
	/**
	 *  Get the typename.
	 *  @return The typename. 
	 */
	public String getTypename()
	{
		return SReflect.getInnerClassName((Class)state.getAttributeValue(
			getOriginalBelief(), OAVBDIMetaModel.typedelement_has_class));
	}
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname)
	{
		Object ret = null;
//		boolean found = false;
		
		// Todo: default value of references.
//		Object config;
//		if(configname==null)
//		{
//			config = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_defaultconfiguration);
//		}
//		else
//		{
//			config = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_configurations, configname);
//		}
//		
//		if(config!=null)
//		{
//			Collection inibels = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialbeliefs);
//			if(inibels!=null)
//			{
//				for(Iterator it=inibels.iterator(); it.hasNext(); )
//				{
//					Object inibel = it.next();
//					if(state.getAttributeValue(inibel, OAVBDIMetaModel.configbelief_has_ref).equals(handle))
//					{	
//						Object exp = state.getAttributeValue(inibel, OAVBDIMetaModel.belief_has_fact);
//						// todo: string rep?
//						IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
//						ret = parsedexp.getExpressionText();
//						found = true;
//					}
//				}
//			}
//		}
//		
//		if(!found)
//		{
//			Object exp = state.getAttributeValue(handle, OAVBDIMetaModel.belief_has_fact);
//			IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
//			ret = parsedexp.getExpressionText();
//		}
		
		return ret;
	}
	
	/**
	 *  Check the validity of an input.
	 *  @param input The input.
	 *  @return True, if valid.
	 */
	public boolean validate(String input)
	{
		// todo
		return true;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the original belief element.
	 */
	protected Object getOriginalBelief()
	{
		return getOriginalBelief(state, handle, scope);
	}
	
	/**
	 *  Get the original belief element.
	 */
	protected static Object getOriginalBelief(IOAVState state, Object mbelref, Object mcapa)
	{
		String name = (String)state.getAttributeValue(mbelref, OAVBDIMetaModel.elementreference_has_concrete);
		int	idx=name.indexOf('.');
		String	capname	= name.substring(0, idx);
		name = name.substring(idx+1);
		if(name.indexOf('.')!=-1)
			throw new RuntimeException("Character '.' not allowed in element names.");
		
		Object mcaparef = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capname);
		Object mscope = state.getAttributeValue(mcaparef, OAVBDIMetaModel.capabilityref_has_capability);
		Object mbel = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefs, name);
		
		if(mbel==null)
		{
			mbelref = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_beliefrefs, name);
			mbel = getOriginalBelief(state, mbelref, mscope);
		}
		
		return mbel;
	}
}
