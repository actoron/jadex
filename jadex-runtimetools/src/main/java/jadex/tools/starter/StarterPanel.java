package jadex.tools.starter;

import jadex.base.SComponentFactory;
import jadex.base.gui.ComponentSelectorDialog;
import jadex.base.gui.ElementPanel;
import jadex.base.gui.ParserValidator;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IErrorReport;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.FixedJComboBox;
import jadex.commons.Future;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.JValidatorTextField;
import jadex.commons.jtable.ClassRenderer;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.library.ILibraryService;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

/**
 * The starter gui allows for starting components platform independently.
 */
public class StarterPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"overlay_check", SGUI.makeIcon(StarterPanel.class, "/jadex/tools/common/images/overlay_check.png"),
		"Browse", SGUI.makeIcon(StarterPanel.class,	"/jadex/tools/common/images/dots_small.png"),
		"delete", SGUI.makeIcon(StarterPanel.class,	"/jadex/tools/common/images/delete_small.png")
	});

	//-------- attributes --------

	/** The model. */
	protected IModelInfo model;
	
	/** The error (last loading). */
	protected String error;

	/** The last loaded filename. */
	protected String lastfile;

	/** The selected parent (if any). */
	protected IComponentIdentifier	parent;

	//-------- gui widgets --------

	/** The filename. */
	protected JTextField filename;

//	/** The file chooser. */
//	protected JFileChooser filechooser;

	/** The configuration. */
	protected JComboBox config;

	/** The component type. */
	protected JTextField componentname;
	protected JLabel componentnamel;
	protected JTextField parenttf;

	/** The suspend mode. */
	protected JCheckBox suspend;
	
	/** The termination flags. */
	protected JCheckBox mastercb;
	protected JCheckBox daemoncb;
	protected JCheckBox autosdcb;

//	/** The application name. */
//	protected JComboBox appname;
//	protected JLabel appnamel;
//	protected DefaultComboBoxModel appmodel;
	
	protected JLabel confl;
	protected JLabel confdummy = new JLabel("Component Name"); // Hack! only for reading sizes
	protected JLabel filenamel;
	
	/** The component name generator flag. */
	protected JCheckBox genname;

	/** The component arguments. */
	protected JPanel arguments;
	protected List argelems;
	
	/** loaded from jccproject.xml and kept until gui is refreshed asynchronously. */
	protected String[]	loadargs;	
	
	/** loaded from jccproject.xml and kept until gui is refreshed asynchronously. */
	protected String	loadconfig;	
	
	/** loaded from jccproject.xml and kept until gui is refreshed asynchronously. */
	protected String	loadname;	
	
	/** The component results. */
	protected JPanel results;
	protected List reselems;
	protected JCheckBox storeresults;
	protected JComboBox selectavail;
	protected MultiCollection resultsets;

	/** The start button. */
	protected JButton start;

	/** The description panel. */
	protected ElementPanel modeldesc;

	/** The component specific panel. */
	protected JPanel componentpanel;
	
