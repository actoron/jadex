package jadex.rules.eca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The matcher node is the base class for event based rule matching.
 *  
 *  The task is to deliver the set of rules that needs to be checked
 *  against the event.
 */
public class MatcherNode
{
	//-------- attributes --------
	
	/** The map of child matcher nodes. */
	protected Map<String, MatcherNode> children;
	
	/** The directly managed rule sets. */
	protected Map<String, List<IRule<?>>> rules;
	
	//-------- methods --------
	
	/**
	 *  Get the rules for an event type (as string, delim is .)
	 *  @param type The event type.
	 *  @return The list of rules relevant for the event type.
	 */
	public List<IRule<?>> getRules(String type)
	{
		return getRules(new EventType(type));
	}
	
	/**
	 *  Get the rules for an event type.
	 *  @param type The event type.
	 *  @return The list of rules relevant for the event type.
	 */
	public List<IRule<?>> getRules(EventType type)
	{
		List<IRule<?>> ret = new ArrayList<IRule<?>>();
		getRules(type, 0, ret);
		return ret;
	}
	
	/**
	 *  Add a rule to the matcher.
	 *  @param rule The rule.
	 */
	public void addRule(IRule<?> rule)
	{
		for(EventType type: rule.getEvents())
		{
			addRule(type, rule, 0);
		}
	}
	
	/**
	 *  Remove a rule from the matcher.
	 *  @param rule The rule.
	 */
	public void removeRule(IRule<?> rule)
	{
		for(EventType type: rule.getEvents())
		{
			removeRule(type, rule, 0);
		}
	}

	//-------- helper methods --------

	/**
	 *  Get the rules for an event type at level i.
	 *  @param type The event type.
	 *  @param i The level.
	 */
	protected void getRules(EventType type, int i, List<IRule<?>> ret)
	{
		String[] subtypes = type.getTypes();
		if(i+1==subtypes.length)
		{
			List<IRule<?>> tmp = internalGetRules(subtypes[i]);
			if(tmp!=null)
				ret.addAll(tmp);
		}
		else
		{
			MatcherNode node = getChild(subtypes[i]);
			if(node!=null)
				node.getRules(type, i+1, ret);
			
		}
		List<IRule<?>> tmp = internalGetRules("*");
		if(tmp!=null)
			ret.addAll(tmp);
	}
	
	/**
	 *  Add a rule for event at a level.
	 *  @param type The event type.
	 *  @param rule The rule.
	 *  @param i The level.
	 */
	protected void addRule(EventType type, IRule<?> rule, int i)
	{
		if(type==null)
			throw new IllegalArgumentException("Type must not null");
		String[] subtypes = type.getTypes();
		if(i+1==subtypes.length)
		{
			addRule(subtypes[i], rule);
		}
		else
		{
			MatcherNode node = getOrCreateMatcherNode(subtypes[i]);
			node.addRule(type, rule, i+1);
		}
	}
	
	/**
	 *  Remove a rule for event from a level.
	 *  @param type The event type.
	 *  @param rule The rule.
	 *  @param i The level.
	 */
	protected void removeRule(EventType type, IRule<?> rule, int i)
	{
		String[] subtypes = type.getTypes();
		if(i+1==subtypes.length)
		{
			removeRule(subtypes[i], rule);
		}
		else
		{
			MatcherNode node = getOrCreateMatcherNode(subtypes[i]);
			node.removeRule(type, rule, i+1);
		}
	}
	
	/**
	 *  Get or create a matcher child node.
	 *  @param subtype The event string for the child matcher.
	 *  @return The child matcher.
	 */
	protected MatcherNode getOrCreateMatcherNode(String subtype)
	{
		MatcherNode node = getChild(subtype);
		if(node==null)
		{
			node = new MatcherNode();
			putChild(subtype, node);
		}
		return node;
	}
	
