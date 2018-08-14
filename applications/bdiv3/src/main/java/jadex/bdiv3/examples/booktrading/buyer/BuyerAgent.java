package jadex.bdiv3.examples.booktrading.buyer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@RequiredServices(
{
	@RequiredService(name="buyservice", type=IBuyBookService.class, multiple=true),
	@RequiredService(name="clockser", type=IClockService.class)
})
@Arguments(@Argument(name="initial_orders", clazz=Order[].class))
public class BuyerAgent implements INegotiationAgent
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
		
		SwingUtilities.invokeLater(()->
		{
			gui	= new Future<>();
			if(agent!=null)
			{
				try
				{
					gui.setResult(new Gui(agent.getExternalAccess()));
				}
				catch(ComponentTerminatedException cte)
				{
 				}
			}
		});
	}
	
	/**
	 *  Called when agent terminates.
	 */
	@AgentKilled
	public void shutdown()
	{
		agent	= null;
		if(gui!=null)
		{
			gui.addResultListener(thegui ->
			{
				SwingUtilities.invokeLater(()->thegui.dispose());
			});
		}
	}
	
	@Goal(recur=true, recurdelay=10000, unique=true)
	public class PurchaseBook implements INegotiationGoal
	{
		@GoalParameter
		protected Order order;

		/**
		 *  Create a new PurchaseBook. 
		 */
		public PurchaseBook(Order order)
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
	
	/**
	 * 
	 */
	@Belief(rawevents={@RawEvent(ChangeEvent.GOALADOPTED), @RawEvent(ChangeEvent.GOALDROPPED), 
		@RawEvent(ChangeEvent.PARAMETERCHANGED)})
	public List<Order> getOrders()
	{
//		System.out.println("getOrders belief called");
		List<Order> ret = new ArrayList<Order>();
		Collection<PurchaseBook> goals = agent.getFeature(IBDIAgentFeature.class).getGoals(PurchaseBook.class);
		for(PurchaseBook goal: goals)
		{
			ret.add(goal.getOrder());
		}
		return ret;
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
	 * 
	 */
	@Plan(trigger=@Trigger(goals=PurchaseBook.class))
	protected void purchaseBook(PurchaseBook goal)
	{
		Order order = goal.getOrder();
		double time_span = order.getDeadline().getTime() - order.getStartTime();
		double elapsed_time = getTime() - order.getStartTime();
		double price_span = order.getLimit() - order.getStartPrice();
		int acceptable_price = (int)(price_span * elapsed_time / time_span)
			+ order.getStartPrice();

		// Find available seller agents.
		IBuyBookService[]	services = agent.getFeature(IRequiredServicesFeature.class).getServices("buyservice").get().toArray(new IBuyBookService[0]);
		if(services.length == 0)
		{
//			System.out.println("No seller found, purchase failed.");
			generateNegotiationReport(order, null, acceptable_price);
			throw new PlanFailureException();
		}

		// Initiate a call-for-proposal.
		Future<Collection<Tuple2<IBuyBookService, Integer>>>	cfp	= new Future<Collection<Tuple2<IBuyBookService, Integer>>>();
		final CollectionResultListener<Tuple2<IBuyBookService, Integer>>	crl	= new CollectionResultListener<Tuple2<IBuyBookService, Integer>>(services.length, true,
			new DelegationResultListener<Collection<Tuple2<IBuyBookService, Integer>>>(cfp));
		for(int i=0; i<services.length; i++)
		{
			final IBuyBookService	seller	= services[i];
			seller.callForProposal(order.getTitle()).addResultListener(new IResultListener<Integer>()
			{
				public void resultAvailable(Integer result)
				{
					crl.resultAvailable(new Tuple2<IBuyBookService, Integer>(seller, result));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					crl.exceptionOccurred(exception);
				}
			});
		}
		// Sort results by price.
		Tuple2<IBuyBookService, Integer>[]	proposals	= cfp.get().toArray(new Tuple2[0]);
		Arrays.sort(proposals, new Comparator<Tuple2<IBuyBookService, Integer>>()
		{
			public int compare(Tuple2<IBuyBookService, Integer> o1, Tuple2<IBuyBookService, Integer> o2)
			{
				return o1.getSecondEntity().compareTo(o2.getSecondEntity());
			}
		});

		// Do we have a winner?
		if(proposals.length>0 && proposals[0].getSecondEntity().intValue()<=acceptable_price)
		{
			proposals[0].getFirstEntity().acceptProposal(order.getTitle(), proposals[0].getSecondEntity().intValue()).get();
			
			generateNegotiationReport(order, proposals, acceptable_price);
			
			// If contract-net succeeds, store result in order object.
			order.setState(Order.DONE);
			order.setExecutionPrice(proposals[0].getSecondEntity());
			order.setExecutionDate(new Date(getTime()));
		}
		else
		{
			generateNegotiationReport(order, proposals, acceptable_price);
			
			throw new PlanFailureException();
		}
		//System.out.println("result: "+cnp.getParameter("result").getValue());
	}
	
	/**
	*  Generate and add a negotiation report.
	*/
	protected void generateNegotiationReport(Order order, Tuple2<IBuyBookService, Integer>[] proposals, double acceptable_price)
	{
		String report = "Accepable price: "+acceptable_price+", proposals: ";
		if(proposals!=null)
		{
			for(int i=0; i<proposals.length; i++)
			{
				report += proposals[i].getSecondEntity()+"-"+proposals[i].getFirstEntity().toString();
				if(i+1<proposals.length)
					report += ", ";
			}
		}
		else
		{
			report	+= "No seller found, purchase failed.";
		}
		NegotiationReport nr = new NegotiationReport(order, report, getTime());
		//System.out.println("REPORT of agent: "+getAgentName()+" "+report);
		reports.add(nr);
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
		PurchaseBook goal = new PurchaseBook(order);
		agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(goal);
	}
	
	/**
	 *  Get all purchase or sell goals.
	 */
	public Collection<INegotiationGoal> getGoals()
	{
		return (Collection)agent.getFeature(IBDIAgentFeature.class).getGoals(PurchaseBook.class);
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




