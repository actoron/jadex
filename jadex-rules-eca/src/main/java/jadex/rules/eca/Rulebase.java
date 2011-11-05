package jadex.rules.eca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rulebase implements IRulebase
{
	/** The rules. */
	protected Map<String, List<IRule>>rules;

	/**
	 * 
	 */
	public void addRule(IRule rule)
	{
		if(rules==null)
			rules = new HashMap<String, List<IRule>>();
		List<String> events = rule.getEvents();
		for(int i=0; i<events.size(); i++)
		{
			String event = events.get(i);
			List<IRule> rs = rules.get(event);
			if(rs==null)
			{
				rs = new ArrayList<IRule>();
				rules.put(event, rs);
			}
			rs.add(rule);
		}
	}
	
	/**
	 * 
	 */
	public void removeRule(IRule rule)
	{
		if(rules!=null)
		{
			List<String> events = rule.getEvents();
			for(int i=0; i<events.size(); i++)
			{
				String event = events.get(i);
				List<IRule> rs = rules.get(event);
				rs.remove(rule);
				if(rs.size()==0)
				{
					rules.remove(event);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public List<IRule> getRules(String event)
	{
		return rules!=null? rules.get(event): null;
	}

	
}
