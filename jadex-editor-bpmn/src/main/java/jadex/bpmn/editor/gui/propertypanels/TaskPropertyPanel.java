package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *  Property panel for task activities.
 *
 */
public class TaskPropertyPanel extends BasePropertyPanel
{
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
		Set<String> tasknameset = new HashSet<String>(BpmnEditor.TASK_INFOS.keySet());
		
		if (modelcontainer.getProjectTaskMetaInfos() != null && modelcontainer.getProjectTaskMetaInfos().size() > 0)
		{
			tasknameset.addAll(modelcontainer.getProjectTaskMetaInfos().keySet());
		}
		
		String[] tasknames = tasknameset.toArray(new String[BpmnEditor.TASK_INFOS.size()]);
		Arrays.sort(tasknames);
		final JComboBox cbox = new JComboBox(tasknames);
		
		cbox.setEditable(true);
		if (getBpmnTask().getClazz() != null)
		{
			cbox.setSelectedItem(getBpmnTask().getClazz().getTypeName());
		}
		
		configureAndAddInputLine(column, label, cbox, y++, SUtil.createHashMap(new String[] { "second_fill" }, new Object[] { GridBagConstraints.HORIZONTAL }));
		
		final JEditorPane descarea = new JEditorPane("text/html", "");
		final JScrollPane descpane = new JScrollPane(descarea);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = y;
		gc.gridwidth = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.fill = GridBagConstraints.BOTH;
		column.add(descpane, gc);
		
		final ActivityParameterTable atable = new ActivityParameterTable(container, task);
		
		processTaskInfos((String) cbox.getSelectedItem(), descarea);
		
		final JButton defaultParameterButton = new JButton();
		defaultParameterButton.setEnabled(getTaskMetaInfo((String) cbox.getSelectedItem()) != null);
		
		cbox.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				String taskname = (String) ((JComboBox)e.getSource()).getSelectedItem();
				getBpmnTask().setClazz(new ClassInfo(taskname));
				
				processTaskInfos(taskname, descarea);
				
