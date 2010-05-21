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
		IDFComponentDescription[] ret = (IDFComponentDescription[])df.search(cd, null).get(this);
		
		if(ret.length>0)
		{
			IComponentManagementService cms = (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
			IComponentIdentifier cid = ret[0].getName();
			IExternalAccess exa = (IExternalAccess)cms.getExternalAccess(cid).get(this);
			IShop shop = (IShop)exa.getService(IShop.class);
			Object item = shop.buyItem((String)getParameter("name").getValue()).get(this);
			System.out.println("Bought item: "+item);
			getParameter("result").setValue(item);
		}
		else
		{
			System.out.println("No seller found.");
		}
	}
}
