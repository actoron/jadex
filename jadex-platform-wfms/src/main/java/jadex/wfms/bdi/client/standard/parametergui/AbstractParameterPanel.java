package jadex.wfms.bdi.client.standard.parametergui;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

public abstract class AbstractParameterPanel extends JPanel
{
	private String parameterName;
	
	private boolean readOnly;
	
	public AbstractParameterPanel(String parameterName, boolean readOnly)
	{
		super(new GridBagLayout());
		this.parameterName = parameterName;
		this.readOnly = readOnly;
	}
	
	/**
	 * Returns whether the current value is valid for the given parameter.
	 * Calling this operation may cause the panel to mark errors for the user.
	 * @returns true if the value is valid, false otherwise
	 */
	public abstract boolean isParameterValueValid();
	
	/**
	 * Returns the parameter name.
	 * @return the parameter name
	 */
	public String getParameterName()
	{
		return parameterName;
	}
	
	/**
	 * Tests if the parameter is read-only
	 * @return true if the parameter is read-only
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}
	
	/**
	 * Returns the current parameter value.
	 * @return the parameter value
	 */
	public abstract Object getParameterValue();
}
