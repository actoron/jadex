package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.ConstraintIndexer;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  A not node lets tuples (from left side) pass,
 *  when there is no match from the right side.
 */
public class NotNode extends AbstractBetaNode
{
	//-------- constructors --------
	
	/**
	 *  Create a new not node.
	 */
	public NotNode(int nodeid, IConstraintEvaluator[] evaluators, ConstraintIndexer[] indexers)
	{
		super(nodeid, evaluators, indexers);
	}
	
	//-------- tuple consumer interface (left) --------
	
	/**
	 *  Send a tuple to this node.
	 *  @param tuple The tuple.
	 */
	public void addTuple(Tuple left, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==1137)
//			System.out.println(this+".addTuple: "+left);
		
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);

		NotMemory	nomem	= (NotMemory)mem.getNodeMemory(this);
		
		// Use super implementation to update matches, but don't propagate
		nomem.setDelay(true);
		super.addTuple(left, state, mem, agenda);
		nomem.setDelay(false);
		
		// When no mapping exists, tuple can be propagated.
		if(nomem.getMappings(left).isEmpty())
		{
			nomem.addResultTuple(left);
			ITupleConsumerNode[] tcs = tconsumers;
			for(int j=0; tcs!=null && j<tcs.length; j++)
				tcs[j].addTuple(left, state, mem, agenda);
		}

//		checkConsistency(mem);
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple left, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==1137)
//			System.out.println(this+".removeTuple: "+left);

		//System.out.println("Remove tuple called: "+this+" "+left);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);

		// Remove tuple from indexed memories
		// todo: what if value of object has already changed!
		if(indexers!=null)
		{
			BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
			for(int i=0; i<indexers.length; i++)
			{
				indexers[i].removeTuple(left, bmem);
			}
		}

		if(mem.hasNodeMemory(this))
		{
			// When no mapping exists, tuple will be retracted.
			NotMemory	nomem	= (NotMemory)mem.getNodeMemory(this);
			if(nomem.removeResultTuple(left))
			{
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].removeTuple(left, state, mem, agenda);
			}
		
			nomem.removeMappings(left);
		}

//		checkConsistency(mem);
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Modify a tuple in this node.
	 *  @param left The tuple.
	 */
	public void modifyTuple(Tuple left, int tupleindex, OAVAttributeType type,
		Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(!getRelevantAttributes().contains(type))
			return;

//		if(getNodeId()==1137)
//			System.out.println(this+".modifyTuple: "+left);

		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);
		
		NotMemory	nomem	= (NotMemory)mem.getNodeMemory(this);

		// Use super implementation to update matches, but don't propagate
		nomem.setDelay(true);
		super.modifyTuple(left, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
		nomem.setDelay(false);

		boolean	oldprop	= mem.hasNodeMemory(this)
			&& ((NotMemory)mem.getNodeMemory(this)).getResultMemory().contains(left);
		if(isAffected(type))
		{
			// Left tuple is propagated, when no right element matches.
			boolean	newprop	= !mem.hasNodeMemory(this)
				|| ((NotMemory)mem.getNodeMemory(this)).getMappings(left).isEmpty();

			// When now no mapping exists, tuple is now propagated.
			if(newprop && !oldprop)
			{
				((NotMemory)mem.getNodeMemory(this)).addResultTuple(left);
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].addTuple(left, state, mem, agenda);
			}
	
			// When now a mapping exists, tuple is retracted.
			else if(!newprop && oldprop)
			{
				((NotMemory)mem.getNodeMemory(this)).removeResultTuple(left);
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].removeTuple(left, state, mem, agenda);
			}
	
			// When modified already propagated tuple, propagate modification.
			else if(newprop)
			{
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].modifyTuple(left, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
			}
		}
			
		// When not affected but previously propagated, propagate modify call.
		else if(oldprop)
		{
			ITupleConsumerNode[] tcs = tconsumers;
			for(int j=0; tcs!=null && j<tcs.length; j++)
				tcs[j].modifyTuple(left, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
		}

//		checkConsistency(mem);
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}

	
	/**
	 *  Propagate an indirect object change to this node.
	 *  @param object The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		NotMemory	nomem	= (NotMemory)mem.getNodeMemory(this);

		// Use super implementation to update matches, but don't propagate
		nomem.setDelay(true);
		super.modifyIndirectObject(object, type, oldvalue, newvalue, state, mem, agenda);
		nomem.setDelay(false);

		Collection	linput	= getTupleSource().getNodeMemory(mem);
		if(linput!=null)
		{
			// Todo: Use index for avoiding the need for checking all tuple/object pairs.
			for(Iterator it=linput.iterator(); it.hasNext(); )
			{
				Tuple	left	= (Tuple)it.next();
				boolean	oldprop	= mem.hasNodeMemory(this)
					&& ((NotMemory)mem.getNodeMemory(this)).getResultMemory().contains(left);
				if(isAffected(type))
				{
					// Left tuple is propagated, when no right element matches.
					boolean	newprop	= !mem.hasNodeMemory(this)
						|| ((NotMemory)mem.getNodeMemory(this)).getMappings(left).isEmpty();
		
					// When now no mapping exists, tuple is now propagated.
					if(newprop && !oldprop)
					{
						((NotMemory)mem.getNodeMemory(this)).addResultTuple(left);
						ITupleConsumerNode[] tcs = tconsumers;
						for(int j=0; tcs!=null && j<tcs.length; j++)
							tcs[j].addTuple(left, state, mem, agenda);
					}
			
					// When now a mapping exists, tuple is retracted.
					else if(!newprop && oldprop)
					{
						((NotMemory)mem.getNodeMemory(this)).removeResultTuple(left);
						ITupleConsumerNode[] tcs = tconsumers;
						for(int j=0; tcs!=null && j<tcs.length; j++)
							tcs[j].removeTuple(left, state, mem, agenda);
					}
				}
			}
		}
//		checkConsistency(mem);
	}

	//-------- template methods --------
	
	/**
	 *  Add a match to the node memory and propagate if necessary.
	 */
	protected void addMatch(Tuple left, Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==1137)
