package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MContextVariable;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.gui.autocombo.AutoComboTableCellEditor;
import jadex.commons.gui.autocombo.AutoComboTableCellRenderer;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 *  BPMN process property panel.
 *
 */
public class BpmnPropertyPanel extends BasePropertyPanel
{
	/** The column names for the imports table. */
	protected String[] IMPORTS_COLUMN_NAMES = { "Import" };
	
	/** The column names for the configurations table. */
	protected String[] CONFIGURATIONS_COLUMN_NAMES = { "Name", "Activated Pool.Lane", "Suspend", "Master", "Daemon", "Autoshutdown" };
	
	/** The column names for the parameters table. */
	protected String[] PARAMETERS_COLUMN_NAMES = { "Name", "Argument", "Result", "Description", "Type", "Initial Value" };
	
	/** The column names for the properties table. */
	protected String[] PROPERTIES_COLUMN_NAMES = { "Name", "Type", "Value" };
	
	/** The column names for the provided services table. */
	protected String[] PROVIDED_SERVICES_COLUMN_NAMES = { "Name", "Interface", "Proxytype", "Implementation Class", "Implementation Expression" };
	
	/** The column names for the required services table. */
	protected String[] REQUIRED_SERVICES_COLUMN_NAMES = { "Name", "Interface", "Multiplex", "Scope" };
	
	/** The proxy types. */
	protected String[] PROXY_TYPES = { BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED,
									   BasicServiceInvocationHandler.PROXYTYPE_DIRECT,
									   BasicServiceInvocationHandler.PROXYTYPE_RAW };
	
	/** The scope types. */
	protected String[] SCOPE_TYPES = { RequiredServiceInfo.SCOPE_APPLICATION,
									   RequiredServiceInfo.SCOPE_COMPONENT,
									   RequiredServiceInfo.SCOPE_PARENT,
									   RequiredServiceInfo.SCOPE_LOCAL,
									   RequiredServiceInfo.SCOPE_PLATFORM,
									   RequiredServiceInfo.SCOPE_GLOBAL,
									   RequiredServiceInfo.SCOPE_UPWARDS };
	
	/** Cache for handling configurations. */
	protected List<ConfigurationInfo> confcache;
	
	/** The import table. */
	protected JTable importtable;
	
	/** The configurations table. */
	protected JTable conftable;
	
	/** The parameter table. */
	protected JTable paramtable;
	
	/** The properties table. */
	protected JTable proptable;
	
	/** The provided services table. */
	protected JTable pstable;
	
	/** The required services table. */
	protected JTable rstable;
	
	/** Cache for handling parameters. */
	protected IndexMap paramcche;
	
	/** Properties Index list. */
	protected List<String> propertynames;
	
