package jadex.bdi.interpreter;

import jadex.bridge.IArgument;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Beliefset/ref implementation for an argument.
 */
public class MBeliefSetArgument	implements IArgument
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
	public MBeliefSetArgument(IOAVState state, Object scope, Object handle)
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
	 *  Find the beliefset/ref value.
	 */
	protected Object findDefaultValue(Object scope, Object handle, String configname)
	{
		Object ret = null;
		boolean found = false;
		
		// Search initial value in configurations.
		Object config;
		if(configname==null)
		{
			config = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_defaultconfiguration);
		}
		else
		{
			config = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_configurations, configname);
		}
	
		if(config!=null)
		{
			Collection inibelsets = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialbeliefsets);
			if(inibelsets!=null)
			{
				for(Iterator it=inibelsets.iterator(); it.hasNext(); )
				{
					Object inibelset = it.next();
					if(state.getAttributeValue(inibelset, OAVBDIMetaModel.configbeliefset_has_ref).equals(handle))
					{	
						Collection vals = state.getAttributeValues(inibelset, OAVBDIMetaModel.beliefset_has_facts);
						if(vals==null)
						{
							Object exp = state.getAttributeValue(inibelset, OAVBDIMetaModel.beliefset_has_factsexpression);
							IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
							ret = parsedexp.getExpressionText();
						}
						else
						{
							ret = vals.toString();
						}
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
			if(OAVBDIMetaModel.beliefset_type.equals(state.getType(handle)))
			{
				Collection vals = state.getAttributeValues(handle, OAVBDIMetaModel.beliefset_has_facts);
				if(vals==null)
				{
					Object exp = state.getAttributeValue(handle, OAVBDIMetaModel.beliefset_has_factsexpression);
					IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
					ret = parsedexp.getExpressionText();
				}
				else
				{
					ret = vals.toString();
				}
			}
			else
			{
				String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				Object belref;
				int idx = name.indexOf(".");
				if(idx==-1)
				{
					belref = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_beliefsetrefs, name);
					name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
				}
				String capaname = name.substring(0, idx);
				String belname = name.substring(idx+1);
				
				Object subcaparef = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
				Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefsets, belname);
				if(belref==null)
					belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, belname);
				
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
