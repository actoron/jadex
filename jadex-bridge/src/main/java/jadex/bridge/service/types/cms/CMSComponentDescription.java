package jadex.bridge.service.types.cms;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.Cause;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;


/**
 *  Java class for concept CMSComponentDescription
 *  of beanynizer_beans_fipa_new ontology.
 */
public class CMSComponentDescription implements IComponentDescription, Cloneable //, Serializable
{
	//-------- attributes ----------

	/** Attribute for slot state. */
	protected String state;

	/** Attribute for slot component identifier. */
	protected IComponentIdentifier name;

	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** Attribute for children. */
	protected Set<IComponentIdentifier> children;

	/** Attribute for slot ownership. */
	protected String ownership;

	/** The component type. */
	protected String type;

	/** The breakpoints. */
	protected String[] breakpoints;
	
	/** The master flag. */
	protected boolean master;
	
	/** The daemon flag. */
	protected boolean daemon;
	
	/** The auto shutdown flag. */
	protected boolean autoshutdown;

	/** The synchronous flag. */
	protected boolean synchronous;
	
	/** The persistable flag. */
	protected boolean persistable;
	
	/** The monitoring flag. */
	protected PublishEventLevel monitoring;
	
	/** The model name. */
	protected String modelname;
	
	/** The local type name (from parent). */
	protected String localtype;
	
	/** The creation time. */
	protected long creationtime;
	
	/** The creator. */
	protected IComponentIdentifier creator;
	
	/** The cause. */
	protected Cause cause;
	
	/** The step info for debugging. To determine the next step. */
	protected String stepinfo;
	
	/** Boolean flag if it is a system component. */
	protected boolean systemcomponent;
	
	//-------- constructors --------

	/**
	 *  Create a new CESComponentDescription.
	 */
	public CMSComponentDescription()
	{
//		System.out.println("created desc: "+hashCode());
	}

	/**
	 *  Create a new CMSComponentDescription.
	 */
	public CMSComponentDescription(IComponentIdentifier cid, String type, boolean master, 
		boolean daemon, boolean autoshutdown, boolean synchronous, boolean persistable,
		PublishEventLevel monitoring, String modelname, String localtype, IResourceIdentifier rid,
		long creationtime, IComponentIdentifier creator, Cause cause, boolean systemcomponent)
	{
//		if(cid.getName().indexOf("Dyn")!=-1)
//			System.out.println("created desc: "+cid+" "+hashCode());
		setName(cid);
		setType(type);
//		setParent(parent);
		setState(IComponentDescription.STATE_ACTIVE);
//		setProcessingState(IComponentDescription.PROCESSINGSTATE_IDLE);
		setMaster(master);
		setDaemon(daemon);
		setAutoShutdown(autoshutdown);
		setSynchronous(synchronous);
		setPersistable(persistable);
		setMonitoring(monitoring!=null? monitoring: PublishEventLevel.OFF);
		setModelName(modelname);
		setLocalType(localtype);
		setResourceIdentifier(rid);
		setCreationTime(creationtime);
		setCreator(creator);
		setCause(cause);
		setSystemComponent(systemcomponent);
	}

	//-------- accessor methods --------

	/**
	 *  Get the state of this CESComponentDescription.
	 *  @return state
	 */
	public String getState()
	{
		return this.state;
	}

	/**
	 *  Set the state of this CESComponentDescription.
	 *  @param state the value to be set
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 *  Get the componentidentifier of this CESComponentDescription.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getName()
	{
		return this.name;
	}

	/**
	 *  Set the componentidentifier of this CESComponentDescription.
	 * @param name the value to be set
	 */
	public void setName(IComponentIdentifier name)
	{
		this.name = name;
	}

	/**
	 *  Get the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return rid;
	}

	/**
	 *  Set the resource identifier.
	 *  @param rid The resource identifier.
	 */
	public void setResourceIdentifier(IResourceIdentifier rid)
	{
		this.rid = rid;
	}
	
	/**
	 *  Add a child component.
	 *  @param child The child component.
	 */
	public void addChild(IComponentIdentifier child)
	{
		// CMS / external access / component may access description concurrently?!
		synchronized(this)
		{
//			System.out.println("add child: "+child+" "+getName()+" "+hashCode());
			if(children==null)
				children = new LinkedHashSet();
			children.add(child);
		}
	}
	
	/**
	 *  Remove a child component.
	 *  @param child The child component.
	 */
	public void removeChild(IComponentIdentifier child)
	{
		synchronized(this)
		{
			if(children!=null)
				children.remove(child);
		}
	}
	
	/**
	 *  Get the children.
	 *  @return The children.
	 */
	public IComponentIdentifier[] getChildren()
	{
		synchronized(this)
		{
			return children==null? new IComponentIdentifier[0]: (IComponentIdentifier[])children.toArray(new IComponentIdentifier[children.size()]);
		}
	}
	
	/**
	 *  Get the ownership of this CESComponentDescription.
	 * @return ownership
	 */
	public String getOwnership()
	{
		return this.ownership;
	}

	/**
	 *  Set the ownership of this CESComponentDescription.
	 * @param ownership the value to be set
	 */
	public void setOwnership(String ownership)
	{
		this.ownership = ownership;
	}

	/**
	 *  Get the component type.
	 *  @return The component type name (e.g. 'BDI Agent').
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 *  Set the component type.
	 *  @param type	The component type name (e.g. 'BDI Agent').
	 */
	public void setType(String type)
	{
		this.type	= type;
	}

	/**
	 *  Get the enabled breakpoints (if any).
	 *  @return The enabled breakpoints.
	 */
	public String[]	getBreakpoints()
	{
		return breakpoints!=null ? breakpoints : new String[0];
	}

