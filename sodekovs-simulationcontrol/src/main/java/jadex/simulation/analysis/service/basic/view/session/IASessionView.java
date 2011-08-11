package jadex.simulation.analysis.service.basic.view.session;

import jadex.simulation.analysis.common.events.service.IAServiceListener;


public interface IASessionView extends IAServiceListener
{
	public SessionProperties getSessionProperties();

}
