package jadex.bpmn.editor.gui.propertypanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.IModelContainer;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.model.task.ITaskPropertyGui;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.PropertyMetaInfo;
import jadex.bpmn.task.info.STaskMetaInfoExtractor;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;
import jadex.javaparser.SJavaParser;

/**
 *  Property panel for task activities.
 */
public class TaskPropertyPanel extends BasePropertyPanel
{
	/** Label text for file name. */
	protected final String FILE_NAME_TEXT = "File";
	
	/** Label text for file expression. */
	protected final String FILE_EXPRESSION_TEXT = "File Expression";
	
	/** The task. */
	protected VActivity task;
	
	/** Parameter table. */
	protected ActivityParameterTable atable;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public TaskPropertyPanel(final ModelContainer container, VActivity task, MParameter selectedparameter)
	{
		super(null, container);
		
		assert SwingUtilities.isEventDispatchThread();
		
		setLayout(new BorderLayout());
		
		final ClassLoader cl = container.getProjectClassLoader()!=null? container.getProjectClassLoader()
			: TaskPropertyPanel.class.getClassLoader();
		
		this.task = task;
		
		JTabbedPane tabpane = new JTabbedPane();
		
		int y = 0;
		JPanel column = new JPanel(new GridBagLayout());
		
		if(!isSubprocess())
		{
			tabpane.addTab("Task", column);
		}
		else if(task instanceof VExternalSubProcess)
		{
			tabpane.addTab("External Sub-Process", createExternalSubprocessTab());
		}
		
		JLabel label = new JLabel("Class");
//		Set<String> tasknameset = new HashSet<String>(BpmnEditor.TASK_INFOS.keySet());
//		if(modelcontainer.getProjectTaskMetaInfos() != null && modelcontainer.getProjectTaskMetaInfos().size() > 0)
//		{
//			tasknameset.addAll(modelcontainer.getProjectTaskMetaInfos().keySet());
//		}
//		String[] tasknames = tasknameset.toArray(new String[BpmnEditor.TASK_INFOS.size()]);
//		Arrays.sort(tasknames);
//		final AutoCompleteCombo<String> cbox = new AutoCompleteCombo<String>(null);
//		StringComboModel model = new StringComboModel(cbox, 20, SUtil.createArrayList(tasknames));
//		cbox.setModel(model);
		
//		System.out.println("taskcl: "+modelcontainer.getTaskClasses());
		
		final AutoCompleteCombo cbox = new AutoCompleteCombo(null, cl);
		final FixedClassInfoComboModel model = new FixedClassInfoComboModel(cbox, -1, new ArrayList<ClassInfo>(modelcontainer.getTaskClasses()));
//		System.out.println("cls: "+modelcontainer.getTaskClasses());
//		final ClassComboModel model = new ClassComboModel(cbox, 20, false, false, true, true, null,// null);
//			new IFilter<Class<?>>()
//		{
//			public boolean filter(Class<?> obj)
//			{
//				boolean ret = false;
//				try
//				{
//					ClassLoader cl = obj.getClassLoader();
//					Class<?> taskcl = Class.forName(ITask.class.getName(), true, cl);
//					return SReflect.isSupertype(taskcl, obj);
//				}
//				catch(Exception e)
//				{
//				}
//				return ret;
//			}
//		});
		cbox.setModel(model);
//		System.out.println(combo.getEditor().getClass());
//		MetalComboBoxEditor
		cbox.setEditor(new MetalComboBoxEditor()//BasicComboBoxEditor()
		{
			Object val;
			public void setItem(Object obj)
			{
				if(obj==null || SUtil.equals(val, obj))
					return;
				
				String text = obj instanceof ClassInfo? model.convertToString((ClassInfo)obj): "";
			    if(text!=null && !text.equals(editor.getText())) 
			    {
			    	val = obj;
			    	editor.setText(text);
			    }
			}
			
			public Object getItem()
			{
				return val;
			}
		});
		cbox.setRenderer(new BasicComboBoxRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
			{
				ClassInfo ci = (ClassInfo)value;
				Class<?> cl = ci.getType(modelcontainer.getProjectClassLoader());
				String txt = null;
				if(cl!=null)
				{
					txt = SReflect.getInnerClassName(cl)+" - "+cl.getPackage().getName();
				}
				else
				{
					String fn = ci.getTypeName();
					int idx = fn.lastIndexOf(".");
					if(idx!=-1)
					{
						String cn = fn.substring(idx+1);
						String pck = fn.substring(0, idx);
						txt = cn+" - "+pck;
					}
					else
					{
						txt = fn;
					}
				}
				return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
			}
		});
		
