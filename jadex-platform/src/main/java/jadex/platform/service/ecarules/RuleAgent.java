package jadex.platform.service.ecarules;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.ecarules.IRuleService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.RuleEvent;
import jadex.rules.eca.RuleSystem;

import java.util.ArrayList;
import java.util.List;

/**
 *  Agent that exposes an eca rule engine as service.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IRuleService.class, implementation=@Implementation(expression="$pojoagent")))
public class RuleAgent implements IRuleService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;

	/** The rule engine. */
	protected RuleSystem rulesystem;
	
	/** The subscriptions. */
	protected List<SubscriptionIntermediateFuture<RuleEvent>> subscribers;
	
	//-------- methods --------
	
	/**
	 *  Init method.
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		this.rulesystem = new RuleSystem(agent);
		this.subscribers = new ArrayList<SubscriptionIntermediateFuture<RuleEvent>>();
		return IFuture.DONE;
	}
	
//	/**
//	 *  The agent body.
//	 */
//	@AgentBody
//	public void body()
//	{
//	}
	
	/**
	 *  Add an external event to the rule engine.
	 *  It will process the event and fire rules
	 *  accordingly.
	 *  @param event The event.
	 */
	public IIntermediateFuture<RuleEvent> addEvent(IEvent event)
	{
		final IntermediateFuture<RuleEvent> ret = new IntermediateFuture<RuleEvent>();
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
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> addRule(IRule<?> rule)
	{
		rulesystem.getRulebase().addRule(rule);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> removeRule(String rulename)
	{
		rulesystem.getRulebase().removeRule(rulename);
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe to rule executions.
	 */
	public ISubscriptionIntermediateFuture<RuleEvent> subscribe()
	{
		final SubscriptionIntermediateFuture<RuleEvent> ret = new SubscriptionIntermediateFuture<RuleEvent>();
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscribers.remove(ret);
			}
		});
		subscribers.add(ret);
		// signal with null subscribe done
		ret.addIntermediateResultIfUndone(null);
		return ret;
	}
	
	/**
	 * 
	 */
	protected void publishEvent(RuleEvent event)
	{
		for(SubscriptionIntermediateFuture<RuleEvent> sub: subscribers)
		{
			if(!sub.addIntermediateResultIfUndone(event))
			{
				subscribers.remove(sub);
			}
		}
	}
}
