package jadex.tools.common;

import jadex.tools.ontology.CurrentState;

import java.awt.Component;


/**
 *  Interface to allow management of tool panels.
 */
public interface IToolPanel
{
	/**
	 *  The globally unique tool id is used to route messages to
	 *  the corresponding tools. 
	 */
	public String getId();

	/**
	 *  Called, once the agent is ready to manage the tool.
	 */
	public void activate();

	/**
	 *  Called, when the state of the observed agent changes.
	 */
	public void update(CurrentState state);

	/**
	 *  Used to check if management of the tool is still required.
	 */
	public boolean isActive();

	/**
	 *  Called when the tool should be deactivated (e.g. when the agent dies).
	 */
	public void deactivate();

	/**
	 *  Get the component.
	 *  @return The component.
	 */
	public Component getComponent();

}