		if(getBpmnTask().getClazz()!=null)
		{
			cbox.setSelectedItem(getBpmnTask().getClazz());
			
			if(getBpmnTask().getProperties()==null || getBpmnTask().getProperties().size()==0)
			{
				TaskMetaInfo info = getTaskMetaInfo(getBpmnTask().getClazz().getTypeName());
				if(info!=null)
				{
					List<PropertyMetaInfo> pmis = info.getPropertyInfos();
					if(pmis!=null)
					{
						for(PropertyMetaInfo pmi: pmis)
						{
							UnparsedExpression uexp = new UnparsedExpression(null, 
								pmi.getClazz().getType(modelcontainer.getProjectClassLoader()), pmi.getInitialValue(), null);
							MProperty mprop = new MProperty(pmi.getClazz(), pmi.getName(), uexp);
							getBpmnTask().addProperty(mprop);
						}
					}
				}
			}
//			initTaskProperties(getBpmnTask().getClazz());
//			System.out.println("setting: "+getBpmnTask().getClazz());
		}
		
//		JButton sel = new JButton("...");
//		sel.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				Class<?> clazz = ClassSearchPanel.showDialog(modelcontainer.getProjectClassLoader(), null, TaskPropertyPanel.this);
//				if(clazz!=null)
//					cbox.setSelectedItem(clazz.getName());
////				cbox.addItem(clazz.getName());
//			}
//		});
		
//		column.add(label, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, 
//			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
//		column.add(cbox, new GridBagConstraints(1, y, 1, 1, 1, 1, GridBagConstraints.WEST, 
//			GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
//		column.add(sel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
//			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		
		configureAndAddInputLine(column, label, cbox, y++, SUtil.createHashMap(new String[]{"second_fill"}, new Object[]{GridBagConstraints.HORIZONTAL}));
		
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
		
		atable = new ActivityParameterTable(container, task);
		
		processTaskInfos((ClassInfo)cbox.getSelectedItem(), descarea);
		
		final JButton defaultParameterButton = new JButton();
		defaultParameterButton.setEnabled(getTaskMetaInfo((ClassInfo)cbox.getSelectedItem()) != null);
		
		// Property panel
		
		JPanel proppanel = new JPanel(new GridBagLayout());
		final JPanel propsp = new JPanel(new BorderLayout());
		JButton refresh = new JButton("Refresh");
		final ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("pressed");
				
				propsp.removeAll();
				
				String taskname = getBpmnTask().getClazz() != null? getBpmnTask().getClazz().getTypeName() : null;
				TaskMetaInfo info = getTaskMetaInfo(taskname);
				List<PropertyMetaInfo> props = info!=null? info.getPropertyInfos(): null;
				
