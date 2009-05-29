package jadex.tools.starter;

import jadex.adapter.base.appdescriptor.ApplicationModel;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IApplicationFactory;
import jadex.bridge.IArgument;
import jadex.bridge.IContext;
import jadex.bridge.IContextService;
import jadex.bridge.ILibraryService;
import jadex.bridge.ILoadableElementModel;
import jadex.bridge.IReport;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.IChangeListener;
import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.tools.common.ElementPanel;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.JValidatorTextField;
import jadex.tools.common.ParserValidator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The starter gui allows for starting agents platform independently.
 */
public class StarterPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"Browse", SGUI.makeIcon(StarterPanel.class,	"/jadex/tools/common/images/dots_small.png"),
	});

	//-------- attributes --------

	/** The model. */
	protected ILoadableElementModel model;

	/** The last loaded filename. */
	protected String lastfile;

	//-------- gui widgets --------

	/** The filename. */
	protected JTextField filename;

	/** The file chooser. */
	protected JFileChooser filechooser;

	/** The configuration. */
	protected JComboBox config;

	/** The agent type. */
	protected JTextField agentname;
	protected JLabel agentnamel;

	/** The application name. */
	protected JComboBox appname;
	protected JLabel appnamel;
	protected DefaultComboBoxModel appmodel;
	
	protected JLabel confl;
	protected JLabel confdummy = new JLabel("Configuration"); // Hack! only for reading sizes
	protected JLabel filenamel;
	
	/** The agent name generator flag. */
	protected JCheckBox genagentname;

	/** The agent type. */
	protected JPanel arguments;
	protected List argelems;

	/** The start button. */
	protected JButton start;

	/** The description panel. */
	protected ElementPanel modeldesc;

	/** The agent specific panel. */
	protected JPanel agentpanel;
	
	/** The application specific panel. */
	protected JPanel apppanel;
	
	/** The agent factory. */
	protected IAgentFactory agentfactory;
	
	/** The application factory. */
	protected IApplicationFactory appfactory;
	
	/** The starter plugin. */
	protected StarterPlugin	starter;

	/** The spinner for the number of agents to start. */
	protected JSpinner numagents;
	
	//-------- constructors --------

	/**
	 * Open the GUI.
	 * @param starter The starter.
	 */
	public StarterPanel(final StarterPlugin starter)
	{
		super(new BorderLayout());
		this.starter	= starter;

		JPanel content = new JPanel(new GridBagLayout());

	   	// The browse button.
		//final JButton browse = new JButton("browse...");
		final JButton browse = new JButton(icons.getIcon("Browse"));
		browse.setToolTipText("Browse via file requester to locate a model");
		browse.setMargin(new Insets(0,0,0,0));
		// Create the filechooser.
		// Hack!!! might trhow exception in applet / webstart
		agentfactory = starter.getJCC().getAgent().getPlatform().getAgentFactory();
		appfactory = starter.getJCC().getAgent().getPlatform().getApplicationFactory();
		try
		{
			filechooser = new JFileChooser(".");
			filechooser.setAcceptAllFileFilterUsed(true);
			javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter()
			{
				public String getDescription()
				{
					return "ADFs";
				}

				public boolean accept(File f)
				{
					String name = f.getName();
//					return f.isDirectory() || name.endsWith(SXML.FILE_EXTENSION_AGENT) || name.endsWith(SXML.FILE_EXTENSION_CAPABILITY);
					boolean	ret	= f.isDirectory() || agentfactory.isLoadable(name) || appfactory.isLoadable(name);

//					Thread.currentThread().setContextClassLoader(oldcl);

					return ret;
				}
			};
			filechooser.addChoosableFileFilter(filter);
		}
		catch(SecurityException e)
		{
			browse.setEnabled(false);
		}
		browse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(filechooser.showDialog(SGUI.getWindowParent(StarterPanel.this)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
					File file = filechooser.getSelectedFile();
					String	model	= file!=null ? ""+file : null;

//					if(file!=null && file.getName().endsWith(".jar"))
//					{
//						// Start looking into the jar-file for description-files
//						try
//						{
//							DynamicURLClassLoader.addURLToInstance(new URL("file", "", file.toString()));
//
//							JarFile jarFile = new JarFile(file);
//							Enumeration e = jarFile.entries();
//							java.util.List	models	= new ArrayList();
//							while (e.hasMoreElements())
//							{
//								ZipEntry jarFileEntry = (ZipEntry) e.nextElement();
//								if(SXML.isJadexFilename(jarFileEntry.getName()))
//								{
//									models.add(jarFileEntry.getName());
//								}
//							}
//							jarFile.close();
//
//							if(models.size()>1)
//							{
//								Object[]	choices	= models.toArray(new String[models.size()]);
//								JTreeDialog td = new JTreeDialog(
//									null,
////									(Frame)StarterGui.this.getParent(),
//									"Select Model", true,
//									"Select an model to load:",
//									(String[])choices, (String)choices[0]);
//								td.setVisible(true);
//								model = td.getResult();
//							}
//							else if(models.size()==1)
//							{
//								model	= (String)models.get(0);
//							}
//							else
//							{
//								model	= null;
//							}
//						}
//						catch(Exception e)
//						{
//							//e.printStackTrace();
//						}
//					}

					//System.out.println("... load model: "+model);
//					lastfile	= model;
					loadModel(model);
				}
			}
		});

		// Create the filename combo box.
		filename = new JTextField();
		filename.setEditable(true);
		ActionListener filelistener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				loadModel(filename.getText());
			}
		};
		filename.addActionListener(filelistener);

		// The configuration.
		config = new JComboBox();
		config.setToolTipText("Choose the configuration to start with");
		config.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				refreshArguments();
			}
		});

		// The agent name.
		agentname = new JTextField();
		
		// The application name.
		appmodel = new DefaultComboBoxModel();
		appmodel.addElement("");
		IContextService cs = (IContextService)starter.getJCC()
			.getAgent().getPlatform().getService(IContextService.class);
		cs.addContextListener(new IChangeListener()
		{
			public void changeOccurred(jadex.commons.ChangeEvent event)
			{
				if(IContextService.EVENT_TYPE_CONTEXT_CREATED.equals(event.getType()))
				{
					appmodel.addElement(((IContext)event.getValue()).getName());
				}
				else if(IContextService.EVENT_TYPE_CONTEXT_DELETED.equals(event.getType()))
				{
					appmodel.removeElement(((IContext)event.getValue()).getName());
				}
			}
		});
		appname = new JComboBox();

		// The generate flag for the agentname;
		genagentname = new JCheckBox("Auto generate", false);
		genagentname.setToolTipText("Auto generate the agent instance name");
		genagentname.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agentname.setEditable(!genagentname.isSelected());
				numagents.setEnabled(genagentname.isSelected());
			}
		});
		
		numagents = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		((JSpinner.DefaultEditor)numagents.getEditor()).getTextField().setColumns(4);
		
		// The arguments.
		arguments = new JPanel(new GridBagLayout());

		// The reload button.
		final JButton reload = new JButton("Reload");
		reload.setToolTipText("Reload the current model");
		reload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				reloadModel(lastfile);
			}
		});

		int mw = (int)reload.getMinimumSize().getWidth();
		int pw = (int)reload.getPreferredSize().getWidth();
		int mh = (int)reload.getMinimumSize().getHeight();
		int ph = (int)reload.getPreferredSize().getHeight();

		// The start button.
		this.start = new JButton("Start");
		start.setToolTipText("Start selected model");
		start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(model!=null)
				{
					String configname = (String)config.getModel().getSelectedItem();
					Map args = SCollection.createHashMap();
					String errortext = null;
					for(int i=0; i<argelems.size(); i++)
					{
						String argname = ((JLabel)arguments.getComponent(i*4+1)).getText();
						String argval = ((JTextField)arguments.getComponent(i*4+3)).getText();
						if(argval.length()>0)
						{
							Object arg = null;
							try
							{
								ILibraryService ls = (ILibraryService)StarterPanel.this.starter.getJCC().getAgent().getPlatform().getService(ILibraryService.class);
								arg = new JavaCCExpressionParser().parseExpression(argval, null, null, ls.getClassLoader()).getValue(null);
							}
							catch(Exception e)
							{
								if(errortext==null)
									errortext = "Error within argument expressions:\n";
								errortext += argname+" "+e.getMessage()+"\n";
							}
							args.put(argname, arg);
						}
					}
					if(errortext!=null)
					{
						JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), errortext, 
							"Display Problem", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						if(model instanceof ApplicationModel)
						{
							IApplicationFactory fac = starter.getJCC().getAgent().getPlatform().getApplicationFactory();
							try
							{
								fac.createApplication((String)appname.getSelectedItem(), filename.getText(), configname, args);
							}
							catch(Exception e)
							{
								e.printStackTrace();
								JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), "Could not start application: "+e, 
									"Application Problem", JOptionPane.INFORMATION_MESSAGE);
							}
						}
						else
						{
							IApplicationContext ac = null;
							final String apn = (String)appname.getSelectedItem();
							if(apn!=null && apn.length()>0)
							{
								IContextService cs = (IContextService)starter.getJCC().getAgent().getPlatform().getService(IContextService.class);
								if(cs!=null)
								{
									ac = (IApplicationContext)cs.getContext(apn);
								}
							}	
							String typename = ac!=null? ac.getAgentType(filename.getText()): filename.getText();
							if(typename==null)
							{
								JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), "Could not resolve agent type: "
									+filename.getText()+"\n in application: "+ac.getName(), 
									"Agent Type Problem", JOptionPane.INFORMATION_MESSAGE);
							}
							else
							{
								String an = genagentname.isSelected()?  null: agentname.getText();
								if(an==null) // i.e. name auto generate
								{
									int max = ((Integer)numagents.getValue()).intValue();
									for(int i=0; i<max; i++)
									{
										if(ac!=null)
											ac.createAgent(an, typename, configname, args, true, false, null, null);
										else
											starter.getJCC().createAgent(typename, an, configname, args);
									}
								}
								else
								{
									if(ac!=null)
										ac.createAgent(an, typename, configname, args, true, false, null, null);
									else
										starter.getJCC().createAgent(typename, an, configname, args);
								}
							}
						}
					}
				}
			}
		});
		start.setMinimumSize(new Dimension(mw, mh));
		start.setPreferredSize(new Dimension(pw, ph));

		// The reset button.
		final JButton reset = new JButton("Reset");
		reset.setToolTipText("Reset all fields");
		reset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				reset();
			}
		});
		reset.setMinimumSize(new Dimension(mw, mh));
		reset.setPreferredSize(new Dimension(pw, ph));

		// The description panel.
		modeldesc = new ElementPanel("Description", null);
		ChangeListener desclistener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent ce)
			{
				Object id = modeldesc.getId(modeldesc.getSelectedComponent());
				if(id instanceof String)
				{
					//System.out.println("SystemEvent: "+id);
					loadModel((String)id);
					updateGuiForNewModel((String)id);
				}
			}
		};
		modeldesc.addChangeListener(desclistener);
		modeldesc.setMinimumSize(new Dimension(200, 150));
		modeldesc.setPreferredSize(new Dimension(400, 150));

		// Avoid panel being not resizeable when long filename is displayed
		filename.setMinimumSize(filename.getMinimumSize());

		int y = 0;
	
		agentpanel = new JPanel(new GridBagLayout());
		agentnamel = new JLabel("Agent name");
		agentpanel.add(agentnamel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 2), 0, 0));
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(agentname, BorderLayout.CENTER);
		JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.add(genagentname, BorderLayout.WEST);
		tmp2.add(numagents, BorderLayout.EAST);
		tmp.add(tmp2, BorderLayout.EAST);
		agentpanel.add(tmp, new GridBagConstraints(1, 0, 4, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.BOTH, new Insets(0, 2, 0, 2), 0, 0));
		
		apppanel = new JPanel(new GridBagLayout());
		appnamel = new JLabel("Application name");
		apppanel.add(appnamel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 2), 0, 0));
		apppanel.add(appname, new GridBagConstraints(1, 0, 4, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.BOTH, new Insets(0, 2, 0, 2), 0, 0));
		
		JPanel upper = new JPanel(new GridBagLayout());
		upper.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Settings "));
		filenamel = new JLabel("Filename");
		upper.add(filenamel, new GridBagConstraints(0, y, 1, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		upper.add(filename, new GridBagConstraints(1, y, 3, 1, 1, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		upper.add(browse, new GridBagConstraints(4, y, 1, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		y++;
		confl = new JLabel("Configuration");
		upper.add(confl, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		upper.add(config, new GridBagConstraints(1, y, 4, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		y++;
		upper.add(agentpanel, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		y++;
		upper.add(apppanel, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		y = 0;
		content.add(upper, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		y++;
		content.add(arguments, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		agentnamel.setMinimumSize(confl.getMinimumSize());
		agentnamel.setPreferredSize(confl.getPreferredSize());

		/*y++;
		agentnamel = new JLabel("Agent name");
		content.add(agentnamel, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(agentname, "Center");
		tmp.add(genagentname, "East");
		//content.add(agentname, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.WEST,
		//			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		content.add(tmp, new GridBagConstraints(1, y, 4, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		y++;
		argumentsl = new JLabel("Arguments");
		content.add(argumentsl, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		content.add(arguments, new GridBagConstraints(1, y, 4, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));*/

		/*y++;
		content.add(new JButton("1"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		content.add(new JButton("2"), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		content.add(new JButton("3"), new GridBagConstraints(2, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		content.add(new JButton("4"), new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		content.add(new JButton("5"), new GridBagConstraints(4, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
*/
		JPanel buts = new JPanel(new GridBagLayout());
		buts.add(start, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));
		buts.add(reload, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));
		buts.add(reset, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0));

		HelpBroker hb = GuiProperties.setupHelp(this, "tools.starter");
		if(hb!=null)
		{
			JButton help = new JButton("Help");
			help.setToolTipText("Activate JavaHelp system");
			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
			help.setMinimumSize(new Dimension(mw, mh));
			help.setPreferredSize(new Dimension(pw, ph));
			buts.add(help, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(2, 2, 2, 2), 0, 0));
		}

		//content.add(prodmode, new GridBagConstraints(3, 4, 1, 1, 1, 0,
		//	GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		y++;
		content.add(buts, new GridBagConstraints(0, y, 5, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
			new Insets(2, 2, 2, 2), 0, 0));

		y++;
		content.add(modeldesc, new GridBagConstraints(0, y, 5, 1, 1, 1, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		this.add("Center", content);
	}

	/**
	 *  Reload the model.
	 *  @param adf The adf.
	 */
	public void reloadModel(String adf)
	{
		if(lastfile==null)
			return;
		
		// todo: remove this hack
//		String cachename = lastfile.substring(0, lastfile.length()-3)+"cam";
//		SXML.clearModelCache(cachename);
		
		String toload = lastfile;
		lastfile = null;
		loadModel(toload);
	}
	
	/**
	 *  Load an agent model.
	 *  @param adf The adf to load.
	 */
	public void loadModel(final String adf)
	{
		// Don't load same model again (only on reload).
		if(adf!=null && adf.equals(lastfile))
			return;
		
		//System.out.println("loadModel: "+adf+" "+modelname.getActionListeners().length+" "+SUtil.arrayToString(modelname.getActionListeners()));

		String	error	= null;
		if(adf!=null)
		{
//			ClassLoader	oldcl	= Thread.currentThread().getContextClassLoader();
//			if(starter.getModelExplorer().getClassLoader()!=null)
//				Thread.currentThread().setContextClassLoader(starter.getModelExplorer().getClassLoader());

			try
			{
				if(appfactory.isLoadable(adf))
				{
					model = appfactory.loadModel(adf);
//					System.out.println("Model loaded: "+adf);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							updateGuiForNewModel(adf);
						}
					});
					createArguments();
					arguments.setVisible(false);
					apppanel.setVisible(true);
					agentpanel.setVisible(false);
					start.setVisible(true);
					
					filenamel.setMinimumSize(appnamel.getMinimumSize());
					filenamel.setPreferredSize(appnamel.getPreferredSize());
					confl.setMinimumSize(appnamel.getMinimumSize());
					confl.setPreferredSize(appname.getPreferredSize());
				}
				else if(agentfactory.isStartable(adf))
				{
					model = agentfactory.loadModel(adf);
	//				System.out.println("Model loaded: "+adf);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							updateGuiForNewModel(adf);
						}
					});
					createArguments();
					apppanel.setVisible(true);
					arguments.setVisible(true);
					agentpanel.setVisible(true);
					start.setVisible(true);
					
					filenamel.setMinimumSize(confdummy.getMinimumSize());
					filenamel.setPreferredSize(confdummy.getPreferredSize());
					confl.setMinimumSize(confdummy.getMinimumSize());
					confl.setPreferredSize(confdummy.getPreferredSize());
				}
				else if(agentfactory.isLoadable(adf))
				{
					model = agentfactory.loadModel(adf);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							updateGuiForNewModel(adf);
						}
					});
					apppanel.setVisible(false);
					arguments.setVisible(false);
					agentpanel.setVisible(false);
					start.setVisible(false);
					
					agentnamel.setMinimumSize(confdummy.getMinimumSize());
					agentnamel.setPreferredSize(confdummy.getPreferredSize());
					confl.setMinimumSize(confdummy.getMinimumSize());
					confl.setPreferredSize(confdummy.getPreferredSize());
				}
				else
				{
					model = null;
				}
				lastfile = adf;
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				model = null;
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				error	= sw.toString();
			}
			
//			Thread.currentThread().setContextClassLoader(oldcl);
		}
		else
		{
			model	= null;
		}

		if(model==null)
		{
			start.setEnabled(false);
			config.removeAllItems();
			clearArguments();
			setAgentName("");
			clearApplicationName();
			filename.setText("");
			if(error!=null)
				modeldesc.addTextContent("Error", null, "No model loaded:\n"+error, "error");
			else
				modeldesc.addTextContent("Model", null, "No model loaded.", "model");
		}
	}

	/**
	 *  Update the GUI for a new model.
	 *  @param adf The adf.
	 */
	void updateGuiForNewModel(final String adf)
	{
		if(model==null)
			return;
		
//		ClassLoader	oldcl	= Thread.currentThread().getContextClassLoader();
//		if(starter.getModelExplorer().getClassLoader()!=null)
//			Thread.currentThread().setContextClassLoader(starter.getModelExplorer().getClassLoader());
		
//		System.out.println("updategui "+model);
		
		filename.setText(adf);

//		if(model.getName()!=null && SXML.isAgentFilename(adf))
		if(model.getName()!=null && model instanceof ApplicationModel)
		{
			appname.setModel(new DefaultComboBoxModel(new String[]{model.getName()}));
			appname.setEditable(true);
		}
		else if(model.isStartable())
		{
			appname.setModel(appmodel);
			agentname.setText(model.getName());
//			appname.removeAllItems();
//			appname.addItem("");
//			appname.setSelectedItem("");
//			IContextService cs = (IContextService)starter.getJCC().getAgent().getPlatform().getService(IContextService.class);
//			if(cs!=null)
//			{
//				IContext[] contexts =  cs.getContexts(IApplicationContext.class);
//				for(int i=0; contexts!=null && i<contexts.length; i++)
//				{
//					appname.addItem(contexts[i].getName());
//				}
//			}
			appname.setEditable(false);
		}
		else
		{
			agentname.setText("");
			appname.setEditable(true);
		}
		
		lastfile = model.getFilename();

		ItemListener[] lis = config.getItemListeners();
		for(int i=0; i<lis.length; i++)
			config.removeItemListener(lis[i]);
		config.removeAllItems();
		
		// Add all known agent configuration names to the config chooser.
		
		String[] confignames = model.getConfigurations();
		for(int i = 0; i<confignames.length; i++)
		{
			((DefaultComboBoxModel)config.getModel()).addElement(confignames[i]);
		}
		if(confignames.length>0)
			config.getModel().setSelectedItem(confignames[0]);
		
		/*IMConfiguration[] states = model.getConfigurationbase().getConfigurations();
		for(int i = 0; i<states.length; i++)
		{
			((DefaultComboBoxModel)config.getModel()).addElement(states[i].getName());
		}
		IMConfiguration defstate = model.getConfigurationbase().getDefaultConfiguration();
		if(defstate!=null)
		{
			config.getModel().setSelectedItem(defstate.getName());
		}*/

		//		if(modeldesc.getSelectedComponent()==null
		//			|| !modeldesc.getId(modeldesc.getSelectedComponent()).equals(adf))
		{
			String clazz = SReflect.getInnerClassName(model.getClass());
			if(clazz.endsWith("Data")) clazz = clazz.substring(0, clazz.length()-4);

			final IReport report = model.getReport();
			if(report!=null && !report.isEmpty())
			{
				final Icon icon = GuiProperties.getElementIcon(clazz+"_broken");
				try
				{
					modeldesc.addHTMLContent(model.getName(), icon, report.toHTMLString(), adf, report.getDocuments());
				}
				catch(final Exception e)
				{
					//e.printStackTrace();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							String text = SUtil.wrapText("Could not display HTML content: "+e.getMessage());
							JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), text, "Display Problem", JOptionPane.INFORMATION_MESSAGE);
							modeldesc.addTextContent(model.getName(), icon, report.toString(), adf);
						}
					});
				}
			}
			else
			{
				final Icon icon = GuiProperties.getElementIcon(clazz);
				try
				{
					modeldesc.addHTMLContent(model.getName(), icon, model.getDescription(), adf, null);
				}
				catch(final Exception e)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							String text = SUtil.wrapText("Could not display HTML content: "+e.getMessage());
							JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), text, "Display Problem", JOptionPane.INFORMATION_MESSAGE);
							modeldesc.addTextContent(model.getName(), icon, model.getDescription(), adf);
						}
					});
				}
			}

			// Adjust state of start button depending on model checking state.
