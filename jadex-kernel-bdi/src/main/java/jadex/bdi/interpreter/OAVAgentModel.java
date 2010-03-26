package jadex.bdi.interpreter;

import jadex.javaparser.IParsedExpression;
import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.rulesystem.IRule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;

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
	public OAVAgentModel(IOAVState state, Object handle, 
		OAVTypeModel typemodel, Set types, String filename, long lastmod, Report report)
	{
		super(state, handle, typemodel, types, filename, lastmod, report);
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
	protected void setMatcherFunctionality(IPatternMatcherFunctionality matcherfunc)
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
			Map props	= new HashMap();

			// Debugger breakpoints for BDI and user rules.
			List	names	= new ArrayList();
			for(Iterator it=matcherfunc.getRulebase().getRules().iterator(); it.hasNext(); )
				names.add(((IRule)it.next()).getName());
			props.put("debugger.breakpoints", names);
			this.properties	= props;

			// Properties from loaded model.
			Collection	oprops	= state.getAttributeKeys(handle, OAVBDIMetaModel.capability_has_properties);
			if(oprops!=null)
			{
				for(Iterator it=oprops.iterator(); it.hasNext(); )
				{
					Object	key	= it.next();
					Object	mexp	= state.getAttributeValue(handle, OAVBDIMetaModel.capability_has_properties, key);
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
		return properties;
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
