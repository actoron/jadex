package jadex.bpmn.editor.gui.propertypanels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.task.ITaskPropertyGui;
import jadex.bpmn.task.info.PropertyMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.ClassInfoComboBoxRenderer;
import jadex.commons.gui.autocombo.ComboBoxEditor;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;
import jadex.javaparser.SJavaParser;

/**
 * 
 */
public class TaskPropertyPanel2 extends InternalSubprocessPropertyPanel
{
	/** The task combo box. */
	protected AutoCompleteCombo cbox;
	
	/** The description area. */
	protected JEditorPane descarea;
	
	/** The add task param button. */
	protected JButton pbut;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public TaskPropertyPanel2(final ModelContainer container, Object selection)
	{
		super(container, selection);
		
//		if(cbox.getItemCount()>0)
//		{
//			int idx = cbox.getSelectedIndex();
//			cbox.setSelectedIndex(idx!=-1? idx: 0);
//		}
		
		cbox.setSelectedItem(getBpmnTask().getClazz());
	}
	
	/**
	 * 
	 */
	protected JTabbedPane createTabPanel()
	{
		JTabbedPane tabpane = new JTabbedPane();
		tabpane.addTab("Task", createTaskPanel());
		tabpane.addTab("Properties", createPropertyPanel());
		tabpane.addTab("Parameters", createParameterPanel());
		return tabpane;
	}
	
	/**
	 * 
	 */
	protected JPanel createTaskPanel()
	{
		JLabel label = new JLabel("Class");
		
		int y = 0;
		JPanel column = new JPanel(new GridBagLayout());
		
		final ClassLoader cl = getModelContainer().getProjectClassLoader()!=null? getModelContainer().getProjectClassLoader()
			: TaskPropertyPanel.class.getClassLoader();
			
		// Hack, side effect
		descarea = new JEditorPane("text/html", "");
		final JScrollPane descpane = new JScrollPane(descarea);
		
		// Hack, side effect :-(
		cbox = new AutoCompleteCombo(null, cl);
		final FixedClassInfoComboModel model = new FixedClassInfoComboModel(cbox, -1, new ArrayList<ClassInfo>(modelcontainer.getTaskClasses()));
		cbox.setModel(model);
		cbox.setEditor(new ComboBoxEditor(model));
		cbox.setRenderer(new ClassInfoComboBoxRenderer());
		
		if(getBpmnTask().getClazz()!=null)
		{
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
		
		JPanel cboxpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		cboxpanel.add(cbox, gc);
		
		final JCheckBox externalcheckbox = new JCheckBox();
		externalcheckbox.setAction(new AbstractAction(BpmnEditor.getString("External"))
		{
			public void actionPerformed(ActionEvent e)
			{
				MActivity act = ((MActivity)task.getBpmnElement());
				if(externalcheckbox.isSelected())
				{
					UnparsedExpression exp = new UnparsedExpression("external", "java.lang.Boolean", "true", null);
					act.setPropertyValue("external", exp);
				}
				else
				{
					act.removeProperty("external");
				}
				
			}
		});
		MActivity act = ((MActivity)task.getBpmnElement());
		String ext = act.getPropertyValueString("external");
		externalcheckbox.setSelected(ext!=null? Boolean.parseBoolean(ext): false);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		cboxpanel.add(externalcheckbox);
		
		configureAndAddInputLine(column, label, cboxpanel, y++, SUtil.createHashMap(new String[]{"second_fill"}, new Object[]{GridBagConstraints.HORIZONTAL}));
		
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = y;
		gc.gridwidth = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.fill = GridBagConstraints.BOTH;
		column.add(descpane, gc);
		
//		atable = new ActivityParameterTable(getModelContainer(), task);
//		
//		processTaskInfos((ClassInfo)cbox.getSelectedItem(), descarea);
//		
//		final JButton defaultParameterButton = new JButton();
//		defaultParameterButton.setEnabled(getTaskMetaInfo((ClassInfo)cbox.getSelectedItem()) != null);
		
		return column;
	}
	
	/**
	 * 
	 */
	protected AddRemoveButtonPanel createButtonPanel()
	{
		AddRemoveButtonPanel ret = super.createButtonPanel();
		
		// hack, side effect
		pbut = new JButton();
		pbut.setEnabled(getTaskMetaInfo(getSelectedTask()) != null);
		
		Action setDefaultParametersAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				processTaskParameters(getSelectedTask(), atable);
				modelcontainer.setDirty(true);
			}
		};
		
		Icon[] icons = modelcontainer.getSettings().getImageProvider().generateGenericFlatImageIconSet(ret.getIconSize(), ImageProvider.EMPTY_FRAME_TYPE, "page", ret.getIconColor());
		pbut.setAction(setDefaultParametersAction);
		pbut.setIcon(icons[0]);
		pbut.setPressedIcon(icons[1]);
		pbut.setRolloverIcon(icons[2]);
		pbut.setContentAreaFilled(false);
		pbut.setBorder(new EmptyBorder(0, 0, 0, 0));
		pbut.setMargin(new Insets(0, 0, 0, 0));
		pbut.setToolTipText("Enter default parameters appropriate for the selected task.");
		((GridLayout)ret.getLayout()).setRows(3);
		ret.add(pbut);
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected ClassInfo getSelectedTask()
	{
		return (ClassInfo)cbox.getSelectedItem();
	}
	
	/**
	 * 
	 */
	protected JPanel createPropertyPanel()
	{
		final ClassLoader cl = getModelContainer().getProjectClassLoader()!=null? 
			getModelContainer().getProjectClassLoader(): TaskPropertyPanel.class.getClassLoader();
		
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
								gui.init(getModelContainer(), getBpmnTask(), cl);
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
									getBpmnTask().setProperty(pmi.getName(), val, false);
//									if(val.length()>0)
//									{
//										IndexMap<String, MProperty> mprops = getBpmnTask().getProperties();
//										MProperty mprop = mprops.get(pmi.getName());
//										UnparsedExpression uexp = new UnparsedExpression(null, 
//											pmi.getClazz().getType(modelcontainer.getProjectClassLoader()), val, null);
//										mprop.setInitialValue(uexp);
//									}
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
				
				// renew properties
				if(!taskcl.equals(getBpmnTask().getClazz()))
				{
					IndexMap<String, MProperty> props = getBpmnTask().getProperties();
					if(props!=null)
						props.clear();
	
//					TaskMetaInfo info = getTaskMetaInfo(taskname);
//					if(info!=null)
//					{
//						List<PropertyMetaInfo> pmis = info.getPropertyInfos();
//						if(pmis!=null)
//						{
//							for(PropertyMetaInfo pmi: pmis)
//							{
//								UnparsedExpression uexp = new UnparsedExpression(null, 
//									pmi.getClazz().getType(cl), pmi.getInitialValue(), null);
//								MProperty mprop = new MProperty(pmi.getClazz(), pmi.getName(), uexp);
//								getBpmnTask().addProperty(mprop);
//							}
//						}
						
						pbut.setEnabled(getTaskMetaInfo(taskname) != null);
//					}
				}
				
				// change task class
				getBpmnTask().setClazz(taskcl);
				
				processTaskInfos(taskname, descarea);
				
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
		
		return proppanel;
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
}
