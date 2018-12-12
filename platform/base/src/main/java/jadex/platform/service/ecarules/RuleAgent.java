package jadex.platform.service.ecarules;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.ecarules.IRuleService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRulebase;
import jadex.rules.eca.RuleEvent;
import jadex.rules.eca.RuleSystem;

/**
 *  Agent that exposes an eca rule engine as service.
 *  Allows for adding/removing rules and getting events.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IRuleService.class))
public class RuleAgent extends RulebaseAgent implements IRuleService
{
//	/** The agent. */
//	@Agent
//	protected MicroAgent agent;

	/** The rule engine. */
	protected RuleSystem rulesystem;
	
	/** The subscriptions. */
	protected List<SubscriptionIntermediateFuture<RuleEvent>> resubscribers;
	
	//-------- methods --------
	
	/**
	 *  Init method.
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		this.rulesystem = new RuleSystem(agent);
		this.resubscribers = new ArrayList<SubscriptionIntermediateFuture<RuleEvent>>();
		return IFuture.DONE;
	}
	
	/**
	 *  Get the rulebase.
	 */
	public IRulebase getRulebase()
	{
		return rulesystem.getRulebase();
	}
	
	/**
	 *  Add an external event to the rule engine.
	 *  It will process the event and fire rules
	 *  accordingly.
	 *  @param event The event.
	 */
	public IIntermediateFuture<RuleEvent> addEvent(IEvent event)
	{
		final SubscriptionIntermediateFuture<RuleEvent> ret = (SubscriptionIntermediateFuture<RuleEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		rulesystem.addEvent(event);
		// todo: process more than one event until quiescience?
		rulesystem.processEvent().addResultListener(new IntermediateDelegationResultListener<RuleEvent>(ret)
		{
			public void customIntermediateResultAvailable(RuleEvent result)
			{
				super.customIntermediateResultAvailable(result);
				publishEvent(result);
			}
		});
		return ret;
	}
	
	/**
	 *  Subscribe to rule executions.
	 */
	public ISubscriptionIntermediateFuture<RuleEvent> subscribeToEngine()
	{
		final SubscriptionIntermediateFuture<RuleEvent> ret = (SubscriptionIntermediateFuture<RuleEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				resubscribers.remove(ret);
			}
		});
		resubscribers.add(ret);
		// signal with null subscribe done
		ret.addIntermediateResultIfUndone(null);
		return ret;
	}
	
	/**
	 * 
	 */
	protected void publishEvent(RuleEvent event)
	{
		for(SubscriptionIntermediateFuture<RuleEvent> sub: resubscribers)
		{
			if(!sub.addIntermediateResultIfUndone(event))
			{
				resubscribers.remove(sub);
			}
		}
	}
}
