package jadex.bdi.runtime.impl;

import jadex.bdi.runtime.IExternalCondition;
import jadex.bdi.runtime.Plan;
import jadex.commons.Future;
import jadex.commons.ISuspendable;
import jadex.commons.concurrent.IResultListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *  A plan future is a future that can be called from a
 *  plan and blocks this plan using waitForExternalCondition.
 * 
 *  Future that includes mechanisms for callback notification.
 *  This allows a caller to decide if 
 *  a) a blocking call to get() should be used
 *  b) a callback shall be invoked
 */
public class PlanSuspendable implements ISuspendable, IExternalCondition
{	
	//-------- attributes --------

	/** The plan. */
	protected Plan plan;
	
	/** Property change listener handling support. */
    private PropertyChangeSupport pcs	= new PropertyChangeSupport(this);

	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public PlanSuspendable(Plan plan)
	{
		this.plan = plan;
	}

	/**
	 * 
	 */
	public void suspend(long timeout)
	{
		if(!plan.getInterpreter().isPlanThread())
			throw new RuntimeException("SyncResultListener may only be used from plan thread.");
		
		plan.waitForExternalCondition(this, timeout);
	}
	
	/**
	 * 
	 */
	public void resume()
	{
	   	pcs.firePropertyChange("true", Boolean.FALSE, Boolean.TRUE);
	}
    
    //-------- IExternalCondition --------
	
	/**
	 *  Test if the condition holds.
	 */
	public boolean	isTrue()
	{
		return true;
	}

	/**
	 *  Add a property change listener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
        pcs.addPropertyChangeListener(listener);
    }

	/**
	 *  Remove a property change listener.
	 */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        pcs.removePropertyChangeListener(listener);
    }
}
