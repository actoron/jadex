package jadex.platform.service.persistence;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.IBootstrapFactory;
import jadex.platform.service.cms.DecoupledComponentManagementService;
import jadex.platform.service.cms.StandaloneComponentAdapter;

/**
 *  CMS with additional persistence functionality.
 */
@Service
public class PersistenceComponentManagementService	extends DecoupledComponentManagementService
{
	/**
	 *  Static method for reflective creation to allow platform start without add-on.
	 */
	public static PersistenceComponentManagementService	create(IComponentAdapter root, IBootstrapFactory componentfactory,
		boolean copy, boolean realtime, boolean persist, boolean uniqueids)
	{
		return new PersistenceComponentManagementService(root, componentfactory, copy, realtime, persist, uniqueids);
	}
	
	/**
	 *  Create a persistence CMS.
	 */
	public PersistenceComponentManagementService(IComponentAdapter root, IBootstrapFactory componentfactory,
		boolean copy, boolean realtime, boolean persist, boolean uniqueids)
	{
		super(root, componentfactory, copy, realtime, persist, uniqueids);
	}
	
	/**
	 *  Gets the component state.
	 *  
	 *  @param cid The component.
	 *  @return The component state.
	 */
	public IFuture<IPersistInfo> getPersistableState(IComponentIdentifier cid)
	{
		final Future<IPersistInfo> ret = new Future<IPersistInfo>();
		
		final IComponentAdapter adapter = adapters.get(cid);
		adapter.invokeLater(new Runnable()
		{
			public void run()
			{
				final IComponentInstance instance = ((StandaloneComponentAdapter)adapter).getComponentInstance();
				instance.getPersistableState().addResultListener(new DelegationResultListener<IPersistInfo>(ret));
			}
		});
		
		return ret;
	}
}
