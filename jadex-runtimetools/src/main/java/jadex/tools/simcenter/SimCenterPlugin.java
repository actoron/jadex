package jadex.tools.simcenter;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.SGUI;
import jadex.tools.help.SHelp;

import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIDefaults;
import javax.swing.border.EmptyBorder;

/**
 *  Plugin for the test center.
 */
public class SimCenterPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"simcenter_sel", SGUI.makeIcon(SimCenterPlugin.class, "/jadex/tools/common/images/stopwatch_sel.png"),
		"simcenter", SGUI.makeIcon(SimCenterPlugin.class, "/jadex/tools/common/images/stopwatch.png"),
	});

	//-------- attributes --------

	/** The sim center panel. */
	protected SimCenterPanel scpanel;

	//-------- methods --------

	/**
	 * Return the unique name of this plugin. Used e.g. to store properties of
	 * each plugin.
	 */
	public String getName()
	{
		return "Simulation Control";
	}

	/**
	 * Return the icon representing this plugin.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("simcenter_sel"): icons.getIcon("simcenter");
	}

	/**
	 * Return the id for the help system
	 */
	public String getHelpID()
	{
		// todo:!
		return "tools.simcontrol";
	}

	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		JComponent[] ret = new JComponent[4];
		JButton b;

		b = new JButton(scpanel.getContextPanel().START);
		b.setBorder(new EmptyBorder(new Insets(0,0,0,3)));
		ret[0] = b;
		
		b = new JButton(scpanel.getContextPanel().STEP_EVENT);
		b.setBorder(new EmptyBorder(new Insets(0,3,0,3)));
		ret[1] = b;

		b = new JButton(scpanel.getContextPanel().STEP_TIME);
		b.setBorder(new EmptyBorder(new Insets(0,3,0,3)));
		ret[2] = b;
		
		b = new JButton(scpanel.getContextPanel().PAUSE);
		b.setBorder(new EmptyBorder(new Insets(0,3,0,0)));
		ret[3] = b;
		
		return ret;
	}
	
	/**
	 *  Create the menu bar.
	 *  @return The menubar.
	 */
	public JMenu[] createMenuBar()
	{	
		ButtonGroup group = new ButtonGroup();
		
		JMenu menu = new JMenu("Time Settings");
//		SHelp.setupHelp(menu, "tools.simcenter");

		JRadioButtonMenuItem time_long = new JRadioButtonMenuItem(new AbstractAction("Long value")
		{
			public void actionPerformed(ActionEvent e)
			{
				scpanel.setTimemode(0);
				scpanel.updateView();
			}
		});
		time_long.setSelected(true);
		group.add(time_long);
		menu.add(time_long);
		
		JRadioButtonMenuItem time_rel = new JRadioButtonMenuItem(new AbstractAction("Relative value")
		{
			public void actionPerformed(ActionEvent e)
			{
				scpanel.setTimemode(1);
				scpanel.updateView();
			}
		});
		group.add(time_rel);
		menu.add(time_rel);
		
		JRadioButtonMenuItem time_date = new JRadioButtonMenuItem(new AbstractAction("Date value")
		{
			public void actionPerformed(ActionEvent e)
			{
				scpanel.setTimemode(2);
				scpanel.updateView();
			}
		});
		group.add(time_date);
		menu.add(time_date);
		
		return new JMenu[]{menu};
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		scpanel = new SimCenterPanel(this);
		return scpanel;
	}

	/**
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#reset()
	 */
	public void reset()
	{
		//scpanel.reset();
	}
}
