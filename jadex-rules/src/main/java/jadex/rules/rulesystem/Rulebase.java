package jadex.rules.rulesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *  The rule base of the system containing all rules.
 */
public class Rulebase implements IRulebase
{
	//-------- attributes --------
	
	/** The rules. */
	protected Set rules;
	
	/** The listeners. */
	protected List listeners;

	//-------- constructors --------
	
	/**
	 *  Create a new rulebase.
	 */
	public Rulebase()
	{
		this.rules = new LinkedHashSet();
		this.listeners = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a rule.
	 *  @param rule The rule to add.
	 */
	public void addRule(IRule rule)
	{
		if(rules.contains(rule))
			throw new RuntimeException("Rulebase already contains rule: "+rule.getName());
		
		rules.add(rule);
		
		for(int i=0; i<listeners.size(); i++)
			((IRulebaseListener)listeners.get(i)).ruleAdded(rule);
	}
	
	/**
	 *  Remove a rule.
	 *  @param rule The rule to remove.
	 */
	public void removeRule(IRule rule)
	{
		rules.remove(rule);
		
		for(int i=0; i<listeners.size(); i++)
			((IRulebaseListener)listeners.get(i)).ruleRemoved(rule);
	}
	
	/**
	 *  Get all rules.
	 *  @return All rules.
	 */
	public Collection getRules()
	{
		return rules;
	}
	
	//-------- rulebase observers --------
	
	/**
	 *  Add a new rulebase listener.
	 *  @param listener The rulebase listener.
	 */
	public void addRulebaseListener(IRulebaseListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList();
		this.listeners.add(listener);
	}
	
	/**
	 *  Remove a rulebase listener.
	 *  @param listener The rulebase listener.
	 */
	public void removeRulebaseListener(IRulebaseListener listener)
	{
		if(listeners!=null)
			this.listeners.remove(listener);
	}
	
	//-------- cloneable --------
	
	/**
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone()
	{
		Rulebase ret = null;
		
		try
		{	
			ret = (Rulebase)super.clone();
			
			// Don't copy listeners
			ret.listeners = new ArrayList();
			
			// Shallow clone rule set
			ret.rules = (LinkedHashSet)((LinkedHashSet)rules).clone();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Cloning did not work.");
		}
		
		return ret;
	}
}