//			System.out.println(this+".addMatch: "+left+", "+right);

		NotMemory nomem = (NotMemory)mem.getNodeMemory(this);
		nomem.addMapping(state, left, right);
		if(!nomem.isDelay() && nomem.removeResultTuple(left))
		{
			ITupleConsumerNode[] tcs = tconsumers;
			for(int j=0; tcs!=null && j<tcs.length; j++)
				tcs[j].removeTuple(left, state, mem, agenda);
		}
	}

	/**
	 *  Remove a match from the node memory and propagate if necessary.
	 */
	protected void removeMatch(Tuple left, Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==1137)
//		if(mem.debug!=null)
//			mem.debug.add(this+".removeMatch?: "+left+", "+right);

		if(mem.hasNodeMemory(this))
		{
			NotMemory nomem = (NotMemory)mem.getNodeMemory(this);
			boolean	removed	= nomem.removeMapping(left, right);
//			if(mem.debug!=null)
//				mem.debug.add(this+".removeMatch: removed="+removed+", delay="+nomem.isDelay()+", mappings="+nomem.getMappings(left));
			if(removed && !nomem.isDelay() && (nomem.getMappings(left)==null || nomem.getMappings(left).isEmpty()))
			{
//				if(mem.debug!=null)
//					mem.debug.add(this+".removeMatch: add result tuple "+left);
				nomem.addResultTuple(left);
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].addTuple(left, state, mem, agenda);
			}
		}
	}

	/**
	 *  Propagate a change of a tuple in the result memory.
	 */
	protected void propagateModification(Tuple left, Object right, int tupleindex, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Nothing to do...
	}


	/**
	 *  Check if a match is contained.
	 */
	protected boolean isMatchContained(IOAVState state, Tuple left, Object right, ReteMemory mem)
	{
		boolean	ret	= false;
		if(mem.hasNodeMemory(this))
		{
			NotMemory	nomem	= (NotMemory)mem.getNodeMemory(this);
			Set	mappings	= nomem.getMappings(left);
			ret	= mappings!=null && mappings.contains(right);
		}
		return ret;
	}

	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return new NotMemory(state);
	}
}
