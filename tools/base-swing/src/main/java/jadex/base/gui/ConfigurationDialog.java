package jadex.base.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.commons.gui.BrowserPane;

/**
 *  Panel for Jadex configuration.
 */
public class ConfigurationDialog extends JAutoPositionDialog
{
	//-------- constants --------
	
	/** The name of a property (in the property file). */
	public static final String	PROPERTY_NAME	= "property_name";
	/** The value of a property (in the property file). */
	public static final String	PROPERTY_VALUE	= "property_value";
	/** The dependency of the property (i.e., a class name). */
	public static final String	PROPERTY_DEPENDENCY	= "property_dependency";
	/** An array of JCheckBoxes, only relevant for a specific property selection. */
	public static final String	PROPERTY_OPTIONS	= "property_options";

	//-------- attributes --------

	/** The components representing properties. */
	protected List	components;

	//-------- constructors --------

	/**
	 *  Create a new panel.
	 */
	public ConfigurationDialog(Frame owner)
	{
		super(owner, true);
		this.getContentPane().setLayout(new GridBagLayout());
		this.setTitle("Platform Settings");
		this.components	= SCollection.createArrayList();
				
		// Expression evaluation.
//		JRadioButton	javacc	= new JRadioButton("Interpreter");
//		javacc.putClientProperty(PROPERTY_NAME, Configuration.PARSER_NAME);
//		javacc.putClientProperty(PROPERTY_VALUE, "jadex.parser.javaccimpl.Parser");
//		javacc.putClientProperty(PROPERTY_DEPENDENCY, "jadex.parser.javaccimpl.Parser");
//		JCheckBox	javacc_plan_reloading	= new JCheckBox("Plan reloading enabled");
//		javacc_plan_reloading.putClientProperty(PROPERTY_NAME, Configuration.JAVACC_PLAN_RELAODING);
//		javacc.putClientProperty(PROPERTY_OPTIONS, new JCheckBox[]{javacc_plan_reloading});
//
//		JRadioButton	janino	= new JRadioButton("Compiler");
//		janino.putClientProperty(PROPERTY_NAME, Configuration.PARSER_NAME);
//		janino.putClientProperty(PROPERTY_VALUE, "jadex.parser.janinoimpl.Parser");
//		janino.putClientProperty(PROPERTY_DEPENDENCY, "jadex.parser.janinoimpl.Parser");
//		JCheckBox	janino_write_cache	= new JCheckBox("Write to file-cache enabled");
//		janino_write_cache.putClientProperty(PROPERTY_NAME, Configuration.JANINO_WRITE_CACHE);
//		JCheckBox	janino_read_cache	= new JCheckBox("Read from file-cache enabled");
//		janino_read_cache.putClientProperty(PROPERTY_NAME, Configuration.JANINO_READ_CACHE);
//		janino.putClientProperty(PROPERTY_OPTIONS, new JCheckBox[]{janino_write_cache, janino_read_cache});
//
//		addChoice(" Expression evaluation ", new JRadioButton[]{javacc, janino});

		// XML databinding.
//		JCheckBox	model_checking	= new JCheckBox("Enable model integrity checking");
//		model_checking.putClientProperty(PROPERTY_NAME, Configuration.MODEL_CHECKING);
//		JCheckBox	model_caching	= new JCheckBox("Enable model caching");
//		model_caching.putClientProperty(PROPERTY_NAME, Configuration.MODEL_CACHING);
//		JCheckBox	model_cache_auto	= new JCheckBox("Enable model cache auto-refresh");
//		model_cache_auto.putClientProperty(PROPERTY_NAME, Configuration.MODEL_CACHE_AUTOREFRESH);

//		addChoice(" XML model loading ", new AbstractButton[]{model_checking, model_caching, model_cache_auto});
		
		// Generic settings.
//		JCheckBox	welcome	= new JCheckBox("Suppress Jadex welcome message on platform start");
//		welcome.putClientProperty(PROPERTY_NAME, Configuration.NO_WELCOME);
//		JRadioButton	shutdown	= new JRadioButton("Shutdown platform on JCC exit");
//		shutdown.putClientProperty(PROPERTY_NAME, Configuration.JCC_EXIT);
//		shutdown.putClientProperty(PROPERTY_VALUE, Configuration.JCC_EXIT_SHUTDOWN);
//		JRadioButton	keep	= new JRadioButton("Keep platform running after JCC exit");
//		keep.putClientProperty(PROPERTY_NAME, Configuration.JCC_EXIT);
//		keep.putClientProperty(PROPERTY_VALUE, Configuration.JCC_EXIT_KEEP);
//		JRadioButton	ask	= new JRadioButton("Ask for platform behavior on JCC exit");
//		ask.putClientProperty(PROPERTY_NAME, Configuration.JCC_EXIT);
//		ask.putClientProperty(PROPERTY_VALUE, Configuration.JCC_EXIT_ASK);
//		addChoice(" Generic settings ", new AbstractButton[]{welcome, shutdown, keep, ask});

		// Refresh GUI state.
		update();

		// Add info about addons.
		BrowserPane	addons	= new BrowserPane();
		addons.setText("Some of these settings require add-ons <br> available from the "
			+"<a href=\"http://www.activecomponents.org/download\">"
			+"Jadex download page</a>.");
		addons.setCaretPosition(0);
		addons.setDefaultOpenMode(true);
		int index	= this.getContentPane().getComponentCount();
		this.getContentPane().add(addons, new GridBagConstraints(0, index, 1, 1, 1, 1, GridBagConstraints.NORTHEAST,
			GridBagConstraints.HORIZONTAL, new Insets(2, 4, 4, 2), 0, 0));

		// Add ok cancel buttons.
		JPanel buts = new JPanel(new GridBagLayout());
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		final JButton apply = new JButton("Apply");
		final JButton help = new JButton("Help");
		buts.add(ok, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(cancel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(apply, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		buts.add(help, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2, 4, 4, 2), 0, 0));
		Dimension md = cancel.getMinimumSize();
		Dimension pd = cancel.getPreferredSize();
		ok.setMinimumSize(md);
		ok.setPreferredSize(pd);
		apply.setMinimumSize(md);
		apply.setPreferredSize(pd);
		help.setMinimumSize(md);
		help.setPreferredSize(pd);

		index	= this.getContentPane().getComponentCount();
		this.getContentPane().add(buts, new GridBagConstraints(0, index, 1, 1, 1, 1, GridBagConstraints.NORTHEAST,
			GridBagConstraints.HORIZONTAL, new Insets(2, 4, 4, 2), 0, 0));

		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				save();
				dispose();
			}
		});
		apply.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				save();
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				dispose();
			}
		});

