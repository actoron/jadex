package jadex.wfms.bdi.client.basic;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class AbstractParameterPanel extends JPanel
{
	public AbstractParameterPanel()
	{
		super(new GridBagLayout());
	}
	
	/**
	 * Returns whether the current value is valid for the given parameter.
	 * Calling this operation may cause the panel to mark errors for the user.
	 * @returns true if the value is valid, false otherwise
	 */
	public abstract boolean isParameterValueValid();
	
	/**
	 * Returns the current parameter value.
	 * @return the parameter value
	 */
	public abstract Object getParameterValue();
}
