package jadex.bdiv3.runtime;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bridge.IInternalAccess;

/**
 *  Base class for non-bytecode-enhanced BDI agents.
 */
public class BDIGoal
{
	/** The bdi agent. */
	public IInternalAccess __agent; // IBDIClassGenerator.AGENT_FIELD_NAME
	
	/** The global name. */
	public String __globalname; // GLOBALNAME_FIELD_NAME
	
	/**
	 *  Set a belief value and throw the change events.
	 *  @param beliefname The belief name.
	 *  @param value The value.
	 */
	public void setParameterValue(String paramname, Object value)
	{
		BDIAgentFeature.writeParameterField(value, paramname, this, __agent);
	}
}
