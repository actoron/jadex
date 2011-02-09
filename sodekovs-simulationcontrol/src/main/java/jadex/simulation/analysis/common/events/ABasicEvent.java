package jadex.simulation.analysis.common.events;

import jadex.simulation.analysis.common.util.AConstants;

import java.util.EventObject;

public class ABasicEvent extends EventObject implements IAEvent
{
	private Object mutex = new Object();
    private String eventCommand;
       
    public ABasicEvent(Object source, String eventCommand)
    {
    	super(source);
    	 this.eventCommand = eventCommand;
    }

    /* (non-Javadoc)
	 * @see jadex.simulation.analysis.common.events.IAEvent#setCommand(java.lang.String)
	 */
    public void setCommand(String eventCommand)
    {
        this.eventCommand = eventCommand;
    }
    
    /* (non-Javadoc)
	 * @see jadex.simulation.analysis.common.events.IAEvent#getCommand()
	 */
    public String getCommand()
    {
        return eventCommand;
    }   
    
    /* (non-Javadoc)
	 * @see jadex.simulation.analysis.common.events.IAEvent#getEventType()
	 */
    public String getEventType()
    {
    	return AConstants.EVENT;
    }
    
    /* (non-Javadoc)
	 * @see jadex.simulation.analysis.common.events.IAEvent#getMutex()
	 */
    public Object getMutex()
    {
    	return mutex;
    }
}