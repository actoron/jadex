package jadex.bdi.examples.shop;

import jadex.bdi.runtime.Plan;
import jadex.commons.IFuture;

/**
 *  Buy a specific item in a given shop.
 */
public class BuyItemPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IShop shop = (IShop)getParameter("shop").getValue();
		String name	= (String)getParameter("name").getValue();
		double price = ((Double)getParameter("price").getValue()).doubleValue();
		
		System.out.println(getComponentName()+" buying item: "+name);
		IFuture	future	= shop.buyItem(name, price);
		System.out.println(getComponentName()+" getting item: "+future);
		Object item = future.get(this);
		System.out.println(getComponentName()+" bought item: "+item);
		getParameter("result").setValue(item);
	}
}