//			start.setEnabled(SXML.isAgentFilename(adf) && (report==null || report.isEmpty()));
			start.setEnabled(model.isStartable() && (report==null || report.isEmpty()));
		
			for(int i=0; i<lis.length; i++)
				config.addItemListener(lis[i]);
		}
//		Thread.currentThread().setContextClassLoader(oldcl);
	}

	/**
	 *  Get the properties.
	 *  @param props The properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		
		String m = filename.getText();
		if(m!=null) props.addProperty(new Property("model", m));

		String c = (String)config.getSelectedItem();
		if(c!=null) props.addProperty(new Property("config", c));

		props.addProperty(new Property("autogenerate", ""+genagentname.isSelected()));
		
		props.addProperty(new Property("name", agentname.getText()));
		for(int i=0; argelems!=null && i<argelems.size(); i++)
		{
			JTextField valt = (JTextField)arguments.getComponent(i*4+3);
			props.addProperty(new Property("argument", valt.getText()));
		}
		
		return props;
	}

	/**
	 *  Set the properties.
	 *  @param props The propoerties.
	 */
	protected void setProperties(Properties props)
	{
		String mo = props.getStringProperty("model");
		if(mo!=null)
		{
			loadModel(mo);
			selectConfiguration(props.getStringProperty("config"));
		}
		Property[]	aargs	= props.getProperties("argument");
		String[] argvals = new String[aargs.length];
		for(int i=0; i<aargs.length; i++)
		{
			argvals[i] = aargs[i].getValue();
		}
		setArguments(argvals);

		setAgentName(props.getStringProperty("name"));
		setAutoGenerate(props.getBooleanProperty("autogenerate"));
		
	}

	/**
	 *  Reset the gui.
	 */
	public void reset()
	{
		filename.setText("");
		modeldesc.removeAll();
		loadModel(null);
		config.removeAllItems();
		clearArguments();
		setAgentName("");
		//model = null;
		//start.setEnabled(false);
	}

	/**
	 *  Select a configuration.
	 *  @param conf The configuration.
	 */
	protected void selectConfiguration(final String conf)
	{
		if(conf!=null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					//System.out.println("selecting: "+conf+" "+config.getModel().getSize());
					config.getModel().setSelectedItem(conf);
				}
			});
		}
	}

	/**
	 *  Set the arguments.
	 *  @param args The arguments.
	 */
	protected void setArguments(final String[] args)
	{
		if(args!=null && args.length>0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if(arguments==null || argelems==null || arguments.getComponentCount()!=4*argelems.size())
						return;
					
					for(int i=0; i<args.length; i++)
					{
						JTextField valt = (JTextField)arguments.getComponent(i*4+3);
						valt.setText(args[i]);
					}
				}
			});
		}
	}
	
	/**
	 *  Refresh the argument values.
	 *  Called only from gui thread.
	 */
	protected void refreshArguments()
	{
		// Assert that all argument components are there.
		if(arguments==null || argelems==null || arguments.getComponentCount()!=4*argelems.size())
			return;
		
		for(int i=0; argelems!=null && i<argelems.size(); i++)
		{
			JTextField valt = (JTextField)arguments.getComponent(i*4+2);
//			String val  = findValue((IMReferenceableElement)argelems.get(i), (String)config.getSelectedItem());
			valt.setText(""+((IArgument)argelems.get(i)).getDefaultValue((String)config.getSelectedItem()));
			//valt.setMinimumSize(new Dimension(valt.getPreferredSize().width/4, valt.getPreferredSize().height/4));
		}
	}
	
	/**
	 *  Refresh the argument values.
	 */
	protected void clearArguments()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				// Assert that all argument components are there.
				if(arguments==null || argelems==null || arguments.getComponentCount()!=4*argelems.size())
					return;
				
				for(int i=0; i<argelems.size(); i++)
				{
					JTextField valt = (JTextField)arguments.getComponent(i*4+3);
					valt.setText("");
				}
			}
		});
	}
	
	/**
	 *  Create the arguments panel.
	 */
	protected void createArguments()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				argelems = SCollection.createArrayList();
				arguments.removeAll();
				arguments.setBorder(null);
				
				IArgument[] args = model.getArguments();
				
				for(int i=0; i<args.length; i++)
				{
					argelems.add(args[i]);
					createArgumentGui(args[i], i);
				}
				
				if(args.length>0)
					arguments.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Arguments "));
			}
		});
	}
	
	/**
	 *  Create the gui for one argument. 
	 *  @param arg The belief or belief reference.
	 *  @param y The row number where to add.
	 */
	protected void createArgumentGui(final IArgument arg, int y)
	{
		JLabel namel = new JLabel(arg.getName());
		final JValidatorTextField valt = new JValidatorTextField(15);
		
		// todo:
		ILibraryService ls = (ILibraryService)StarterPanel.this.starter.getJCC().getAgent().getPlatform().getService(ILibraryService.class);
		valt.setValidator(new ParserValidator(ls.getClassLoader()));
		
		String configname = (String)config.getSelectedItem();
		JTextField mvalt = new JTextField(""+arg.getDefaultValue(configname));
		// Java JTextField bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4247013
		//mvalt.setMinimumSize(new Dimension(mvalt.getPreferredSize().width/4, mvalt.getPreferredSize().height/4));
		mvalt.setEditable(false);
		
		/*Class	clazz = null;
		String	description	= null;
		IMReferenceableElement	myelem	= arg;
		while((clazz==null || description==null) && myelem instanceof IMBeliefReference)
		{
			IMBeliefReference	mbelref	= (IMBeliefReference)myelem;
			clazz	= clazz!=null ? clazz : mbelref.getClazz();
			description	= description!=null ? description : mbelref.getDescription();
			myelem	= ((IMBeliefReference)myelem).getReferencedElement();
		}
		if((clazz==null || description==null) && myelem instanceof IMBelief)
		{
			IMBelief	mbel	= (IMBelief)myelem;
			clazz	= clazz!=null ? clazz : mbel.getClazz();
			description	= description!=null ? description : mbel.getDescription();
		}*/
		
//		JLabel typel = new JLabel(clazz!=null ? SReflect.getInnerClassName(clazz) : "undefined");
		JLabel typel = new JLabel(arg.getTypename()!=null? arg.getTypename(): "undefined");
		
		String description = arg.getDescription();
		if(description!=null)
		{
			namel.setToolTipText(description);
			valt.setToolTipText(description);
			mvalt.setToolTipText(description);
//			typel.setToolTipText(description);
		}
		
		int x = 0;
		arguments.add(typel, new GridBagConstraints(x++, y, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		arguments.add(namel, new GridBagConstraints(x++, y, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		arguments.add(mvalt, new GridBagConstraints(x++, y, 1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		arguments.add(valt, new GridBagConstraints(x++, y, 1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		y++;
	}
	
	/**
	 *  Set the agent name.
	 *  @param name The name.
	 */
	protected void setAgentName(final String name)
	{
		if(name!=null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					agentname.setText(name);
				}
			});
		}
	}
	
	/**
	 *  Clear the application name.
	 *  @param name The name.
	 */
	protected void clearApplicationName()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				appname.removeAll();
			}
		});
	}

	/**
	 *  Set the auto generate in gui.
	 *  @param autogen The autogen property.
	 */
	protected void setAutoGenerate(final boolean autogen)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				genagentname.setSelected(autogen);
				agentname.setEditable(!autogen);
				numagents.setEnabled(autogen);
			}
		});
	}
	
	/**
	 *  Get the last loaded filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return lastfile;
	}

	/**
	 *  Main for testing only.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.getContentPane().add(new StarterPanel(null));
		f.pack();
		f.setVisible(true);
	}
}

