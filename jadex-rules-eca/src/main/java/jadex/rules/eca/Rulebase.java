package jadex.rules.eca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Rulebase implementation.
 */
public class Rulebase implements IRulebase
{
	//-------- attributes --------
	
	/** The rules. */
	protected Map<String, List<IRule<?>>>rules;

	//-------- methods --------
	
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public void addRule(IRule<?> rule)
	{
		if(rules==null)
			rules = new HashMap<String, List<IRule<?>>>();
		List<String> events = rule.getEvents();
		for(int i=0; i<events.size(); i++)
		{
			String event = events.get(i);
			List<IRule<?>> rs = rules.get(event);
			if(rs==null)
			{
				rs = new ArrayList<IRule<?>>();
				rules.put(event, rs);
			}
			rs.add(rule);
		}
	}
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule.
	 */
	public void removeRule(IRule<?> rule)
	{
		if(rules!=null)
		{
			List<String> events = rule.getEvents();
			for(int i=0; i<events.size(); i++)
			{
				String event = events.get(i);
				List<IRule<?>> rs = rules.get(event);
				rs.remove(rule);
				if(rs.size()==0)
				{
					rules.remove(event);
				}
			}
		}
	}
	
	/**
	 *  Get all rules that are relevant for an event type.
	 *  @param event The event type.
	 *  @return The rules.
	 */
	public List<IRule<?>> getRules(String event)
	{
		return rules!=null? rules.get(event): null;
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
			// todo: optimize me and save rules additionally in rule list
			Iterator<List<IRule<?>>> rrls = rules.values().iterator();
			for(; rrls.hasNext() && ret==null;)
			{
				List<IRule<?>> rls = rrls.next(); 
				for(int i=0; i<rls.size() && ret==null; i++)
				{
					IRule<?> rl = rls.get(i);
					if(rl.getName().equals(name))
					{
						ret = rl;
					}
				}
			}
		}
		
		return ret;
	}
}
