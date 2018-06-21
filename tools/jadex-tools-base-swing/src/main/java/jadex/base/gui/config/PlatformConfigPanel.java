package jadex.base.gui.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.SBootstrapLoader;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.xml.PropertiesXMLHelper;


/**
 *  A panel to configure and start a Jadex platform.
 */
public class PlatformConfigPanel	extends JPanel
{
//	/** The default factories for given extensions. */
//	public static Map<String, String>	FACTORIES;
//	
//	/** The default platform models. */
//	public static Set<String>	MODELS;
//	
//	static
//	{
//		// Todo: externalize/dynamically check available factories!?
//		FACTORIES	= new LinkedHashMap<String, String>();
//		FACTORIES.put(".component.xml", "jadex.component.ComponentComponentFactory");
//		FACTORIES.put("Agent.class", "jadex.micro.MicroAgentFactory");
//		FACTORIES.put(".agent.xml", "jadex.bdi.BDIAgentFactory");
//		FACTORIES.put(".bpmn", "jadex.bpmn.BpmnFactory");
//		FACTORIES.put(".application.xml", "jadex.application.ApplicationComponentFactory");
//		FACTORIES.put(".gpmn", "jadex.gpmn.GpmnFactory");
//
//		// Todo: externalize/dynamically check available platform models!?
//		MODELS	= new LinkedHashSet<String>();
//		MODELS.add("jadex.standalone.Platform.component.xml");
//		MODELS.add("jadex.standalone.PlatformAgent.class");
//		MODELS.add("jadex.standalone.Platform.bpmn");
//	}
	
	//-------- attributes --------
	
	/** The argument panel (stored for later removal). */
	protected Component	argpanel;
	
	/** The argument table model (stored for changing the configuration). */
	protected ArgumentTableModel	argmodel;
	
	/** The argument table model (stored for changing the configuration). */
	protected ClasspathPanel	classpath;
	
	/** The found factories (component file suffix -> factory class name). */
	protected Map<String, String>	factories;
	
	//-------- constructors --------
	
