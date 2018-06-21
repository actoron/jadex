package jadex.rules.rulesystem.rules;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;


/**
 *  A rule consists of a condition part
 *  and an action part that gets executed
 *  when the rule triggers.
 */
public class Rule implements IRule
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The condition. */
	protected ICondition condition;
	
	/** The action. */
	protected IAction action;
	
	/** The priority evaluator. */
	protected IPriorityEvaluator priority;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rule.
	 *  @param name The name.
	 *  @param condition The condition.
	 *  @param action The action.
	 */
	public Rule(String name, ICondition condition, IAction action)
	{
		this(name, condition, action, null);
	}
	
	/**
	 *  Create a new rule.
	 *  @param name The name.
	 *  @param condition The condition.
	 *  @param action The action.
	 */
	public Rule(String name, ICondition condition, IAction action, IPriorityEvaluator priority)
	{
		this.name = name;
		this.condition = condition;
		this.action = action;
		this.priority = priority;
	}

	//-------- methods --------
	
	/**
	 *  Get the condition.
	 *  @return The condition.
	 */
	public ICondition getCondition()
	{
		return condition;
	}

	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public IAction getAction()
	{
		return action;
	}	
	
	/**
	 *  Get the rule name.
	 *  @return The rule name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Get the priority. 
	 *  @return The priority.
	 */
	public IPriorityEvaluator getPriorityEvaluator()
	{
		return priority;
	}

	/**
	 *  Compute the hash code.
	 *  @return The hash code.
	 */
	public int hashCode()
	{
		return 31 + ((name == null) ? 0 : name.hashCode());
	}

	/**
	 *  Test for equality.
	 *  @param obj The object.
	 *  @return True, if this object equals obj. 
	 */
	public boolean equals(Object obj)
	{
		if(obj==this)
			return true;
		
		boolean ret = false;
		
		if(obj instanceof IRule)
			ret = SUtil.equals(getName(), ((Rule)obj).getName());
		
		return ret;
	}

	/**
	 *  Create a string representation of the rule.
	 */
	public String	toString()
	{
//		return name;
		return "("+condition+" => "+action+")";
	}
}
