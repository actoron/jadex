package jadex.simulation.analysis.common.events;

public interface IATaskListener
{
	 /**
     * Invoked when an {@link ATaskEvent} occurs
     */
    public void taskEventOccur(ATaskEvent event);
}
