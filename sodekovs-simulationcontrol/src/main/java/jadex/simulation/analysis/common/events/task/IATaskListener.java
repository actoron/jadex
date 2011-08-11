package jadex.simulation.analysis.common.events.task;

public interface IATaskListener
{
	 /**
     * Invoked when an {@link ATaskEvent} occurs
     */
    public void taskEventOccur(ATaskEvent event);
}
