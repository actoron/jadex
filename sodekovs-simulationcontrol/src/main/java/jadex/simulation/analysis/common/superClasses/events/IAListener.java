package jadex.simulation.analysis.common.superClasses.events;


public interface IAListener
{
	 /**
     * Invoked when an {@link IAEvent} occurs
     */
    public void update(IAEvent event);
}
