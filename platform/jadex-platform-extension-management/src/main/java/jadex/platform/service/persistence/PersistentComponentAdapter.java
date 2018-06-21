package jadex.platform.service.persistence;

/**
 *  Adapter with special functionality for persistence.
 *  E.g. managing time of last activity.
 */
public class PersistentComponentAdapter //extends StandaloneComponentAdapter
{
	//-------- attributes --------
	
	/** The persistence service. */
	protected PersistenceComponentManagementService	ps;
	
	/** The active flag to avoid too many active/idle calls. */
	protected boolean active;
	
	//-------- constructors --------
	
//	/**
//	 *  Create a component adapter.
//	 */
//	public PersistentComponentAdapter(IComponentDescription desc, IModelInfo model,
//		IComponentInterpreter component, IExternalAccess parent, PersistenceComponentManagementService ps)
//	{
//		super(desc, model, component, parent);
//		this.ps	= ps;
//	}
//	
//	//-------- methods --------
//
//	/**
//	 *  Execute a step.
//	 */
//	public boolean execute()
//	{
//		if(!active)
//		{
//			active	= true;
//			ps.componentActive(getComponentIdentifier());
//		}
//		boolean ret	= super.execute();
//		if(!ret)
//		{
//			active	= false;
//			ps.componentIdle(getComponentIdentifier());
//		}
//		return ret;
//	}
}
