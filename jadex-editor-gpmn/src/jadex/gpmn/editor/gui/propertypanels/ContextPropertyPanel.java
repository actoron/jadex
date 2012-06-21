package jadex.gpmn.editor.gui.propertypanels;

import jadex.gpmn.editor.gui.IModelContainer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ContextPropertyPanel extends BasePropertyPanel
{
	/**
	 *  Creates a new property panel.
	 *  @param graph The graph.
	 */
	public ContextPropertyPanel(IModelContainer container)
	{
		super(container);
		setLayout(new GridBagLayout());
		
		final ContextTableModel model = new ContextTableModel(container.getGpmnModel().getContext());
		final JTable table = new JTable(model);
		JScrollPane tablepane = new JScrollPane(table);
		
		JPanel buttonpanel = new JPanel(new GridLayout(2, 1));
		JButton addbutton = new JButton(new AbstractAction("Add Parameter")
		{
			public void actionPerformed(ActionEvent e)
			{
				model.addParameter();
			}
		});
		buttonpanel.add(addbutton);
		JButton removebutton = new JButton(new AbstractAction("Remove Parameters")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] rows = table.getSelectedRows();
				if (rows.length == 1)
				{
					model.removeParameter(rows[0]);
				}
				else
				{
					model.removeParameters(rows);
				}
			}
		});
		buttonpanel.add(removebutton);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		add(tablepane, gc);
		
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		add(buttonpanel, gc);
	}
}
