package jadex.rules.eca;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface IRulebase
{
	/**
	 * 
	 */
	public void addRule(IRule rule);
	
	/**
	 * 
	 */
	public void removeRule(IRule rule);
	
	/**
	 * 
	 */
	public List<IRule> getRules(String event);
}
