package jadex.platform.service.ecarules;

import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.ecarules.IRulebaseEvent;
import jadex.bridge.service.types.ecarules.IRulebaseService;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.rules.eca.IRule;
import jadex.rules.eca.IRulebase;
import jadex.rules.eca.Rulebase;

import java.util.ArrayList;
import java.util.List;

/**
 *  Agent that encapsulates a rulebase and allows for tracking
 *  changes of it. Can be used to distribute rules among different
 *  rule engine agents that listen on the changes of the rulebase
 *  and do add/remove the same rules locally.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IRulebaseService.class))
public class RulebaseAgent implements IRulebaseService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The subscriptions. */
	protected List<SubscriptionIntermediateFuture<IRulebaseEvent>> rbsubscribers;
	
	/** The rulebase. */
	protected IRulebase rulebase;
	
	/**
	 *  Called on agent creation.
	 */
	@AgentCreated
	public void agentCreated()
	{
		this.rbsubscribers = new ArrayList<SubscriptionIntermediateFuture<IRulebaseEvent>>();
	}
	
	/**
	 *  Get the rulebase.
	 */
	public IRulebase getRulebase()
	{
		if(rulebase==null)
			rulebase = new Rulebase();
		return rulebase;
	}
	
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> addRule(IRule<?> rule)
	{
		Future<Void> ret = new Future<Void>();
		try
		{
			getRulebase().addRule(rule);
			notifySubscribers(new RuleAddedEvent(rule));
			ret.setResult(null);
		}
		catch(RuntimeException e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> removeRule(String rulename)
	{
		Future<Void> ret = new Future<Void>();
		try
		{
			getRulebase().removeRule(rulename);
			notifySubscribers(new RuleRemovedEvent(rulename));
			ret.setResult(null);
		}
		catch(RuntimeException e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Subscribe to rule base changes.
	 */
	public ISubscriptionIntermediateFuture<IRulebaseEvent> subscribeToRulebase()
	{
		final SubscriptionIntermediateFuture<IRulebaseEvent> ret = (SubscriptionIntermediateFuture<IRulebaseEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		ret.addCommand(new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof ARulebaseEvent;
			}
		}, new ICommand<Object>()
		{
			public void execute(Object args)
			{
				ARulebaseEvent ev = (ARulebaseEvent)args;
				int id = ev.getId();
			}
		});
		ret.setTerminationCommand(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				rbsubscribers.remove(ret);
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		rbsubscribers.add(ret);
		return ret;
	}
	
	/**
	 *  Notify all subscribers of an event.
	 */
	protected IFuture<Void> notifySubscribers(ARulebaseEvent event)
	{
		Future<Void> ret = new Future<Void>();
		
		if(!rbsubscribers.isEmpty())
		{
			ARulebaseEvent re = event;
			
			for(SubscriptionIntermediateFuture<IRulebaseEvent> sub: rbsubscribers)
			{
				if(!sub.addIntermediateResultIfUndone(re))
				{
					rbsubscribers.remove(sub);
				}
				re = re.createCopy();
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Notify the current state.
	 */
	protected void notifyCurrentState(SubscriptionIntermediateFuture<IRulebaseEvent> sub)
	{
		for(IRule<?> rule: rulebase.getRules())
		{
			if(!sub.addIntermediateResultIfUndone(new RuleAddedEvent(rule)))
			{
				rbsubscribers.remove(sub);
			}
		}
	}
}