//		HelpBroker hb = GuiProperties.setupHelp(this, "tools.controlcenter.settings");
//		if(hb!=null)
//			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
	}

	//-------- methods --------

	/**
	 *  Add a choice section to the GUI.
	 *  Choices may be (mutual exclusive) radio buttons,
	 *  or simple checkboxes. 
	 *  @param title	The title of the section.
	 *  @param choices	The available choices.
	 */
	protected void	addChoice(String title, AbstractButton[] choices)
	{
		// Create panel for holding the choice radio buttons.
		JPanel choicepanel = new JPanel(new GridBagLayout());
		choicepanel.setBorder(new TitledBorder(new EtchedBorder(), title));
		int index	= this.getContentPane().getComponentCount();
		this.getContentPane().add(choicepanel, new GridBagConstraints(0, index, 1, 1, 1, 0, GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(2, 4, 4, 2), 0, 0));
		ButtonGroup bgroup = new ButtonGroup();
		
		// Add radio buttons to choice.
		int	row	= 0;
		for(int i=0; i<choices.length; i++)
		{
			components.add(choices[i]);
			if(choices[i] instanceof JRadioButton)
				bgroup.add(choices[i]);

			final JCheckBox[]	options	= (JCheckBox[])choices[i].getClientProperty(PROPERTY_OPTIONS);
				// Add button to panel (use larger insets for buttons without options).
				choicepanel.add(choices[i], new GridBagConstraints(0, row++, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL, new Insets(4,4, options!=null && options.length>0 ? 0 : 4,2), 0, 0));

			// Add checkboxes for choice-specific options.
			if(options!=null && options.length>0)
			{
				JPanel optionpanel = new JPanel(new GridBagLayout());
				choicepanel.add(optionpanel, new GridBagConstraints(0, row++, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL, new Insets(0,4,4,2), 0, 0));

				for(int j=0; j<options.length; j++)
				{
					optionpanel.add(options[j], new GridBagConstraints(0, j, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(0,12,0,2), 0, 0));
				}

				// Auto-(de)activate options of (de)selected choices.
				choices[i].addItemListener(new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						boolean	active	= ((JRadioButton)e.getSource()).isSelected();
						for(int j=0; j<options.length; j++)
						{
							String	dependency	=(String)options[j].getClientProperty(PROPERTY_DEPENDENCY);
							options[j].setEnabled(active && (dependency==null || SReflect.classForName0(dependency, null)!=null));
						}
					}
				});
			}
		}
	}
	
	/**
	 *  Update the GUI state by reading the current values from the configuration.
	 */
	protected void	update()
	{
		for(int i=0; i<components.size(); i++)
		{
			update((JComponent)components.get(i), true);
		}
	}
	
	/**
	 *  Update the component's state by reading the current values from the configuration.
	 *  @param comp	The component.
	 *  @param active	If false, the component will de disabled.
	 */
	protected void	update(JComponent comp, boolean active)
	{
//		// Component may be active, when parent component is active and
//		// class dependency (if any) is available.
//		String	dependency	=(String)comp.getClientProperty(PROPERTY_DEPENDENCY);
//		active	= active && (dependency==null || SReflect.findClass0(dependency, null)!=null);
//		comp.setEnabled(active);
//		
//		// For missing dependencies, change label, so that user knows whats going on.
//		if((dependency!=null && SReflect.findClass0(dependency, null)==null))
//		{
//			if(comp instanceof AbstractButton)
//			{
//				AbstractButton	ab	= (AbstractButton)comp;
//				ab.setText(ab.getText()+" (n/a)");
//			}
//		}
//		
//		// Update state depending on component type.
//		String	state	= Configuration.getConfiguration().getProperty((String)comp.getClientProperty(PROPERTY_NAME));
//		if(comp instanceof JRadioButton)
//		{
//			((JRadioButton)comp).setSelected(state!=null && state.equals(comp.getClientProperty(PROPERTY_VALUE)));
//			// Deactivate subcomponents of unselected options.
//			active	= state!=null && state.equals(comp.getClientProperty(PROPERTY_VALUE));
//		}
//		else if(comp instanceof JCheckBox)
//		{
//			((JCheckBox)comp).setSelected(state!=null && state.equals("true"));
//		}
//		
//		// Recurse for subcomponents (if any).
//		JCheckBox[]	options	= (JCheckBox[])comp.getClientProperty(PROPERTY_OPTIONS);
//		for(int i=0; options!=null && i<options.length; i++)
//		{
//			update(options[i], active);
//		}
	}
	
	/**
	 *  Save the values from the dialog.
	 */
	protected void save()
	{
//		for(int i=0; i<components.size(); i++)
//		{
//			save((JComponent)components.get(i));
//		}
//
//		try
//		{
//			Configuration.getConfiguration().persist();
//		}
//		catch(IOException e)
//		{
//			String txt = SUtil.wrapText("Could not save configuration: "+e.getMessage());
//			JOptionPane.showMessageDialog(this, txt, "Configuration Error", JOptionPane.ERROR_MESSAGE);
//		}
	}

	/**
	 *  Save the value(s) from the given component.
	 */
	protected void save(JComponent comp)
	{
//		String	name	= (String)comp.getClientProperty(PROPERTY_NAME);
//
//		if(comp instanceof JRadioButton)
//		{
//			if(((JRadioButton)comp).isSelected())
//			{
//				String	value	= (String)comp.getClientProperty(PROPERTY_VALUE);
//				Configuration.getConfiguration().setProperty(name, value);
//			}
//		}
//		else if(comp instanceof JCheckBox)
//		{
//			String	value	= ((JCheckBox)comp).isSelected() ? "true" : "false";
//			Configuration.getConfiguration().setProperty(name, value);
//		}
//
//		// Recurse for subcomponents (if any).
//		JCheckBox[]	options	= (JCheckBox[])comp.getClientProperty(PROPERTY_OPTIONS);
//		for(int i=0; options!=null && i<options.length; i++)
//		{
//			save(options[i]);
//		}
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		ConfigurationDialog cf = new ConfigurationDialog(null);
		cf.pack();
		cf.setVisible(true);
		cf.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
}