				if(props!=null)
				{					
					ClassInfo cinfo = info.getGuiClassInfo();
		
					if(cinfo!=null)
					{
						Class<?> clazz = cinfo.getType(cl);
						if(clazz!=null)
						{
							try
							{
								ITaskPropertyGui gui = (ITaskPropertyGui)clazz.newInstance();
								gui.init(container, getBpmnTask(), cl);
								JComponent comp = gui.getComponent();
								propsp.add(comp, BorderLayout.CENTER);
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
					else
					{
						final PropertiesPanel pp = new PropertiesPanel();
						IndexMap<String, MProperty> mprops = getBpmnTask().getProperties();
						for(final PropertyMetaInfo pmi: props)
						{
							String lab = pmi.getName();
							if(pmi.getClazz()!=null)
								lab += " ("+pmi.getClazz().getTypeName()+")";
							
							String valtxt = "";
							if(mprops!=null && mprops.containsKey(pmi.getName()))
							{
								MProperty mprop = mprops.get(pmi.getName());
								if(SJavaParser.evaluateExpression(mprop.getInitialValue().getValue(), null)!=null)
									valtxt = mprop.getInitialValue().getValue();
							}
							
							final JTextField tf = pp.createTextField(lab, valtxt, true, 0, pmi.getDescription().length()>0? pmi.getDescription(): null);
							tf.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									String val = tf.getText();
									if(val.length()>0)
									{
										IndexMap<String, MProperty> mprops = getBpmnTask().getProperties();
										MProperty mprop = mprops.get(pmi.getName());
										UnparsedExpression uexp = new UnparsedExpression(null, 
											pmi.getClazz().getType(modelcontainer.getProjectClassLoader()), val, null);
										mprop.setInitialValue(uexp);
									}
								}
							});
						}
						propsp.add(pp, BorderLayout.CENTER);
					}
				}
			}
		};
		refresh.addActionListener(al);

		ActionListener alcbox = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
//				String taskname = (String) ((JComboBox)e.getSource()).getSelectedItem();
				ClassInfo taskcl = (ClassInfo)cbox.getSelectedItem();
				if(taskcl==null)
					return;
				
				String taskname = taskcl.getTypeName();
				
				// change task class
				getBpmnTask().setClazz(taskcl);

				// and renew properties
//				TaskMetaInfo info = getTaskMetaInfo(taskname);
//				initTaskProperties(taskcl);
				
				IndexMap<String, MProperty> props = getBpmnTask().getProperties();
				if(props!=null)
					props.clear();

				TaskMetaInfo info = getTaskMetaInfo(taskname);
				if(info!=null)
				{
					List<PropertyMetaInfo> pmis = info.getPropertyInfos();
					if(pmis!=null)
					{
						for(PropertyMetaInfo pmi: pmis)
						{
							UnparsedExpression uexp = new UnparsedExpression(null, 
								pmi.getClazz().getType(modelcontainer.getProjectClassLoader()), pmi.getInitialValue(), null);
							MProperty mprop = new MProperty(pmi.getClazz(), pmi.getName(), uexp);
							getBpmnTask().addProperty(mprop);
						}
					}
					
					processTaskInfos(taskname, descarea);
					
					defaultParameterButton.setEnabled(getTaskMetaInfo(taskname) != null);
				}
				
//				if(info!=null)
//				{
//					processTaskInfos(taskname, descarea);
//					defaultParameterButton.setEnabled(getTaskMetaInfo(taskname) != null);
//				}
				al.actionPerformed(null);
			}
		};
		cbox.addActionListener(alcbox);
		
		//addVerticalFiller(column, y);
		
		proppanel.add(propsp, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//		proppanel.add(refresh, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, 
//			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		
		// Parameter panel
		
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
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(modelcontainer.getSettings().getImageProvider(), addaction, removeaction);
		Action setDefaultParametersAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				processTaskParameters((ClassInfo)cbox.getSelectedItem(), atable);
				modelcontainer.setDirty(true);
			}
		};
		if(!isSubprocess())
		{
			Icon[] icons = modelcontainer.getSettings().getImageProvider().generateGenericFlatImageIconSet(buttonpanel.getIconSize(), ImageProvider.EMPTY_FRAME_TYPE, "page", buttonpanel.getIconColor());
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
		}
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		parameterpanel.add(buttonpanel, gc);
		
		if(isSubprocess())
		{
			tabpane.addTab("Parameters", parameterpanel);
			tabpane.addTab("Properties", proppanel);
		}
		else
		{
			tabpane.addTab("Properties", proppanel);
			tabpane.addTab("Parameters", parameterpanel);
		}
		
		if(selectedparameter != null)
		{
			tabpane.setSelectedComponent(parameterpanel);
			int row = getBpmnTask().getParameters().indexOf(selectedparameter);
			atable.setRowSelectionInterval(row, row);
		}
		
		add(tabpane, BorderLayout.CENTER);
		
		tabpane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				terminateEditing();
			}
		});
		