	public BpmnPropertyPanel(ModelContainer container)
	{
		super(null, container);
		this.modelcontainer = container;
		this.propertynames = new ArrayList<String>();
		setLayout(new BorderLayout());
		JTabbedPane tabpane = new JTabbedPane();
		
		int y = 0;
		JPanel bpmnpanel = new JPanel(new GridLayout(1, 2));
		JPanel column = new JPanel(new GridBagLayout());
		bpmnpanel.add(column);
		tabpane.addTab("BPMN", bpmnpanel);
		
		JLabel label = new JLabel("Description");
		JTextArea textarea = new JTextArea();
		textarea.setWrapStyleWord(true);
		textarea.setLineWrap(true);
		textarea.setText(getModelInfo().getDescription() != null? getModelInfo().getDescription() : "");
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getModelInfo().setDescription(getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		JScrollPane sp =  new JScrollPane(textarea);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		configureAndAddInputLine(column, label, sp, y++);
		textarea.setRows(3);
		sp.setMinimumSize(textarea.getPreferredSize());
		
		label = new JLabel("Package");
		JTextField textfield = new JTextField();
		textfield.setText(getModelInfo().getPackage() != null? getModelInfo().getPackage() : "");
		textfield.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getModelInfo().setPackage(getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textfield, y++);
		//textarea = new JTextArea();
		//textarea.setText(getModelInfo().getPackage() != null? getModelInfo().getPackage() : "");
//		textarea.getDocument().addDocumentListener(new DocumentAdapter()
//		{
//			public void update(DocumentEvent e)
//			{
//				getModelInfo().setPackage(getText(e.getDocument()));
//				modelcontainer.setDirty(true);
//			}
//		});
//		configureAndAddInputLine(column, label, textarea, y++);
		
		addVerticalFiller(column, y);
		
		column = new JPanel(new FlowLayout(FlowLayout.LEADING));
		bpmnpanel.add(column);
		
		JCheckBox cbox = new JCheckBox();
		cbox.setSelected(convBool(getModelInfo().getSuspend()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModelInfo().setSuspend(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Suspend");
		column.add(cbox);
		
		cbox = new JCheckBox();
		cbox.setSelected(convBool(getModelInfo().getMaster()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModelInfo().setMaster(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Master");
		column.add(cbox);
		
		cbox = new JCheckBox();
		cbox.setSelected(convBool(getModelInfo().getDaemon()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModelInfo().setDaemon(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Daemon");
		column.add(cbox);
		
		cbox = new JCheckBox();
		cbox.setSelected(convBool(getModelInfo().getAutoShutdown()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModelInfo().setAutoShutdown(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Autoshutdown");
		column.add(cbox);
		
		cbox = new JCheckBox();
		cbox.setSelected(convBool(getModel().isKeepAlive()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModel().setKeepAlive(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Keep Alive");
		column.add(cbox);
		
		cbox = new JCheckBox();
		cbox.setSelected(convBool(getModelInfo().getMonitoring()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModelInfo().setMonitoring(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Monitoring");
		column.add(cbox);
		
		cbox = new JCheckBox();
		cbox.setSelected(convBool(getModelInfo().getSynchronous()));
		cbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				getModelInfo().setSynchronous(((JCheckBox) e.getSource()).isSelected());
			}
		});
		cbox.setText("Synchronous");
		column.add(cbox);
		
		setupImportsTable(tabpane);
		
		setupConfigurationsTable(tabpane);
		
		setupParametersTable(tabpane);
		
		setupPropertiesTable(tabpane);
		
		setupProvidedServicesTable(tabpane);
		
		setupRequiredServicesTable(tabpane);
		
		tabpane.add("Subcomponents", new SubcomponentTab(container));
		
		add(tabpane, BorderLayout.CENTER);
		
		tabpane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				terminateEditing();
			}
		});
	}
	
	/**
	 *  Initializes the imports table.
	 */
	protected void setupImportsTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		importtable = new JTable(new ImportTableModel());
		JScrollPane tablescrollpane = new JScrollPane(importtable);
		tablepanel.add(tablescrollpane, gc);
		
		Action addaction = new AbstractAction("Add Import")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = importtable.getRowCount();
				getModelInfo().addImport("jadex.*");
				modelcontainer.setDirty(true);
				((ImportTableModel) importtable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		Action removeaction = new AbstractAction("Remove Imports")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = importtable.getSelectedRows();
				Arrays.sort(ind);
				
				List imports = SUtil.arrayToList(getModelInfo().getImports());
				for (int i = ind.length - 1; i >= 0; --i)
				{
					
					imports.remove(ind[i]);
					getModelInfo().setImports((String[]) imports.toArray(new String[imports.size()]));
					((ImportTableModel) importtable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				modelcontainer.setDirty(true);
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		tabpane.add(tablepanel, "Imports");
	}
	
	/**
	 *  Initializes the configurations table.
	 */
	protected void setupConfigurationsTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		confcache = new ArrayList<ConfigurationInfo>(Arrays.asList(getModelInfo().getConfigurations()));
		conftable = new JTable(new ConfigurationTableModel());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JScrollPane tablescrollpane = new JScrollPane(conftable);
		tablepanel.add(tablescrollpane, gc);
		
		Action addaction = new AbstractAction("Add Configuration")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = conftable.getRowCount();
				ConfigurationInfo conf = new ConfigurationInfo(createFreeName("name", new ConfigurationContains(confcache)));
				conf.setSuspend(getModelInfo().getSuspend());
				conf.setMaster(getModelInfo().getMaster());
				conf.setDaemon(getModelInfo().getDaemon());
				conf.setAutoShutdown(getModelInfo().getAutoShutdown());
				confcache.add(conf);
				getModelInfo().setConfigurations((ConfigurationInfo[]) confcache.toArray(new ConfigurationInfo[confcache.size()]));
				modelcontainer.setDirty(true);
				((ConfigurationTableModel) conftable.getModel()).fireTableRowsInserted(row, row);
				
			}
		};
		Action removeaction = new AbstractAction("Remove Configurations")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = conftable.getSelectedRows();
				Arrays.sort(ind);
				
				for (int i = ind.length - 1; i >= 0; --i)
				{
					ConfigurationInfo conf = confcache.remove(ind[i]);
					getModelInfo().setConfigurations((ConfigurationInfo[]) confcache.toArray(new ConfigurationInfo[confcache.size()]));
					getModel().removePoolLane(conf.getName());
					modelcontainer.setDirty(true);
					((ConfigurationTableModel) conftable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
					
					for (int j = 0; j < paramcche.size(); ++j)
					{
						CachedParameter param = (CachedParameter) paramcche.get(j);
						if (param.inivals.containsKey(conf.getName()))
						{
							removeParameter(param.name);
							
							param.inivals.remove(conf.getName());
							
							addParameter(param, j);
							//TODO Update param model?
						}
					}
				}
			}
		};
		JPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		tabpane.add(tablepanel, "Configurations");
	}
	
	/**
	 *  Initializes the parameters table.
	 */
	protected void setupParametersTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		paramcche = new IndexMap();
		paramtable = new JTable(new ParameterTableModel());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JScrollPane tablescrollpane = new JScrollPane(paramtable);
		tablepanel.add(tablescrollpane, gc);
		
		final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, modelcontainer.getAllClasses());
		acc.setModel(accm);
		TableColumn col = paramtable.getColumnModel().getColumn(4);
		col.setCellEditor(new AutoComboTableCellEditor(acc));
		col.setCellRenderer(new AutoComboTableCellRenderer(acc));
		
		Action addaction = new AbstractAction("Add Parameter")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = paramtable.getRowCount();
				CachedParameter param = new CachedParameter(createFreeName("name", new BasePropertyPanel.IndexMapContains(paramcche)), false, false, "", "");
				addParameter(param, null);
				modelcontainer.setDirty(true);
				((ParameterTableModel) paramtable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		Action removeaction = new AbstractAction("Remove Parameters")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = paramtable.getSelectedRows();
				Arrays.sort(ind);
				
				for (int i = ind.length - 1; i >= 0; --i)
				{
					CachedParameter param = (CachedParameter) paramcche.get(ind[i]);
					removeParameter(param.name);
					modelcontainer.setDirty(true);
					((ParameterTableModel) paramtable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
			}
		};
		
		MBpmnModel model = modelcontainer.getBpmnModel();
		List<MContextVariable> cvs = model.getContextVariables();
		for (MContextVariable cv : cvs)
		{
			String type = cv.getClazz().getTypeName();
			CachedParameter cparam = new CachedParameter(cv.getName(), false, false, null, type);
			
			for (ConfigurationInfo conf : getModelInfo().getConfigurations())
			{
				//UnparsedExpression exp = model.getContextVariableExpression(cvname, conf.getName());
				UnparsedExpression exp = cv.getValue(conf.getName());
				if (exp != null)
				{
					cparam.inivals.put(conf.getName(), exp.getValue());
				}
			}
			
			//UnparsedExpression exp = model.getContextVariableExpression(cvname, null);
//			if (exp != null)
//			{
//				cparam.inivals.put(null, exp.getValue());
//			}
			cparam.inivals.put(null, cv.getValue());
			
			paramcche.put(cv.getName(), cparam);
		}
		
		IArgument[] args = getModelInfo().getArguments();
		for (IArgument arg : args)
		{
			String type = arg.getClazz().getTypeName();
			CachedParameter cparam = new CachedParameter(arg.getName(), true, false, arg.getDescription(), type);
			
			UnparsedExpression exp = arg.getDefaultValue();
			if (exp != null)
			{
				cparam.inivals.put(null, exp.getValue());
			}
			
			paramcche.put(arg.getName(), cparam);
		}
		
		IArgument[] ress = getModelInfo().getResults();
		for (IArgument res : ress)
		{
			CachedParameter cparam = (CachedParameter) paramcche.get(res.getName());
			if (cparam != null)
			{
				cparam.res = true;
			}
			else
			{
				String type = res.getClazz().getTypeName();
				cparam = new CachedParameter(res.getName(), false, true, res.getDescription(), type);
				
				UnparsedExpression exp = res.getDefaultValue();
				if (exp != null)
				{
					cparam.inivals.put(null, exp.getValue());
				}
				
				paramcche.put(res.getName(), cparam);
			}
		}
		
		ConfigurationInfo[] confs = getModelInfo().getConfigurations();
		for (ConfigurationInfo conf : confs)
		{
			UnparsedExpression[] argexps = conf.getArguments();
			for (UnparsedExpression argexp : argexps)
			{
				((CachedParameter) paramcche.get(argexp.getName())).inivals.put(conf.getName(), argexp.getValue());
			}
			
			UnparsedExpression[] resexps = conf.getResults();
			for (UnparsedExpression resexp : resexps)
			{
				((CachedParameter) paramcche.get(resexp.getName())).inivals.put(conf.getName(), resexp.getValue());
			}
		}
		
		JPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		tabpane.add(tablepanel, "Parameters");
	}
	
	/**
	 *  Initializes the properties table.
	 */
	protected void setupPropertiesTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		proptable = new JTable(new PropertyTableModel());
		JScrollPane tablescrollpane = new JScrollPane(proptable);
		tablepanel.add(tablescrollpane, gc);
		
		final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, modelcontainer.getAllClasses());
		acc.setModel(accm);
		TableColumn col = proptable.getColumnModel().getColumn(1);
		col.setCellEditor(new AutoComboTableCellEditor(acc));
		col.setCellRenderer(new AutoComboTableCellRenderer(acc));
		
		Action addaction = new AbstractAction("Add Property")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = propertynames.size();
				String name = createFreeName("name", new BasePropertyPanel.MapContains(getModelInfo().getProperties()));
				getModelInfo().addProperty(new UnparsedExpression(name, "", "", null));
				propertynames.add(name);
				((PropertyTableModel) proptable.getModel()).fireTableRowsInserted(row, row);
				modelcontainer.setDirty(true);
			}
		};
		Action removeaction = new AbstractAction("Remove Properties")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = proptable.getSelectedRows();
				
				Arrays.sort(ind);
				
				Map<String, Object> props = getModelInfo().getProperties();
				for (int i = ind.length - 1; i >= 0; --i)
				{
					String name = propertynames.remove(i);
					props.remove(name);
					((PropertyTableModel) proptable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
					modelcontainer.setDirty(true);
				}
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		tabpane.add(tablepanel, "Properties");
		
		for (Object expobj : getModelInfo().getProperties().values())
		{
			UnparsedExpression exp = (UnparsedExpression) expobj;
			propertynames.add(exp.getName());
		}
	}
	
	/**
	 *  Initializes the provided services table.
	 */
	protected void setupProvidedServicesTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		JComboBox proxybox = new JComboBox(PROXY_TYPES);
		final DefaultCellEditor proxyeditor = new DefaultCellEditor(proxybox);
		pstable = new JTable(new ProvidedServicesTableModel())
		{
			public TableCellEditor getCellEditor(int row, int column)
			{
				if (column == 2)
				{
					return proxyeditor;
				}
				return super.getCellEditor(row, column);
			}
		};
		
		final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, modelcontainer.getInterfaces());
		acc.setModel(accm);
		TableColumn col = pstable.getColumnModel().getColumn(1);
		col.setCellEditor(new AutoComboTableCellEditor(acc));
		col.setCellRenderer(new AutoComboTableCellRenderer(acc));
		
		final AutoCompleteCombo acc2 = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm2 = new FixedClassInfoComboModel(acc2, 20, modelcontainer.getAllClasses());
		acc2.setModel(accm2);
		TableColumn col2 = pstable.getColumnModel().getColumn(3);
		col2.setCellEditor(new AutoComboTableCellEditor(acc2));
		col2.setCellRenderer(new AutoComboTableCellRenderer(acc2));
		
		JScrollPane tablescrollpane = new JScrollPane(pstable);
		tablepanel.add(tablescrollpane, gc);
		
		conftable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				((ProvidedServicesTableModel) pstable.getModel()).fireTableStructureChanged();
			}
		});
		
		Action addaction = new AbstractAction("Add Provided Service")
		{
			public void actionPerformed(ActionEvent e)
			{
				ProvidedServiceImplementation psimpl = new ProvidedServiceImplementation();
				psimpl.setProxytype(BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED);
				String name = createFreeName("name", new PSContains());
				ProvidedServiceInfo ps = new ProvidedServiceInfo();
				ps.setName(name);
				ps.setType(null);
				ps.setImplementation(psimpl);
				int row = getModelInfo().getProvidedServices().length;
				getModelInfo().addProvidedService(ps);
				modelcontainer.setDirty(true);
				
				((ProvidedServicesTableModel) pstable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		Action removeaction = new AbstractAction("Remove Provided Services")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = pstable.getSelectedRows();
				
				Arrays.sort(ind);
				
				ProvidedServiceInfo[] services = getModelInfo().getProvidedServices();
				for (int i = ind.length - 1; i >= 0; --i)
				{
					getModelInfo().removeProvidedService(services[i]);
					for (ConfigurationInfo conf : getModelInfo().getConfigurations())
					{
						conf.removeProvidedService(services[i]);
					}
					((ProvidedServicesTableModel) pstable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				modelcontainer.setDirty(true);
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		tabpane.add(tablepanel, "Provided Services");
	}
	
	/**
	 *  Initializes the required services table.
	 */
	protected void setupRequiredServicesTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		String[] scopeboxscopes = new String[SCOPE_TYPES.length];
		for (int i = 0; i < SCOPE_TYPES.length; ++i)
		{
			String firstchar = SCOPE_TYPES[i].substring(0, 1);
			scopeboxscopes[i] = SCOPE_TYPES[i].replaceFirst(firstchar, firstchar.toUpperCase());
		}
		JComboBox scopebox = new JComboBox(scopeboxscopes);
		final DefaultCellEditor scopeeditor = new DefaultCellEditor(scopebox);
		rstable = new JTable(new RequiredServicesTableModel())
		{
			public TableCellEditor getCellEditor(int row, int column)
			{
				if (column == 3)
				{
					return scopeeditor;
				}
				return super.getCellEditor(row, column);
			}
		};
		
		final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, modelcontainer.getInterfaces());
		acc.setModel(accm);
		TableColumn col = rstable.getColumnModel().getColumn(1);
		col.setCellEditor(new AutoComboTableCellEditor(acc));
		col.setCellRenderer(new AutoComboTableCellRenderer(acc));
		
		JScrollPane tablescrollpane = new JScrollPane(rstable);
		tablepanel.add(tablescrollpane, gc);
		
		conftable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				((RequiredServicesTableModel) rstable.getModel()).fireTableStructureChanged();
			}
		});
		
		Action addaction = new AbstractAction("Add Required Service")
		{
			public void actionPerformed(ActionEvent e)
			{
				String name = createFreeName("name", new RSContains());
				RequiredServiceInfo rs = new RequiredServiceInfo();
				rs.setName(name);
				int row = getModelInfo().getRequiredServices().length;
				getModelInfo().addRequiredService(rs);
				modelcontainer.setDirty(true);
				
				((RequiredServicesTableModel) rstable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		Action removeaction = new AbstractAction("Remove Required Services")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = rstable.getSelectedRows();
				
				Arrays.sort(ind);
				
				RequiredServiceInfo[] services = getModelInfo().getRequiredServices();
				for (int i = ind.length - 1; i >= 0; --i)
				{
					getModelInfo().removeRequiredService(services[i]);
					for (ConfigurationInfo conf : getModelInfo().getConfigurations())
					{
						conf.removeRequiredService(services[i]);
					}
					((RequiredServicesTableModel) rstable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				modelcontainer.setDirty(true);
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		tabpane.add(tablepanel, "Required Services");
	}
	
	/**
	 *  Gets the model info.
	 *  @return The model info.
	 */
	protected ModelInfo getModelInfo()
	{
		return (ModelInfo) getModel().getModelInfo();
	}
	
	/**
	 *  Adds a parameter to the model.
	 *  
	 *  @param param The parameter.
	 */
	protected void addParameter(CachedParameter param, Integer index)
	{
		String defval = param.inivals.get(null);
		
		for (ConfigurationInfo conf : confcache)
		{
			if (param.inivals.containsKey(conf.getName()))
			{
				String inival = param.inivals.get(conf.getName());
				
				if (param.arg)
				{
					conf.addArgument(new UnparsedExpression(param.name, param.type, inival, null));
				}
				if (param.res)
				{
					conf.addResult(new UnparsedExpression(param.name, param.type, inival, null));
				}
			}
		}
		
		
		
		if (param.arg)
		{
			getModelInfo().addArgument(new Argument(param.name, param.desc, param.type, defval));
		}
		
		
		if (param.res)
		{
			getModelInfo().addResult(new Argument(param.name, param.desc, param.type, defval));
		}
		
		if (!param.arg && !param.res)
		{
			MContextVariable cv = new MContextVariable(param.name, param.desc, param.type, defval);
			getModel().addContextVariable(cv);
			//getModel().addContextVariable(param.name, new ClassInfo(param.type), new UnparsedExpression(param.name, param.type, defval, null), param.inivals);
		}
		
		if (index != null)
		{
			paramcche.add(index, param.name, param);
		}
		else
		{
			paramcche.put(param.name, param);
		}
	}
	
	/**
	 *  Removes a parameter to the model.
	 *  
	 *  @param paramname The parameter name.
	 *  @return The removed parameter.
	 */
	protected CachedParameter removeParameter(String paramname)
	{
		CachedParameter param = (CachedParameter) paramcche.removeKey(paramname);
		
		if (param.arg || param.res)
		{
			for (ConfigurationInfo conf : confcache)
			{
				if (param.inivals.containsKey(conf.getName()))
				{
					if (param.arg)
					{
						conf.setArguments(removeExpression(conf.getArguments(), param.name));
					}
					if (param.res)
					{
						conf.setResults(removeExpression(conf.getResults(), param.name));
					}
				}
			}
		}
		
		if (param.arg)
		{
			getModelInfo().setArguments(removeArgument(getModelInfo().getArguments(), param.name));
		}
		
		if (param.res)
		{
			getModelInfo().setResults(removeArgument(getModelInfo().getResults(), param.name));
		}
		
		if (!param.arg && !param.res)
		{
			getModel().removeContextVariable(paramname);
		}
		
		return param;
	}
	
	/**
	 *  Method for removing an argument from an array.
	 */
	protected IArgument[] removeArgument(IArgument[] args, String name)
	{
		IArgument[] newargs = new IArgument[args.length - 1];
		int ni = 0;
		for (int i = 0; i < args.length; ++i)
		{
			if (!args[i].getName().equals(name))
			{
				newargs[ni++] = args[i];
			}
		}
		return newargs;
	}
	
	/**
	 *  Method for removing an unparsed expression from an array.
	 */
	protected UnparsedExpression[] removeExpression(UnparsedExpression[] exp, String name)
	{
		UnparsedExpression[] newexp = new UnparsedExpression[exp.length - 1];
		int ni = 0;
		for (int i = 0; i < exp.length; ++i)
		{
			if (!exp[i].getName().equals(name))
			{
				newexp[ni++] = exp[i];
			}
		}
		return newexp;
	}
	
	/**
	 *  Convenience method to convert Boolean null values to false.
	 *  
	 *  @param value The value.
	 *  @return False, if value is null.
	 */
	protected boolean convBool(Object value)
	{
		return value != null? ((Boolean) value).booleanValue(): false;
	}
	
	/**
	 *  Returns the selected configuration.
	 *  
	 *  @return Selected configuration.
	 */
	protected ConfigurationInfo getSelectedConfiguration()
	{
		int sel = conftable.getSelectedRow();
		ConfigurationInfo ret = null;
		if (sel >= 0)
		{
			ret = confcache.get(sel);
		}
		return ret;
	}
	
	/**
	 *  Returns the selected configuration name.
	 *  
	 *  @return Selected configuration name.
	 */
	protected String getSelectedConfigurationName()
	{
		int sel = conftable.getSelectedRow();
		ConfigurationInfo conf = null;
		if (sel >= 0)
		{
			conf = confcache.get(sel);
		}
		return conf != null? conf.getName() : null;
	}
	
	/**
	 *  Terminates.
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
		if (importtable.isEditing())
		{
			importtable.getCellEditor().stopCellEditing();
		}
		
		if (conftable.isEditing())
		{
			conftable.getCellEditor().stopCellEditing();
		}
		
		if (paramtable.isEditing())
		{
			paramtable.getCellEditor().stopCellEditing();
		}
		
		if (proptable.isEditing())
		{
			proptable.getCellEditor().stopCellEditing();
		}
		
		if (pstable.isEditing())
		{
			pstable.getCellEditor().stopCellEditing();
		}
		
		if (rstable.isEditing())
		{
			rstable.getCellEditor().stopCellEditing();
		}
	}
	
	/**
	 *  Table model for imports.
	 */
	protected class ImportTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return IMPORTS_COLUMN_NAMES[column];
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return getModelInfo().getImports() != null? getModelInfo().getImports().length : 0;
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return 1;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return getModelInfo().getImports()[rowIndex];
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			List imports = SUtil.arrayToList(getModelInfo().getImports());
			imports.set(rowIndex, value);
			getModelInfo().setImports((String[]) imports.toArray(new String[imports.size()]));
			fireTableCellUpdated(rowIndex, columnIndex);
			modelcontainer.setDirty(true);
		}
	}
	
	/**
	 *  Table model for configurations.
	 */
	protected class ConfigurationTableModel extends AbstractTableModel
	{
		/**
		 *  Returns the column class.
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex > 1)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}
		
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return CONFIGURATIONS_COLUMN_NAMES[column];
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return confcache.size();
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return CONFIGURATIONS_COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Object ret = null;
			switch (columnIndex)
			{
				case 0:
					ret = confcache.get(rowIndex).getName();
					break;
				case 2:
					ret = convBool(confcache.get(rowIndex).getSuspend());
					break;
				case 3:
					ret = convBool(confcache.get(rowIndex).getMaster());
					break;
				case 4:
					ret = convBool(confcache.get(rowIndex).getDaemon());
					break;
				case 5:
					ret = convBool(confcache.get(rowIndex).getAutoShutdown());
					break;
				case 1:
				default:
					ret = getModel().getPoolLane(confcache.get(rowIndex).getName());
			}
			return ret;
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					if (!value.equals(getValueAt(rowIndex, columnIndex)))
					{
						ConfigurationInfo cinfo = confcache.get(rowIndex);
						String oldname = cinfo.getName();
						String poollane = getModel().removePoolLane(oldname);
						cinfo.setName(createFreeName((String) value, new ConfigurationContains(confcache)));
						getModelInfo().setConfigurations((ConfigurationInfo[]) confcache.toArray(new ConfigurationInfo[confcache.size()]));
						getModel().addPoolLane(cinfo.getName(), poollane);
						
						for (MContextVariable cv : getModel().getContextVariables())
						{
							//UnparsedExpression exp = getModel().getContextVariableExpression(cvname, oldname);
	//						if (exp != null)
	//						{
								//getModel().setContextVariableExpression(cvname, oldname, null);
								UnparsedExpression exp = cv.removeValue(oldname);
								if (exp != null)
								{
									cv.setValue(cinfo.getName(), exp);
								}
								//getModel().setContextVariableExpression(cvname, cinfo.getName(), exp);
	//						}
						}
						
						for (Object paramobj : paramcche.values())
						{
							CachedParameter param = (CachedParameter) paramobj;
							String val = param.inivals.remove(oldname);
							if (val != null)
							{
								param.inivals.put(cinfo.getName(), val);
							}
						}
						
						((AbstractTableModel) paramtable.getModel()).fireTableStructureChanged();
						((AbstractTableModel) pstable.getModel()).fireTableStructureChanged();
						((AbstractTableModel) rstable.getModel()).fireTableStructureChanged();
					}
					break;
				case 2:
					confcache.get(rowIndex).setSuspend((Boolean) value);
					break;
				case 3:
					confcache.get(rowIndex).setMaster(((Boolean) value));
					break;
				case 4:
					confcache.get(rowIndex).setDaemon(((Boolean) value));
					break;
				case 5:
					confcache.get(rowIndex).setAutoShutdown(((Boolean) value));
					break;
				case 1:
				default:
					getModel().addPoolLane(confcache.get(rowIndex).getName(), (String) value);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
			modelcontainer.setDirty(true);
		}
	}
	
	/**
	 *  Table model for imports.
	 */
	protected class ParameterTableModel extends AbstractTableModel
	{
		public ParameterTableModel()
		{
			conftable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					/*for (int i = 0; i < paramtable.getColumnCount(); ++i)
					{
						TableColumn column = paramtable.getColumnModel().getColumn(i);
						column.setHeaderValue(getColumnName(i));
						paramtable.getTableHeader().repaint();
					}*/
					fireTableStructureChanged();
				}
			});
		}
		
		/**
		 *  Returns the column class.
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			if ((columnIndex == 1) || (columnIndex == 2))
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}
		
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			String ret = PARAMETERS_COLUMN_NAMES[column];
			if (column == 5)
			{
				String confname = getSelectedConfigurationName();
				if (confname != null)
				{
					ret += " [" + confname + "]";
				}
			}
			return ret;
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return paramcche.size();
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return PARAMETERS_COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			CachedParameter param = (CachedParameter)paramcche.get(rowIndex);
			switch(columnIndex)
			{
				case 0:
				default:
					return param.name;
				case 1:
					return param.arg;
				case 2:
					return param.res;
				case 3:
					return param.desc;
				case 4:
					return new ClassInfo(param.type);
				case 5:
					String confname = getSelectedConfigurationName();
					Object ret = param.inivals.get(confname);
					return ret != null? ret: "";
			}
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			Object oldval = getValueAt(rowIndex, columnIndex);
			CachedParameter param = (CachedParameter) paramcche.get(rowIndex);
			removeParameter(param.name);
			switch(columnIndex)
			{
				case 0:
				default:
					if (!value.equals(oldval))
					{
						param.name = createFreeName((String) value, new BasePropertyPanel.IndexMapContains(paramcche));
					}
					break;
				case 1:
					param.arg = convBool(value);
					break;
				case 2:
					param.res = convBool(value);
					break;
				case 3:
					param.desc = (String) value;
					break;
				case 4:
					param.type = value!=null? ((ClassInfo)value).getTypeName(): null;
					break;
				case 5:
					String confname = getSelectedConfigurationName();
					param.inivals.put(confname, (String) value);
					break;
			}
			addParameter(param, rowIndex);
			fireTableCellUpdated(rowIndex, columnIndex);
			modelcontainer.setDirty(true);
		}
	}
	
	/**
	 *  Table model for model properties.
	 */
	protected class PropertyTableModel extends AbstractTableModel
	{
		
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return PROPERTIES_COLUMN_NAMES[column];
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return propertynames.size();
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return PROPERTIES_COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Object ret = null;
			UnparsedExpression prop = (UnparsedExpression) getModelInfo().getProperties().get(propertynames.get(rowIndex));
			switch (columnIndex)
			{
				case 0:
				default:
					ret = prop.getName();
					break;
				case 1:
					ret = prop.getClazz();
					break;
				case 2:
					ret = prop.getValue();
			}
			return ret;
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			UnparsedExpression prop = (UnparsedExpression) getModelInfo().getProperties().get(propertynames.get(rowIndex));
			switch (columnIndex)
			{
				case 0:
				default:
					if (!value.equals(getValueAt(rowIndex, columnIndex)))
					{
						getModelInfo().getProperties().remove(prop.getName());
						prop.setName(createFreeName((String) value, new MapContains(getModelInfo().getProperties())));
						getModelInfo().getProperties().put(prop.getName(), prop);
						propertynames.set(rowIndex, prop.getName());
					}
					break;
				case 1:
					prop.setClazz((ClassInfo)value);
					break;
				case 2:
					prop.setValue(((String) value));
			}
			fireTableCellUpdated(rowIndex, columnIndex);
			modelcontainer.setDirty(true);
		}
	}
	
	/**
	 *  Table model for provided services.
	 */
	protected class ProvidedServicesTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			String ret = PROVIDED_SERVICES_COLUMN_NAMES[column];
			String confname = getSelectedConfigurationName();
			if (confname != null && column > 1)
			{
				ret += " [" + confname + "]";
			}
			return ret;
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return getModelInfo().getProvidedServices().length;
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return PROVIDED_SERVICES_COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			ProvidedServiceInfo ps = getModelInfo().getProvidedServices()[rowIndex];
			ProvidedServiceInfo cs = getProvService(ps.getName(), getSelectedConfiguration());
			switch (columnIndex)
			{
				case 0:
				default:
					return ps.getName();
				case 1:
					return ps.getType();
				case 2:
				{
					Object ret = null;
					if (cs != null)
					{
						ps = cs;
					}
					if (ps != null && ps.getImplementation() != null)
					{
						ret = ps.getImplementation().getProxytype();
					}
					return ret;
				}
				case 3:
				{
					Object ret = null;
					if (cs != null)
					{
						ps = cs;
					}
					if (ps != null && ps.getImplementation() != null && ps.getImplementation().getClazz() != null)
					{
						ret = ps.getImplementation().getClazz();
					}
					return ret;
				}
				case 4:
				{
					Object ret = null;
					if (cs != null)
					{
						ps = cs;
					}
					if (ps != null && ps.getImplementation() != null && ps.getImplementation().getValue() != null)
					{
						ret = ps.getImplementation().getValue();
					}
					return ret;
				}
			}
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			ProvidedServiceInfo ps = getModelInfo().getProvidedServices()[rowIndex];
			ConfigurationInfo conf = getSelectedConfiguration();
			ProvidedServiceInfo cs = getProvService(ps.getName(), getSelectedConfiguration());
			if ((columnIndex == 2 ||
				columnIndex == 3) &&
				cs == null &&
				conf != null)
			{
				cs = new ProvidedServiceInfo();
				cs.setName(ps.getName());
				conf.addProvidedService(cs);
			}
			switch (columnIndex)
			{
				case 0:
				default:
				{
					if (!value.equals(getValueAt(rowIndex, columnIndex)))
					{
						String newname = createFreeName((String) value, new PSContains());
						ConfigurationInfo[] confs = getModelInfo().getConfigurations();
						for (ConfigurationInfo itconf : confs)
						{
							ProvidedServiceInfo itcs = getProvService(ps.getName(), itconf);
							itcs.setName(newname);
						}
						ps.setName(newname);
					}
					break;
				}
				case 1:
				{
//					ClassInfo type = nullifyString(value) != null? new ClassInfo(nullifyString(value)) : null;
					ps.setType((ClassInfo)value);
					break;
				}
				case 2:
				{
					if (cs != null)
					{
						createImplementation(cs);
						cs.getImplementation().setProxytype(nullifyString(value));
						if (compareService(ps, cs))
						{
							conf.removeProvidedService(cs);
						}
					}
					else
					{
						createImplementation(ps);
						ps.getImplementation().setProxytype(nullifyString(value));
						for (ConfigurationInfo itconf : getModelInfo().getConfigurations())
						{
							cs = getProvService(ps.getName(), itconf);
							if (compareService(ps, cs))
							{
								itconf.removeProvidedService(cs);
							}
						}
					}
					break;
				}
				case 3:
				{
					ClassInfo type = (ClassInfo)value;
					if (cs != null)
					{
						createImplementation(cs);
						cs.getImplementation().setClazz(type);
						if (compareService(ps, cs))
						{
							conf.removeProvidedService(cs);
						}
					}
					else
					{
						createImplementation(ps);
						ps.getImplementation().setClazz(type);
						for (ConfigurationInfo itconf : getModelInfo().getConfigurations())
						{
							cs = getProvService(ps.getName(), itconf);
							if (compareService(ps, cs))
							{
								itconf.removeProvidedService(cs);
							}
						}
					}
					break;
				}
				case 4:
				{
//					ClassInfo type = nullifyString(value) != null? new ClassInfo(nullifyString(value)) : null;
					if (cs != null)
					{
						createImplementation(cs);
						cs.getImplementation().setValue((String)value);
						if (compareService(ps, cs))
						{
							conf.removeProvidedService(cs);
						}
					}
					else
					{
						createImplementation(ps);
						ps.getImplementation().setValue((String)value);
						for (ConfigurationInfo itconf : getModelInfo().getConfigurations())
						{
							cs = getProvService(ps.getName(), itconf);
							if (compareService(ps, cs))
							{
								itconf.removeProvidedService(cs);
							}
						}
					}
					break;
				}
			}
		}
	}
	
	/**
	 *  Table model for provided services.
	 */
	protected class RequiredServicesTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			String ret = REQUIRED_SERVICES_COLUMN_NAMES[column];
			String confname = getSelectedConfigurationName();
			if (confname != null && column == 3)
			{
				ret += " [" + confname + "]";
			}
			return ret;
		}
		
