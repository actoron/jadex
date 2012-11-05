package jadex.platform.service.ecarules;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.ecarules.IRuleService;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.RuleSystem;

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
	
	//-------- methods --------
	
	/**
	 *  Init method.
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		this.rulesystem = new RuleSystem(agent);
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
	public IFuture<Void> addEvent(IEvent event)
	{
		rulesystem.addEvent(event);
		rulesystem.processAllEvents();
		return IFuture.DONE;
	}
	
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> addRule(IRule rule)
	{
		rulesystem.getRulebase().addRule(rule);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public IFuture<Void> removeRule(IRule rule)
	{
		rulesystem.getRulebase().removeRule(rule);
		return IFuture.DONE;
	}
}
