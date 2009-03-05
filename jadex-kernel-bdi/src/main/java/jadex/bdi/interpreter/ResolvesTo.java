package jadex.bdi.interpreter;

import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

import java.util.HashSet;
import java.util.Set;

/**
 *  Test if a reference string resolves to a given element.
 *  E.g. dfcap.register resolves to any register goal in a dfcap subcapability.
 *  Requires four parameters: local scope, reference string, target runtime element and target scope.
 *  Example: (ResolvesTo ?rcapa "dfcap.register" ?rgoal ?rtargetscope)
 *  Also supports elements in local scope. 
 */
public class ResolvesTo implements IFunction
{
	//-------- attributes --------
	
	/** The set with the attribute. */
	protected Set relevants;
	
	//-------- IFunction interface --------
	
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		if(paramvalues==null || paramvalues.length!=4)
			throw new IllegalArgumentException("Function needs four parameters: "+paramvalues);
			
		Object rstartcapa	= paramvalues[0];
		String ref	 = (String)paramvalues[1];
		Object relem	= paramvalues[2];
		Object rtargetcapa = paramvalues[3];
		
		// Ensure that startcapa -(ref)-> targetcapa && relem.getModelElement().getName()==<ref>.<name>
		Object melem	= state.getAttributeValue(relem, OAVBDIRuntimeModel.element_has_model);
		String name	= (String)state.getAttributeValue(melem, OAVBDIMetaModel.modelelement_has_name);
		OAVObjectType otype = state.getType(melem);
		Object[] scope	= AgentRules.resolveCapability(ref, otype, rstartcapa, state);
		return new Boolean(name.equals(scope[0]) && rtargetcapa.equals(scope[1]));
	}
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType()
	{
		return Boolean.class;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public Set	getRelevantAttributes()
	{
		if(relevants==null)
		{
			relevants = new HashSet();
			relevants.add(OAVBDIRuntimeModel.element_has_model);
			relevants.add(OAVBDIMetaModel.modelelement_has_name);
			relevants.add(OAVBDIRuntimeModel.capability_has_beliefs);
			relevants.add(OAVBDIRuntimeModel.capability_has_beliefsets);
			relevants.add(OAVBDIRuntimeModel.capability_has_goals);
			relevants.add(OAVBDIRuntimeModel.capability_has_internalevents);
			relevants.add(OAVBDIRuntimeModel.capability_has_messageevents);
			relevants.add(OAVBDIRuntimeModel.capability_has_subcapabilities);
			relevants.add(OAVBDIRuntimeModel.capabilityreference_has_capability);
			
		}
		return relevants;
	}
}
