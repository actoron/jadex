package jadex.simulation.analysis.common.events;

import java.util.EventListener;

public interface ADataListener extends EventListener{

	
	 /**
     * Invoked when an {@link ADataEvent} occurs
     */
    public void dataEventOccur(ADataEvent event);
}
