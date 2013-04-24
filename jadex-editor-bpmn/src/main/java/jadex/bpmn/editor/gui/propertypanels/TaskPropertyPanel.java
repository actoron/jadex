package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.STaskMetaInfoExtractor;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
				modelcontainer.setDirty(true);
			}
		};
		Action removeaction = new AbstractAction("Remove Parameters")
		{
			public void actionPerformed(ActionEvent e)
			{
				atable.removeParameters(atable.getSelectedRows());
				modelcontainer.setDirty(true);
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		
		Action setDefaultParametersAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				processTaskParameters((String) cbox.getSelectedItem(), atable);
				modelcontainer.setDirty(true);
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
			
			if(info.getDescription()!=null && info.getDescription().length()>0)
			{
				text.append("<p>");
				text.append(info.getDescription());
				text.append("</p>\n");
			}
			
			List <ParameterMetaInfo> pmis = info.getParameterInfos();
			if(pmis!=null && !pmis.isEmpty())
			{
				text.append("Parameters:");
				text.append("<ul>\n");
				for (int i = 0; i < pmis.size(); ++i)
				{
					text.append("<li><b>");
					text.append(pmis.get(i).getName());
					text.append("</b> - ");
					text.append(pmis.get(i).getDescription()==null || pmis.get(i).getDescription().length()==0? "n/a": pmis.get(i).getDescription());
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
		
		if(info != null)
		{
			List<ParameterMetaInfo> pmis = info.getParameterInfos();
			if(pmis != null && pmis.size() > 0)
			{
				Set<String> validparams = new HashSet<String>();
				MActivity mact = (MActivity) task.getBpmnElement();
				for(int i = 0; i < pmis.size(); ++i)
				{
					validparams.add(pmis.get(i).getName());
					if(mact.hasParameter(pmis.get(i).getName()))
					{
						int ind = -1;
//						for(int j = 0; j < mact.getParameters().size(); ++j)
						Iterator<String> it=mact.getParameters().keySet().iterator();
						for(int j = 0; it.hasNext(); ++j)
						{
							String key = it.next();
							if(pmis.get(i).getName().equals(key))
							{
								ind = j;
								break;
							}
						}
						MParameter param = (MParameter) mact.getParameters().get(ind);
						atable.removeParameters(new int[]{ind});
						param.setClazz(new ClassInfo(pmis.get(i).getClazz().getTypeName()));
						param.setDirection(pmis.get(i).getDirection());
						atable.addParameter(param);
					}
					else
					{
						String name = pmis.get(i).getName();
						ClassInfo clazz = new ClassInfo(pmis.get(i).getClazz().getTypeName());
						String value = pmis.get(i).getInitialValue() != null? pmis.get(i).getInitialValue() : "";
						UnparsedExpression inival = new UnparsedExpression(name, clazz.getTypeName(), value, null);
						MParameter param = new MParameter(pmis.get(i).getDirection(), clazz, name, inival);
						atable.addParameter(param);
					}
				}
				
				List<Integer> lind = new ArrayList<Integer>();
				int i = 0;
				for(String pname: mact.getParameters().keySet())
				{
					if(!validparams.contains(pname))
					{
						lind.add(i);
					}
					i++;
				}
				
				int[] ind = new int[lind.size()];
				for(i = 0; i < ind.length; ++i)
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
		
		try
		{
			Class<?> clazz = SReflect.classForName(taskname, null);

			if(modelcontainer.getProjectTaskMetaInfos() != null && modelcontainer.getProjectTaskMetaInfos().size() > 0)
			{
				ret = modelcontainer.getProjectTaskMetaInfos().get(taskname);
			}
			if(ret == null)
			{
				ret = BpmnEditor.TASK_INFOS.get(taskname);
			}
			// try to extract
			if(ret==null)
			{
				try
				{
					ret = STaskMetaInfoExtractor.getMetaInfo(clazz);
					modelcontainer.getProjectTaskMetaInfos().put(taskname, ret);
				}
				catch(Exception e)
				{
				}
			}

			TaskMetaInfo copy = new TaskMetaInfo();
			Method m = clazz.getMethod("getExtraParameters", new Class[]{Map.class, IModelInfo.class, ClassLoader.class});
			// todo: use classloader of tool!
			List<ParameterMetaInfo> pis = new ArrayList<ParameterMetaInfo>();
			if(ret.getParameterInfos()!=null && !ret.getParameterInfos().isEmpty())
			{
				pis.addAll(ret.getParameterInfos());
			}
			if(getBpmnTask().getParameters()!=null)
			{
				Map<String, MParameter> ps = getBpmnTask().getParameters().getAsMap();
				List<ParameterMetaInfo> params = (List<ParameterMetaInfo>)m.invoke(null, new Object[]{ps, modelcontainer.getBpmnModel().getModelInfo(), modelcontainer.getBpmnModel().getClassLoader()});
				pis.addAll(params);
			}
			ret = new TaskMetaInfo(ret!=null? ret.getDescription(): null, pis);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			// ignore
		}
		
		
		return ret;
	}
}
