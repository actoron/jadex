package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MProperty;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.ClassInfoComboBoxRenderer;
import jadex.commons.gui.autocombo.ComboBoxEditor;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;

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
	public ErrorEventPropertyPanel(ModelContainer container, Object selection)
	{
		super("Exception", container);
		
		VActivity exceptionevent = (VActivity) selection;
		vevent = exceptionevent;
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		if (exceptionevent.getMActivity().isThrowing())
		{
			JLabel label = new JLabel("Exception");
			JTextArea textarea = new JTextArea();
			textarea.setWrapStyleWord(true);
			textarea.setLineWrap(true);
			textarea.setText(vevent.getMActivity().getPropertyValueString(MBpmnModel.PROPERTY_EVENT_ERROR) != null? vevent.getMActivity().getPropertyValueString(MBpmnModel.PROPERTY_EVENT_ERROR) : "");
			textarea.getDocument().addDocumentListener(new DocumentAdapter()
			{
				public void update(DocumentEvent e)
				{
					String expval = getText(e.getDocument());
					if (expval != null)
					{
						UnparsedExpression exp = new UnparsedExpression(MBpmnModel.PROPERTY_EVENT_ERROR, (String) null, expval, null); 
						MProperty prop = new MProperty(null, MBpmnModel.PROPERTY_EVENT_ERROR, exp);
						vevent.getMActivity().addProperty(prop);
					}
					else
					{
						vevent.getMActivity().removeProperty(MBpmnModel.PROPERTY_EVENT_ERROR);
					}
					modelcontainer.setDirty(true);
				}
			});
			JScrollPane sp =  new JScrollPane(textarea);
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			configureAndAddInputLine(column, label, sp, y++);
			textarea.setRows(3);
			sp.setMinimumSize(textarea.getPreferredSize());
			
			configureAndAddInputLine(column, label, sp, y++, SUtil.createHashMap(new String[] { "second_fill" }, new Object[] { GridBagConstraints.HORIZONTAL } ));
		}
		else
		{
			JLabel label = new JLabel("Exception Class");
			
			AutoCompleteCombo cbox = new AutoCompleteCombo(null, container.getProjectClassLoader());
			final FixedClassInfoComboModel model = new FixedClassInfoComboModel(cbox, -1, new ArrayList<ClassInfo>(modelcontainer.getExceptions()));
			cbox.setModel(model);
			configureAndAddInputLine(column, label, cbox, y++, SUtil.createHashMap(new String[] { "second_fill" }, new Object[] { GridBagConstraints.HORIZONTAL } ));
			cbox.setEditor(new ComboBoxEditor(model));
			cbox.setRenderer(new ClassInfoComboBoxRenderer());
			
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
		}
		
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
