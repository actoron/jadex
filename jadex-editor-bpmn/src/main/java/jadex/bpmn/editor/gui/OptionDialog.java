package jadex.bpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/** An option dialog. */
public class OptionDialog extends JDialog
{
	public static final String OPTIONS_CHANGED_PROPERTY = OptionDialog.class.getCanonicalName() + " property changed";
	
	/**
	 *  Creates the dialog
	 * @param optioncomponent The component.
	 */
	public OptionDialog(Frame owner, String title, boolean modal, Component optioncomponent, final Action applyaction)
	{
		super(owner, title, modal);
		setLayout(new BorderLayout());
		add(optioncomponent, BorderLayout.CENTER);
		
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.fill = GridBagConstraints.HORIZONTAL;
		buttonpanel.add(new JPanel(), g);
		
		JButton okbutton = new JButton(new AbstractAction("OK")
		{
			public void actionPerformed(ActionEvent e)
			{
				OptionDialog.this.setVisible(false);
				applyaction.actionPerformed(e);
			}
		});
		g = new GridBagConstraints();
		g.gridx = 1;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		buttonpanel.add(okbutton, g);
		
		JButton cancelbutton = new JButton(new AbstractAction("Cancel")
		{
			public void actionPerformed(ActionEvent e)
			{
				OptionDialog.this.setVisible(false);
			}
		});
		g = new GridBagConstraints();
		g.gridx = 2;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		buttonpanel.add(cancelbutton, g);
		
		final JButton applybutton = new JButton(new AbstractAction("Apply")
		{
			public void actionPerformed(ActionEvent e)
			{
				applyaction.actionPerformed(e);
				((JButton) e.getSource()).setEnabled(false);
			}
		});
		applybutton.setEnabled(false);
		g = new GridBagConstraints();
		g.gridx = 3;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		buttonpanel.add(applybutton, g);
		
		optioncomponent.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (OPTIONS_CHANGED_PROPERTY.equals(evt.getPropertyName()))
				{
					applybutton.setEnabled(true);
				}
			}
		});
		
		add(buttonpanel, BorderLayout.PAGE_END);
		
		doLayout();
		pack();
	}
	
}