		/**
		 *  Returns the column class.
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 2)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return getModelInfo().getRequiredServices().length;
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return REQUIRED_SERVICES_COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			RequiredServiceInfo rs = getModelInfo().getRequiredServices()[rowIndex];
			RequiredServiceInfo cs = getReqService(rs.getName(), getSelectedConfiguration());
			switch (columnIndex)
			{
				case 0:
				default:
					return rs.getName();
				case 1:
					return rs.getType();
				case 2:
				{
					return rs.isMultiple();
				}
				case 3:
				{
					String ret = RequiredServiceInfo.SCOPE_COMPONENT;
					if (cs != null)
					{
						rs = cs;
					}
					if (rs != null && rs.getDefaultBinding() != null && rs.getDefaultBinding().getScope() != null)
					{
						ret = rs.getDefaultBinding().getScope();
					}
					String firstchar = ret.substring(0, 1);
					ret = ret.replaceFirst(firstchar, firstchar.toUpperCase());
					return ret;
				}
			}
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			RequiredServiceInfo rs = getModelInfo().getRequiredServices()[rowIndex];
			ConfigurationInfo conf = getSelectedConfiguration();
			RequiredServiceInfo cs = getReqService(rs.getName(), getSelectedConfiguration());
			if (columnIndex == 3 &&
				cs == null &&
				conf != null)
			{
				cs = new RequiredServiceInfo();
				cs.setName(rs.getName());
				conf.addRequiredService(cs);
			}
			switch (columnIndex)
			{
				case 0:
				default:
				{
					if (!value.equals(getValueAt(rowIndex, columnIndex)))
					{
						String newname = createFreeName((String) value, new RSContains());
						ConfigurationInfo[] confs = getModelInfo().getConfigurations();
						for (ConfigurationInfo itconf : confs)
						{
							RequiredServiceInfo itcs = getReqService(rs.getName(), itconf);
							itcs.setName(newname);
						}
						rs.setName(newname);
						getModelInfo().setRequiredServices(getModelInfo().getRequiredServices());
					}
					break;
				}
				case 1:
				{
//					ClassInfo type = nullifyString(value) != null? new ClassInfo(nullifyString(value)) : null;
					rs.setType((ClassInfo)value);
					break;
				}
				case 2:
				{
					rs.setMultiple((Boolean) value);
					break;
				}
				case 3:
				{
					String val = ((String) value).substring(0, 1);
					val = ((String) value).replaceFirst(val, val.toLowerCase());
					if (cs != null)
					{
						createBinding(cs);
						cs.getDefaultBinding().setScope(val);
						if (compareService(rs, cs))
						{
							conf.removeRequiredService(cs);
						}
					}
					else
					{
						createBinding(rs);
						rs.getDefaultBinding().setScope(val);
						for (ConfigurationInfo itconf : getModelInfo().getConfigurations())
						{
							cs = getReqService(rs.getName(), itconf);
							if (compareService(rs, cs))
							{
								itconf.removeRequiredService(cs);
							}
						}
					}
				}
			}
		}
	}
	
	protected class RSContains implements IFilter<String>
	{
		public boolean filter(String obj)
		{
			RequiredServiceInfo[] rsi = getModelInfo().getRequiredServices();
			for (int i = 0; i < rsi.length; ++i)
			{
				if (obj.equals(rsi[i].getName()))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	protected class PSContains implements IFilter<String>
	{
		public boolean filter(String obj)
		{
			ProvidedServiceInfo[] psi = getModelInfo().getProvidedServices();
			for (int i = 0; i < psi.length; ++i)
			{
				if (obj.equals(psi[i].getName()))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 *  Configuration containment filter.
	 */
	protected static final class ConfigurationContains implements IFilter<String>
	{
		/** The list. */
		protected List<ConfigurationInfo> confs;
		
