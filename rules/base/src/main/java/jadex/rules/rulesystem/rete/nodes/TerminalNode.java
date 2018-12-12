package jadex.rules.rulesystem.rete.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.Activation;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rete.extractors.IValueExtractor;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  A terminal node stores the full matches and notifies
 *  the agenda of the activated conditions.
 */
public class TerminalNode extends AbstractNode implements ITupleConsumerNode
{
	//-------- attributes --------
	
	/** The tuple source. */
	protected ITupleSourceNode tsource;
	
	/** A mapping for fetching variable values (variable -> extractor). */
	protected Map extractors;
	
	/** The rule of the terminal node. */
	protected IRule	rule;

	/** The set of relevant attributes. */
	protected volatile AttributeSet relevants;

	/** The set of indirect attributes. */
	protected volatile AttributeSet indirects;

	//-------- constructors --------
	
	/**
	 *  Create a new node.
	 */
	public TerminalNode(int nodeid, IRule rule, Map extractors)
	{
		super(nodeid);
		this.rule = rule;
		this.extractors = extractors;
	}
	
	//-------- tuple consumer interface --------
	
	/**
	 *  Send an tuple to this node.
	 *  @param tuple The tuple.
	 */
	public void addTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		System.out.println("Add Tuple: "+tuple);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);

		// Store variable assignments.
		Map nodemem = (Map)mem.getNodeMemory(this);
		Map	vars	= new HashMap();
		for(Iterator it=extractors.keySet().iterator(); it.hasNext(); )
		{
			Object	variable	= it.next();
			vars.put(variable, ((IValueExtractor)extractors.get(variable)).getValue(tuple, null, null, state));
		}
		ReteVariableAssignments	assignments	= new ReteVariableAssignments(vars, rule);
		nodemem.put(tuple, assignments);
		
		// Create activation for tuple.
//		Activation act = new Activation(rule, new ReteVariableAssignments(state, tuple, extractors));
		Activation act = new Activation(rule, assignments, state);
		agenda.addActivation(act);

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		System.out.println("Remove Tuple: "+tuple);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);

		// Get old assignments.
		Map nodemem = (Map)mem.getNodeMemory(this);
		ReteVariableAssignments	assignments	= (ReteVariableAssignments)nodemem.remove(tuple);

//		Activation act = new Activation(rule, new ReteVariableAssignments(state, tuple, extractors));
		Activation act = new Activation(rule, assignments, state);
		agenda.removeActivation(act);

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Modify a tuple in this node.
	 *  @param tuple The tuple.
	 */
	public void modifyTuple(Tuple tuple, int tupleindex, OAVAttributeType type, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);

		// Get old assignments.
		Map nodemem = (Map)mem.getNodeMemory(this);
		ReteVariableAssignments	oldass	= (ReteVariableAssignments)nodemem.get(tuple);

		// Calculate new variable assignments.
		Map	newvars	= new HashMap();
		for(Iterator it=extractors.keySet().iterator(); it.hasNext(); )
		{
			Object	variable	= it.next();
			newvars.put(variable, ((IValueExtractor)extractors.get(variable)).getValue(tuple, null, null, state));
		}

		// Change activation, if necessary.
		if(!oldass.assignments.equals(newvars))
		{
			ReteVariableAssignments	newass	= new ReteVariableAssignments(newvars, rule);
			nodemem.put(tuple, newass);
//			System.out.println("Modify triggered: rule="+rule.getName()+" tuple="+tuple+", index="+tupleindex
//				+", attribute="+type+" oldvalue="+oldvalue+", newvalue="+newvalue);
			agenda.removeActivation(new Activation(rule, oldass, state));	
			agenda.addActivation(new Activation(rule, newass, state));
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}

	/**
	 *  Propagate an indirect object change to this node.
	 *  @param object The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Recheck all tuples
		Collection	input	= getTupleSource().getNodeMemory(mem);
		if(input!=null)
		{
			for(Iterator it=input.iterator(); it.hasNext(); )
			{
				modifyTuple((Tuple)it.next(), -1, null, null, null, state, mem, agenda);
			}
		}
	}

	/**
	 *  Set the tuple source of this node.
	 *  @param node The tuple source node.
	 */
	public void setTupleSource(ITupleSourceNode node)
	{
		this.tsource = node;
	}
	
	/**
	 *  Get the tuple source of this node.
	 *  @return The object source node.
	 */
	public ITupleSourceNode getTupleSource()
	{
		return tsource;
	}
	
	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		// Memory stores old variable assignments:
		// Map(tuple -> Map(variable -> value))
		return new HashMap();
	}
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		// Hack???
//		try
		{
			return getTupleSource().getNodeMemory(mem);
		}
