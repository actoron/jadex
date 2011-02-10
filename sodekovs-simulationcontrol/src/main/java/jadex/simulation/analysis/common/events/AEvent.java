package jadex.simulation.analysis.common.events;

import jadex.simulation.analysis.common.util.AConstants;

import java.util.EventObject;

public class AEvent extends EventObject implements IAEvent
{
	private Object mutex = new Object();
    private String eventCommand;
       
    public AEvent(Object source, String eventCommand)
    {
    	super(source);
    	 this.eventCommand = eventCommand;
    }

    public void setCommand(String eventCommand)
    {
        this.eventCommand = eventCommand;
    }
    
    public String getCommand()
    {
        return eventCommand;
    }   
    
    public String getEventType()
    {
    	return AConstants.EVENT;
    }
    
    public Object getMutex()
    {
    	return mutex;
    }
}