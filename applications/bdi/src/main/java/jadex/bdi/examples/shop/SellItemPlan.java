package jadex.bdi.examples.shop;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan for selling an item.
 */
public class SellItemPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Fetch item data.
		String name = (String)getParameter("name").getValue();
		double price = ((Double)getParameter("price").getValue()).doubleValue();
		ItemInfo ii = (ItemInfo)getBeliefbase().getBeliefSet("catalog").getFact(new ItemInfo(name));
		
		// Check if enough money is given and it is in stock.
		if(ii.getQuantity()>0 && ii.getPrice()<=price)
		{
			// Sell item by updating catalog and account
//			System.out.println(getComponentName()+" sell item: "+name+" for: "+price);
			getParameter("result").setValue(new ItemInfo(name, ii.getPrice(), 1));
			ii.setQuantity(ii.getQuantity()-1);
			getBeliefbase().getBeliefSet("catalog").modified(ii);
			
			double money = ((Double)getBeliefbase().getBelief("money").getFact()).doubleValue();
			getBeliefbase().getBelief("money").setFact(Double.valueOf(money+price));
		}
		else if(ii.getQuantity()==0)
		{
			throw new RuntimeException("Item not in store: "+name);
		}
		else
		{
			throw new RuntimeException("Payment not sufficient: "+price);
		}
	}
}
