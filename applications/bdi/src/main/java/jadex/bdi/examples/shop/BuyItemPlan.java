package jadex.bdi.examples.shop;

import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.IFuture;

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
		IShopService shop = (IShopService)getParameter("shop").getValue();
		String name	= (String)getParameter("name").getValue();
		double price = ((Double)getParameter("price").getValue()).doubleValue();
		double money = ((Double)getBeliefbase().getBelief("money").getFact()).doubleValue();

		// Check if enough money to buy the item
		if(money<price)
			throw new RuntimeException("Not enough money to buy: "+name);
		
		// Buy the item at the shop (the shop is a service at another agent)
//		System.out.println(getComponentName()+" buying item: "+name);
		IFuture<ItemInfo>	future	= shop.buyItem(name, price);
//		System.out.println(getComponentName()+" getting item: "+future);
		ItemInfo item = (ItemInfo)future.get();
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
		// Re-read money, could have changed due to executed sell plan
		money = ((Double)getBeliefbase().getBelief("money").getFact()).doubleValue();
		getBeliefbase().getBelief("money").setFact(Double.valueOf(money-price));
	}
}
