package jadex.wfms.client.standard;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddProcessModelDialog extends JDialog
{
	private static final String DIALOG_TITLE = "Add Process Model";
	
	private static final String PROCESS_MODEL_LABEL = "Enter process model path";
	
	private static final String ADD_BUTTON_TEXT = "Add";
	
	private static final String CANCEL_BUTTON_TEXT = "Cancel";
	
	private JComboBox modelBox;
	
	private boolean canceled;
	
	public AddProcessModelDialog(Frame owner, Set selections)
	{
		super(owner, DIALOG_TITLE, true);
		canceled = true;
		
		setLayout(new GridBagLayout());
		
		List sortSel = new ArrayList(selections);
		Collections.sort(sortSel);
		addModelBox(sortSel);
		
		JPanel buttonFiller = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 4;
		gbc.weightx = 1;
		getContentPane().add(buttonFiller, gbc);
		
		JButton addButton = new JButton();
		addButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				canceled = false;
				AddProcessModelDialog.this.setVisible(false);
			}
		});
		addButton.setText(ADD_BUTTON_TEXT);
		addButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.SOUTH;
		getContentPane().add(addButton, gbc);
		
		JButton cancelButton = new JButton();
		cancelButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				AddProcessModelDialog.this.setVisible(false);
			}
		});
		cancelButton.setText(CANCEL_BUTTON_TEXT);
		cancelButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.SOUTH;
		getContentPane().add(cancelButton, gbc);
		
		pack();
		setSize(600, 120);
	}
	
	public String getProcessPath()
	{
		if (canceled)
			return null;
		return (String) modelBox.getSelectedItem();
	}
	
	private void addModelBox(List selections)
	{
		JLabel modelLabel = new JLabel(PROCESS_MODEL_LABEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(modelLabel, gbc);
		
		modelBox = new JComboBox(selections.toArray());
		modelBox.setEditable(true);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.weightx = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 30;
		add(modelBox, gbc);
	}
}
