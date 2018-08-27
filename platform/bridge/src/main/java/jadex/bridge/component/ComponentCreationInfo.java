package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Internal parameter object for data required during component initialization.
 */
public class ComponentCreationInfo
{
	//-------- attributes --------
	
	/** The model. */
	protected IModelInfo model;
	
	/** The start configuration name. */
	protected String config;
	
	/** The arguments. */
	protected Map<String, Object> arguments;
	
	/** The component description. */
	// Hack??? Should be only available in CMS (single thread access)
	protected IComponentDescription desc;
	
	/** The provided service infos. */
	protected ProvidedServiceInfo[]	infos;
	
	/** The required service bindings. */
	protected RequiredServiceBinding[] bindings;
	
	//-------- constructors --------
	
	/**
	 *  Create an info object.
	 *  @param model	The model (required).
	 *  @param config	The configuration name or null for default (if any).
	 *  @param arguments	The arguments (if any).
	 *  @param desc	The component description (required).
	 *  @param registry	The service registry of the local platform.
	 *  @param realtime	The real time flag.
	 *  @param copy	The copy flag.
	 */
	public ComponentCreationInfo(IModelInfo model, String config, Map<String, Object> arguments, 
		IComponentDescription desc, ProvidedServiceInfo[] infos, RequiredServiceBinding[] bindings)
	{
		this.model	= model;
		this.config = config!=null ? config : model.getConfigurationNames().length>0 ? model.getConfigurationNames()[0] : null;
		this.arguments	= arguments;
		this.desc	= desc;
		this.infos	= infos;
		this.bindings	= bindings;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the model.
	 */
	public IModelInfo getModel()
	{
		return this.model;
	}
	
	/**
	 *  Get the configuration.
	 */
	public String getConfiguration()
	{
		return this.config;
	}
	
	/**
	 *  Get the arguments.
	 */
	public Map<String, Object>	getArguments()
	{
		return arguments;
	}
	
	/**
	 *  Get the component description.
	 */
	public IComponentDescription getComponentDescription()
	{
		return this.desc;
	}

	/**
	 *  Get the provided service infos.
	 *  
	 *  @return The provided service infos..
	 */
	public ProvidedServiceInfo[] getProvidedServiceInfos()
	{
		return infos;
	}
	
	/**
	 *  Get the bindings.
	 *  @return The bindings.
	 */
	public RequiredServiceBinding[] getRequiredServiceBindings()
	{
		return bindings;
	}
	
//	/**
//	 *  Create a component creation info.
//	 */
//	public static ComponentCreationInfo createComponentCreationInfo(boolean systemcomponent, IComponentIdentifier cid, CreationInfo cinfo, IModelInfo lmodel, long time, Cause cause, IComponentIdentifier creator)
//	{
//		Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
//		Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
//		Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
//		Boolean sync = cinfo.getSynchronous()!=null? cinfo.getSynchronous(): lmodel.getSynchronous(cinfo.getConfiguration());
//		Boolean persistable = cinfo.getPersistable()!=null? cinfo.getPersistable(): lmodel.getPersistable(cinfo.getConfiguration());
//		PublishEventLevel moni = cinfo.getMonitoring()!=null? cinfo.getMonitoring(): lmodel.getMonitoring(cinfo.getConfiguration());
//		// Inherit monitoring from parent if null
//		if(moni==null && cinfo.getParent()!=null)
//		{
//			CMSComponentDescription desc = (CMSComponentDescription)SComponentManagementService.getDescription(cinfo.getParent());
//			moni = desc.getMonitoring();
//		}
//		
//		// todo: how to do platform init so that clock is always available?
//		final CMSComponentDescription ad = new CMSComponentDescription(cid, lmodel.getType(), master!=null ? master.booleanValue() : false,
//			daemon!=null ? daemon.booleanValue() : false, autosd!=null ? autosd.booleanValue() : false, sync!=null ? sync.booleanValue() : false,
//			persistable!=null ? persistable.booleanValue() : false, moni,
//			lmodel.getFullName(), cinfo.getLocalType(), lmodel.getResourceIdentifier(), time, creator, cause, systemcomponent);
//		
//		// Use first configuration if no config specified.
//		String config = cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
//			: lmodel.getConfigurationNames().length>0 ? lmodel.getConfigurationNames()[0] : null;
//		ComponentCreationInfo cci = new ComponentCreationInfo(lmodel, config, cinfo.getArguments(), ad, cinfo.getProvidedServiceInfos(), cinfo.getRequiredServiceBindings());
//		
//		return cci;
//	}
//	
//	/**
//	 *  Create a component creation info.
//	 */
//	public static ComponentCreationInfo createComponentCreationInfo(CMSComponentDescription desc, CreationInfo cinfo, IModelInfo lmodel)
//	{
//		Boolean master = cinfo.getMaster()!=null? cinfo.getMaster(): lmodel.getMaster(cinfo.getConfiguration());
//		Boolean daemon = cinfo.getDaemon()!=null? cinfo.getDaemon(): lmodel.getDaemon(cinfo.getConfiguration());
//		Boolean autosd = cinfo.getAutoShutdown()!=null? cinfo.getAutoShutdown(): lmodel.getAutoShutdown(cinfo.getConfiguration());
//		Boolean sync = cinfo.getSynchronous()!=null? cinfo.getSynchronous(): lmodel.getSynchronous(cinfo.getConfiguration());
//		Boolean persistable = cinfo.getPersistable()!=null? cinfo.getPersistable(): lmodel.getPersistable(cinfo.getConfiguration());
//		PublishEventLevel moni = cinfo.getMonitoring()!=null? cinfo.getMonitoring(): lmodel.getMonitoring(cinfo.getConfiguration());
//		
//		// Use first configuration if no config specified.
//		String config = cinfo.getConfiguration()!=null ? cinfo.getConfiguration()
//			: lmodel.getConfigurationNames().length>0 ? lmodel.getConfigurationNames()[0] : null;
//		ComponentCreationInfo cci = new ComponentCreationInfo(lmodel, config, cinfo.getArguments(), desc, cinfo.getProvidedServiceInfos(), cinfo.getRequiredServiceBindings());
//		
//		return cci;
//	}
}