	/**
	 *  Create a platform config panel.
	 */
	public PlatformConfigPanel(final ClasspathPanel classpath)
	{
		super(new BorderLayout());
		this.classpath	= classpath;
		this.factories	= new LinkedHashMap<String, String>();
		
		final PropertiesPanel	modelpanel	= new PropertiesPanel("Platform Model");
//		final JComboBox	model	= modelpanel.createComboBox("Model", MODELS.toArray(new String[MODELS.size()]), true, 0);
//		final JComboBox	factory	= modelpanel.createComboBox("Factory", FACTORIES.values().toArray(new String[FACTORIES.size()]), true, 0);
		final JComboBox	model	= modelpanel.createComboBox("Model", SUtil.EMPTY_STRING_ARRAY, true, 0);
		final JComboBox	factory	= modelpanel.createComboBox("Factory", SUtil.EMPTY_STRING_ARRAY, true, 0);
		final JComboBox	config	= modelpanel.createComboBox("Configuration", SUtil.EMPTY_STRING_ARRAY, false, 0);
		config.setEnabled(false);
		JButton[] buts	= modelpanel.createButtons("buts", new String[]
		{
			"Load Model", "Scan for Factories",  "Scan for Models"
		}, 0);
		JButton[] buts1	= modelpanel.createButtons("buts", new String[]
		{
			"Load Settings...", "Save Settings...", "Start Platform"
		}, 0);
		
		model.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// When platform model is selected, choose appropriate factory, if any.
				for(String suffix: factories.keySet())
				{
					if(((String)model.getSelectedItem()).endsWith(suffix))
					{
						factory.setSelectedItem(factories.get(suffix));
						break;
					}
				}
			}
		});
		
		config.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(argmodel!=null)
				{
					argmodel.setConfiguration((String)config.getSelectedItem());
				}
			}
		});
		
		buts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				loadModel(model, factory, config)
					.addResultListener(new SwingDefaultResultListener<Void>(PlatformConfigPanel.this)
				{
					public void customResultAvailable(Void result)
					{
					}
				});
			}
		});
		
		buts[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Class<?>[]	facts	= classpath.scanForFactories();
				factories.clear();
				factory.removeAllItems();
				for(int i=0; i<facts.length; i++)
				{
					try
					{
						String[]	ctypes	= (String[])facts[i].getField("FILETYPES").get(null);
						for(int j=0; j<ctypes.length; j++)
						{
							factories.put(ctypes[j], facts[i].getName());
						}
						factory.addItem(facts[i].getName());
					}
					catch(Exception ex)
					{
						System.err.println("Error loading "+facts[i].getName()+" :"+ex);
//						ex.printStackTrace();
					}
				}
				
				System.out.println(factories);
			}
		});
		
		buts[2].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String[]	models	= classpath.scanForFiles(factories.keySet().toArray(new String[factories.size()]));
				model.removeAllItems();
				for(int i=0; i<models.length; i++)
				{
					model.addItem(models[i]);
				}
			}
		});
		
		final JFileChooser	fc	= new JFileChooser();
		fc.setFileFilter(new FileFilter()
		{
			public boolean accept(File file)
			{
				return file.isDirectory() || file.getName().endsWith(".launch.xml");
			}

			public String getDescription()
			{
				return "Jadex launch configurations (.launch.xml)";
			}
		});
		
		buts1[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int	ok	= fc.showOpenDialog(PlatformConfigPanel.this);
				if(ok==JFileChooser.APPROVE_OPTION)
				{
					try
					{
						String	filename	= fc.getSelectedFile().getPath();
						if(!filename.endsWith(".launch.xml"))
							filename	+= ".launch.xml";
						FileInputStream is = new FileInputStream(new File(filename));
						final Properties	props	= (Properties)PropertiesXMLHelper.read(is, getClass().getClassLoader());
						is.close();
						
						if(props.getSubproperty("Classpath")!=null)
						{
							classpath.setProperties(props.getSubproperty("Classpath"));
						}
						
						if(props.getStringProperty(IPlatformConfiguration.CONFIGURATION_FILE)!=null)
						{
							factory.setSelectedItem(props.getStringProperty(IPlatformConfiguration.COMPONENT_FACTORY));
							model.setSelectedItem(props.getStringProperty(IPlatformConfiguration.CONFIGURATION_FILE));
							
							loadModel(model, factory, config)
								.addResultListener(new SwingDefaultResultListener<Void>(PlatformConfigPanel.this)
							{
								public void customResultAvailable(Void result)
								{
									if(props.getProperty(IPlatformConfiguration.CONFIGURATION_NAME)!=null)
									{
										config.setSelectedItem(props.getStringProperty(IPlatformConfiguration.CONFIGURATION_NAME));
									}
									
									Property[]	argprops	= props.getProperties("argument");
									for(int i=0; i<argprops.length; i++)
									{
										argmodel.getArguments().put(argprops[i].getName(), argprops[i].getValue());
									}
									argmodel.fireTableDataChanged();
								}
							});
						}
					}
					catch(Exception ex)
					{
						SGUI.showError(PlatformConfigPanel.this, "Load Problem", "Warning: Could not load settings: "+ex, ex);
					}
				}
			}
		});
		
		buts1[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Properties	props	= new Properties();
				props.addProperty(new Property(IPlatformConfiguration.COMPONENT_FACTORY, (String)factory.getSelectedItem()));
				props.addProperty(new Property(IPlatformConfiguration.CONFIGURATION_FILE, (String)model.getSelectedItem()));
				
				if(config.isEnabled())
				{
					props.addProperty(new Property(IPlatformConfiguration.CONFIGURATION_NAME, (String)config.getSelectedItem()));					
				}
				
				if(argmodel!=null)
				{
					Map<String, String>	margs	= argmodel.getArguments();
					for(String arg: margs.keySet())
					{
						props.addProperty(new Property(arg, "argument", margs.get(arg)));											
					}
				}
				
				props.addSubproperties("Classpath", classpath.getProperties());
				
				int	ok	= fc.showOpenDialog(PlatformConfigPanel.this);
				if(ok==JFileChooser.APPROVE_OPTION)
				{
					try
					{
						String	filename	= fc.getSelectedFile().getPath();
						if(!filename.endsWith(".launch.xml"))
							filename	+= ".launch.xml";
						FileOutputStream os = new FileOutputStream(new File(filename));
						PropertiesXMLHelper.write(props, os, getClass().getClassLoader());
						os.close();
					}
					catch(Exception ex)
					{
						SGUI.showError(PlatformConfigPanel.this, "Save Problem", "Warning: Could not save settings: "+ex, ex);
					}
				}
			}
		});

		buts1[2].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				List<String>	args	= new ArrayList<String>();
				
				args.add("-"+IPlatformConfiguration.COMPONENT_FACTORY);
				args.add((String)factory.getSelectedItem());
				
				args.add("-"+IPlatformConfiguration.CONFIGURATION_FILE);
				args.add((String)model.getSelectedItem());
				
				if(config.isEnabled())
				{
					args.add("-"+IPlatformConfiguration.CONFIGURATION_NAME);
					args.add((String)config.getSelectedItem());					
				}
				
				if(argmodel!=null)
				{
					Map<String, String>	margs	= argmodel.getArguments();
					for(String arg: margs.keySet())
					{
						args.add("-"+arg);
						args.add(margs.get(arg));											
					}
				}
				
				try
				{
					Class<?> starterclass = SReflect.classForName(Starter.class.getName(), classpath.getClassLoader());
					starterclass.getMethod("createPlatform", new Class<?>[]{String[].class}).
						invoke(null, new Object[]{args.toArray(new String[args.size()])});
				}
				catch(Exception ex)
				{
					// TODO: handle exception
					ex.printStackTrace();
				}
