package jadex.bdi.interpreter;

import jadex.bridge.IArgument;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 *  Belief implementation for an argument.
 */
public class MBeliefArgument	implements IArgument
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
	public MBeliefArgument(IOAVState state, Object scope, Object handle)
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
		return SReflect.getInnerClassName(findType(scope, handle));
	}
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname)
	{
		return  findDefaultValue(scope, handle, configname);
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
	
	/**
	 *  Find the belief/ref value.
	 */
	protected Object findDefaultValue(Object mcapa, Object handle, String configname)
	{
		Object ret = null;
		boolean found = false;
		
		// Search initial value in configurations.
		Object config;
		if(configname==null)
		{
			configname = (String)state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_defaultconfiguration);
			config = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_configurations, configname);
		}
		else
		{
			config = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_configurations, configname);
		}
	
		if(config!=null)
		{
			Object[] belres;
			if(OAVBDIMetaModel.beliefreference_type.equals(state.getType(handle)))
			{
				String ref = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				belres = AgentRules.resolveMCapability(ref, OAVBDIMetaModel.belief_type, mcapa, state);
			}
			else
			{
				belres = new Object[]{getName(), mcapa};
			}
			
			Collection inibels = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialbeliefs);
			if(inibels!=null)
			{
				for(Iterator it=inibels.iterator(); it.hasNext(); )
				{
					Object inibel = it.next();
					String ref = (String)state.getAttributeValue(inibel, OAVBDIMetaModel.configbelief_has_ref);
					Object[] inibelres = AgentRules.resolveMCapability(ref, OAVBDIMetaModel.belief_type, mcapa, state);
					
					if(Arrays.equals(inibelres, belres))
					{	
						Object exp = state.getAttributeValue(inibel, OAVBDIMetaModel.belief_has_fact);
						// todo: string rep?
						IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
						ret = parsedexp.getExpressionText();
						found = true;
					}
				}
			}
		}
		
		// If not found 
		// a) its a belief -> get default value
		// b) its a ref -> recursively call this method with ref, subcapa and config
		
		if(!found)
		{
			if(OAVBDIMetaModel.belief_type.equals(state.getType(handle)))
			{
				Object exp = state.getAttributeValue(handle, OAVBDIMetaModel.belief_has_fact);
				if(exp!=null)
				{
					IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
					ret = parsedexp.getExpressionText();
				}
			}
			else
			{
				String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				Object belref;
				int idx = name.indexOf(".");
				if(idx==-1)
				{
					belref = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, name);
					name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
				}
				String capaname = name.substring(0, idx);
				String belname = name.substring(idx+1);
				
				Object subcaparef = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
				Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefs, belname);
				if(belref==null)
					belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname);
				
				String subconfigname = null;
				if(config!=null)
				{
					Collection inicapas = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialcapabilities);
					if(inicapas!=null)
					{
						for(Iterator it=inicapas.iterator(); subconfigname==null && it.hasNext(); )
						{
							Object inicapa = it.next();
							
							if(state.getAttributeValue(inicapa, OAVBDIMetaModel.configelement_has_ref).equals(subcaparef))
							{	
								subconfigname = (String)state.getAttributeValue(inicapa, OAVBDIMetaModel.initialcapability_has_configuration);
							}
						}
					}
				}
				
				ret = findDefaultValue(subcapa, belref, subconfigname);
			}
		}
		
		return ret;
	}

	/**
	 *  Find the belief/ref type.
	 */
	protected Class	findType(Object scope, Object handle)
	{
		Class	ret	= null;
		
		if(OAVBDIMetaModel.belief_type.equals(state.getType(handle)))
		{
			ret	= (Class)state.getAttributeValue(handle, OAVBDIMetaModel.typedelement_has_class);
		}
		else
		{
			String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
			Object belref;
			int idx = name.indexOf(".");
			if(idx==-1)
			{
				belref = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_beliefrefs, name);
				name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
			}
			String capaname = name.substring(0, idx);
			String belname = name.substring(idx+1);
			
			Object subcaparef = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
			Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
			
			belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefs, belname);
			if(belref==null)
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname);
			
			ret = findType(subcapa, belref);
		}
		
		return ret;
	}
}
