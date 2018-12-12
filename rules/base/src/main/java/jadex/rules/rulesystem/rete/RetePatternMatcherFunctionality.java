package jadex.rules.rulesystem.rete;

import java.util.Iterator;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.rulesystem.IPatternMatcherState;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.IRulebaseListener;
import jadex.rules.rulesystem.rete.nodes.ReteMemory;
import jadex.rules.rulesystem.rete.nodes.ReteNode;
import jadex.rules.state.IOAVState;

/**
 *  The static (reusable) part of a Rete matcher (i.e. the Rete network).
 */
public class RetePatternMatcherFunctionality 
	implements IPatternMatcherFunctionality, IRulebaseListener, Cloneable
{
	//-------- attributes --------
	
	/** The rulebase. */
	protected IRulebase rulebase;
	
	/** The rete node. */
	protected ReteNode node;
	
	//-------- constructors --------
	
	/**
	 *  Create a new Rete pattern matcher functionality.
	 */
	public RetePatternMatcherFunctionality(IRulebase rulebase)
	{
		this.rulebase = rulebase;
		this.node = new ReteNode();
		
		// Build existing rules of the rulebase.
		for(Iterator it=rulebase.getRules().iterator(); it.hasNext(); )
			node.addRule((IRule)it.next());
		
		node.setInited(true);

		rulebase.addRulebaseListener(this);
	}
	
	/**
	 *  Get the rulebase.
	 *  @return The rulebase.
	 */
	public IRulebase getRulebase()
	{
		return rulebase;
	}
	
	/**
	 * 
	 * /
	public void setRulebase(IRulebase rulebase)
	{
		// Remove listener from old rulebase
		if(this.rulebase!=null)
			this.rulebase.removeRulebaseListener(this);
		
		// Create executeable rules for new rulebase
		this.rulebase = rulebase;
		this.node = new ReteNode();
		for(Iterator it=rulebase.getRules().iterator(); it.hasNext(); )
			node.addRule((IRule)it.next());
		
		// Track rulebase changes
		rulebase.addRulebaseListener(this);
	}*/
	
	//-------- IRulebaseListener interface --------
	
	/**
	 *  Notification when a rule has been added.
	 *  @param rule The added rule.
	 */
	public void ruleAdded(IRule rule)
	{
		node.addRule(rule);
	}
	
	/**
	 *  Notification when a rule has been removed.
	 *  @param rule The removed rule.
	 */
	public void ruleRemoved(IRule rule)
	{
		node.removeRule(rule);
	}
	
	//-------- IPatternMatcherFunctionality interface --------
	
	/**
	 *  Create a pattern matcher instance for a given state.
	 */
	public IPatternMatcherState createMatcherState(IOAVState state, AbstractAgenda agenda)
	{
		return new RetePatternMatcherState(node, state, new ReteMemory(state), agenda);
	}

	//-------- methods --------
	
	/**
	 *  Get the Rete node.
	 */
	public ReteNode	getReteNode()
	{
		return node;
	}
	
	//-------- cloneable --------
	
	/**
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone()
	{
		RetePatternMatcherFunctionality ret = null;
		
		try
		{	
			ret = (RetePatternMatcherFunctionality)super.clone();
			ret.rulebase = (IRulebase)rulebase.clone();
			ret.rulebase.addRulebaseListener(ret);
			ret.node = (ReteNode)node.clone();
		}
		catch(CloneNotSupportedException exception)
		{
			throw new RuntimeException("Cloning not supported: "+this);
		}
		
		return ret;
	}
}