//	/** The application specific panel. */
//	protected JPanel apppanel;
	
	/** The starter plugin. */
	protected StarterPlugin	starter;

	/** The spinner for the number of components to start. */
	protected JSpinner numcomponents;

	/** The used services. */
	protected JPanel requiredservices;
	
	/** The provided services. */
	protected JPanel providedservices;
	
	//-------- constructors --------

	/**
	 * Open the GUI.
	 * @param starter The starter.
	 */
	public StarterPanel(final StarterPlugin starter)
	{
		super(new BorderLayout());
		this.starter	= starter;
		this.resultsets = new MultiCollection();
		
		JPanel content = new JPanel(new GridBagLayout());

		// Create the filename combo box.
		filename = new JTextField();
		filename.setEditable(false);
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
				refreshFlags();
			}
		});

		// The suspend mode.
		suspend = new JCheckBox("Start suspended");
		suspend.setToolTipText("Start in suspended mode");

		// The component name.
		componentname = new JTextField();

		// The generate flag for the componentname;
		genname = new JCheckBox("Auto generate", false);
		genname.setToolTipText("Auto generate the component instance name");
		genname.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				componentname.setEditable(!genname.isSelected());
				numcomponents.setEnabled(genname.isSelected());
			}
		});
		
		numcomponents = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		((JSpinner.DefaultEditor)numcomponents.getEditor()).getTextField().setColumns(4);
		
		// The arguments.
		arguments = new JPanel(new GridBagLayout());
		
		// The results.
		results = new JPanel(new GridBagLayout());
		
		// The required services.
		requiredservices = new JPanel(new BorderLayout());
		
		// The provided services.
		providedservices = new JPanel(new BorderLayout());

		// The reload button.
		final JButton reload = new JButton("Reload");
		reload.setToolTipText("Reload the current model");
		reload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				reloadModel();
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
					starter.getJCC().getExternalAccess().scheduleStep(new IComponentStep()
					{
						public static final String XML_CLASSNAME = "start";
						public Object execute(IInternalAccess ia)
						{
							ia.getRequiredService("libservice").addResultListener(new SwingDefaultResultListener(StarterPanel.this)
							{
								public void customResultAvailable(Object result)
								{
									ILibraryService ls = (ILibraryService)result;
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
										String typename = /*ac!=null? ac.getComponentType(filename.getText()):*/ filename.getText();
										final String fullname = model.getPackage()+"."+model.getName();
										final IModelInfo mymodel = model;
										boolean dokilllis = storeresults!=null && storeresults.isSelected();
												
										String an = genname.isSelected()?  null: componentname.getText();
										if(an==null) // i.e. name auto generate
										{
											int max = ((Integer)numcomponents.getValue()).intValue();
											for(int i=0; i<max; i++)
											{
												Future fut = new Future();
												IResultListener killlistener = dokilllis? new KillListener(mymodel, fullname, fut, StarterPanel.this): null;
												starter.createComponent(typename, an, configname, args, 
													suspend.isSelected()? Boolean.TRUE: Boolean.FALSE, 
													mastercb.isSelected()? Boolean.TRUE: Boolean.FALSE, 
													daemoncb.isSelected()? Boolean.TRUE: Boolean.FALSE, 
													autosdcb.isSelected()? Boolean.TRUE: Boolean.FALSE, killlistener)
												.addResultListener(new DelegationResultListener(fut));
											}
										}
										else
										{
											Future fut = new Future();
											IResultListener killlistener = dokilllis? new KillListener(mymodel, fullname, fut, StarterPanel.this): null;
											starter.createComponent(typename, an, configname, args, 
												suspend.isSelected()? Boolean.TRUE: Boolean.FALSE, 
												mastercb.isSelected()? Boolean.TRUE: Boolean.FALSE, 
												daemoncb.isSelected()? Boolean.TRUE: Boolean.FALSE, 
												autosdcb.isSelected()? Boolean.TRUE: Boolean.FALSE, killlistener)
											.addResultListener(new DelegationResultListener(fut));
										}
									}
									
								}
							});
							return null;
						}
					});
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
				}
			}
		};
		modeldesc.addChangeListener(desclistener);
		modeldesc.setMinimumSize(new Dimension(200, 150));
		modeldesc.setPreferredSize(new Dimension(400, 150));

		// Avoid panel being not resizeable when long filename is displayed
		filename.setMinimumSize(filename.getMinimumSize());

		int y = 0;
	
		componentpanel = new JPanel(new GridBagLayout());
		componentnamel = new JLabel("Component name");
		componentpanel.add(componentnamel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 2), 0, 0));
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(componentname, BorderLayout.CENTER);
		JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.add(genname, BorderLayout.WEST);
		tmp2.add(numcomponents, BorderLayout.EAST);
		tmp.add(tmp2, BorderLayout.EAST);
		componentpanel.add(tmp, new GridBagConstraints(1, 0, 4, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.BOTH, new Insets(0, 2, 2, 2), 0, 0));
			
		componentpanel.add(new JLabel("Parent"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 0, 0, 2), 0, 0));
		parenttf = new JTextField();
		parenttf.setEditable(false);
		componentpanel.add(parenttf, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
	
		JButton	chooseparent	= new JButton(icons.getIcon("Browse"));
		chooseparent.setMargin(new Insets(0,0,0,0));
		chooseparent.setToolTipText("Choose parent");
		componentpanel.add(chooseparent, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
		final ComponentSelectorDialog	agentselector = new ComponentSelectorDialog(this, starter.getJCC().getExternalAccess());
		chooseparent.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final IComponentIdentifier paid = (IComponentIdentifier)parent;
				IComponentIdentifier newparent	= agentselector.selectAgent(paid);
				if(newparent!=null)
					setParent(newparent);
			}
		});
		JButton	clearparent	= new JButton(icons.getIcon("delete"));
		clearparent.setMargin(new Insets(0,0,0,0));
		clearparent.setToolTipText("Clear parent");
		componentpanel.add(clearparent, new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
		clearparent.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setParent(null);
			}
		});
		
		JPanel flags = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//		Dimension pd = suspend.getPreferredSize();
