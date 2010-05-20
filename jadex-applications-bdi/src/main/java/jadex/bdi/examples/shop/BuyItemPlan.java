package jadex.bdi.examples.shop;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.bdi.runtime.Plan;
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
		IDFServiceDescription sd = df.createDFServiceDescription(null, "shop", null);
		IDFComponentDescription cd = df.createDFComponentDescription(null, sd);
		IFuture fut = df.search(cd, null);
		IDFComponentDescription[] ret = (IDFComponentDescription[])fut.get(this);
		
		if(ret.length>1)
		{
			IComponentManagementService cms = (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
			IComponentIdentifier cid = ret[0].getName();
			IExternalAccess exa = (IExternalAccess)cms.getExternalAccess(cid);
			IShop shop = (IShop)exa.getService(IShop.class);
			fut = shop.buyItem("cookie");
			Object cookie = fut.get(this);
			System.out.println("Bought cookie: "+cookie);
		}
	}
}
