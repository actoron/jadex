package jadex.rules.rulesystem.rete;

import java.util.Iterator;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.IAgenda;
import jadex.rules.rulesystem.IPatternMatcherState;
import jadex.rules.rulesystem.rete.nodes.ReteMemory;
import jadex.rules.rulesystem.rete.nodes.ReteNode;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

/**
 *  The state specific part of a Rete pattern matcher.
 */
public class RetePatternMatcherState implements IPatternMatcherState, IOAVStateListener
{
	//-------- attributes --------
	
	/** The rete node. */
	protected ReteNode node;
	
	/** The rete memory. */
	protected ReteMemory retemem;
	
	/** The state. */
	protected IOAVState state;
	
	/** The agenda. */
	protected AbstractAgenda agenda;
	
	//-------- constructors --------
	
	/**
	 *  Create a state specific part of a Rete pattern matcher.
	 */
	public RetePatternMatcherState(ReteNode node, IOAVState state, ReteMemory retemem, AbstractAgenda agenda)
	{
		this.node = node;
		this.retemem = retemem;
		this.state = state;
		this.agenda = agenda;
	}
	
	//-------- IPatternMatcherState interface --------
	
	/**
	 *  Initialize the pattern matcher.
	 *  Called before the agenda is accessed
	 *  to perform any initialization, if necessary.
	 */
	public void init()
	{
		// Initialize initial fact node, if any.
		if(node.getInitialFactNode()!=null)
			node.getInitialFactNode().init(state, retemem, agenda);
		
		// Add initial objects.
		for(Iterator objects=state.getDeepObjects(); objects.hasNext(); )
		{
			Object	object	= objects.next();
			objectAdded(object, state.getType(object), false);	// Hack!!! Should check if root?
		}

		state.addStateListener(this, true);
//		state.addStateListener(this, false);
	}

	/**
	 *  Get the agenda.
	 *  The agenda can only be accessed, after the rule system
	 *  has been initialized with {@link #init()}.
	 *  @return The agenda.
	 */
	public IAgenda getAgenda()
	{
		return this.agenda;
	}

	//-------- IOAVStateListener interface --------
	
	// flag for debugging threading issues
	private boolean	running;
	
	/**
	 *  Notification when an attribute value of an object has been set.
	 *  @param id The object id.
	 *  @param type The object type.
	 *  @param attr The attribute type.
	 *  @param oldvalue The oldvalue.
	 *  @param newvalue The newvalue.
	 */
	public void objectModified(Object id, OAVObjectType type, OAVAttributeType attr, 
		Object oldvalue, Object newvalue)
	{
		assert !running;
		running	= true;
		
		state.getProfiler().start(IProfiler.TYPE_OBJECT, type);
		state.getProfiler().start(IProfiler.TYPE_OBJECTEVENT, IProfiler.OBJECTEVENT_MODIFIED);

		node.modifyObject(id, type, attr, oldvalue, newvalue, state, retemem, agenda);

		state.getProfiler().stop(IProfiler.TYPE_OBJECTEVENT, IProfiler.OBJECTEVENT_MODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_OBJECT, type);
		
		running	= false;
	}
	
	/**
	 *  Notification when an object has been added to the state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectAdded(Object id, OAVObjectType type, boolean root)
	{
		assert !running;
		running	= true;
		
		state.getProfiler().start(IProfiler.TYPE_OBJECT, type);
		state.getProfiler().start(IProfiler.TYPE_OBJECTEVENT, IProfiler.OBJECTEVENT_ADDED);

		node.addObject(id, type, state, retemem, agenda);

		state.getProfiler().stop(IProfiler.TYPE_OBJECTEVENT, IProfiler.OBJECTEVENT_ADDED);
		state.getProfiler().stop(IProfiler.TYPE_OBJECT, type);
			
		running	= false;
	}
	
	/**
	 *  Notification when an object has been removed from state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectRemoved(Object id, OAVObjectType type)
	{
		assert !running;
		running	= true;
		
		state.getProfiler().start(IProfiler.TYPE_OBJECT, type);
		state.getProfiler().start(IProfiler.TYPE_OBJECTEVENT, IProfiler.OBJECTEVENT_REMOVED);
		
		node.removeObject(id, type, state, retemem, agenda);
		
		state.getProfiler().stop(IProfiler.TYPE_OBJECTEVENT, IProfiler.OBJECTEVENT_REMOVED);
		state.getProfiler().stop(IProfiler.TYPE_OBJECT, type);
		
		running	= false;
	}

	//-------- methods --------
	
	/**
	 *  Get the Rete memory.
	 */
	public ReteMemory	getReteMemory()
	{
		return retemem;
	}

	/**
	 *  Get the node.
	 *  @return The node.
	 */
	public ReteNode getReteNode()
	{
		return node;
	}
}
