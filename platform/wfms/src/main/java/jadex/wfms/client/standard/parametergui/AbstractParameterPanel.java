package jadex.wfms.client.standard.parametergui;

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
	 * @return true if the value is valid, false otherwise
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
	 * Returns whether the panel requires a label.
	 * @return true if the panel requires a label, false otherwise
	 */
	public abstract boolean requiresLabel();
	
	/**
	 * Returns the current parameter value.
	 * @return the parameter value
	 */
	public abstract Object getParameterValue();
}
