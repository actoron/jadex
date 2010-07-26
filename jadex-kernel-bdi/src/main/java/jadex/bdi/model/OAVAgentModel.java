package jadex.bdi.model;

import jadex.bdi.runtime.interpreter.Report;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.rulesystem.IRule;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  The agent model contains the OAV agent model in a state and
 *  a type-specific compiled rulebase (matcher functionality).
 */
public class OAVAgentModel	extends OAVCapabilityModel
{
	//-------- attributes --------

	/** The compiled rulebase of the agent (including additional capability rules). */
	protected IPatternMatcherFunctionality matcherfunc;
	
	/** The properties (e.g. rule break points). */
	protected Map	properties;
	
	//-------- constructors --------

	/**
	 *  Create a model.
	 */
	public OAVAgentModel(IOAVState state, Object handle, Set types, String filename, long lastmod, Report report)
	{
		super(state, handle, types, filename, lastmod, report);
	}
	
	//-------- IAgentModel methods --------
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return true;
	}
	
	/**
	 *  Get the model type.
	 *  @reeturn The model type (kernel specific).
	 * /
	public String getType()
	{
		// todo: 
		return "v2bdiagent";
	}*/


	/**
	 *  Get the matcherfunc.
	 *  @return The matcherfunc.
	 */
	public IPatternMatcherFunctionality getMatcherFunctionality()
	{
		return matcherfunc;
	}
		
	/**
	 *  Get the matcherfunc.
	 *  @return The matcherfunc.
	 */
	public void setMatcherFunctionality(IPatternMatcherFunctionality matcherfunc)
	{
		this.matcherfunc	= matcherfunc;
	}
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools. 
	 *  @return The properties.
	 */
	public Map	getProperties()
	{
		if(properties==null)
		{
			this.properties = new HashMap();

			// Debugger breakpoints for BDI and user rules.
			List names = new ArrayList();
			for(Iterator it=matcherfunc.getRulebase().getRules().iterator(); it.hasNext(); )
				names.add(((IRule)it.next()).getName());
			properties.put("debugger.breakpoints", names);
			
			addCapabilityProperties(properties, handle);
		}
		return properties;
	}
	
	/**
	 *  Add the properties of a capability.
	 *  @param props The map to add the properties.
	 *  @param capa The start capability.
	 */
	public void addCapabilityProperties(Map props, Object capa)
	{
		// Properties from loaded model.
		Collection	oprops	= state.getAttributeKeys(capa, OAVBDIMetaModel.capability_has_properties);
		if(oprops!=null)
		{
			for(Iterator it=oprops.iterator(); it.hasNext(); )
			{
				Object	key	= it.next();
				Object	mexp	= state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_properties, key);
				Class	clazz	= (Class)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_class);
				// Ignore future properties, which are evaluated at component instance startup time.
				if(clazz==null || !SReflect.isSupertype(IFuture.class, clazz))
				{
					IParsedExpression	pex = (IParsedExpression)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_content);
					try
					{
						Object	value	= pex.getValue(null);
						props.put(key, value);
					}
					catch(Exception e)
					{
						// Hack!!! Exception should be propagated.
						System.err.println(pex.getExpressionText());
						e.printStackTrace();
					}
				}
			}
		}
		
		// Merge with subproperties
		Collection subcaparefs = state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_capabilityrefs);
		if(subcaparefs!=null)
		{
			for(Iterator it=subcaparefs.iterator(); it.hasNext(); )
			{
				Object subcaparef = it.next();
				Object subcapa = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				addCapabilityProperties(props, subcapa);
			}
		}
	}

	/**
	 *  Copy content from another capability model.
	 * /
	protected void	copyContentFrom(OAVCapabilityModel model)
	{
		super.copyContentFrom(model);
		this.matcherfunc	= ((OAVAgentModel)model).getMatcherFunctionality();		
	}*/
	
	//-------- methods --------
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
		return "OAVAgentModel("+name+")";
	}
}