	/**
	 *  Set the enabled breakpoints (if any).
	 *  @param breakpoints The enabled breakpoints.
	 */
	public void	setBreakpoints(String[] breakpoints)
	{
		this.breakpoints	= breakpoints;
	}
	
	/**
	 *  Get the master.
	 *  @return the master.
	 */
	public boolean isMaster()
	{
		return master;
	}

	/**
	 *  Set the master.
	 *  @param master The master to set.
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
	}

	/**
	 *  Get the daemon.
	 *  @return the daemon.
	 */
	public boolean isDaemon()
	{
		return daemon;
	}

	/**
	 *  Set the daemon.
	 *  @param daemon The daemon to set.
	 */
	public void setDaemon(boolean daemon)
	{
		this.daemon = daemon;
	}

	/**
	 *  Get the autoshutdown.
	 *  @return the autoshutdown.
	 */
	public boolean isAutoShutdown()
	{
		return autoshutdown;
	}

	/**
	 *  Set the autoshutdown.
	 *  @param autoshutdown The autoshutdown to set.
	 */
	public void setAutoShutdown(boolean autoshutdown)
	{
		this.autoshutdown = autoshutdown;
	}

	/**
	 *  Get the model name.
	 *  @return The name.
	 */
	public String getModelName()
	{
		return modelname;
	}

	/**
	 *  Set the model name.
	 *  @param modelname The model name.
	 */
	public void setModelName(String modelname)
	{
		this.modelname = modelname;
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
	public void setLocalType(String localtype)
	{
		this.localtype = localtype;
	}
	
	/**
	 *  Get the creation time.
	 *  @return The creation time.
	 */
	public long getCreationTime()
	{
		return creationtime;
	}
	
	/**
	 *  Set the creationtime.
	 *  @param creationtime The creationtime to set.
	 */
	public void setCreationTime(long creationtime)
	{
		this.creationtime = creationtime;
	}
	
	/**
	 *  Get the creator.
	 *  @return The creator.
	 */
	public IComponentIdentifier getCreator()
	{
		return creator;
	}

	/**
	 *  Set the creator.
	 *  @param creator The creator to set.
	 */
	public void setCreator(IComponentIdentifier creator)
	{
		this.creator = creator;
	}
	
	/**
	 *  Get the cause.
	 *  @return The cause.
	 */
	public Cause getCause()
	{
		return cause;
	}

	/**
	 *  Set the cause.
	 *  @param cause The cause to set.
	 */
	public void setCause(Cause cause)
	{
		this.cause = cause;
	}

	/**
	 *  Get the synchronous flag.
	 *  @return	The synchronous flag.
	 */
	public boolean isSynchronous()
	{
		return synchronous;
	}
	
	/**
	 *  Set the synchronous flag.
	 *  @param synchronous	The synchronous flag.
	 */
	public void	setSynchronous(boolean synchronous)
	{
		this.synchronous = synchronous;
	}
	
	/**
	 *  Get the persistable flag.
	 *  @return	The persistable flag.
	 */
	public boolean isPersistable()
	{
		return persistable;
	}

	/**
	 *  Set the persistable flag.
	 *  @param persistable	The persistable flag.
	 */
	public void	setPersistable(boolean persistable)
	{
		this.persistable	= persistable;
	}

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
	public void setMonitoring(PublishEventLevel monitoring)
	{
		this.monitoring = monitoring;
	}
	
//	/**
//	 *  Get the stepinfo.
//	 *  @return The stepinfo.
//	 */
//	public String getStepInfo()
//	{
//		return stepinfo;
//	}
//
//	/**
//	 *  Set the stepinfo.
//	 *  @param stepinfo The stepinfo to set.
//	 */
//	public void setStepInfo(String stepinfo)
//	{
//		this.stepinfo = stepinfo;
//	}
	
	/**
	 *  Test if it is a system component.
	 *  @return True, if it is a system component.
	 */
	public boolean isSystemComponent()
	{
		return this.systemcomponent;
	}
	
	/**
	 *  Set the system component flag.
	 *  @param systemcomponent The flag.
	 */
	public void setSystemComponent(boolean systemcomponent)
	{
		this.systemcomponent = systemcomponent;
	}

	/**
	 *  Test if this description equals another description.
	 */
	public boolean equals(Object o)
	{
		return o == this || o instanceof CMSComponentDescription && getName() != null && getName().equals(((CMSComponentDescription)o).getName());
	}

	/**
	 *  Get the hash code of this description.
	 */
	public int hashCode()
	{
		return getName() != null ? getName().hashCode() : 0;
	}

	/**
	 *  Get a string representation of this description.
	 */
	public String toString()
	{
		return "CMSComponentDescription(name=" + getName() + ", state=" + getState() + ", ownership=" + getOwnership() + ")";
	}

	/**
	 *  Clone a component description.
	 */
	public Object clone()
	{
		try
		{
			CMSComponentDescription ret = (CMSComponentDescription)super.clone();
			if(name!=null)
				ret.setName((BasicComponentIdentifier)((BasicComponentIdentifier)name).clone());
			
			// Do not clone -> only used internally
//			if(children!=null)
//			{
//				ret.children = new LinkedHashSet(); 
//				ret.children.addAll(children);
//			}
			
			ret.cause = cause!=null ? new Cause(cause) : cause;
			return ret;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Cannot clone: " + this);
		}
	}
	
//	public static void main(String[] args)
//	{
//		CMSComponentDescription desc = new CMSComponentDescription();
//		desc.setMonitoring(Boolean.TRUE);
//		
//		CMSComponentDescription cl = (CMSComponentDescription)desc.clone();
//		System.out.println(cl);
//	}
}