//		catch(Exception e)
//		{
//			Object o = getTupleSource().getNodeMemory(mem);
//			return null;
//		}
	}

	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet getRelevantAttributes()
	{
		if(relevants==null)
		{
			synchronized(this)
			{
				if(relevants==null)
				{
					if(extractors.isEmpty())
					{
						relevants = AttributeSet.EMPTY_ATTRIBUTESET;
					}
					else
					{
						AttributeSet	relevants	= new AttributeSet();
						for(Iterator it=extractors.values().iterator(); it.hasNext(); )
						{
							IValueExtractor ex = (IValueExtractor)it.next();
							relevants.addAll(ex.getRelevantAttributes());
						}
						this.relevants	= relevants;
					}
				}
			}
		}
		return relevants;
	}
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		if(indirects==null)
		{
			synchronized(this)
			{
				if(indirects==null)
				{
					if(extractors.isEmpty())
					{
						indirects	= AttributeSet.EMPTY_ATTRIBUTESET;
					}
					else
					{
						AttributeSet	indirects	= new AttributeSet();
						for(Iterator it=extractors.values().iterator(); it.hasNext(); )
						{
							IValueExtractor ex = (IValueExtractor)it.next();
							indirects.addAll(ex.getIndirectAttributes());
						}
						this.indirects	= indirects;
					}
				}
			}
		}
		return indirects;
	}

	/**
	 *  Get the rule.
	 *  @return The rule.
	 */
	public IRule getRule()
	{
		return rule;
	}
	
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{
		TerminalNode ret = (TerminalNode)theclone;
		
		// Source node is set from creating node
		ret.tsource = (ITupleSourceNode)tsource.clone();
	
		// Extractors shallow copy
		ret.extractors = (Map)((HashMap)extractors).clone();
		
		// Rule keeps the same
	}
	
	//-------- helpers --------
	
	/**
	 *  The rete variable assignment help extracting values for varaibles.
	 */
	public static class ReteVariableAssignments implements IVariableAssignments
	{
		//-------- attributes --------
		
//		/** The state. */
//		protected IOAVState state;
//		
//		/** The tuple. */
//		protected Tuple tuple;
//		
//		/** The extractors. */
//		protected Map extractors;
		
		/** The map with assignments. */
		protected Map assignments;
		
		/** The cached hashcode as multi-slots could change and would prevent lookup. */
		protected int hashcode;
		
		//-------- constructors --------
		
		/**
		 *  Create a new variable assignments. 
		 */
//		public ReteVariableAssignments(IOAVState state, Tuple tuple, Map extractors)
		public ReteVariableAssignments(Map assignments, IRule rule)
		{
//			this.state = state;
//			this.tuple = tuple;
//			this.extractors = extractors;
			
			this.assignments	= assignments;
			this.hashcode	= 31 * assignments.hashCode();
			
			for(Iterator it=assignments.keySet().iterator(); it.hasNext(); )
			{
				// Todo: check if variable is used in action to avoid unnecessary cloning?
				Object	key	= it.next();
				Object	val	= assignments.get(key);
				if(val instanceof Map)
				{
					Map	newval	= new HashMap();
					newval.putAll((Map)val);
					assignments.put(key, newval);
//					System.out.println("replacing "+rule.getName()+": "+newval);
				}
				else if(val instanceof Set)
				{
					Set	newval	= new HashSet();
					newval.addAll((Set)val);
					assignments.put(key, newval);
//					System.out.println("replacing "+rule.getName()+": "+newval);
				}
				else if(val instanceof List)
				{
					List	newval	= new ArrayList();
					newval.addAll((List)val);
					assignments.put(key, newval);
//					System.out.println("replacing "+rule.getName()+": "+newval);
				}
			}
		}
		
		//-------- constructors --------
			
		/**
		 *  Get a variable values.
		 *  @param var The variable name.
		 */
		public Object getVariableValue(String var)
		{
			if(!assignments.containsKey(var))
				throw new RuntimeException("Variable not found: "+var);

//			IValueExtractor ex = (IValueExtractor)extractors.get(var);
//			return ex.getValue(tuple, null, state);

			return assignments.get(var);
		}
		
		/**
		 *  Get the variable names.
		 *  @return All variable names.
		 */
		public String[] getVariableNames()
		{
//			return (String[])extractors.keySet().toArray(new String[extractors.keySet().size()]);
			return (String[])assignments.keySet().toArray(new String[assignments.keySet().size()]);
		}
		
		//-------- methods --------
		
		/**
		 *  Get the hashcode of this object.
		 *  @return The hashcode.
		 */
		public int hashCode()
		{
//			int result = 31 * state.hashCode();
//			result = 31 * result +  tuple.hashCode();
//			result = 31 * result +  extractors.hashCode();
//			return result;
			
			return hashcode;
		}
		
		/**
		 *  Test if an object equals this.
		 *  @param obj The object.
		 */
		public boolean equals(Object obj)
		{
			boolean ret = this==obj;
			
			if(!ret && obj instanceof ReteVariableAssignments)
			{
				ReteVariableAssignments va = (ReteVariableAssignments)obj;
//				ret = va.state == this.state && va.tuple.equals(this.tuple)
//					&& va.extractors.equals(this.extractors);
				
				ret	= va.assignments.equals(this.assignments);
			}
			
			return ret;
		}

		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
//			return ""+tuple.getObjects();

			return "VariableAssignment"+assignments;
		}
	}
}
