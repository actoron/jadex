package jadex.bdi.examples.shop;

import jadex.base.fipa.IDF;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;

/**
 * 
 */
public class BuyItemPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IDF df = (IDF)getScope().getServiceContainer().getService(IDF.class);
		
//		df.search(adesc, null)
		
		IComponentManagementService cms = (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
//		IFuture ret = cms.getExternalAccess(cid);
//		IExternalAccess exa = (IExternalAccess)ret.get(this);
//		exa.getService(I)
	}
}
