package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bridge.ClassInfo;
import jadex.commons.SUtil;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *  Property panel for exception boundary events.
 *
 */
public class ErrorEventPropertyPanel extends BasePropertyPanel
{
	/** The event. */
	protected VActivity vevent;
	
	/**
	 *  Creates the panel.
	 */
	public ErrorEventPropertyPanel(ModelContainer container, VActivity exceptionevent)
	{
		super("Exception", container);
		
		vevent = exceptionevent;
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("Exception Class");
		
		AutoCompleteCombo cbox = new AutoCompleteCombo(null, container.getProjectClassLoader());
		final FixedClassInfoComboModel model = new FixedClassInfoComboModel(cbox, -1, new ArrayList<ClassInfo>(modelcontainer.getExceptions()));
		cbox.setModel(model);
		configureAndAddInputLine(column, label, cbox, y++, SUtil.createHashMap(new String[] { "second_fill" }, new Object[] { GridBagConstraints.HORIZONTAL } ));
		
		if(getMEvent().getClazz() != null)
		{
			cbox.setSelectedItem(getMEvent().getClazz());
		}
		
		cbox.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
//				String taskname = (String) ((JComboBox)e.getSource()).getSelectedItem();
				ClassInfo taskcl = (ClassInfo)((JComboBox)e.getSource()).getSelectedItem();
				if(taskcl==null)
					return;
				
				// change task class
				getMEvent().setClazz(taskcl);
			}
		});
		
		addVerticalFiller(column, y);
	}
	
	/**
	 *  Gets the semantic exception event.
	 * 
	 *  @return The event.
	 */
	protected MActivity getMEvent()
	{
		return (MActivity) vevent.getBpmnElement();
	}
}