		/**
		 *  Creates a new filter.
		 *  @param confs The configurations.
		 */
		public ConfigurationContains(List<ConfigurationInfo> confs)
		{
			this.confs = confs;
		}
		
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(String obj)
		{
			for (ConfigurationInfo conf : confs)
			{
				if (conf.getName().equals(obj))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 *  Parameter cache item.
	 */
	protected static final class CachedParameter
	{
		/** Parameter name. */
		public String name;
		
		/** Parameter argument flag. */
		public boolean arg;
		
		/** Parameter result flag. */
		public boolean res;
		
		/** Parameter description. */
		public String desc;
		
		/** Parameter type. */
		public String type;
		
		/** Parameter initial values. */
		public Map<String, String> inivals;
		
		/**
		 * 
		 */
		public CachedParameter(String name, boolean arg, boolean res, String desc, String type)
		{
			this.name = name;
			this.arg = arg;
			this.res = res;
			this.desc = desc;
			this.type = type;
			this.inivals = new HashMap<String, String>();
		}
	}
	
	/**
	 * Creates the service implementation if null. 
	 * @param s The service.
	 */
	private static final void createImplementation(ProvidedServiceInfo s)
	{
		if (s.getImplementation() == null)
		{
			s.setImplementation(new ProvidedServiceImplementation());
		}
	}
	
	/**
	 * Creates the service binding if null. 
	 * @param s The service.
	 */
	private static final void createBinding(RequiredServiceInfo s)
	{
		if (s.getDefaultBinding() == null)
		{
			s.setDefaultBinding(new RequiredServiceBinding());
		}
	}
	
	/**
	 *  Compares regular service with configuration service.
	 *  
	 *  @param s Regular service.
	 *  @param cs Configuration service.
	 *  @return True, if equal.
	 */
	private static final boolean compareService(RequiredServiceInfo s, RequiredServiceInfo cs)
	{
		if (s == null || cs == null)
		{
			return s == cs;
		}
		
		if (s.getDefaultBinding() == null || cs.getDefaultBinding() == null)
		{
			return s.getDefaultBinding() == cs.getDefaultBinding();
		}
		
		if (s.getDefaultBinding().getScope() == null || cs.getDefaultBinding().getScope() == null)
		{
			return s.getDefaultBinding() == cs.getDefaultBinding();
		}
		
		return s.getDefaultBinding().getScope().equals(cs.getDefaultBinding().getScope());
	}
	
	/**
	 *  Compares regular service with configuration service.
	 *  
	 *  @param s Regular service.
	 *  @param cs Configuration service.
	 *  @return True, if equal.
	 */
	private static final boolean compareService(ProvidedServiceInfo s, ProvidedServiceInfo cs)
	{
		if (s == null || cs == null)
		{
			return s == cs;
		}
		
		if (s.getImplementation() == null || cs.getImplementation() == null)
		{
			return s.getImplementation() == cs.getImplementation();
		}
		
		if (s.getImplementation().getProxytype() == null || cs.getImplementation().getProxytype() == null)
		{
			return s.getImplementation().getProxytype() == cs.getImplementation().getProxytype();
		}
		
		if (!(s.getImplementation().getProxytype().equals(cs.getImplementation().getProxytype())))
		{
			return false;
		}
		
		if (s.getImplementation().getClazz() == null || cs.getImplementation().getClazz() == null)
		{
			return s.getImplementation().getClazz() == cs.getImplementation().getClazz();
		}
		
		if (s.getImplementation().getClazz().getTypeName() == null || cs.getImplementation().getClazz().getTypeName() == null)
		{
			return s.getImplementation().getClazz().getTypeName() == cs.getImplementation().getClazz().getTypeName();
		}
		
		return s.getImplementation().getClazz().getTypeName().equals(cs.getImplementation().getClazz().getTypeName());
	}
	
	/**
	 *  Finds a service.
	 *  
	 * 	@param name The name.
	 * 	@param conf The configuration.
	 * 	@return The service.
	 */
	private static final ProvidedServiceInfo getProvService(String name, ConfigurationInfo conf)
	{
		if (conf == null)
		{
			return null;
		}
		
		ProvidedServiceInfo[] services = conf.getProvidedServices();
		
		if (services == null)
		{
			return null;
		}
		
		for (int i = 0; i < services.length; ++i)
		{
			if (name.equals(services[i].getName()))
			{
				return services[i];
			}
		}
		
		return null;
	}
	
	/**
	 *  Finds a service.
	 *  
	 * 	@param name The name.
	 * 	@param conf The configuration.
	 * 	@return The service.
	 */
	private static final RequiredServiceInfo getReqService(String name, ConfigurationInfo conf)
	{
		if (conf == null)
		{
			return null;
		}
		
		RequiredServiceInfo[] services = conf.getRequiredServices();
		
		if (services == null)
		{
			return null;
		}
		
		for (int i = 0; i < services.length; ++i)
		{
			if (name.equals(services[i].getName()))
			{
				return services[i];
			}
		}
		
		return null;
	}
}
