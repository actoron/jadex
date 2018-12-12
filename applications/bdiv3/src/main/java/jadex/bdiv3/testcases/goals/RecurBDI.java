package jadex.bdiv3.testcases.goals;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.rules.eca.annotations.Event;

/**
 *  Agent that has a goal for buying an amount of items.
 *  
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class RecurBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The list of items. */
	@Belief
	protected List<Item> items = new ArrayList<Item>();
	
	/** The initial money. */
	@Belief
	protected double money = 100;
	
	/** The items in store. */
	protected List<Item> store;
	
	protected TestReport tr = new TestReport("#1", "Test if recur works");
	
	/**
	 *  A buy items goal that is responsible for buying
	 *  a number of items.
	 */
	@Goal(excludemode=ExcludeMode.WhenFailed, recur=true)
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
		
		@GoalTargetCondition//(beliefs="items")
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
		
//		@GoalResult
//		protected String getResult()
//		{
//			return "hello";
//		}
	}
	
//	@GoalMethod(BuyItemsGoal.class, kind=subgoal)
//	protected native IFuture<String> buyItems(int num);
	
//  lamdba goal
//	@GoalMethod(kind=subgoal)
//	protected native IFuture<LamdbaGoal> buyItems(int num);
	
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
		
		IFuture<BuyItemsGoal> fut = agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
		fut.addResultListener(new IResultListener<RecurBDI.BuyItemsGoal>()
		{
			public void resultAvailable(BuyItemsGoal result)
			{
				System.out.println("succ: "+result);
				tr.setSucceeded(true);
				agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				agent.killComponent();
			}
		});
		
//		agent.addBeliefListener("items", new BeliefAdapter()
//		{
//			public void factAdded(Object value)
//			{
//				System.out.println("added: "+value);
//			}
//		});
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				setMoney(getMoney()+5);
				return IFuture.DONE;
			}
		});
		
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				JFrame f = new JFrame();
//				PropertiesPanel pp = new PropertiesPanel();
//				
//				final JTextField tfm = pp.createTextField("money", ""+money);
//				final NumberFormat formatter = NumberFormat.getCurrencyInstance();
//				agent.addBeliefListener("money", new BeliefAdapter()
//				{
//					public void beliefChanged(Object val)
//					{
//						tfm.setText(formatter.format(((Double)val).doubleValue()));
//					}
//				});
//				
//				final JButton bu = pp.createButton("add", "Add money");
//				bu.addActionListener(new ActionListener()
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						agent.scheduleStep(new IComponentStep<Void>()
//						{
//							public IFuture<Void> execute(IInternalAccess ia)
//							{
//								setMoney(getMoney()+5);
//								return IFuture.DONE;
//							}
//						});
//					}
//				});
//				
//				f.add(pp, BorderLayout.CENTER);
//				f.pack();
//				f.setLocation(SGUI.calculateMiddlePosition(f));
//				f.setVisible(true);
//			}
//		});
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		if(!tr.isFinished())
			tr.setFailed("Recur did not occur");
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
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


/**
 * 
 */
static class Item
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

}