	/**
	 *  Add a rule for an event type.
	 *  @param subtype The subtype.
	 *  @param rule The rule.
	 */
	protected void addRule(String subtype, IRule<?> rule)
	{
		List<IRule<?>> rs = internalGetRules(subtype);
		if(rs==null)
		{
			rs = new ArrayList<IRule<?>>();
			if(rules==null)
				rules = new HashMap<String, List<IRule<?>>>();
			rules.put(subtype, rs);
		}
		rs.add(rule);
//		if(subtype.equals("d"))
//			System.out.println("add: "+subtype+" "+rs);
	}
	
	/**
	 *  Remove a rule for an event type.
	 *  @param subtype The subtype.
	 *  @param rule The rule.
	 */
	protected void removeRule(String subtype, IRule<?> rule)
	{
		List<IRule<?>> rs = internalGetRules(subtype);
		if(rs==null)
			throw new IllegalStateException("Rule not contained: "+rule);
		rs.remove(rule);
		if(rs.isEmpty())
			rules.remove(subtype);
	}
	
	/**
	 *  Add a child matcher node per given subtype.
	 *  @param type The event type.
	 *  @param node The matcher node.
	 */
	protected void putChild(String type, MatcherNode node)
	{
		if(children==null)
			children = new HashMap<String, MatcherNode>();
		children.put(type, node);
	}
	
	/**
	 *  Get the child matcher node.
	 *  @param type The event type.
	 *  @return The child matcher.
	 */
	protected MatcherNode getChild(String type)
	{
		return children==null? null: children.get(type);
	}
	
	/**
	 *  Get the directly stored rules of a matcher.
	 *  @param type The event type.
	 *  @return The list of rules.
	 */
	protected List<IRule<?>> internalGetRules(String type)
	{
		return rules==null? null: rules.get(type);
	}
	
	/** 
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "MatcherNode(children=" + children + ", rules=" + rules + ")";
	}

	/**
	 *  Main only for testing.
	 */
	public static void main(String[] args)
	{
		MatcherNode node = new MatcherNode();
		Rule<?> r1 = new Rule<Object>("a.b.c.d", null, null, new EventType[]{new EventType("a.b.c.d")});
		node.addRule(r1);
		node.addRule(new Rule<Object>("a.b", null, null, new EventType[]{new EventType("a.b")}));
		node.addRule(new Rule<Object>("a.b2", null, null, new EventType[]{new EventType("a.b2")}));
		node.addRule(new Rule<Object>("a.b.c2", null, null, new EventType[]{new EventType("a.b.c2")}));
		node.addRule(new Rule<Object>("a.b2", null, null, new EventType[]{new EventType("a.b2")}));
		node.addRule(new Rule<Object>("a.b2.c", null, null, new EventType[]{new EventType("a.b2.c")}));
		node.addRule(new Rule<Object>("a.b.c.d2", null, null, new EventType[]{new EventType("a.b.c.d")}));
		node.addRule(new Rule<Object>("a2", null, null, new EventType[]{new EventType("a2")}));
		node.addRule(new Rule<Object>("*", null, null, new EventType[]{new EventType("*")}));
		node.addRule(new Rule<Object>("a.b.*", null, null, new EventType[]{new EventType("a.b.*")}));
		node.addRule(new Rule<Object>("a.*", null, null, new EventType[]{new EventType("a.*")}));
		
		System.out.println(node);
		
//		System.out.println("*: "+node.getRules("*"));
//		System.out.println("a.*: "+node.getRules("a.*"));
//		System.out.println("a.b.*: "+node.getRules("a.b.*"));
		
//		System.out.println("a.b: "+node.getRules("a.b"));
		System.out.println("a.b.c: "+node.getRules("a.b.c"));
		System.out.println("a.b.c.d: "+node.getRules("a.b.c.d"));
//		System.out.println("a.b.c.d: "+node.getRules("a.b.c.d"));
		
		node.removeRule(r1);
	}
}
