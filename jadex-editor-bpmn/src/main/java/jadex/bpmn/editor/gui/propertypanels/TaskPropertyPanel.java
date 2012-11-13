package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bridge.ClassInfo;
import jadex.commons.SUtil;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *  Property panel for task activities.
 *
 */
public class TaskPropertyPanel extends BasePropertyPanel
{
	/** Standard task classes. */
	protected static final String[] STANDARD_CLASSES = { "",
														 "jadex.bpmn.runtime.task.PrintTask",
														 "jadex.bpmn.runtime.task.InvokeMethodTask",
														 "jadex.bpmn.runtime.task.CreateComponentTask",
														 "jadex.bpmn.runtime.task.DestroyComponentTask",
														 "jadex.bpmn.runtime.task.StoreResultsTask",
														 "jadex.bpmn.runtime.task.UserInteractionTask",
														 
														 "jadex.bdibpmn.task.DispatchGoalTask",
														 "jadex.bdibpmn.task.WaitForGoalTask",
														 "jadex.bdibpmn.task.DispatchInternalEventTask",
														 "jadex.bdibpmn.task.WriteBeliefTask",
														 "jadex.bdibpmn.task.WriteParameterTask",
														 
														 "jadex.bdibpmn.task.CreateSpaceObjectTaskTask",
														 "jadex.bdibpmn.task.WaitForSpaceObjectTaskTask",
														 "jadex.bdibpmn.task.RemoveSpaceObjectTaskTask",

														 "jadex.wfms.client.task.WorkitemTask"
													   };
	
	/** The task. */
	protected VActivity task;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public TaskPropertyPanel(ModelContainer container, VActivity task)
	{
		super(null, container);
		setLayout(new BorderLayout());
		
		this.task = task;
		
		JTabbedPane tabpane = new JTabbedPane();
		
		int y = 0;
		JPanel column = new JPanel(new GridBagLayout());
		tabpane.addTab("Task", column);
		
		JLabel label = new JLabel("Class");
		JComboBox cbox = new JComboBox(STANDARD_CLASSES);
		
		cbox.setEditable(true);
		if (getBpmnTask().getClazz() != null)
		{
			cbox.setSelectedItem(getBpmnTask().getClazz().getTypeName());
		}
		
		cbox.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getBpmnTask().setClazz(new ClassInfo((String) ((JComboBox)e.getSource()).getSelectedItem()));
			}
		});
		configureAndAddInputLine(column, label, cbox, y++, SUtil.createHashMap(new String[] { "second_fill" }, new Object[] { GridBagConstraints.HORIZONTAL }));
		
		addVerticalFiller(column, y);
		
		final ActivityParameterTable atable = new ActivityParameterTable(container, task);
		JPanel parameterpanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JScrollPane tablescrollpane = new JScrollPane(atable);
		parameterpanel.add(tablescrollpane, gc);
		
		Action addaction = new AbstractAction("Add Parameter")
		{
			public void actionPerformed(ActionEvent e)
			{
				atable.addParameter();
			}
		};
		Action removeaction = new AbstractAction("Remove Parameters")
		{
			public void actionPerformed(ActionEvent e)
			{
				atable.removeParameters(atable.getSelectedRows());
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(modelcontainer.getImageProvider(), addaction, removeaction);
		
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		parameterpanel.add(buttonpanel, gc);
		
		tabpane.addTab("Parameters", parameterpanel);
		
		add(tabpane, BorderLayout.CENTER);
	}
	
	/**
	 *  Gets the BPMN task.
	 *  
	 *  @return The BPMN task.
	 */
	protected MActivity getBpmnTask()
	{
		return (MActivity) task.getBpmnElement();
	}
}
