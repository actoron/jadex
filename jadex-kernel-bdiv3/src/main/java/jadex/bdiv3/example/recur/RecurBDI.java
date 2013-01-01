package jadex.bdiv3.example.recur;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.PlanFailureException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

/**
 *
 */
@Agent
public class RecurBDI
{
	/** The bdi agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The list of items. */
	@Belief
	protected List<Item> items;
	
	/** The initial money. */
	@Belief
	protected double money = 100;
	
	/** The items in store. */
	protected List<Item> store;
	
	/**
	 * 
	 */
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class BuyItemsGoal
	{
		protected int num;
		
		/**
		 *  Create a new goal.
		 */
		public BuyItemsGoal(int num)
		{
			this.num = num;
		}
		
		@GoalTargetCondition
		protected boolean checkTarget(@Event("money") double mon)
		{
			return items.size()>num;
		}
		
		@GoalRecurCondition
		protected boolean checkRecur(@Event("money") double mon)
		{
			boolean ret = false;
			for(Item item: store)
			{
				if(item.getPrice()<money)
				{
					ret = true;
					break;
				}
			}
			return ret;
		}
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		this.store = new ArrayList<Item>();
		store.add(new Item("milk", 0.99));
		store.add(new Item("shoes", 99));
		store.add(new Item("banana", 0.56));
		store.add(new Item("t-shirt", 22));
		store.add(new Item("cookies", 2.99));
		store.add(new Item("apples", 1.98));
		store.add(new Item("salt", 0.98));
		store.add(new Item("pepper", 0.98));
	
		BuyItemsGoal goal = new BuyItemsGoal(5);
		
		agent.dispatchGoalAndWait(goal).addResultListener(new IResultListener<RecurBDI.BuyItemsGoal>()
		{
			public void resultAvailable(BuyItemsGoal result)
			{
				System.out.println("succ: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
	}
	
	/**
	 *  First plan. Fails with exception.
	 */
	@Plan(trigger=@Trigger(goals=BuyItemsGoal.class))
	protected IFuture<Void> buyItemPlan(BuyItemsGoal goal)
	{
		Future<Void> ret = new Future<Void>();
		
		Item bought = null;
		for(Item item: store)
		{
			if(money>item.getPrice())
			{
				bought = item;
				store.remove(item);
				items.add(bought);
				money -= item.getPrice();
				System.out.println("Bought: "+bought+" "+items);
				break;
			}
		}
		
		if(bought!=null)
		{
			ret.setResult(null);
		}
		else
		{
			ret.setException(new PlanFailureException());
		}
		
		return ret;
	}
}

/**
 * 
 */
class Item
{
	/** The item name. */
	protected String name;
	
	/** The item price. */
	protected double price;
	
	/**
	 * 
	 */
	public Item(String name, double price)
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
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
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
	 *  Set the price.
	 *  @param price The price to set.
	 */
	public void setPrice(double price)
	{
		this.price = price;
	}

	public String toString()
	{
		return "Item [name=" + name + ", price=" + price + "]";
	}
}

