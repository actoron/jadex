package jadex.rules.eca;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Rulebase implementation.
 */
public class Rulebase implements IRulebase
{
	//-------- attributes --------
	
	/** The rules per name. */
	protected Map<String, IRule<?>> rules;
	
	protected MatcherNode matcher;

	//-------- methods --------
	
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public void addRule(IRule<?> rule)
	{
		if(rule.getEvents()==null)
			throw new RuntimeException("Rule must have events: "+rule);
		
		if(rules==null)
			rules = new HashMap<String, IRule<?>>();
		if(matcher==null)
			matcher = new MatcherNode();
		
		if(rules.containsKey(rule.getName()))
			throw new IllegalArgumentException("Rule names must be unique: "+rule.getName());
		
		rules.put(rule.getName(), rule);
		
		matcher.addRule(rule);
		
//		System.out.println("added rule: "+rule+" "+rule.getEvents());
		
//		System.out.println("evrules: "+evrules);
	}
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public void removeRule(String rulename)
	{
		IRule<?> rule = null;
		if(rules!=null)
		{
			rule = rules.remove(rulename);
			if(rule==null)
				throw new RuntimeException("Rule not contained: "+rulename);
		}
		
		if(matcher!=null)
			matcher.removeRule(rule);
	}
	
	/**
	 *  Get all rules that are relevant for an event type.
	 *  @param event The event type.
	 *  @return The rules.
	 */
	public List<IRule<?>> getRules(EventType event)
	{
//		if(event.equals("factchanged.environment"))
//			System.out.println("ff");
		
		return matcher==null? null: matcher.getRules(event);
	}
	
	/**
	 *  Get the rule.
	 *  @param event The rule name.
	 *  @return The rule.
	 */
	public IRule<?> getRule(String name)
	{
		IRule<?> ret = null;
		
		if(rules!=null)
		{
			ret = rules.get(name);
		}
		
		return ret;
	}
}
