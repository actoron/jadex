package jadex.platform.service.ecarules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.ecarules.IRulebaseEvent;
import jadex.bridge.service.types.ecarules.IRulebaseService;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.rules.eca.IRule;
import jadex.rules.eca.IRulebase;
import jadex.rules.eca.Rulebase;

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
	protected IInternalAccess agent;
	
	/** The subscriptions. */
	protected List<SubscriptionIntermediateFuture<IRulebaseEvent>> rbsubscribers = new ArrayList<SubscriptionIntermediateFuture<IRulebaseEvent>>();
	
	/** The rulebase. */
	protected IRulebase rulebase;
	
	/** The open calls (callid -> set of event ids that have to be acked. */
	protected Map<Integer, Set<Integer>> opencalls = new HashMap<Integer, Set<Integer>>();
	/** callid -> future . */
	protected Map<Integer, Future<Void>> callfutures = new HashMap<Integer, Future<Void>>();
	
	// todo?!: change validation interceptor to check whether service AND agent have been initialized 
//	/**
//	 *  Called on agent creation.
//	 */
//	@AgentCreated
//	public void agentCreated()
//	{
//		this.rbsubscribers = new ArrayList<SubscriptionIntermediateFuture<IRulebaseEvent>>();
//		this.opencalls = new HashMap<Integer, Set<Integer>>();
//		this.callfutures = new HashMap<Integer, Future<Void>>();
//	}
	
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
			Set<Integer> evs = new HashSet<Integer>();
			Integer callid = Integer.valueOf(ret.hashCode());
			callfutures.put(callid, ret);
			opencalls.put(callid, evs);
			notifySubscribers(new RuleAddedEvent(callid.intValue(), rule), evs).addResultListener(new DelegationResultListener<Void>(ret));
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
			Set<Integer> evs = new HashSet<Integer>();
			Integer callid = Integer.valueOf(ret.hashCode());
			callfutures.put(callid, ret);
			opencalls.put(callid, evs);
			notifySubscribers(new RuleRemovedEvent(callid.intValue(), rulename), evs).addResultListener(new DelegationResultListener<Void>(ret));
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
//		System.out.println("subscribed: "+ServiceCall.getCurrentInvocation().getCaller());
		
		final SubscriptionIntermediateFuture<IRulebaseEvent> ret = (SubscriptionIntermediateFuture<IRulebaseEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		ret.addBackwardCommand(new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof FinishedEvent;
			}
		}, new ICommand<Object>()
		{
			public void execute(Object args)
			{
				// received a finished id
				FinishedEvent ev = (FinishedEvent)args;
				Integer callid = Integer.valueOf(ev.getCallId());
				Set<Integer> evs = opencalls.get(callid);
				evs.remove(Integer.valueOf(ev.getId()));
				if(evs.isEmpty())
				{
					opencalls.remove(callid);
					Future<Void> ret = callfutures.remove(callid);
					ret.setResult(null);
				}
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
	protected IFuture<Void> notifySubscribers(ARulebaseEvent event, Set<Integer> evs)
	{
		Future<Void> ret = new Future<Void>();
		
		if(!rbsubscribers.isEmpty())
		{
			ARulebaseEvent re = event;
			
			for(SubscriptionIntermediateFuture<IRulebaseEvent> sub: rbsubscribers)
			{
				evs.add(Integer.valueOf(re.getId()));
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
			if(!sub.addIntermediateResultIfUndone(new RuleAddedEvent(-1, rule)))
			{
				rbsubscribers.remove(sub);
			}
		}
	}
}
