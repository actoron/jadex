package jadex.tools.debugger.bdi;

import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.commons.IRemoteChangeListener;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

/**
 *  The listener installed remotely in the BDI agent.
 */
public class BDIChangeListener	extends RemoteChangeListenerHandler	implements IOAVStateListener
{
	//-------- constructs --------
	
	/**
	 *  Create a BDI listener.
	 */
	public BDIChangeListener(String id, BDIInterpreter interpreter, IRemoteChangeListener rcl)
	{
		super(id, new CapabilityFlyweight(interpreter.getState(), interpreter.getAgent()), rcl);
	}
	
	//-------- IOAVStateListener interface --------
	
	/**
	 *  Called when an object is removed.
	 */
	public void objectRemoved(Object id, OAVObjectType type)
	{
	}
	
	/**
	 *  Called when an object is modified.
	 */
	public void objectModified(Object id, OAVObjectType type, OAVAttributeType attr, Object oldvalue, Object newvalue)
	{
		if(((ElementFlyweight)instance).getState().getType(id).isSubtype(OAVBDIRuntimeModel.capability_type)
			&& OAVBDIRuntimeModel.capability_has_goals.equals(attr))
		{
			// Goal added.
			if(oldvalue==null && newvalue!=null)
			{
				elementAdded(BDIViewerPanel.EVENT_GOAL, BDIViewerPanel.createGoalInfo(((ElementFlyweight)instance).getState(), newvalue));
			}
			
			// Goal removed
			else if(oldvalue!=null && newvalue==null)
			{
				elementRemoved(BDIViewerPanel.EVENT_GOAL, BDIViewerPanel.createGoalInfo(((ElementFlyweight)instance).getState(), oldvalue));
			}
		}
		
		// Goal changed.
		else if(((ElementFlyweight)instance).getState().getType(id).isSubtype(OAVBDIRuntimeModel.goal_type)
			&& (OAVBDIRuntimeModel.goal_has_lifecyclestate.equals(attr)
				|| OAVBDIRuntimeModel.goal_has_processingstate.equals(attr)))
		{
			elementChanged(BDIViewerPanel.EVENT_GOAL, BDIViewerPanel.createGoalInfo(((ElementFlyweight)instance).getState(), id));
		}
	}
	
	/**
	 *  Called when an object is added.
	 */
	public void objectAdded(Object id, OAVObjectType type, boolean root)
	{
	}
	
	//-------- RemoteChangeListenerHandler methods --------
	
	/**
	 *  Remove local listeners.
	 */
	protected void dispose()
	{
		super.dispose();
		
		((ElementFlyweight)instance).getState().removeStateListener(BDIChangeListener.this);
	}
}
