package jadex.simulation.analysis.common.events.data;

import java.util.EventListener;

public interface IADataListener extends EventListener
{
	/**
	 * Invoked when an {@link ADataEvent} occurs
	 */
	public void dataEventOccur(ADataEvent event);
}
