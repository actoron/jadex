package jadex.bdiv3.runtime;

import java.util.List;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;

/**
 *  Base class for non-bytecode-enhanced BDI agents.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class BDIAgent // extends IInternalAccess (Proxy)
{
	/** The bdi agent. */
	public IInternalAccess __agent; // IBDIClassGenerator.AGENT_FIELD_NAME
	
	/** The init arguments. */
	public List<Object> __initargs; // IBDIClassGenerator.INITARGS_FIELD_NAME
	
	/** The global name. */
	public String __globalname; // IBDIClassGenerator.GLOBALNAME_FIELD_NAME
	
	/**
	 *  Invoke to indicate a belief change.
	 *  @param name The belief name.
	 *  @param oldval The oldvalue.
	 *  @param newval The newvalue.
	 * /
	public void beliefChanged(String name, Object oldval, Object newval)
	{
		BDIAgentFeature.createChangeEvent(newval, oldval, null, __agent, name);
	}*/
	
	/**
	 *  Set a belief value and throw the change events.
	 *  @param beliefname The belief name.
	 *  @param value The value.
	 */
	public void setBeliefValue(String beliefname, Object value)
	{
		BDIAgentFeature.writeField(value, beliefname, this, __agent);
	}
}
