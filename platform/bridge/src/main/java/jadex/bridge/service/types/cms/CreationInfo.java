package jadex.bridge.service.types.cms;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;

/**
 *  A parameter object to capture
 *  extra information for component creation.
 *  All of the information is optional, i.e.
 *  may be null.
 */
public class CreationInfo
{
	//-------- attributes --------
	
	/** The instance name. */
	protected String name;
	
	/** The file name. */
	protected String filename;
	
	/** The configuration. */
	protected String config;
	
	/** The arguments (map with name/value pairs). */
	protected Map<String, Object> args;
	
	/** The parent component. */
//	protected IComponentIdentifier parent;
	
	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** The suspend flag (default: false). */
	protected Boolean suspend;

//	/** The master flag (default: false). */
//	protected Boolean master;
	
//	/** The daemon flag (default: false). */
//	protected Boolean daemon;
	
//	/** The auto shutdown flag (default: false). */
//	protected Boolean autoshutdown;

//	/** The monitoring flag (default: false). */
//	protected Boolean monitoring;
	protected PublishEventLevel monitoring;
	
	/** The synchronous flag (default: false). */
	protected Boolean synchronous;
	
//	/** The persistable flag (default: false). */
//	protected Boolean persistable;
	
//	/** The platform classloader flag (default: false). */
//	protected Boolean platformloader;
	
	/** The imports. */
	protected String[] imports;
	
	/** The bindings. */
	protected RequiredServiceBinding[] bindings;
	
	/** The provided service infos. */
	protected ProvidedServiceInfo[] pinfos;
	
	/** The local component type name. */
	protected String localtype;
	
	/** The pojo (optional). */
	protected Object pojo;
	
	//-------- constructors --------
	
	/**
	 *  Create a new creation info. 
	 */
	public CreationInfo()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new creation info. 
	 */
	public CreationInfo(CreationInfo info)
	{
		if(info!=null)
		{
			this.name = info.getName();
			this.filename = info.getFilename();
			this.config	= info.getConfiguration();
			this.args	= info.getArguments();
//			this.parent	= info.getParent();
			this.suspend	= info.getSuspend(); 
//			this.master = info.getMaster();
//			this.daemon = info.getDaemon();
//			this.autoshutdown = info.getAutoShutdown();
			this.monitoring = info.getMonitoring();
			this.synchronous = info.getSynchronous();
			this.imports	= info.getImports();
			this.bindings = info.getRequiredServiceBindings();
			this.pinfos	= info.getProvidedServiceInfos();
			this.rid = info.getResourceIdentifier();
			this.localtype = info.getLocalType();
			this.pojo = info.getPojo();
		}
	}

	/**
	 *  Create a new creation info.
	 */
//	public CreationInfo(IComponentIdentifier parent)
//	{
//		this(null, parent);
//	}
	
