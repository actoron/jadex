package jadex.platform.service.persistence;

import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.platform.service.cms.StandaloneComponentAdapter;

/**
 *  Adapter with special functionality for persistence.
 *  E.g. managing time of last activity.
 */
public class PersistentComponentAdapter extends StandaloneComponentAdapter
{
	//-------- attributes --------
	
	/** The cms. */
	protected PersistenceComponentManagementService	cms;
	
	/** The clock service. */
	protected IClockService	clockservice;
	
	/** The time of the completion of the last step. */
	protected long	laststep;
	
	/** Flag if the component is persistable. */
	
	//-------- constructors --------
	
	/**
	 *  Create a component adapter.
	 */
	public PersistentComponentAdapter(IComponentDescription desc, IModelInfo model,
		IComponentInstance component, IExternalAccess parent, PersistenceComponentManagementService cms, IClockService clockservice)
	{
		super(desc, model, component, parent);
		this.cms	= cms;
		this.clockservice	= clockservice;
	}
	
	//-------- methods --------

	/**
	 *  Execute a step.
	 */
	public boolean execute()
	{
		cms.removeLRUComponent(getComponentIdentifier());
		boolean ret	= super.execute();
		this.laststep	= clockservice.getTime();
		cms.addLRUComponent(getComponentIdentifier());
		return ret;
	}
	
	/**
	 *  Get the time of the last step.
	 */
	public long	getLastStepTime()
	{
		return laststep;
	}
}
