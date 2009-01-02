package jadex.adapter.jade;

import jade.core.Agent;
import jade.wrapper.PlatformController;

/**
 * 
 */
public class PlatformAgent extends Agent
{
	
    public void setup() 
    { 
    	PlatformController container = getContainerController(); 
        Platform.getPlatform().setPlatformController(container);
    }
}
