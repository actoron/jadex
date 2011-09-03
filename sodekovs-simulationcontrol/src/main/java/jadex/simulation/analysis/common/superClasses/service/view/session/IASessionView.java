package jadex.simulation.analysis.common.superClasses.service.view.session;

import javax.swing.JComponent;

import jadex.simulation.analysis.common.superClasses.events.IAListener;


public interface IASessionView extends IAListener
{
	/**
	 * Returns a Session Properties for use in workflowview
	 * @return the session properties
	 */
	public SessionProperties getSessionProperties();
	
	/**
	 * Returns the component to display for service
	 * @return a component
	 */
	public JComponent getComponent();

}
