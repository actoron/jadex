package jadex.platform.service.persistence;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.kernelbase.IBootstrapFactory;
import jadex.platform.service.cms.DecoupledComponentManagementService;

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
	
	
}
