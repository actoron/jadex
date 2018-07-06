/**
 * 
 */
package org.activecomponents.udp.testgui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 *
 */
public class LabeledTextField extends JTextField
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The label. */
	protected String label;
	
	/**
	 * 
	 */
	public LabeledTextField(String label)
	{
		this.label = label;
		this.setText(label);
		
		this.addFocusListener(new FocusListener()
		{
			
			public void focusLost(FocusEvent e)
			{
				if (getText() == null || getText().length() == 0)
				{
					setText(LabeledTextField.this.label);
				}
			}
			
			public void focusGained(FocusEvent e)
			{
				if (LabeledTextField.this.label.equals(getText()))
				{
					setText("");
				}
			}
		});
	}
	
	
}
