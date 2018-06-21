package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  An alpha node is a 1-input -> 1-output node which
 *  propagates objects matching its constraints. 
 */
public class AlphaNode extends AbstractNode implements IObjectConsumerNode, IObjectSourceNode
{
	//-------- attributes --------
	
	/** The object source. */
	protected IObjectSourceNode osource;
	
	/** The object consumers. */
	protected IObjectConsumerNode[]	oconsumers;

	/** The constraint evaluator. */
	protected IConstraintEvaluator[] evaluators;
	
	/** The set of relevant attributes. */
	protected volatile AttributeSet	relevants;

	/** The set of indirect attributes. */
	protected volatile AttributeSet indirects;

	//-------- constructors --------
	
	/**
	 *  Create a new node.
	 *  @param evaluators The evaluators.
	 */
	public AlphaNode(int nodeid, IConstraintEvaluator[] evaluators)
	{
		super(nodeid);
		this.evaluators	= evaluators;
	}

	//-------- object consumer interface --------
	
	/**
	 *  Send a new object to this node.
	 *  @param object The object.
	 */
	public void addObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Add object called: "+this+" "+object);
		
//		if(object.getClass().toString().indexOf("Order")!=-1)
//			System.out.println("here: "+object);
//		if(state.getType(object).getName().indexOf("goal")!=-1)
//			System.out.println("here: "+object);
		
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		
		assert !mem.hasNodeMemory(this) || !((Collection)mem.getNodeMemory(this)).contains(object) : "New objects shouldn't be contained.";
		
		if(checkConstraints(object, state))
		{
			((Collection)mem.getNodeMemory(this)).add(object);
			//System.out.println("Object passed constraint check: "+this+" "+object);
			propagateAdditionToObjectConsumers(object, state, mem, agenda);
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Send a removed object to this node.
	 *  @param object The object.
	 */
	public void removeObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Remove object called: "+this+" "+object);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		
		if(mem.hasNodeMemory(this) && ((Collection)mem.getNodeMemory(this)).remove(object))
		{
			//System.out.println("Object passed constraint check: "+this+" "+object);
			propagateRemovalToObjectConsumers(object, state, mem, agenda);
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Propagate an object change to this node.
	 *  @param object The new object.
	 */
	public void modifyObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, 
		IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==369)
//		{
//			System.out.println("Modify object called: "+this+" "+object);
//		}
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);

		if(getRelevantAttributes().contains(type))
		{
			// Check if modification changes node memory.
			boolean affected = isAffected(type);
			boolean	contains	= mem.hasNodeMemory(this) && ((Collection)mem.getNodeMemory(this)).contains(object);
		
			if(affected)
			{
				boolean check = checkConstraints(object, state);
				
				// Object no longer valid -> remove
				if(contains && !check)
				{
					((Collection)mem.getNodeMemory(this)).remove(object);
					propagateRemovalToObjectConsumers(object, state, mem, agenda);
				}
		
				// Object newly valid -> add
				else if(!contains && check)
				{
					((Collection)mem.getNodeMemory(this)).add(object);
					propagateAdditionToObjectConsumers(object, state, mem, agenda);
				}
				
				// Propagate modification for deeper nodes if here no change.
				else if(contains)
				{
					propagateModificationToObjectConsumers(object, type, oldvalue, newvalue, 
						state, mem, agenda);
				}
			}
			else
			{
				// Object changed in memory -> propagate modification
				if(contains)
				{
					propagateModificationToObjectConsumers(object, type, oldvalue, newvalue, 
						state, mem, agenda);
				}
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}

	/**
	 *  Propagate an indirect object change to this node.
	 *  @param id The changed object.
	 */
	public void modifyIndirectObject(Object id, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		Collection	oldmem	= getNodeMemory(mem);
		Collection	input	= getObjectSource().getNodeMemory(mem);
		if(input!=null)
		{
			// Todo: Use index for avoiding the need for checking all objects.
			for(Iterator it=input.iterator(); it.hasNext(); )
			{
				Object	object	= it.next();
				boolean	contains	= oldmem!=null && oldmem.contains(object);		
				boolean check = checkConstraints(object, state);

				// Object no longer valid -> remove
				if(contains && !check)
				{
					((Collection)mem.getNodeMemory(this)).remove(object);
					propagateRemovalToObjectConsumers(object, state, mem, agenda);
				}

				// Object newly valid -> add
				else if(!contains && check)
				{
					((Collection)mem.getNodeMemory(this)).add(object);
					propagateAdditionToObjectConsumers(object, state, mem, agenda);
				}
			}
		}
	}

	/**
	 *  Set the object source of this node.
	 *  @param node The object source node.
	 */
	public void setObjectSource(IObjectSourceNode node)
	{
		this.osource = node;
	}
	
	/**
	 *  Get the object source of this node.
	 *  @return The object source node.
	 */
	public IObjectSourceNode getObjectSource()
	{
		return osource;
	}
	
	//-------- object source interface --------
		
	/**
	 *  Add an object consumer node.
	 *  @param node A new consumer node.
	 */
	public void addObjectConsumer(IObjectConsumerNode node)
	{
		if(oconsumers==null)
		{
			oconsumers = new IObjectConsumerNode[]{node};
		}
		else
		{
			IObjectConsumerNode[]	tmp	= new IObjectConsumerNode[oconsumers.length+1];
			System.arraycopy(oconsumers, 0, tmp, 0, oconsumers.length);
			tmp[oconsumers.length]	= node;
			oconsumers	= tmp;
		}

		relevants	= null;	// Will be recalculated on next access;
	}
	
	/**
	 *  Remove an object consumer.
	 *  @param node The consumer node.
	 */
	public void removeObjectConsumer(IObjectConsumerNode node)
	{
		if(oconsumers!=null)
		{
			for(int i=0; i<oconsumers.length; i++)
			{
				if(oconsumers[i].equals(node))
				{
					if(oconsumers.length==1)
					{
						oconsumers	= null;
					}
					else
					{
						IObjectConsumerNode[]	tmp	= new IObjectConsumerNode[oconsumers.length-1];
						if(i>0)
							System.arraycopy(oconsumers, 0, tmp, 0, i);
						if(i<oconsumers.length-1)
							System.arraycopy(oconsumers, i+1, tmp, i, oconsumers.length-1-i);
						oconsumers	= tmp;
					}
					break;
				}
			}
		}
	}
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		return mem.hasNodeMemory(this) ? (Collection)mem.getNodeMemory(this) : null;
	}

