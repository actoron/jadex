package jadex.bdiv3.example.recur;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.BeliefAdapter;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.PlanFailureException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *  Agent that has a goal for buying an amount of items.
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
	 *  A buy items goal that is responsible for buying
	 *  a number of items.
	 */
	@Goal(excludemode=MGoal.EXCLUDE_WHEN_FAILED)
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
		
		@GoalTargetCondition(events="items")
		protected boolean checkTarget()//@Event("items") Object ev)
		{
			return items.size()>=num;
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
		
		agent.dispatchTopLevelGoalAndWait(goal).addResultListener(new IResultListener<RecurBDI.BuyItemsGoal>()
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
		
//		agent.addBeliefListener("items", new BeliefAdapter()
//		{
//			public void factAdded(Object value)
//			{
//				System.out.println("added: "+value);
//			}
//		});
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame f = new JFrame();
				PropertiesPanel pp = new PropertiesPanel();
				
				final JTextField tfm = pp.createTextField("money", ""+money);
				final NumberFormat formatter = NumberFormat.getCurrencyInstance();
				agent.addBeliefListener("money", new BeliefAdapter()
				{
					public void beliefChanged(Object val)
					{
						tfm.setText(formatter.format(((Double)val).doubleValue()));
					}
				});
				
				final JButton bu = pp.createButton("add", "Add money");
				bu.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						setMoney(getMoney()+5);
					}
				});
				
				f.add(pp, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
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

	/**
	 *  Get the money.
	 *  @return The money.
	 */
	public double getMoney()
	{
		return money;
	}

	/**
	 *  Set the money.
	 *  @param money The money to set.
	 */
	public void setMoney(double money)
	{
		this.money = money;
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

