package jadex.rules.rulesystem;

import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.state.IOAVState;


/**
 *  An activation is a rule and an associated fact
 *  tuple which represents a full match.
 *  Activations are created from terminal nodes
 *  and will be added to the agenda.
 */
public class Activation
{
	//-------- attributes --------
	
	/** The rule. */
	protected IRule rule;

	/** The values. */
	protected IVariableAssignments values;
	
	/** The state. */
	protected IOAVState state;
	
	/** The priority. */
	protected int priority;
	protected boolean inited;
	
	//-------- constructors --------
	
	/**
	 *  Create a new Activation.
	 *  @param rule The rule.
	 */
	public Activation(IRule rule, IVariableAssignments values, IOAVState state)
	{
		this.rule = rule;
		this.values = values;
		this.state = state;
	}

	//-------- methods --------

	/**
	 *  Get the rule.
	 *  @return The rule.
	 */
	public IRule getRule()
	{
		return rule;
	}
	
	/**
	 *  Get the variable assignments.
	 */
	public IVariableAssignments	getVariableAssignments()
	{
		return values;
	}
	
	/**
	 *  Get the priority.
	 *  @return The priority.
	 */
	public int getPriority()
	{
		if(!inited)
		{
			IPriorityEvaluator pe = rule.getPriorityEvaluator();
			if(pe!=null)
				priority = pe.getPriority(state, values);
			inited = true;
		}
		return priority;
	}
	
	/**
	 *  Execute the activation.
	 */
	public void execute()
	{
		getRule().getAction().execute(state, values);
	}

	/**
	 *  Get the hashcode of this object.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		int result = 31 * rule.hashCode();
		result = 31 * result +  values.hashCode();
		return result;
	}

	/**
	 *  Test if two activations are equal.
	 *  @param o The object to test.
	 */
	public boolean equals(Object o)
	{
		if(o==this)
			return true;
		
		boolean ret = false;
		
		if(o instanceof Activation)
		{
			Activation act = (Activation)o;
			ret = rule.equals(act.getRule()) && values.equals(act.values);
		}
		
		return ret;
	}

	/**
	 *  Create a string representation of the activation.
	 */
	public String	toString()
	{
		return "Activation(rule="+rule.getName()+", values="+values+", priority="+priority+")";
//		return "Activation(rule="+rule.getName()+" priority="+priority+" "+values.hashCode()+")";
	}
}
