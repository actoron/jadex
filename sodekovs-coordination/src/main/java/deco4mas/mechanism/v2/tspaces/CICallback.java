package deco4mas.mechanism.v2.tspaces;

import jadex.extension.envsupport.environment.IEnvironmentSpace;

import com.ibm.tspaces.Callback;
import com.ibm.tspaces.SuperTuple;
import com.ibm.tspaces.TupleSpaceException;

import deco4mas.coordinate.environment.CoordinationSpace;


/**
 * This class provides an event-listener to the TSpaces-bases tuple spaces. 
 * 
 * @author Ante Vilenica & Jan Sudeikat 
 *
 */
public class CICallback implements Callback {

	//-------- attributes ----------

//	/** The access to the surrounding agent, used to notify the agent about perceptions. */
//	private IExternalAccess agent_access;
	
	/** The access to the surrounding environment space, used to notify the space about perceptions. */
	private IEnvironmentSpace env_space;
	
	//-------- constructors --------
	
//	/**
//	 * @param externalAccess
//	 */
	public CICallback(IEnvironmentSpace env_space) {
		super();
		this.env_space = env_space;
	}

	//-------- methods -------------
	
	/** Callback interface.
	 * 
	 * @see com.ibm.tspaces.Callback#call(java.lang.String, java.lang.String, int, com.ibm.tspaces.SuperTuple, boolean)
	 */
	@Override
	public boolean call(String eventName,String tsName,int seqNum,SuperTuple tuple,boolean isException) {
		
//		if (!isException)   		// ignore exceptions
//			System.out.println("#CICallback# : Received call....");
			TupleContent coordInfo = null;
			try {				
				coordInfo = (TupleContent) tuple.getField(1).getValue();
			} catch (TupleSpaceException e) {
				System.out.println("#CICallback# error on call method.");
				e.printStackTrace();
			}
//			System.out.println("Result of EventHandler: " + coordInfo.getName());
//			System.out.println("#CICallback# Firing event percept....");
			((CoordinationSpace) env_space).publishCoordinationEvent(coordInfo);
			
//			coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, AgentElementType.BDI_GOAL.toString());
			
			
//			IInternalEvent ie = agent_access.createInternalEvent("trigger_tuple_lookup");
//			agent_access.dispatchInternalEvent(ie);
			
		
		return false;
	}

}
