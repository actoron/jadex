package jadex.simulation.analysis.common.superClasses.events;

/**
 * A Class with this Interface can observe
 * @author 5Haubeck
 *
 */
public interface IAListener
{
	 /**
     * Invoked when an {@link IAEvent} occurs
     */
    public void update(IAEvent event);
}
