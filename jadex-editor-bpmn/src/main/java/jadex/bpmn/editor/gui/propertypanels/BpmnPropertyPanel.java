package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;

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
import java.util.Set;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

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
	protected String[] PROVIDED_SERVICES_COLUMN_NAMES = { "Name", "Interface", "Proxytype", "Implementation" };
	
	/** The column names for the required services table. */
	protected String[] REQUIRED_SERVICES_COLUMN_NAMES = { "Name", "Interface", "Multiple", "Binding" };
	
	/** The proxy types. */
	protected String[] PROXY_TYPES = { BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED,
									   BasicServiceInvocationHandler.PROXYTYPE_DIRECT,
									   BasicServiceInvocationHandler.PROXYTYPE_RAW };
	
	/** Cache for handling configurations. */
	protected List<ConfigurationInfo> confcache;
	
	/** The configurations table. */
	protected JTable conftable;
	
	/** The configurations table. */
	protected JTable paramtable;
	
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
		textarea.setText(getModelInfo().getDescription() != null? getModelInfo().getDescription() : "");
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getModelInfo().setDescription(getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
		label = new JLabel("Package");
		textarea = new JTextArea();
		textarea.setText(getModelInfo().getPackage() != null? getModelInfo().getPackage() : "");
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getModelInfo().setPackage(getText(e.getDocument()));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
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
		
		setupImportsTable(tabpane);
		
		setupConfigurationsTable(tabpane);
		
		setupParametersTable(tabpane);
		
		setupPropertiesTable(tabpane);
		
		setupProvidedServicesTable(tabpane);
		
		add(tabpane, BorderLayout.CENTER);
	}
	
	/**
	 *  Initializes the imports table.
	 */
	protected void setupImportsTable(JTabbedPane tabpane)
	{
		JPanel tablepanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		final JTable importtable = new JTable(new ImportTableModel());
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
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(ImageProvider.getInstance(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
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
							modelcontainer.setDirty(true);
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
		Set<String> cvs = model.getContextVariables();
		for (String cvname : cvs)
		{
			String type = model.getContextVariableClass(cvname).getTypeName();
			CachedParameter cparam = new CachedParameter(cvname, false, false, null, type);
			
			for (ConfigurationInfo conf : getModelInfo().getConfigurations())
			{
				UnparsedExpression exp = model.getContextVariableExpression(cvname, conf.getName());
				if (exp != null)
				{
					cparam.inivals.put(conf.getName(), exp.getValue());
				}
			}
			
			UnparsedExpression exp = model.getContextVariableExpression(cvname, null);
			if (exp != null)
			{
				cparam.inivals.put(null, exp.getValue());
			}
			
			paramcche.put(cvname, cparam);
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
				cparam = new CachedParameter(res.getName(), true, false, res.getDescription(), type);
				
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
		final JTable proptable = new JTable(new PropertyTableModel());
		JScrollPane tablescrollpane = new JScrollPane(proptable);
		tablepanel.add(tablescrollpane, gc);
		
		Action addaction = new AbstractAction("Add Property")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = propertynames.size();
				String name = createFreeName("name", new BasePropertyPanel.MapContains(getModelInfo().getProperties()));
				getModelInfo().addProperty(new UnparsedExpression(name, "", "", null));
				propertynames.add(name);
				((PropertyTableModel) proptable.getModel()).fireTableRowsInserted(row, row);
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
		final JTable pstable = new JTable(new ProvidedServicesTableModel());
		JComboBox proxybox = new JComboBox(PROXY_TYPES);
		pstable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(proxybox));
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
				final ProvidedServiceInfo[] services = ((ProvidedServicesTableModel) pstable.getModel()).getSelectedServices();
				ProvidedServiceImplementation psimpl = new ProvidedServiceImplementation();
				psimpl.setProxytype(BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED);
				psimpl.setClazz(new ClassInfo(""));
				String name = createFreeName("name",
					new IFilter<String>()
					{
						public boolean filter(String obj)
						{
							for (int i = 0; i < services.length; ++i)
							{
								if (obj.equals(services[i].getName()))
								{
									return true;
								}
							}
							return false;
						}
					});
				ProvidedServiceInfo ps = new ProvidedServiceInfo();
				ps.setName(name);
				ps.setType(new ClassInfo("class"));
				ps.setImplementation(psimpl);
				
				ProvidedServiceInfo[] newservices = new ProvidedServiceInfo[services.length + 1];
				System.arraycopy(services, 0, newservices, 0, services.length);
				newservices[services.length] = ps;
				((ProvidedServicesTableModel) pstable.getModel()).setSelectedServices(newservices);
				((ProvidedServicesTableModel) pstable.getModel()).fireTableRowsInserted(services.length, services.length);
			}
		};
		Action removeaction = new AbstractAction("Remove Provided Services")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = pstable.getSelectedRows();
				
				Arrays.sort(ind);
				
				for (int i = ind.length - 1; i >= 0; --i)
				{
					ProvidedServicesTableModel model = (ProvidedServicesTableModel) pstable.getModel();
					List<ProvidedServiceInfo> services = SUtil.arrayToList(model.getSelectedServices());
					services.remove(i);
					model.setSelectedServices((ProvidedServiceInfo[]) services.toArray(new ProvidedServiceInfo[services.size()]));
					model.fireTableRowsDeleted(ind[i], ind[i]);
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
		tabpane.add(tablepanel, "Provided Services");
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
			getModel().addContextVariable(param.name, new ClassInfo(param.type), new UnparsedExpression(param.name, param.type, defval, null), param.inivals);
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
					System.out.println("Change");
					ConfigurationInfo cinfo = confcache.get(rowIndex);
					String oldname = cinfo.getName();
					String poollane = getModel().removePoolLane(oldname);
					cinfo.setName(createFreeName((String) value, new ConfigurationContains(confcache)));
					getModelInfo().setConfigurations((ConfigurationInfo[]) confcache.toArray(new ConfigurationInfo[confcache.size()]));
					getModel().addPoolLane(cinfo.getName(), poollane);
					
					for (String cvname : getModel().getContextVariables())
					{
						UnparsedExpression exp = getModel().getContextVariableExpression(cvname, oldname);
						if (exp != null)
						{
							getModel().setContextVariableExpression(cvname, oldname, null);
							getModel().setContextVariableExpression(cvname, cinfo.getName(), exp);
						}
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
			CachedParameter param = (CachedParameter) paramcche.get(rowIndex);
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
					return param.type;
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
			CachedParameter param = (CachedParameter) paramcche.get(rowIndex);
			removeParameter(param.name);
			switch(columnIndex)
			{
				case 0:
				default:
					param.name = createFreeName((String) value, new BasePropertyPanel.IndexMapContains(paramcche));
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
					param.type = (String) value;
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
					ret = prop.getClazz().getTypeName();
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
					getModelInfo().getProperties().remove(prop.getName());
					prop.setName(createFreeName((String) value, new MapContains(getModelInfo().getProperties())));
					getModelInfo().getProperties().put(prop.getName(), prop);
					propertynames.set(rowIndex, prop.getName());
					break;
				case 1:
					prop.setClazz(new ClassInfo(((String) value)));
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
			if (confname != null)
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
			return getSelectedServices().length;
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
			ProvidedServiceInfo ps = getSelectedServices()[rowIndex];
			switch (columnIndex)
			{
				case 0:
				default:
					return ps.getName();
				case 1:
					return ps.getType().getTypeName();
				case 2:
					return ps.getImplementation().getProxytype();
				case 3:
					return ps.getImplementation().getClazz().getTypeName();
			}
		}
		
		
		
		/**
		 *  Gets the currently selected services.
		 *  
		 *  @return The services.
		 */
		protected ProvidedServiceInfo[] getSelectedServices()
		{
			ConfigurationInfo conf = getSelectedConfiguration();
			ProvidedServiceInfo[] ret = null;
			
			if (conf == null)
			{
				ret = getModelInfo().getProvidedServices();
			}
			else
			{
				ret = conf.getProvidedServices();
			}
			
			return ret;
		}
		
		/**
		 *  Sets the currently selected services.
		 *  
		 *  @param services The services.
		 */
		protected void setSelectedServices(ProvidedServiceInfo[] services)
		{
			ConfigurationInfo conf = getSelectedConfiguration();
			
			if (conf == null)
			{
				getModelInfo().setProvidedServices(services);
			}
			else
			{
				conf.setProvidedServices(services);
			}
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
}
