package jadex.bdi.model;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.rulesystem.IRule;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
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
	
//	/** The properties (e.g. rule break points). */
//	protected Map	properties;
	
	//-------- constructors --------

	/**
	 *  Create a model.
	 */
	public OAVAgentModel(IOAVState state, Object handle, Set types, String filename, long lastmod)//, IReport report)
	{
		super(state, handle, types, filename, lastmod);//, report);
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
	public void	addAgentProperties()
	{
		// Hack!!!!! todo: remove
		// Debugger breakpoints for BDI and user rules.
		List names = new ArrayList();
		for(Iterator it=matcherfunc.getRulebase().getRules().iterator(); it.hasNext(); )
			names.add(((IRule)it.next()).getName());
		modelinfo.addProperty("debugger.breakpoints", names);
		
		// Exclude IExternalAccess 
		// Exclude all IBDIExternalAccess methods! :-( they work on flyweights
		// Exclude many IEACapability methods
		addMethodInfos(modelinfo.getProperties(), "remote_excluded", new String[]{
			"getServiceProvider", 
			
			"dispatchTopLevelGoal", "createGoal", "sendMessage",
			"dispatchInternalEvent", "createMessageEvent", "createInternalEvent",
			"waitFor", "waitForTick", "waitForInternalEvent", "waitForInternalEvent",
			"sendMessageAndWait", "waitForMessageEvent", "waitForReply", "waitForGoal",
			"waitForFactChanged", "waitForFactAdded", "waitForFactRemoved", 
			"dispatchTopLevelGoalAndWait",
			
			"getExternalAccess", "getBeliefbase", "getGoalbase", "getPlanbase",
			"getEventbase", "getExpressionbase", "getPropertybase", "getLogger", 
			"getPlatformComponent", "getTime", "getClassLoader", "addAgentListener", 
			"removeAgentListener"
			});
		
//		Map ret = super.getProperties();
//		
////		if(properties==null)
////		{
////			this.properties = new HashMap();
//
//			// Debugger breakpoints for BDI and user rules.
//			List names = new ArrayList();
//			for(Iterator it=matcherfunc.getRulebase().getRules().iterator(); it.hasNext(); )
//				names.add(((IRule)it.next()).getName());
//			ret.put("debugger.breakpoints", names);
//			
////			addCapabilityProperties(properties, handle);
////		}
////		return properties;
//			
//		return ret;
	}
	
	/**
	 *  Add method info.
	 */
	public static void addMethodInfos(Map props, String type, String[] names)
	{
		Object ex = props.get(type);
		if(ex!=null)
		{
			List newex = new ArrayList();
			for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
			{
				newex.add(it.next());
			}
			for(int i=0; i<names.length; i++)
			{
				newex.add(names[i]);
			}
		}
		else
		{
			props.put(type, names);
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