//					.addResultListener(new SwingDefaultResultListener<IExternalAccess>(PlatformConfigPanel.this)
//				{
//					public void customResultAvailable(IExternalAccess result)
//					{
//					}
//				});
			}
		});

		this.add(modelpanel, BorderLayout.NORTH);
	}
	
	/**
	 *  Load a model.
	 */
	public IFuture<Void>	loadModel(JComboBox model, JComboBox factory, final JComboBox config)
	{
		final Future<Void>	ret	= new Future<Void>();
		SBootstrapLoader.loadModel(classpath.getClassLoader(), (String)model.getSelectedItem(), (String)factory.getSelectedItem())
			.addResultListener(new SwingExceptionDelegationResultListener<IModelInfo, Void>(ret)
		{
			public void customResultAvailable(final IModelInfo mi)
			{
				if(argpanel!=null)
				{
					PlatformConfigPanel.this.remove(argpanel);
				}
	
				config.removeAllItems();
				if(mi.getConfigurationNames().length>0)
				{
					for(int i=0; i<mi.getConfigurationNames().length; i++)
						config.addItem(mi.getConfigurationNames()[i]);
					config.setSelectedIndex(0);
					config.setEnabled(true);
				}
				else
				{
					config.setSelectedItem("");
					config.setEnabled(false);
				}
				
				argmodel	= new ArgumentTableModel(mi);
				JTable	argtable	= new JTable(argmodel);
				argtable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						
						JComponent	ret	= (JComponent)super.getTableCellRendererComponent(table, value, selected, focus, row, column);
//						ret.setEnabled(table.getModel().isCellEditable(row, column));
						ret.setBackground(UIManager.getColor(table.getModel().isCellEditable(row, column)
							? "TextField.background" : "Label.background"));
						ret.setFont(UIManager.getFont(column==0 ? "Label.font" : "TextField.font"));
						ret.setToolTipText(mi.getArguments()[row].getDescription());
						return ret;
					}
				});
				argpanel	= new JScrollPane(argtable);
				PlatformConfigPanel.this.add(argpanel, BorderLayout.CENTER);
				SGUI.getWindowParent(PlatformConfigPanel.this).pack();
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	//-------- main for testing --------
	
	/**
	 *  Open the GUI.
	 */
	public static void	main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				ClasspathPanel	classpath	= new ClasspathPanel();
				JTabbedPane	tabs	= new JTabbedPane();
				tabs.addTab("Platform Config", new PlatformConfigPanel(classpath));
				tabs.addTab("Classpath", classpath);
				
				JFrame	f	= new JFrame("Jadex Platform Configuration");
				f.getContentPane().add(tabs, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
			}
		});
	}
}
