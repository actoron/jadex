package jadex.bpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS));
		buttonpanel.add(Box.createHorizontalGlue());
		
		JButton okbutton = new JButton(new AbstractAction("OK")
		{
			public void actionPerformed(ActionEvent e)
			{
				OptionDialog.this.setVisible(false);
				applyaction.actionPerformed(e);
			}
		});
		buttonpanel.add(okbutton);
		
		JButton cancelbutton = new JButton(new AbstractAction("Cancel")
		{
			public void actionPerformed(ActionEvent e)
			{
				OptionDialog.this.setVisible(false);
			}
		});
		buttonpanel.add(cancelbutton);
		
		final JButton applybutton = new JButton(new AbstractAction("Apply")
		{
			public void actionPerformed(ActionEvent e)
			{
				applyaction.actionPerformed(e);
				((JButton) e.getSource()).setEnabled(false);
			}
		});
		applybutton.setEnabled(false);
		buttonpanel.add(applybutton);
		
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
