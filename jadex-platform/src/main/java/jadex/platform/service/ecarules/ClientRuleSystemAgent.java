package jadex.platform.service.ecarules;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.types.ecarules.IRuleEngineService;
import jadex.bridge.service.types.ecarules.IRulebaseEvent;
import jadex.bridge.service.types.ecarules.IRulebaseService;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.RuleEvent;
import jadex.rules.eca.RuleSystem;

/**
 *  Agent that registers with an IRulebaseService and
 *  follows the master rule base.
 */
@RequiredServices(@RequiredService(name="rulebaseservice", type=IRulebaseService.class))
@ProvidedServices(@ProvidedService(type=IRuleEngineService.class))
@Agent
public class ClientRuleSystemAgent implements IRuleEngineService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The subscriptions. */
	protected List<SubscriptionIntermediateFuture<RuleEvent>> resubscribers;
	
	/**
	 *  Init method.
	 */
	@AgentCreated
	public void init()
	{
		this.rulesystem = new RuleSystem(agent);
		this.resubscribers = new ArrayList<SubscriptionIntermediateFuture<RuleEvent>>();
	}
	
	/**
	 * 
	 */
	@AgentServiceSearch
	public void	setRulebaseService(IRulebaseService rbser)
	{
		final ISubscriptionIntermediateFuture<IRulebaseEvent> subscription = rbser.subscribeToRulebase();
		subscription.addResultListener(new IntermediateDefaultResultListener<IRulebaseEvent>()
		{
			public void intermediateResultAvailable(IRulebaseEvent event)
			{
				if(event instanceof RuleAddedEvent)
				{
					rulesystem.getRulebase().addRule(((RuleAddedEvent)event).getRule());
//					System.out.println("Added rule: "+event);
				}
				else if(event instanceof RuleRemovedEvent)
				{
					rulesystem.getRulebase().removeRule(((RuleRemovedEvent)event).getRuleName());
//					System.out.println("Removed rule: "+event);
				}
				((ARulebaseEvent)event).setFinished(subscription);
			}
			
		    public void finished()
		    {
		    	// todo: find other rulebase service ?
		    	System.out.println("Terminated subscription to rule base service");
		    }
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
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
