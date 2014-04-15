package jadex.platform.service.persistence;

import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.kernelbase.IBootstrapFactory;
import jadex.platform.service.cms.DecoupledComponentManagementService;

/**
 *  CMS with additional persistence functionality.
 */
public class PersistenceComponentManagementService	extends DecoupledComponentManagementService
{
	/**
	 *  Create a persistence CMS.
	 */
	public PersistenceComponentManagementService(IComponentAdapter root, IBootstrapFactory componentfactory,
		boolean copy, boolean realtime, boolean persist, boolean uniqueids)
	{
		super(root, componentfactory, copy, realtime, uniqueids);
	}
	
	
}
