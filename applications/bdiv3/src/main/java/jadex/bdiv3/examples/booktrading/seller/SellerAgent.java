package jadex.bdiv3.examples.booktrading.seller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.booktrading.IBuyBookService;
import jadex.bdiv3.examples.booktrading.INegotiationAgent;
import jadex.bdiv3.examples.booktrading.INegotiationGoal;
import jadex.bdiv3.examples.booktrading.common.Gui;
import jadex.bdiv3.examples.booktrading.common.NegotiationReport;
import jadex.bdiv3.examples.booktrading.common.Order;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent(type=BDIAgentFactory.TYPE)
@Service
@ProvidedServices(@ProvidedService(type=IBuyBookService.class))
@RequiredServices(@RequiredService(name="clockser", type=IClockService.class))
@Arguments(@Argument(name="initial_orders", clazz=Order[].class))
public class SellerAgent implements IBuyBookService, INegotiationAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@Belief
	protected List<NegotiationReport> reports = new ArrayList<NegotiationReport>();

	protected Future<Gui> gui;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		Order[] ios = (Order[])agent.getFeature(IArgumentsResultsFeature.class).getArguments().get("initial_orders");
		if(ios!=null)
		{
			for(Order o: ios)
			{
				createGoal(o);
			}
		}
		
		gui	= new Future<>();
		SwingUtilities.invokeLater(()->
		{
			try
			{
				gui.setResult(new Gui(agent.getExternalAccess()));
			}
			catch(ComponentTerminatedException cte)
			{
			}
		});
	}
	
	/**
	 *  Called when agent terminates.
	 */
	@AgentKilled
	public void shutdown()
	{
		if(gui!=null)
		{
			gui.addResultListener(thegui ->
			{
				SwingUtilities.invokeLater(()->thegui.dispose());
			});
		}
	}
	
	@Goal(recur=true, recurdelay=10000, unique=true)
	public class SellBook implements INegotiationGoal
	{
		@GoalParameter
		protected Order order;

		/**
		 *  Create a new SellBook. 
		 */
		public SellBook(Order order)
		{
			this.order = order;
		}

		/**
		 *  Get the order.
		 *  @return The order.
		 */
		public Order getOrder()
		{
			return order;
		}
		
		@GoalDropCondition(parameters="order")
		public boolean checkDrop()
		{
			return order.getState().equals(Order.FAILED);
		}
		
		@GoalTargetCondition(parameters="order")
		public boolean checkTarget()
		{
			return Order.DONE.equals(order.getState());
		}
	}
	
	@Goal
	public class MakeProposal
	{
		protected String cfp;
		protected int proposal;
		
		/**
		 *  Create a new MakeProposal. 
		 */
		public MakeProposal(String cfp)
		{
			this.cfp = cfp;
		}

		/**
		 *  Get the cfp.
		 *  @return The cfp.
		 */
		public String getCfp()
		{
			return cfp;
		}

		/**
		 *  Get the proposal.
		 *  @return The proposal.
		 */
		public int getProposal()
		{
			return proposal;
		}

		/**
		 *  Set the proposal.
		 *  @param proposal The proposal to set.
		 */
		public void setProposal(int proposal)
		{
			this.proposal = proposal;
		}
		
	}
	
	@Goal
	public class ExecuteTask
	{
		protected String cfp;
		protected int proposal;
		
		/**
		 *  Create a new ExecuteTask. 
		 */
		public ExecuteTask(String cfp, int proposal)
		{
			super();
			this.cfp = cfp;
			this.proposal = proposal;
		}

		/**
		 *  Get the cfp.
		 *  @return The cfp.
		 */
		public String getCfp()
		{
			return cfp;
		}

		/**
		 *  Get the proposal.
		 *  @return The proposal.
		 */
		public int getProposal()
		{
			return proposal;
		}
	}

	/**
	 * 
	 */
	@Belief(rawevents={@RawEvent(ChangeEvent.GOALADOPTED), @RawEvent(ChangeEvent.GOALDROPPED)})
	public List<Order> getOrders()
	{
		List<Order> ret = new ArrayList<Order>();
		Collection<SellBook> goals = agent.getFeature(IBDIAgentFeature.class).getGoals(SellBook.class);
		for(SellBook goal: goals)
		{
			ret.add(goal.getOrder());
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public List<Order> getOrders(String title)
	{
		List<Order> ret = new ArrayList<Order>();
		Collection<SellBook> goals = agent.getFeature(IBDIAgentFeature.class).getGoals(SellBook.class);
		for(SellBook goal: goals)
		{
			if(title==null || title.equals(goal.getOrder().getTitle()))
			{
				ret.add(goal.getOrder());
			}
		}
		return ret;
	}
	
	@Plan(trigger=@Trigger(goals=MakeProposal.class))
	protected void makeProposal(MakeProposal goal)
	{
		final long time = getTime();
		List<Order> orders = getOrders(goal.getCfp());
		
		if(orders.isEmpty())
			throw new PlanFailureException();
			
		Collections.sort(orders, new Comparator<Order>()
		{
			public int compare(Order o1, Order o2)
			{
				double prio1 = (time-o1.getStartTime()) / (o1.getDeadline().getTime()-o1.getStartTime());
				double prio2 = (time-o1.getStartTime()) / (o1.getDeadline().getTime()-o1.getStartTime());
				return prio1>prio2? 1: prio1<prio2? -1: o1.hashCode()-o2.hashCode();
			}
		});
		Order order = orders.get(0);
		
		// Use most urgent order for preparing proposal.
//		if(suitableorders.length > 0)
		if(order!=null)
		{
//				Order order = suitableorders[0];
			
			double time_span = order.getDeadline().getTime() - order.getStartTime();
			double elapsed_time = getTime() - order.getStartTime();
			double price_span = order.getLimit() - order.getStartPrice();
			int acceptable_price =  (int)(price_span * elapsed_time / time_span) + order.getStartPrice();
			agent.getLogger().info(agent.getId().getName()+" proposed: " + acceptable_price);
			
			// Store proposal data in plan parameters.
			goal.setProposal(acceptable_price);
			
			String report = "Made proposal: "+acceptable_price;
			NegotiationReport nr = new NegotiationReport(order, report, getTime());
			reports.add(nr);
		}
	}
	
	@Plan(trigger=@Trigger(goals=ExecuteTask.class))
	protected void executeTask(ExecuteTask goal)
	{
		// Search suitable open orders.
		final long time = getTime();
		List<Order> orders = getOrders(goal.getCfp());
		
		if(orders.isEmpty())
			throw new PlanFailureException();
			
		Collections.sort(orders, new Comparator<Order>()
		{
			public int compare(Order o1, Order o2)
			{
				double prio1 = (time-o1.getStartTime()) / (o1.getDeadline().getTime()-o1.getStartTime());
				double prio2 = (time-o1.getStartTime()) / (o1.getDeadline().getTime()-o1.getStartTime());
				return prio1>prio2? 1: prio1<prio2? -1: o1.hashCode()-o2.hashCode();
			}
		});
		Order order = orders.get(0);
		
		// Use most urgent order for preparing proposal.
	//	if(suitableorders.length > 0)
		if(order!=null)
		{
	//		Order order = suitableorders[0];
			
			double time_span = order.getDeadline().getTime() - order.getStartTime();
			double elapsed_time = getTime() - order.getStartTime();
			double price_span = order.getLimit() - order.getStartPrice();
			int acceptable_price =  (int)(price_span * elapsed_time / time_span) + order.getStartPrice();
		
			// Extract order data.
			int price = goal.getProposal();
			
			if(price>=acceptable_price)
			{
	//			getLogger().info("Execute order plan: "+price+" "+order);
	
				// Initiate payment and delivery.
				// IGoal pay = createGoal("payment");
				// pay.getParameter("order").setValue(order);
				// dispatchSubgoalAndWait(pay);
				// IGoal delivery = createGoal("delivery");
				// delivery.getParameter("order").setValue(order);
				// dispatchSubgoalAndWait(delivery);
			
				// Save successful transaction data.
				order.setState(Order.DONE);
				order.setExecutionPrice(price);
				order.setExecutionDate(new Date(getTime()));
				
				String report = "Sold for: "+price;
				NegotiationReport nr = new NegotiationReport(order, report, getTime());
				reports.add(nr);
			}
			else
			{
				throw new PlanFailureException();
			}
		}
	}
	
	/**
	 *  Get the current time.
	 */
	protected long getTime()
	{
		IClockService cs = (IClockService)agent.getFeature(IRequiredServicesFeature.class).getService("clockser").get();
		return cs.getTime();
	}
	
	/**
	 *  Ask the seller for a a quote on a book.
	 *  @param title	The book title.
	 *  @return The price.
	 */
	public IFuture<Integer> callForProposal(String title)
	{
		final Future<Integer>	ret	= new Future<Integer>();
		final MakeProposal goal = new MakeProposal(title);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal).addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				ret.setResult(Integer.valueOf(goal.getProposal()));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}

	/**
	 *  Buy a book
	 *  @param title	The book title.
	 *  @param price	The price to pay.
	 *  @return A future indicating if the transaction was successful.
	 */
	public IFuture<Void> acceptProposal(String title, int price)
	{
		final Future<Void>	ret	= new Future<Void>();
		ExecuteTask goal = new ExecuteTask(title, price);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal).addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public IInternalAccess getAgent()
	{
		return agent;
	}
	
	/**
	 *  Create a purchase or sell oder.
	 */
	public void createGoal(Order order)
	{
		SellBook goal = new SellBook(order);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
	}
	
	/**
	 *  Get all purchase or sell goals.
	 */
	public Collection<INegotiationGoal> getGoals()
	{
		return (Collection)agent.getFeature(IBDIAgentFeature.class).getGoals(SellBook.class); 
	}
	
	/**
	 *  Get all reports.
	 */
	public List<NegotiationReport> getReports(Order order)
	{
		List<NegotiationReport> ret = new ArrayList<NegotiationReport>();
		for(NegotiationReport rep: reports)
		{
			if(rep.getOrder().equals(order))
			{
				ret.add(rep);
			}
		}
		return ret;
	}
}
