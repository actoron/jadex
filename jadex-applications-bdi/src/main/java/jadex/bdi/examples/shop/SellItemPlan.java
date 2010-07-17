package jadex.bdi.examples.shop;

import jadex.bdi.runtime.Plan;

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
		String name = (String)getParameter("name").getValue();
		double money = ((Double)getParameter("price").getValue()).doubleValue();
		ItemInfo ii = (ItemInfo)getBeliefbase().getBeliefSet("catalog").getFact(new ItemInfo(name));
		
		if(ii.getQuantity()>0 && ii.getPrice()<=money)
		{
			System.out.println(getComponentName()+" sell item: "+name+" for: "+money);
			getParameter("result").setValue(new ItemInfo(name, ii.getPrice(), 1));
			ii.setQuantity(ii.getQuantity()-1);
			getBeliefbase().getBeliefSet("catalog").modified(ii);
		}
		else if(ii.getQuantity()==0)
		{
			throw new RuntimeException("Item not in store: "+name);
		}
		else
		{
			throw new RuntimeException("Money not sufficient: "+money);
		}
	}
}
