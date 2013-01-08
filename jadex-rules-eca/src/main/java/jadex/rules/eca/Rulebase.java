package jadex.rules.eca;

import java.util.ArrayList;
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
	
	/** The rules (Event name -> {rules}). */
	protected Map<String, List<IRule<?>>>evrules;

	//-------- methods --------
	
	/**
	 *  Add a new rule.
	 *  @param rule The rule.
	 */
	public void addRule(IRule<?> rule)
	{
		if(rules==null)
			rules = new HashMap<String, IRule<?>>();
		if(evrules==null)
			evrules = new HashMap<String, List<IRule<?>>>();
		
		if(rules.containsKey(rule.getName()))
			throw new IllegalArgumentException("Rule names must be unique: "+rule.getName());
		
		rules.put(rule.getName(), rule);
		
		List<String> events = rule.getEvents();
		for(int i=0; i<events.size(); i++)
		{
			String event = events.get(i);
			List<IRule<?>> rs = evrules.get(event);
			if(rs==null)
			{
				rs = new ArrayList<IRule<?>>();
				evrules.put(event, rs);
			}
			rs.add(rule);
		}
		
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
		
		if(evrules!=null)
		{
			List<String> events = rule.getEvents();
			for(int i=0; i<events.size(); i++)
			{
				String event = events.get(i);
				List<IRule<?>> rs = evrules.get(event);
				rs.remove(rule);
				if(rs.size()==0)
				{
					evrules.remove(event);
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
//		if(event.equals("factchanged.environment"))
//			System.out.println("ff");
		
		return evrules!=null? evrules.get(event): null;
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
