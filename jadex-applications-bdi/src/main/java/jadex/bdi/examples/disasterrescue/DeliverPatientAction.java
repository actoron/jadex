package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Map;

/**
 *  Action for delivering a patient at the hospital.
 */
public class DeliverPatientAction extends SimplePropertyObject implements ISpaceAction
{
	//-------- constants --------
	
	/** The patient property. */
	public static final String	PROPERTY_PATIENT	= "patient";
	
	//-------- ISpaceAction interface --------
	
	/**
	 * Performs the action.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 * @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space)
	{
		IComponentIdentifier owner = (IComponentIdentifier)parameters.get(ISpaceAction.ACTOR_ID);
		ISpaceObject so = space.getAvatar(owner);
		
		assert ((Boolean)so.getProperty(PROPERTY_PATIENT)).booleanValue();
		
		so.setProperty(PROPERTY_PATIENT, Boolean.FALSE);
		
		return null;
	}
}