	/**
	 *  Create a new creation info.
	 *  @param args	The arguments.
	 */
	public CreationInfo(Map<String, Object> args)
	{
		this(null, args);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param rid	The RID.
	 */
	public CreationInfo(IResourceIdentifier rid)
	{
		this(null, null, rid);
	}
	
	/**
	 *  Create a new creation info.
	 *  @param parent	The parent of the component to be created.
	 */
//	public CreationInfo(IComponentIdentifier parent, IResourceIdentifier rid)
//	{
//		this(null, null, parent, null, null, null, null, null, rid);
//	}
	
	/**
	 *  Create a new creation info.
	 *  @param config	The configuration.
	 *  @param args	The arguments.
	 */
	public CreationInfo(String config, Map<String, Object> args)
	{
		this(config, args, null);
	}
	
	/**
	 *  Create a new creation info.
	 */
	public CreationInfo(String config, Map<String, Object> args, IResourceIdentifier rid)
	{
		this.config = config;
		this.args = args;
		this.rid = rid;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the configuration.
	 *  @return the config.
	 */
	public String getConfiguration()
	{
		return config;
	}

	/**
	 *  Set the configuration.
	 *  @param config the config to set.
	 */
	public CreationInfo setConfiguration(String config)
	{
		this.config = config;
		return this;
	}

	/**
	 *  Get the arguments.
	 *  @return the args.
	 */
	public Map<String, Object> getArguments()
	{
		return args;
	}

	/**
	 *  Set the arguments.
	 *  @param args the args to set
	 */
	public CreationInfo setArguments(Map<String, Object> args)
	{
		this.args = args;
		return this;
	}

	/**
	 *  Add one arguments.
	 *  Returns the updated CreationInfo for fluent API purposes.
	 *  @param key The key.
	 *  @param arg The value.
     *  @return CreationInfo
	 */
	public CreationInfo addArgument(String key, Object arg)
	{
		if (this.args == null) {
			this.args = new HashMap<String, Object>();
		}
		this.args.put(key, arg);
		return this;
	}

	/**
	 *  Get the parent.
	 *  @return the parent
	 */
//	public IComponentIdentifier getParent()
//	{
//		return parent;
//	}
//	public IComponentIdentifier getParent()
//	{
//		return null;
//	}

	/**
	 *  Set the parent.
	 *  @param parent the parent to set
	 */
//	public CreationInfo setParent(IComponentIdentifier parent)
//	{
//		this.parent = parent;
//		return this;
//	}

	/**
	 *  Get the resource identifier for loading the component model.
	 *  @return the resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return rid;
	}

	/**
	 *  Set the resource identifier for loading the component model.
	 *  @param rid the resource identifier to set
	 */
	public CreationInfo setResourceIdentifier(IResourceIdentifier rid)
	{
		this.rid = rid;
		return this;
	}

	/**
	 *  Get the suspend flag.
	 *  @return the suspend flag
	 */
	public Boolean getSuspend()
	{
		return suspend;
	}

	/**
	 *  Set the suspend flag.
	 *  @param suspend the suspend to set flag
	 */
	public CreationInfo setSuspend(Boolean suspend)
	{
		this.suspend = suspend;
		return this;
	}

//	/**
//	 *  Get the master.
//	 *  @return The master.
//	 */
//	public Boolean getMaster()
//	{
//		return master;
//	}
//
//	/**
//	 *  Set the master.
//	 *  @param master The master to set.
//	 */
//	public CreationInfo setMaster(Boolean master)
//	{
//		this.master = master;
//		return this;
//	}

//	/**
//	 *  Get the daemon.
//	 *  @return The daemon.
//	 */
//	public Boolean getDaemon()
//	{
//		return daemon;
//	}
//
//	/**
//	 *  Set the daemon.
//	 *  @param daemon The daemon to set.
//	 */
//	public CreationInfo setDaemon(Boolean daemon)
//	{
//		this.daemon = daemon;
//		return this;
//	}

//	/**
//	 *  Get the autoshutdown.
//	 *  @return The autoshutdown.
//	 */
//	public Boolean getAutoShutdown()
//	{
//		return autoshutdown;
//	}
//
//	/**
//	 *  Set the autoshutdown.
//	 *  @param autoshutdown The autoshutdown to set.
//	 */
//	public CreationInfo setAutoShutdown(Boolean autoshutdown)
//	{
//		this.autoshutdown = autoshutdown;
//		return this;
//	}
	
//	public Boolean getPlatformloader()
//	{
//		return platformloader;
//	}
//	
//	public void setPlatformloader(Boolean platformloader)
//	{
//		this.platformloader = platformloader;
//	}

	/**
	 *  Get the imports.
	 *  @return the imports.
	 */
	public String[] getImports()
	{
		return imports;
	}

	/**
	 *  Set the imports
	 *  @param imports The imports to set.
	 */
	public CreationInfo setImports(String[] imports)
	{
		this.imports = imports;
		return this;
	}

	/**
	 *  Get the bindings.
	 *  @return The bindings.
	 */
	public RequiredServiceBinding[] getRequiredServiceBindings()
	{
		return bindings;
	}

	/**
	 *  Set the bindings.
	 *  @param bindings The bindings to set.
	 */
	public CreationInfo setRequiredServiceBindings(RequiredServiceBinding[] bindings)
	{
		this.bindings = bindings;
		return this;
	}

	/**
	 *  Get the service infos.
	 *  @return The infos.
	 */
	public ProvidedServiceInfo[] getProvidedServiceInfos()
	{
		return pinfos;
	}

	/**
	 *  Set the ProvidedServiceInfos.
	 *  @param pinfos The ProvidedServiceInfo to set.
	 */
	public CreationInfo setProvidedServiceInfos(ProvidedServiceInfo[] pinfos)
	{
		this.pinfos = pinfos;
		return this;
	}

	/**
	 *  Get the localtype.
	 *  @return the localtype.
	 */
	public String getLocalType()
	{
		return localtype;
	}

	/**
	 *  Set the localtype.
	 *  @param localtype The localtype to set.
	 */
	public CreationInfo setLocalType(String localtype)
	{
		this.localtype = localtype;
		return this;
	}

//	/**
//	 *  Get the monitoring.
//	 *  @return The monitoring.
//	 */
//	public Boolean getMonitoring()
//	{
//		return monitoring;
//	}
//
//	/**
//	 *  Set the monitoring.
//	 *  @param monitoring The monitoring to set.
//	 */
//	public void setMonitoring(Boolean monitoring)
//	{
//		this.monitoring = monitoring;
//	}

	/**
	 *  Get the monitoring.
	 *  @return The monitoring.
	 */
	public PublishEventLevel getMonitoring()
	{
		return monitoring;
	}

	/**
	 *  Set the monitoring.
	 *  @param monitoring The monitoring to set.
	 */
	public CreationInfo setMonitoring(PublishEventLevel monitoring)
	{
		this.monitoring = monitoring;
		return this;
	}
	
	/**
	 *  Get the synchronous.
	 *  @return The synchronous.
	 */
	public Boolean getSynchronous()
	{
		return synchronous;
	}

	/**
	 *  Set the synchronous.
	 *  @param synchronous The synchronous to set.
	 */
	public CreationInfo setSynchronous(Boolean synchronous)
	{
		this.synchronous = synchronous;
		return this;
	}
	
//	/**
//	 *  Get the persistable.
//	 *  @return The persistable.
//	 */
//	public Boolean getPersistable()
//	{
//		return persistable;
//	}
//
//	/**
//	 *  Set the persistable.
//	 *  @param persistable The persistable to set.
//	 */
//	public CreationInfo setPersistable(Boolean persistable)
//	{
//		this.persistable = persistable;
//		return this;
//	}

	/**
	 *  Get the filename.
	 *  @return the filename
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename to set
	 */
	public CreationInfo setFilename(String filename)
	{
		this.filename = filename;
		return this;
	}

	/**
	 *  Get the instance name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Set the filename.
	 *  @param filename The filename to set
	 */
	public CreationInfo setFilenameClass(Class<?> clazz)
	{
		this.filename = clazz.getName()+".class";
		return this;
	}

	/**
	 *  Set the name.
	 *  @param name the name to set
	 */
	public CreationInfo setName(String name)
	{
		this.name = name;
		return this;
	}

	/**
	 *  Get the pojo.
	 *  @return The pojo
	 */
	public Object getPojo()
	{
		return pojo;
	}

	/**
	 *  Set the pojo.
	 *  @param pojo The pojo to set
	 */
	public CreationInfo setPojo(Object pojo)
	{
		this.pojo = pojo;
		return this;
	}
	
}
