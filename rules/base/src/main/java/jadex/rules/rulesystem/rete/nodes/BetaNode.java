package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.Iterator;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.ConstraintIndexer;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A beta node has the purpose to perform a constraints check
 *  between (at least) two objects. The beta node has to incoming
 *  coming connections which both can activate the node.
 */
public class BetaNode extends AbstractBetaNode
{
	//-------- constructors --------
	
	/**
	 *  Create a new beta node.
	 */
	public BetaNode(int nodeid, IConstraintEvaluator[] evaluators, ConstraintIndexer[] xevaluators)
	{
		super(nodeid, evaluators, xevaluators);
	}
	
	//-------- tuple consumer interface (left) --------
	
	/**
	 *  Modify a tuple in this node.
	 *  @param left The tuple.
	 */
	public void modifyTuple(Tuple left, int tupleindex, OAVAttributeType type,
		Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(!getRelevantAttributes().contains(type))
			return;

		super.modifyTuple(left, tupleindex, type, oldvalue, newvalue, state, mem, agenda);

		if(!isAffected(type))
		{
			Collection omem = fetchObjectMemory(state, left, mem);
			if(omem!=null)
			{
				for(Iterator it=omem.iterator(); it.hasNext(); )
				{
					Object	right = it.next();
					boolean	contains = isMatchContained(state, left, right, mem);
				
					// Tuple changed in memory -> propagate modification
					if(contains)
						propagateModification(left, right, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
				}
			}
		}
	}
	
	//-------- object consumer interface (right) --------
	
	/**
	 *  Propagate an object change to this node.
	 *  @param right The new object.
	 */
	public void modifyObject(Object right, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(!getRelevantAttributes().contains(type))
			return;

		super.modifyObject(right, type, oldvalue, newvalue, state, mem, agenda);
		
		if(!isAffected(type))
		{
			Collection tmem = fetchTupleMemory(state, right, mem);
			if(tmem!=null)
			{
				for(Iterator it=tmem.iterator(); it.hasNext(); )
				{
					Tuple	left = (Tuple)it.next();
					boolean	contains = isMatchContained(state, left, right, mem);
				
					// Tuple changed in memory -> propagate modification
					if(contains)
						propagateModification(left, right, left.size(), type, oldvalue, newvalue, state, mem, agenda);
				}
			}
		}
	}
	
	//-------- template methods --------

	/**
	 *  Add a match to the node memory and propagate if necessary.
	 */
	protected void addMatch(Tuple left, Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		Tuple result = mem.getTuple(state, left, right);
		BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
		if(bmem.addResultTuple(result))
		{
			ITupleConsumerNode[] tcs = tconsumers;
			for(int j=0; tcs!=null && j<tcs.length; j++)
				tcs[j].addTuple(result, state, mem, agenda);
		}
	}

	/**
	 *  Remove a match from the node memory and propagate if necessary.
	 */
	protected void removeMatch(Tuple left, Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(mem.hasNodeMemory(this))
		{
			Tuple result = mem.getTuple(state, left, right);
			if(((BetaMemory)mem.getNodeMemory(this)).removeResultTuple(result))
			{
//				if(getNodeId()==3713)
//					System.out.println("remmi: "+result);
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].removeTuple(result, state, mem, agenda);
			}
		}
	}

	/**
	 *  Propagate a change of a tuple in the result memory.
	 */
	protected void propagateModification(Tuple left, Object right, int tupleindex, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		Tuple	tuple	= mem.getTuple(state, left, right);
		ITupleConsumerNode[] tcs = tconsumers;
		for(int j=0; tcs!=null && j<tcs.length; j++)
			tcs[j].modifyTuple(tuple, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
	}


	/**
	 *  Check if a match is contained.
	 */
	protected boolean isMatchContained(IOAVState state, Tuple left, Object right, ReteMemory mem)
	{
		return mem.hasNodeMemory(this)
		 && ((BetaMemory)mem.getNodeMemory(this)).getResultMemory()
		 	.contains(mem.getTuple(state, left, right));
	}

	/**
	 *  Check the consistency of the node.
	 */
	public boolean	checkNodeConsistency(ReteMemory mem)
	{
		boolean	consistent	= true;
		Collection	tuples	= getNodeMemory(mem);
		Collection	objects	= getObjectSource().getNodeMemory(mem);
		if(tuples!=null)
		{
			for(Iterator it=tuples.iterator(); consistent && it.hasNext(); )
			{
				Tuple	tuple	= (Tuple)it.next();
				consistent	= objects.contains(tuple.getObject());
			}
		}
		
		Object	node	= this;
		while(node instanceof IObjectConsumerNode)
			node	= ((IObjectConsumerNode)node).getObjectSource();
		((ReteNode)node).checked.add(this);

		return consistent;
	}
}
