package jadex.simulation.analysis.common.superClasses.service.view.session;

import jadex.simulation.analysis.common.superClasses.events.IAListener;


public interface IASessionView extends IAListener
{
	public SessionProperties getSessionProperties();

}