//		Dimension md = suspend.getMinimumSize();
		mastercb = new JCheckBox("Master");
		mastercb.setToolTipText("If a master component terminates the parent is killed as well");
//		mastercb.setPreferredSize(pd);
//		mastercb.setMinimumSize(md);
		daemoncb = new JCheckBox("Daemon");
		daemoncb.setToolTipText("A daemon component does not prevent the parent component to terminate");
//		daemoncb.setPreferredSize(pd);
//		daemoncb.setMinimumSize(md);
		autosdcb = new JCheckBox("Auto Shutdown");
		autosdcb.setToolTipText("Auto shutdown terminates a composite components when all (non daemon) components have terminated");
//		autosdcb.setPreferredSize(pd);
//		autosdcb.setMinimumSize(md);
		
		flags.add(suspend);
		flags.add(mastercb);
		flags.add(daemoncb);
		flags.add(autosdcb);
		componentpanel.add(new JLabel("Flags"), new GridBagConstraints(0, 2, 1, 0, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 0));
		componentpanel.add(flags, new GridBagConstraints(1, 2, 4, 0, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
//		componentpanel.add(new JButton("bla"), new GridBagConstraints(0, 2, 5, 1, 1, 0, GridBagConstraints.EAST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));

		
//		componentpanel.add(suspend, new GridBagConstraints(5, 1, 1, 1, 1, 0, GridBagConstraints.EAST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
//		mastercb = new JCheckBox("Master");
//		mastercb.setToolTipText("If a master component terminates the parent is killed as well.");
//		componentpanel.add(mastercb, new GridBagConstraints(5, 1, 1, 1, 1, 0, GridBagConstraints.EAST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
//		daemoncb = new JCheckBox("Daemon");
//		daemoncb.setToolTipText("A daemon component does not prevent the parent component to terminate.");
//		componentpanel.add(daemoncb, new GridBagConstraints(6, 1, 1, 1, 1, 0, GridBagConstraints.EAST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
//		autosdcb = new JCheckBox("Auto Shutdown");
//		autosdcb.setToolTipText("Auto shutdown terminates a composite components when all (non daemon) components have terminated.");
//		componentpanel.add(autosdcb, new GridBagConstraints(7, 1, 1, 1, 1, 0, GridBagConstraints.EAST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
					
//		apppanel = new JPanel(new GridBagLayout());
//		appnamel = new JLabel("Application name");
//		apppanel.add(appnamel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
//			GridBagConstraints.BOTH, new Insets(0, 0, 0, 2), 0, 0));
//		apppanel.add(appname, new GridBagConstraints(1, 0, 4, 1, 1, 0, GridBagConstraints.EAST,
//			GridBagConstraints.BOTH, new Insets(0, 2, 0, 2), 0, 0));
		
		JPanel upper = new JPanel(new GridBagLayout());
		upper.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Settings "));
		filenamel = new JLabel("Filename");
		upper.add(filenamel, new GridBagConstraints(0, y, 1, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		upper.add(filename, new GridBagConstraints(1, y, 4, 1, 1, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//		upper.add(browse, new GridBagConstraints(4, y, 1, 1, 0, 0,
//			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		y++;
		confl = new JLabel("Configuration");
		upper.add(confl, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		upper.add(config, new GridBagConstraints(1, y, 4, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
//		upper.add(suspend, new GridBagConstraints(2, y, 3, 1, 0, 0, GridBagConstraints.WEST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		y++;
		upper.add(componentpanel, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		y++;
//		upper.add(apppanel, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
//			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		y = 0;
		content.add(upper, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		y++;
		content.add(arguments, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		y++;
		content.add(results, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		y++;
		content.add(requiredservices, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		y++;
		content.add(providedservices, new GridBagConstraints(0, y, 5, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		componentnamel.setMinimumSize(confl.getMinimumSize());
		componentnamel.setPreferredSize(confl.getPreferredSize());
		
		/*y++;
		componentnamel = new JLabel("Component name");
		content.add(componentnamel, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(componentname, "Center");
		tmp.add(gencomponentname, "East");
		//content.add(componentname, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.WEST,
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

//		HelpBroker hb = SHelp.setupHelp(this, "tools.starter");
//		if(hb!=null)
//		{
//			JButton help = new JButton("Help");
//			help.setToolTipText("Activate JavaHelp system");
//			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
//			help.setMinimumSize(new Dimension(mw, mh));
//			help.setPreferredSize(new Dimension(pw, ph));
//			buts.add(help, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
//					new Insets(2, 2, 2, 2), 0, 0));
//		}

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
	public void reloadModel()
	{
		if(lastfile==null)
			return;
		
		String toload = lastfile;
		lastfile = null;
		loadModel(toload);
	}
	
	/**
	 *  Load an component model.
	 *  @param adf The adf to load.
	 */
	public void loadModel(final String adf)
	{
		// Don't load same model again (only on reload).
		if(adf!=null && adf.equals(lastfile))
			return;
		
		lastfile	= adf;
		
//		System.out.println("loadModel: "+adf);
//		String	error	= null;
		
		if(adf!=null)
		{
			SComponentFactory.isLoadable(starter.getJCC().getExternalAccess().getServiceProvider(), adf).addResultListener(new SwingDefaultResultListener(StarterPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					if(((Boolean)result).booleanValue())
					{
						SComponentFactory.loadModel(starter.getJCC().getExternalAccess().getServiceProvider(), adf).addResultListener(new SwingDefaultResultListener(StarterPanel.this)
						{
							public void customResultAvailable(Object result)
							{
								model = (IModelInfo)result;
								SComponentFactory.getFileType(starter.getJCC().getExternalAccess().getServiceProvider(), adf).addResultListener(new SwingDefaultResultListener(StarterPanel.this)
								{
									public void customResultAvailable(Object result)
									{
										SComponentFactory.getFileTypeIcon(starter.getJCC().getExternalAccess().getServiceProvider(), (String)result).addResultListener(new SwingDefaultResultListener(StarterPanel.this)
										{
											public void customResultAvailable(Object result)
											{
												updateGuiForNewModel(adf, (Icon)result);
											}
											
											public void customExceptionOccurred(Exception exception)
											{
												updateGuiForNewModel(adf, null);
											}
										});								
									}
									
									public void customExceptionOccurred(Exception exception)
									{
										updateGuiForNewModel(adf, null);
									}
								});								
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								model = null;
								StringWriter sw = new StringWriter();
								exception.printStackTrace(new PrintWriter(sw));
								error = sw.toString();
								updateGuiForNewModel(adf, null);
							}
						});
					}
					else
					{
						model = null;
						updateGuiForNewModel(adf, null);
					}
				}
			});
		}
		else
		{
			model = null;
			error = null;
			updateGuiForNewModel(adf, null);
		}
	}

	
	/**
	 *  Update the GUI for a new model.
	 *  @param adf The adf.
	 *  @param icon The component icon (if available).
	 */
	void updateGuiForNewModel(String adf, Icon icon)
	{
//		System.out.println("updategui "+adf);
		
		ItemListener[] lis = config.getItemListeners();
		for(int i=0; i<lis.length; i++)
			config.removeItemListener(lis[i]);
		config.removeAllItems();
		
		// Add all known component configuration names to the config chooser.
		
		String[] confignames = model!=null? model.getConfigurations(): SUtil.EMPTY_STRING_ARRAY;
		for(int i = 0; i<confignames.length; i++)
		{
			((DefaultComboBoxModel)config.getModel()).addElement(confignames[i]);
		}
		
		if(loadconfig!=null)
		{
			config.getModel().setSelectedItem(loadconfig);
			loadconfig	= null;
		}
		else if(confignames.length>0)
		{
			config.getModel().setSelectedItem(confignames[0]);
		}
		
		if(model!=null && model.isStartable())
		{
			createArguments();
			createResults();
			createRequiredServices();
			createProvidedServices();
			arguments.setVisible(true);
			results.setVisible(true);
			componentpanel.setVisible(true);
			start.setVisible(true);
			
			filenamel.setMinimumSize(confdummy.getMinimumSize());
			filenamel.setPreferredSize(confdummy.getPreferredSize());
			confl.setMinimumSize(confdummy.getMinimumSize());
			confl.setPreferredSize(confdummy.getPreferredSize());
			componentnamel.setMinimumSize(confdummy.getMinimumSize());
			componentnamel.setPreferredSize(confdummy.getPreferredSize());
			
			componentname.setText(loadname!=null ? loadname	: model.getName());
			loadname	= null;
		}
		else
		{
			createRequiredServices();
			createProvidedServices();
			arguments.setVisible(false);
			results.setVisible(false);
			componentpanel.setVisible(false);
			start.setVisible(false);
			
			filenamel.setMinimumSize(confdummy.getMinimumSize());
			filenamel.setPreferredSize(confdummy.getPreferredSize());
			confl.setMinimumSize(confdummy.getMinimumSize());
			confl.setPreferredSize(confdummy.getPreferredSize());
			
			componentname.setText("");
		}
				
		filename.setText(adf);
		
		if(model!=null)
		{
			refreshFlags();
		}

		final IErrorReport report = model!=null? model.getReport(): null;
		if(report!=null)
		{
			icon	= icon!=null ? new CombiIcon(new Icon[]{icon, icons.getIcon("overlay_check")}) : icons.getIcon("overlay_check");
			try
			{
				modeldesc.addHTMLContent(model.getName(), icon, report.getErrorHTML(), adf, report.getDocuments());
			}
			catch(final Exception e)
			{
				String text = SUtil.wrapText("Could not display HTML content: "+e.getMessage());
				JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), text, "Display Problem", JOptionPane.INFORMATION_MESSAGE);
				modeldesc.addTextContent(model.getName(), icon, report.toString(), adf);
			}
		}
		else if(model!=null)
		{
			try
			{
				modeldesc.addHTMLContent(model.getName(), icon, model.getDescription(), adf, null);
			}
			catch(final Exception e)
			{
				String text = SUtil.wrapText("Could not display HTML content: "+e.getMessage());
				JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this), text, "Display Problem", JOptionPane.INFORMATION_MESSAGE);
				modeldesc.addTextContent(model.getName(), icon, model.getDescription(), adf);
			}
		}
		else if(error!=null)
		{
			modeldesc.addTextContent("Error", null, error, adf);
		}

		// Adjust state of start button depending on model checking state.
		start.setEnabled(model!=null&& model.isStartable() && report==null);
	
		for(int i=0; i<lis.length; i++)
			config.addItemListener(lis[i]);
	}
	
	/**
	 *  Refresh the flags. 
	 */
	protected void refreshFlags()
	{
		// todo: suspend?!
		if(model!=null)
		{
			String c = (String)config.getSelectedItem();
			boolean s = model.getSuspend(c)==null? false: model.getSuspend(c).booleanValue();
			boolean m = model.getMaster(c)==null? false: model.getMaster(c).booleanValue();
			boolean d = model.getDaemon(c)==null? false: model.getDaemon(c).booleanValue();
			boolean a = model.getAutoShutdown(c)==null? false: model.getAutoShutdown(c).booleanValue();
			suspend.setSelected(s);
			mastercb.setSelected(m);
			daemoncb.setSelected(d);
			autosdcb.setSelected(a); 
//			System.out.println("smda: "+s+" "+m+" "+d+" "+a);
		}
	}

	/**
	 *  Get the properties.
	 *  @param props The properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		
		String m = SUtil.convertPathToRelative(filename.getText());
		if(m!=null) props.addProperty(new Property("model", m));

		String c = (String)config.getSelectedItem();
		if(c!=null) props.addProperty(new Property("config", c));

		props.addProperty(new Property("startsuspended", ""+suspend.isSelected()));

		props.addProperty(new Property("autogenerate", ""+genname.isSelected()));
		
		props.addProperty(new Property("name", componentname.getText()));
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
	public void setProperties(Properties props)
	{
		// Settings are invoke later'd due to getting overridden otherwise.!?
		
//		System.out.println("setP: "+Thread.currentThread().getName());
		
		Property[]	aargs	= props.getProperties("argument");
		this.loadargs = new String[aargs.length];
		for(int i=0; i<aargs.length; i++)
		{
			loadargs[i] = aargs[i].getValue();
		}

		final String mo = props.getStringProperty("model");
		if(mo!=null)
		{
			lastfile = mo;
			loadconfig	= props.getStringProperty("config");
			loadname	= props.getStringProperty("name");
			reloadModel();
		}
		setStartSuspended(props.getBooleanProperty("startsuspended"));

		setAutoGenerate(props.getBooleanProperty("autogenerate"));
	}

	/**
	 *  Reset the gui.
	 */
	public void reset()
	{
		loadargs	= null;
		filename.setText("");
		modeldesc.removeAll();
		loadModel(null);
		config.removeAllItems();
		clearArguments();
		clearResults();
		setComponentName("");
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
			//System.out.println("selecting: "+conf+" "+config.getModel().getSize());
			config.getModel().setSelectedItem(conf);
		}
	}

//	/**
//	 *  Set the arguments.
//	 *  @param args The arguments.
//	 */
//	protected void setArguments(final String[] args)
//	{
//		if(args!=null && args.length==argelems.size())
//		{
//			if(arguments==null || argelems==null || arguments.getComponentCount()!=4*argelems.size())
//				return;
//			
//			for(int i=0; i<args.length; i++)
//			{
//				JTextField valt = (JTextField)arguments.getComponent(i*4+3);
//				valt.setText(args[i]);
//			}
//		}
//	}
	
	/**
	 *  Refresh the argument values.
	 *  Called only from gui thread.
	 */
	protected void refreshArguments()
	{
		// Assert that all argument components are there.
		if(model==null || arguments==null || argelems==null)
			return;
		
		for(int i=0; argelems!=null && i<argelems.size(); i++)
		{
			JTextField valt = (JTextField)arguments.getComponent(i*4+2);
			valt.setText(""+((IArgument)argelems.get(i)).getDefaultValue((String)config.getSelectedItem()));
		}
	}
	
	/**
	 *  Refresh the argument values.
	 */
	protected void clearArguments()
	{
		// Assert that all argument components are there.
		if(arguments==null || argelems==null)
			return;
		
		for(int i=0; i<argelems.size(); i++)
		{
			JTextField valt = (JTextField)arguments.getComponent(i*4+3);
			valt.setText("");
		}
	}
	
	/**
	 *  Create the arguments panel.
	 */
	protected void createArguments()
	{
		starter.getJCC().getExternalAccess().scheduleStep(new IComponentStep()
		{
			public static final String XML_CLASSNAME = "create-arguments";
			public Object execute(IInternalAccess ia)
			{
				ia.getRequiredService("libservice").addResultListener(new SwingDefaultResultListener(StarterPanel.this)		
				{
					public void customResultAvailable(Object result)
					{
						ILibraryService ls = (ILibraryService)result;
						argelems = SCollection.createArrayList();
						arguments.removeAll();
						arguments.setBorder(null);
						
						if(model!=null)
						{
							IArgument[] args = model.getArguments();
							
							for(int i=0; i<args.length; i++)
							{
								argelems.add(args[i]);
								createArgumentGui(args[i], i, ls);
							}
							loadargs	= null;
							
							if(args.length>0)
								arguments.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Arguments "));
						}
					}
				});
				return null;
			}
		});
	}
	
	/**
	 *  Refresh the result values.
	 */
	protected void refreshResults()
	{
		// Assert that all argument components are there.
		if(model==null || results==null || reselems==null)
			return;
		
		// Find results of specific instance.
		Map mres = null;
		int sel = selectavail.getSelectedIndex();
//		System.out.println("Selected index: "+sel+selectavail.getSelectedItem().hashCode());
		if(sel>0)
		{
			List rs = (List)resultsets.get(model.getFullName());
			Object[] r = (Object[])rs.get(sel-1);
			mres = (Map)r[1];
		}
		
		for(int i=0; reselems!=null && i<reselems.size(); i++)
		{
			IArgument arg = ((IArgument)reselems.get(i));
//			Object value = mres!=null? mres.get(arg.getName()): arg.getDefaultValue((String)config.getSelectedItem());
			Object value = mres!=null? mres.get(arg.getName()): "";
			JTextField valt = (JTextField)results.getComponent(i*4+3);
			valt.setText(""+value);
		}
	}
	
	/**
	 *  Clear the result values.
	 */
	protected void clearResults()
	{
		// Assert that all argument components are there.
		if(results==null || reselems==null)
			return;
		
		for(int i=0; i<reselems.size(); i++)
		{
			JTextField valt = (JTextField)results.getComponent(i*4+3);
			valt.setText("");
		}
	}
	
	/**
	 *  Create the results panel.
	 */
	protected void createResults()
	{
		reselems = SCollection.createArrayList();
		results.removeAll();
		results.setBorder(null);
		
		if(model!=null)
		{
			final IArgument[] res = model.getResults();
			
			for(int i=0; i<res.length; i++)
			{
				reselems.add(res[i]);
				createResultGui(res[i], i);
			}
			
			if(res.length>0)
			{
				results.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Results "));
				
				JLabel sr = new JLabel("Store results");
				storeresults = new JCheckBox();
				
				JButton cr = new JButton("Clear results");
				
				JLabel sa = new JLabel("Select component instance");
				selectavail= new FixedJComboBox();
				
				selectavail.addItem("- no instance selected -");
				
				cr.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						storeresults.removeAll();
						selectavail.removeAllItems();
						selectavail.addItem("- no instance selected -");
						clearResults();
					}
				});
				
				List rs = (List)resultsets.get(model.getFullName());
				if(rs!=null)
				{
					for(int i=0; i<rs.size(); i++)
					{
						Object[] r = (Object[])rs.get(i);
						selectavail.addItem(r[0]);
					}
					selectavail.setSelectedIndex(0);
				}
				
	//					selectavail.addItemListener(new ItemListener()
	//					{
	//						public void itemStateChanged(ItemEvent e)
	//						{
	//							System.out.println("here: "+resultsets);
	//							refreshResults();
	//						}
	//					});
				selectavail.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						refreshResults();
					}
				});
				
				int y = res.length;
				
				results.add(sr, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 0), 0, 0));
				results.add(storeresults, new GridBagConstraints(1, y, 2, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 0, 2, 2), 0, 0));
				results.add(cr, new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(2, 0, 2, 2), 0, 0));
				y++;
				results.add(sa, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
				results.add(selectavail, new GridBagConstraints(1, y, 3, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			}
		}
	}
	
	/**
	 *  Create the gui for one argument. 
	 *  @param arg The belief or belief reference.
	 *  @param y The row number where to add.
	 */
	protected void createArgumentGui(IArgument arg, int y, ILibraryService ls)
	{
		// todo:
//		System.out.println("argument gui: "+arg+" "+model);
		
		JLabel namel = new JLabel(arg.getName());
		final JValidatorTextField valt = new JValidatorTextField(loadargs!=null && loadargs.length>y ? loadargs[y] : "", 15);
		valt.setValidator(new ParserValidator(ls.getClassLoader()));
		
		String configname = (String)config.getSelectedItem();
		JTextField mvalt = new JTextField(""+arg.getDefaultValue(configname));
		// Java JTextField bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4247013
		//mvalt.setMinimumSize(new Dimension(mvalt.getPreferredSize().width/4, mvalt.getPreferredSize().height/4));
		mvalt.setEditable(false);
		
		JLabel typel = new JLabel(arg.getTypename()!=null? arg.getTypename(): "undefined");
		
		String description = arg.getDescription();
		if(description!=null)
		{
			namel.setToolTipText(description);
			valt.setToolTipText(description);
			mvalt.setToolTipText(description);
//					typel.setToolTipText(description);
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
//				y++;
	}
	
	/**
	 *  Create the gui for one argument. 
	 *  @param arg The belief or belief reference.
	 *  @param y The row number where to add.
	 */
	protected void createResultGui(final IArgument arg, int y)
	{
		JLabel namel = new JLabel(arg.getName());
		final JTextField valt = new JTextField();
		valt.setEditable(false);
		
		String configname = (String)config.getSelectedItem();
		JTextField mvalt = new JTextField(""+arg.getDefaultValue(configname));
		// Java JTextField bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4247013
		//mvalt.setMinimumSize(new Dimension(mvalt.getPreferredSize().width/4, mvalt.getPreferredSize().height/4));
		mvalt.setEditable(false);
		
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
		results.add(typel, new GridBagConstraints(x++, y, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		results.add(namel, new GridBagConstraints(x++, y, 1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		results.add(mvalt, new GridBagConstraints(x++, y, 1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		results.add(valt, new GridBagConstraints(x++, y, 1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		y++;
	}
	
	/**
	 *  Create the required services panel.
	 */
	protected void createRequiredServices()
	{
		requiredservices.removeAll();
		requiredservices.setBorder(null);
		
		if(model!=null)
		{
			RequiredServiceInfo[] required = model.getRequiredServices();
			
			if(required.length>0)
			{
				final JTable requiredt = new JTable(new DefaultTableModel(new String[]{"Name", "Interface", "Multiple"}, 0));
				requiredt.setEnabled(false);
				requiredservices.add(requiredt.getTableHeader(), BorderLayout.NORTH);
				requiredservices.add(requiredt, BorderLayout.CENTER);
				for(int i=0; i<required.length; i++)
				{
					((DefaultTableModel)requiredt.getModel()).addRow(new Object[]{required[i].getName(), 
						required[i].getType(), required[i].isMultiple()});
				}
				requiredt.getColumn("Interface").setCellRenderer(new ClassRenderer());

			}
			
			if(required.length>0)
				requiredservices.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Required Services "));
		}
	}
	
	/**
	 *  Create the provided services panel.
	 */
	protected void createProvidedServices()
	{
		providedservices.removeAll();
		providedservices.setBorder(null);
		
		if(model!=null)
		{
			Class[] provided = model.getProvidedServices();
			
			if(provided.length>0)
			{
				JTable providedt = new JTable(new DefaultTableModel(new String[]{"Interface Name"}, 0));
				providedt.setEnabled(false);
//				DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
//				rend.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
//				providedt.getColumn("Interface Name").setCellRenderer(rend);
//				providedservices.add(providedt.getTableHeader(), BorderLayout.NORTH);
				providedservices.add(providedt, BorderLayout.CENTER);
				for(int i=0; i<provided.length; i++)
				{
					((DefaultTableModel)providedt.getModel()).addRow(new Object[]{
						provided[i]!=null? provided[i].getName(): "unknown service type (class definition missing)"});
				}
			}
			
			if(provided.length>0)
				providedservices.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Provided Services "));
		}
	}
	
	
	/**
	 *  Set the component name.
	 *  @param name The name.
	 */
	protected void setComponentName(final String name)
	{
		if(name!=null)
		{
			componentname.setText(name);
		}
	}
	
	/**
	 *  Clear the application name.
	 *  @param name The name.
	 */
	protected void clearApplicationName()
	{
//		appname.removeAll();
	}

	/**
	 *  Set the auto generate in gui.
	 *  @param autogen The autogen property.
	 */
	protected void setAutoGenerate(final boolean autogen)
	{
		genname.setSelected(autogen);
		componentname.setEditable(!autogen);
		numcomponents.setEnabled(autogen);
	}

	/**
	 *  Set the start suspended flag in gui.
	 *  @param startsuspended The start suspended flag property.
	 */
	protected void setStartSuspended(final boolean startsuspended)
	{
		suspend.setSelected(startsuspended);
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

	/**
	 *  Set the current parent.
	 *  @param parent	The component id.
	 */
	public void setParent(IComponentIdentifier parent)
	{
		this.parent	= parent;
		parenttf.setText(parent!=null ? parent.getName() : "");
	}
	
	/**
	 *  Listener that is called on component kill.
	 */
	public class KillListener extends SwingDefaultResultListener
	{
		/** The model info. */
		protected IModelInfo model;
		
		/** The fullname. */
		protected String fullname;
		
		/** The source cid. */
		protected IComponentIdentifier cid;
		
		/**
		 *  Create a new listener.
		 */
		public KillListener(IModelInfo model, String fullname, Future fut, Component parent)
		{
			super(parent);
			this.model = model;
			this.fullname = fullname;
			fut.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					cid = (IComponentIdentifier)result;
				}
				
				public void exceptionOccurred(Exception exception)
				{
					KillListener.this.exceptionOccurred(exception);
				}
			});
		}
		
		/**
		 *  Called when result is available.
		 */
		public void customResultAvailable(Object result)
		{
//			System.out.println("fullname: "+fullname+" "+model.getFilename());
			if(cid!=null)
			{
				String tmp = (String)model.getFullName();
				resultsets.put(tmp, new Object[]{cid, result});
				if(model!=null && fullname.equals(model.getFullName()))
				{
					selectavail.addItem(cid);
					refreshResults();
				}
			}
			else
			{
				exceptionOccurred(new RuntimeException("Unknown component identifier."));
			}
		}
	};
}


