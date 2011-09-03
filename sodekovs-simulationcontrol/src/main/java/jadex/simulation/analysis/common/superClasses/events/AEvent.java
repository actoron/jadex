package jadex.simulation.analysis.common.superClasses.events;

import jadex.simulation.analysis.common.util.AConstants;

import java.util.EventObject;

public class AEvent extends EventObject implements IAEvent
{
	private Object mutex = new Object();
    private String eventCommand;
    private String type;
       
    public AEvent(Object source, String eventCommand)
    {
    	super(source);
    	 this.eventCommand = eventCommand;
    	 type = AConstants.EVENT;
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
    	return type;
    }
    
    public Object getMutex()
    {
    	return mutex;
    }
}