//		alcbox.actionPerformed(null);
		al.actionPerformed(null);
	}
	
//	/**
//	 * 
//	 */
//	protected void initTaskProperties(ClassInfo taskcl)
//	{
//		if(taskcl==null)
//			return;
//		IndexMap<String, MProperty> props = getBpmnTask().getProperties();
//		if(props!=null)
//			props.clear();
//
//		TaskMetaInfo info = getTaskMetaInfo(taskcl.getTypeName());
//		if(info!=null)
//		{
//			List<PropertyMetaInfo> pmis = info.getPropertyInfos();
//			if(pmis!=null)
//			{
//				for(PropertyMetaInfo pmi: pmis)
//				{
//					UnparsedExpression uexp = new UnparsedExpression(null, 
//						pmi.getClazz().getType(modelcontainer.getProjectClassLoader()), pmi.getInitialValue(), null);
//					MProperty mprop = new MProperty(pmi.getClazz(), pmi.getName(), uexp);
//					getBpmnTask().addProperty(mprop);
//				}
//			}
//		}
//	}
	
	/**
	 *  Gets the BPMN task.
	 *  
	 *  @return The BPMN task.
	 */
	protected MActivity getBpmnTask()
	{
		return (MActivity)task.getBpmnElement();
	}
	
	/**
	 *  Terminate.
	 */
	public void terminate()
	{
		terminateEditing();
	}
	
	/**
	 *  Terminates editing.
	 */
	public void terminateEditing()
	{
		if (atable.isEditing())
		{
			atable.getCellEditor().stopCellEditing();
		}
	}
	
	/**
	 *  Processes the task infos.
	 *  
	 *  @param taskname The task name.
	 *  @param descarea The description area.
	 *  @param atable The parameter table.
	 */
	protected void processTaskInfos(ClassInfo task, JEditorPane descarea)
	{
		if(task!=null)
			processTaskInfos(task.getTypeName(), descarea);
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
	protected void processTaskParameters(ClassInfo task, ActivityParameterTable atable)
	{
		if(task!=null)
			processTaskParameters(task.getTypeName(), atable);
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
				MActivity mact = (MActivity)task.getBpmnElement();
				for(int i = 0; i < pmis.size(); ++i)
				{
					validparams.add(pmis.get(i).getName());
					if(mact.hasParameter(pmis.get(i).getName()))
					{
						int ind = -1;
//						for(int j = 0; j < mact.getParameters().size(); ++j)
						Iterator<String> it = mact.getParameters().keySet().iterator();
						for(int j = 0; it.hasNext(); ++j)
						{
							String key = it.next();
							if(pmis.get(i).getName().equals(key))
							{
								ind = j;
								break;
							}
						}
						MParameter param = (MParameter)mact.getParameters().get(ind);
						atable.removeParameters(new int[]{ind});
						param.setClazz(pmis.get(i).getClazz());
						param.setDirection(pmis.get(i).getDirection());
						atable.addParameter(param);
					}
					else
					{
						String name = pmis.get(i).getName();
						ClassInfo clazz = pmis.get(i).getClazz();
						String inivalstr = pmis.get(i).getInitialValue();
						inivalstr = inivalstr != null? !"null".equals(inivalstr)? inivalstr : "" : "";
						UnparsedExpression inival = new UnparsedExpression(name, clazz.getTypeName(), inivalstr, null);
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
			else
			{
				atable.removeAllParameters();
			}
		}
	}
	
	/**
	 *  Gets the task meta information.
	 *  
	 *  @param taskname Name of the task.
	 *  @return The info, null if not found.
	 */
	protected TaskMetaInfo getTaskMetaInfo(ClassInfo task)
	{
		return task==null? null: getTaskMetaInfo(task.getTypeName());
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
			Class<?> clazz = SReflect.classForName(taskname, modelcontainer.getProjectClassLoader());

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

			Method m = clazz.getMethod("getExtraParameters", new Class[]{Map.class, IModelContainer.class, ClassLoader.class});
			// todo: use classloader of tool!
			List<ParameterMetaInfo> pis = new ArrayList<ParameterMetaInfo>();
			if(ret != null && ret.getParameterInfos()!=null && !ret.getParameterInfos().isEmpty())
			{
				pis.addAll(ret.getParameterInfos());
			}
			Map<String, MProperty> ps = getBpmnTask().getProperties().getAsMap();
			List<ParameterMetaInfo> params = (List<ParameterMetaInfo>)m.invoke(null, new Object[]{ps, modelcontainer, modelcontainer.getProjectClassLoader()});
			pis.addAll(params);
			ret = new TaskMetaInfo(ret!=null? ret.getDescription(): null, pis, ret!=null? ret.getPropertyInfos(): null, ret!=null? ret.getGuiClassInfo(): null);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			// ignore
		}
		
		
		return ret;
	}
	
	protected JPanel createExternalSubprocessTab()
	{
		final VExternalSubProcess subprocess = (VExternalSubProcess) task;
		
		int y = 0;
		JPanel column = new JPanel();
		column.setLayout(new GridBagLayout());
		
		final JCheckBox expbox = new JCheckBox();
		
		final JLabel label = new JLabel(FILE_NAME_TEXT);
		final JTextArea filearea = new JTextArea();
		filearea.setWrapStyleWord(true);
		filearea.setLineWrap(true);
		MSubProcess msp = (MSubProcess) subprocess.getBpmnElement();
		String filename = null;
		if (msp.hasProperty("filename"))
		{
			UnparsedExpression exp = msp.getPropertyValue("filename");
			filename = exp != null? exp.getValue() : null;
			filename = filename != null? filename.length() == 2? "" : filename.substring(1, filename.length() - 2) : "";
			filearea.setText(filename);
			expbox.setSelected(false);
		}
		else
		{
			UnparsedExpression fileexp = (UnparsedExpression) msp.getPropertyValue("file");
			filearea.setText(fileexp.getValue());
			expbox.setSelected(true);
			label.setText(FILE_EXPRESSION_TEXT);
		}
		
		
		filearea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String val = getText(e.getDocument());
//				if (subprocess.isCollapsed())
//				{
				if (expbox.isSelected())
				{
					UnparsedExpression exp = new UnparsedExpression("file", String.class, val, null);
					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
					((MSubProcess) subprocess.getBpmnElement()).addProperty(mprop);
				}
				else
				{
					UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"" + val + "\"", null);
					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
					((MSubProcess) subprocess.getBpmnElement()).addProperty(mprop);
				}
//				}
//				else
//				{
//					modelcontainer.getGraph().getModel().setValue(subprocess, val);
//				}
				modelcontainer.setDirty(true);
			}
		});
		JScrollPane sp =  new JScrollPane(filearea);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		expbox.setAction(new AbstractAction("Expression")
		{
			public void actionPerformed(ActionEvent e)
			{
				String val = DocumentAdapter.getText(filearea.getDocument());
				MSubProcess msp = (MSubProcess) subprocess.getBpmnElement();
				if (expbox.isSelected())
				{
					msp.removeProperty("filename");
					label.setText(FILE_EXPRESSION_TEXT);
					UnparsedExpression exp = new UnparsedExpression("file", String.class, val, null);
					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
					msp.addProperty(mprop);
				}
				else
				{
					msp.removeProperty("file");
					label.setText(FILE_NAME_TEXT);
					UnparsedExpression exp = new UnparsedExpression("filename", String.class, "\"" + val + "\"", null);
					MProperty mprop = new MProperty(exp.getClazz(), exp.getName(), exp);
					msp.addProperty(mprop);
				}
			}
		});
		
		JPanel expentrypanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;	
		expentrypanel.add(sp, gbc);
		filearea.setRows(3);
		sp.setMinimumSize(filearea.getPreferredSize());
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		expentrypanel.add(expbox, gbc);
		configureAndAddInputLine(column, label, expentrypanel, y++);
		
		addVerticalFiller(column, y);
		
		return column;
	}
	
	/**
	 * 
	 * @return True if the task is a subprocess.
	 */
	protected boolean isSubprocess()
	{
		return task instanceof VSubProcess || task instanceof VExternalSubProcess;
	}
}
