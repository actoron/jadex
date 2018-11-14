package jadex.bdiv3.examples.shop;

import java.util.List;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Capability
@ProvidedServices(@ProvidedService(type=IShopService.class, //	implementation=@Implementation(value=ShopService.class)))
	implementation=@Implementation(expression="new ShopService($pojocapa.getShopname())")))
public class ShopCapa
{
	@Belief
	public native double getMoney();
	
	@Belief
	public native void setMoney(double money);
	
	/** The shop name. */
	protected String shopname;
	
	/** The shop catalog. */
	@Belief
	protected List<ItemInfo> catalog;
	
	/**
	 *  Create a shop capability.
	 */
	public ShopCapa(String shopname, List<ItemInfo> catalog)
	{
		this.shopname	= shopname;
		this.catalog	= catalog;
	}
	
	/**
	 *  Get the shop name.
	 */
	public String	getShopname()
	{
		return shopname;
	}
	
	/**
	 *  Get the catalog.
	 */
	public List<ItemInfo>	getCatalog()
	{
		return catalog;
	}
	
	@Goal
	public class SellGoal
	{
		/** The text. */
		protected String name;
		
		/** The price. */
		protected double price;
		
		/** The result. */
		@GoalResult
		protected ItemInfo result;

		/**
		 *  Create a new SellGoal. 
		 */
		public SellGoal(String name, double price)
		{
			this.name = name;
			this.price = price;
		}

		/**
		 *  Get the name.
		 *  @return The name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 *  Get the price.
		 *  @return The price.
		 */
		public double getPrice()
		{
			return price;
		}

		/**
		 *  Get the result.
		 *  @return The result.
		 */
		public ItemInfo getResult()
		{
			return result;
		}

		/**
		 *  Set the result.
		 *  @param result The result to set.
		 */
		public void setResult(ItemInfo result)
		{
			this.result = result;
		}
	}
	
	/**
	 *  Plan for handling a sell goal.
	 *  @param goal The goal.
	 */
	@Plan(trigger=@Trigger(goals=SellGoal.class))
	public void sell(SellGoal goal)
	{
		ItemInfo tst = new ItemInfo(goal.getName());
		ItemInfo ii = null;
		int pos = 0;
		for(; pos<catalog.size(); pos++)
		{
			ItemInfo tmp = catalog.get(pos);
			if(tmp.equals(tst))
			{
				ii = tmp;
				break;
			}
		}
		
		// Check if enough money is given and it is in stock.
		if(ii==null || ii.getQuantity()==0)
		{
			throw new RuntimeException("Item not in store: "+goal.getName());
		}
		else if(ii.getQuantity()>0 && ii.getPrice()<=goal.getPrice())
		{
			// Sell item by updating catalog and account
////		System.out.println(getComponentName()+" sell item: "+name+" for: "+price);
			ii.setQuantity(ii.getQuantity()-1);
			goal.setResult(new ItemInfo(goal.getName(), ii.getPrice(), 1));
//			getBeliefbase().getBeliefSet("catalog").modified(ii);
			catalog.set(pos, ii);
			
			setMoney(getMoney()+goal.getPrice());
		}
		else
		{
			throw new RuntimeException("Payment not sufficient: "+goal.getPrice());
		}
	}
}
