package jadex.simulation.analysis.common.superClasses.events.data;

import jadex.simulation.analysis.common.superClasses.events.AEvent;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;
import jadex.simulation.analysis.common.util.AConstants;

/**
 * A event, which occur in a dataobject
 * @author 5Haubeck
 *
 */
public class ADataEvent extends AEvent
{
	protected Object newValue; 
	
	@Override
	public String getEventType()
	{
		return AConstants.DATA_EVENT;
	}
    
    public ADataEvent(IAObservable source, String eventCommand, Object newValue)
    {
    	super(source, eventCommand);
    	this.newValue = newValue;
    	
    }
    
    /**
     * the value, which was set in the dataobjecz
     * @return the object
     */
    public Object getValue()
	{
		return newValue;
	}
    
}