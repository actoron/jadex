package jadex.bdi.examples.shop;

import jadex.bdi.runtime.Plan;
import jadex.commons.IFuture;
import jadex.service.SServiceProvider;

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
		IShop shop = (IShop)SServiceProvider.getService(getScope().getServiceProvider(), IShop.class).get(this);
		if(shop!=null)
		{
			String name	= (String)getParameter("name").getValue();
			System.out.println(getComponentName()+" buying item: "+name);
			IFuture	future	= shop.buyItem(name);
			System.out.println(getComponentName()+" getting item: "+future);
			Object item = future.get(this);
			System.out.println(getComponentName()+" bought item: "+item);
			getParameter("result").setValue(item);
		}
		else
		{
			System.out.println("No seller found.");
		}
	}
}
