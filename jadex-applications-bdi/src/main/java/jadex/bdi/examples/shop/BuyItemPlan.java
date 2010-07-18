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
		// Fetch shop and item data
		IShop shop = (IShop)getParameter("shop").getValue();
		String name	= (String)getParameter("name").getValue();
		double price = ((Double)getParameter("price").getValue()).doubleValue();
		double mon = ((Double)getBeliefbase().getBelief("money").getFact()).doubleValue();

		// Check if enough money to buy the item
		if(mon<price)
			fail(new RuntimeException("Not enough money to buy: "+name));
		
		// Buy the item at the shop (the shop is a service at another agent)
//		System.out.println(getComponentName()+" buying item: "+name);
		IFuture	future	= shop.buyItem(name, price);
//		System.out.println(getComponentName()+" getting item: "+future);
		ItemInfo item = (ItemInfo)future.get(this);
//		System.out.println(getComponentName()+" bought item: "+item);
		getParameter("result").setValue(item);
		
		// Update the customer inventory 
		ItemInfo ii = (ItemInfo)getBeliefbase().getBeliefSet("inventory").getFact(item);
		if(ii==null)
		{
			ii = new ItemInfo(name, price, 1);
			getBeliefbase().getBeliefSet("inventory").addFact(ii);
		}
		else
		{
			ii.setQuantity(ii.getQuantity()+1);
			getBeliefbase().getBeliefSet("inventory").modified(ii);
		}
		
		// Update the account
		getBeliefbase().getBelief("money").setFact(new Double(mon-price));
	}
}
