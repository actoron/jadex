package jadex.bdi.model;

import jadex.bridge.modelinfo.ModelInfo;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
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
	public OAVAgentModel(IOAVState state, Object handle, ModelInfo modelinfo, Set types, 
		long lastmod, MultiCollection entries)
	{
		super(state, handle, modelinfo, types, lastmod, entries);
	}
	
	//-------- IAgentModel methods --------
	
//	/**
//	 *  Is the model startable.
//	 *  @return True, if startable.
//	 */
//	public boolean isStartable()
//	{
//		return true;
//	}
	
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
	 *  Set the matcherfunc.
	 */
	public void setMatcherFunctionality(IPatternMatcherFunctionality matcherfunc)
	{
		this.matcherfunc	= matcherfunc;
	}
	
	/**
	 *  Init the model info.
	 */
	public void initModelInfo()
	{
		super.initModelInfo();
		
		// Hack!!!!! todo: remove
		// Debugger breakpoints for BDI and user rules.
		List names = new ArrayList();
		for(Iterator it=matcherfunc.getRulebase().getRules().iterator(); it.hasNext(); )
			names.add(((IRule)it.next()).getName());
		modelinfo.addProperty("debugger.breakpoints", names);
//		
////		// Exclude IExternalAccess 
////		// Exclude all IBDIExternalAccess methods! :-( they work on flyweights
////		// Exclude many IEACapability methods
////		addMethodInfos(modelinfo.getProperties(), "remote_excluded", new String[]{
////			"getServiceProvider", 
////			
////			"dispatchTopLevelGoal", "createGoal", "sendMessage",
////			"dispatchInternalEvent", "createMessageEvent", "createInternalEvent",
////			"waitFor", "waitForTick", "waitForInternalEvent", "waitForInternalEvent",
////			"sendMessageAndWait", "waitForMessageEvent", "waitForReply", "waitForGoal",
////			"waitForFactChanged", "waitForFactAdded", "waitForFactRemoved", 
////			"dispatchTopLevelGoalAndWait",
////			
////			"getExternalAccess", "getBeliefbase", "getGoalbase", "getPlanbase",
////			"getEventbase", "getExpressionbase", "getPropertybase", "getLogger", 
////			"getPlatformComponent", "getTime", "getClassLoader", "addAgentListener", 
////			"removeAgentListener"
////			});
//		
//		// Init the arguments.
//		IArgument[] args = getModelInfo().getArguments();
//		for(int i=0; i<args.length; i++)
//		{
//			OAVCapabilityModel.initArgument(((Argument)args[i]), state, getHandle());
//		}
//		// Init the results.
//		IArgument[] ress = getModelInfo().getResults();
//		for(int i=0; i<ress.length; i++)
//		{
//			OAVCapabilityModel.initArgument(((Argument)ress[i]), state, getHandle());
//		}
//		
//		// Init the flags.
////		ModelValueProvider suspend = new ModelValueProvider();
////		ModelValueProvider master = new ModelValueProvider();
////		ModelValueProvider daemon = new ModelValueProvider();
////		ModelValueProvider autosd = new ModelValueProvider();
////		
////		Boolean val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_suspend);
////		if(val!=null)
////			suspend.setValue(val);
////		val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_master);
////		if(val!=null)
////			master.setValue(val);
////		val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_daemon);
////		if(val!=null)
////			daemon.setValue(val);
////		val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_autoshutdown);
////		if(val!=null)
////			autosd.setValue(val);
////
////		Collection confs = state.getAttributeValues(getHandle(), OAVBDIMetaModel.capability_has_configurations);
////		if(confs!=null)
////		{
////			for(Iterator it=confs.iterator(); it.hasNext(); )
////			{
////				Object conf = it.next();
////				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_suspend);
////				if(val!=null)
////					suspend.setValue((String)state.getAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name), val);
////				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_master);
////				if(val!=null)
////					master.setValue((String)state.getAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name), val);
////				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_daemon);
////				if(val!=null)
////					daemon.setValue((String)state.getAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name), val);
////				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_autoshutdown);
////				if(val!=null)
////					autosd.setValue((String)state.getAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name), val);
////			}
////		}
////		
////		modelinfo.setSuspend(suspend);
////		modelinfo.setMaster(master);
////		modelinfo.setDaemon(daemon);
////		modelinfo.setAutoShutdown(autosd);
//		
//		Boolean val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_suspend);
//		if(val!=null)
//			modelinfo.setSuspend(val);
//		val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_master);
//		if(val!=null)
//			modelinfo.setMaster(val);
//		val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_daemon);
//		if(val!=null)
//			modelinfo.setDaemon(val);
//		val = (Boolean)state.getAttributeValue(getHandle(), OAVBDIMetaModel.agent_has_autoshutdown);
//		if(val!=null)
//			modelinfo.setAutoShutdown(val);
//
//		Collection confs = state.getAttributeValues(getHandle(), OAVBDIMetaModel.capability_has_configurations);
//		if(confs!=null)
//		{
//			for(Iterator it=confs.iterator(); it.hasNext(); )
//			{
//				Object conf = it.next();
//				String name = (String)state.getAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name);
//				ConfigurationInfo cinfo = modelinfo.getConfiguration(name);
//				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_suspend);
//				if(val!=null)
//					cinfo.setSuspend(val);
//				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_master);
//				if(val!=null)
//					cinfo.setMaster(val);
//				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_daemon);
//				if(val!=null)
//					cinfo.setDaemon(val);
//				val = (Boolean)state.getAttributeValue(conf, OAVBDIMetaModel.configuration_has_autoshutdown);
//				if(val!=null)
//					cinfo.setAutoShutdown(val);
//			}
//		}
//		
////		modelinfo.setSuspend(suspend);
////		modelinfo.setMaster(master);
////		modelinfo.setDaemon(daemon);
////		modelinfo.setAutoShutdown(autosd);
//		
////		Map ret = super.getProperties();
////		
//////		if(properties==null)
//////		{
//////			this.properties = new HashMap();
////
////			// Debugger breakpoints for BDI and user rules.
////			List names = new ArrayList();
////			for(Iterator it=matcherfunc.getRulebase().getRules().iterator(); it.hasNext(); )
////				names.add(((IRule)it.next()).getName());
////			ret.put("debugger.breakpoints", names);
////			
//////			addCapabilityProperties(properties, handle);
//////		}
//////		return properties;
////			
////		return ret;
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