	/**
	 *  Get all object consumer nodes.
	 *  @return All object consumer nodes.
	 */
	public IObjectConsumerNode[] getObjectConsumers()
	{
		return oconsumers;
	}	

	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return state.isJavaIdentity() ? (Set)new MixedIdentityHashSet(state) :	new LinkedHashSet();
	}
	
	//-------- helper methods --------

	/**
	 *  Propagate a new object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateAdditionToObjectConsumers(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		IObjectConsumerNode[]	ocon	= oconsumers;
		for(int i=0; ocon!=null && i<ocon.length; i++)
			ocon[i].addObject(object, state, mem, agenda);
	}
	
	/**
	 *  Propagate a removed object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateRemovalToObjectConsumers(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		IObjectConsumerNode[]	ocon	= oconsumers;
		for(int i=0; ocon!=null && i<ocon.length; i++)
			ocon[i].removeObject(object, state, mem, agenda);
	}
	
	/**
	 *  Propagate a modified object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateModificationToObjectConsumers(Object object, OAVAttributeType type, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		IObjectConsumerNode[]	ocon	= oconsumers;
		for(int i=0; ocon!=null && i<ocon.length; i++)
			ocon[i].modifyObject(object, type, oldvalue, newvalue, state, mem, agenda);
	}
	
	/**
	 *  Check the constraints with respect 
	 *  to the object.
	 *  @return True, if object fits constraints.
	 */
	protected boolean checkConstraints(Object right, IOAVState state)
	{
		boolean pass = true;
		for(int i=0; pass && evaluators!=null && i<evaluators.length; i++)
			pass = evaluators[i].evaluate(right, null, state);
		return pass;
	}
	
	/**
	 *  Test if the node is affected from a modification.
	 *  @param type The attribute type.
	 *  @return True, if possibly affected.
	 */
	public boolean isAffected(OAVAttributeType attr)
	{
		boolean ret = false;
		for(int i=0; !ret && evaluators!=null && i<evaluators.length; i++)
			ret = evaluators[i].isAffected(-1, attr);
		return ret;
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
					AttributeSet	relevants	= new AttributeSet();
					for(int i=0; evaluators!=null && i<evaluators.length; i++)
					{
						relevants.addAll(evaluators[i].getRelevantAttributes());
					}
					for(int i=0; oconsumers!=null && i<oconsumers.length; i++)
					{
						relevants.addAll(oconsumers[i].getRelevantAttributes());
					}
					this.relevants	= relevants;
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
					AttributeSet	indirects	= new AttributeSet();
					for(int i=0; evaluators!=null && i<evaluators.length; i++)
					{
						indirects.addAll(evaluators[i].getIndirectAttributes());
					}
					this.indirects	= indirects;
				}
			}
		}
		return indirects;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return toString(", evaluators="+SUtil.arrayToString(evaluators));
	}

	/**
	 *  Get the constraint evaluators.
	 */
	public IConstraintEvaluator[] getConstraintEvaluators()
	{
		return evaluators;
	}
	
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{
		AlphaNode clone = (AlphaNode)theclone;
			
		// Deep clone tuple consumers
		clone.oconsumers = new IObjectConsumerNode[oconsumers.length];
		for(int i=0; i<oconsumers.length; i++)
			clone.oconsumers[i] = (IObjectConsumerNode)oconsumers[i].clone();
		
		// Set the new source
		clone.setObjectSource((IObjectSourceNode)osource.clone());
		
		// Shallow clone evaluators
		if(evaluators!=null)
		{
			clone.evaluators = new IConstraintEvaluator[evaluators.length];
			System.arraycopy(evaluators, 0, clone.evaluators, 0, evaluators.length);
		}
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
}
