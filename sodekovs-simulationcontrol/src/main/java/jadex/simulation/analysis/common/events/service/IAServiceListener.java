package jadex.simulation.analysis.common.events.service;


public interface IAServiceListener
{
	 /**
     * Invoked when an {@link AServiceEvent} occurs
     */
    public void serviceEventOccur(AServiceEvent event);
    }