				defaultParameterButton.setEnabled(getTaskMetaInfo(taskname) != null);
			}
		});
		
		//addVerticalFiller(column, y);
		
		
		JPanel parameterpanel = new JPanel(new GridBagLayout());
		
		gc = new GridBagConstraints();
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
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		
		Action setDefaultParametersAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				processTaskParameters((String) cbox.getSelectedItem(), atable);
			}
		};
		
		Icon[] icons = ImageProvider.getInstance().generateGenericFlatImageIconSet(buttonpanel.getIconSize(), ImageProvider.EMPTY_FRAME_TYPE, "page", buttonpanel.getIconColor());
		defaultParameterButton.setAction(setDefaultParametersAction);
		defaultParameterButton.setIcon(icons[0]);
		defaultParameterButton.setPressedIcon(icons[1]);
		defaultParameterButton.setRolloverIcon(icons[2]);
		defaultParameterButton.setContentAreaFilled(false);
		defaultParameterButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		defaultParameterButton.setMargin(new Insets(0, 0, 0, 0));
		defaultParameterButton.setToolTipText("Enter default parameters appropriate for the selected task.");
		((GridLayout) buttonpanel.getLayout()).setRows(3);
		buttonpanel.add(defaultParameterButton);
		
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
	
	/**
	 *  Processes the task infos.
	 *  
	 *  @param taskname The task name.
	 *  @param descarea The description area.
	 *  @param atable The parameter table.
	 */
	protected void processTaskInfos(String taskname, JEditorPane descarea)
	{
		TaskMetaInfo info = getTaskMetaInfo(taskname);
		
		descarea.setEditable(true);
		if (info != null)
		{
			String shortname = taskname;
			if (shortname.contains("."))
			{
				shortname = shortname.substring(shortname.lastIndexOf(".") + 1);
			}
			StringBuilder text = new StringBuilder();
			text.append("<html>");
			text.append("<head>");
			text.append("</head>");
			text.append("<body>");
			text.append("<h2>");
			text.append(shortname);
			text.append("</h2>");
			text.append("<p>");
			text.append(info.getDescription());
			text.append("</p>\n");
			
			ParameterMetaInfo[] pmis = info.getParameterMetaInfos();
			if (pmis != null && pmis.length > 0)
			{
				text.append("<ul>\n");
				for (int i = 0; i < pmis.length; ++i)
				{
					text.append("<li><b>");
					text.append(pmis[i].getName());
					text.append("</b> - ");
					text.append(pmis[i].getDescription());
					text.append("</li>\n");
				}
				text.append("</ul>\n");
			}
			
			text.append("</body>");
			text.append("</html>");
			
			descarea.setText(text.toString());
			descarea.setCaretPosition(0);
		}
		else
		{
			descarea.setText("<html><head></head><body></body></html>");
		}
		descarea.setEditable(false);
	}
	
	/**
	 *  Processes the task parameters to match the selected task class.
	 *  
	 *  @param taskname The selected task.
	 *  @param atable The activity parameter table.
	 */
	protected void processTaskParameters(String taskname, ActivityParameterTable atable)
	{
		TaskMetaInfo info = getTaskMetaInfo(taskname);
		
		if (info != null)
		{
			ParameterMetaInfo[] pmis = info.getParameterMetaInfos();
			if (pmis != null && pmis.length > 0)
			{
				Set<String> validparams = new HashSet<String>();
				MActivity mact = (MActivity) task.getBpmnElement();
				for (int i = 0; i < pmis.length; ++i)
				{
					validparams.add(pmis[i].getName());
					if (mact.hasParameter(pmis[i].getName()))
					{
						int ind = -1;
						for (int j = 0; j < mact.getParameters().size(); ++j)
						{
							if (pmis[i].getName().equals(mact.getParameters().getKey(j)))
							{
								ind = j;
								break;
							}
						}
						MParameter param = (MParameter) mact.getParameters().get(ind);
						atable.removeParameters(new int[] { ind });
						param.setClazz(new ClassInfo(pmis[i].getClazz().getTypeName()));
						param.setDirection(pmis[i].getDirection());
						atable.addParameter(param);
					}
					else
					{
						String name = pmis[i].getName();
						ClassInfo clazz = new ClassInfo(pmis[i].getClazz().getTypeName());
						String value = pmis[i].getInitialValue() != null? pmis[i].getInitialValue() : "";
						UnparsedExpression inival = new UnparsedExpression(name, clazz.getTypeName(), value, null);
						MParameter param = new MParameter(pmis[i].getDirection(),
														  clazz,
														  name,
														  inival);
						atable.addParameter(param);
					}
				}
				
				List<Integer> lind = new ArrayList<Integer>();
				for (int i = 0; i < mact.getParameters().size(); ++i)
				{
					Object pname = mact.getParameters().getKey(i);
					if (!validparams.contains(pname))
					{
						lind.add(i);
					}
				}
				
				int[] ind = new int[lind.size()];
				for (int i = 0; i < ind.length; ++i)
				{
					ind[i] = lind.get(i).intValue();
				}
				atable.removeParameters(ind);
			}
		}
	}
	
	/**
	 *  Gets the task meta information.
	 *  
	 *  @param taskname Name of the task.
	 *  @return The info, null if not found.
	 */
	protected TaskMetaInfo getTaskMetaInfo(String taskname)
	{
		TaskMetaInfo ret = null;
		if (modelcontainer.getProjectTaskMetaInfos() != null && modelcontainer.getProjectTaskMetaInfos().size() > 0)
		{
			ret = modelcontainer.getProjectTaskMetaInfos().get(taskname);
		}
		if (ret == null)
		{
			ret = BpmnEditor.TASK_INFOS.get(taskname);
		}
		return ret;
	}
}
