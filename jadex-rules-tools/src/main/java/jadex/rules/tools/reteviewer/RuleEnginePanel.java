package jadex.rules.tools.reteviewer;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIDefaults;

import jadex.commons.ISteppable;
import jadex.commons.gui.SGUI;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.tools.stateviewer.OAVPanel;

/**
 *  Panel containing state and Rete viewer.
 */
public class RuleEnginePanel extends JTabbedPane
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"show_state", SGUI.makeIcon(RuleSystemExecutor.class, "/jadex/rules/tools/reteviewer/images/bulb2.png"),
		"show_rete", SGUI.makeIcon(RuleSystemExecutor.class, "/jadex/rules/tools/reteviewer/images/bug_small.png"),
	});
	
	//-------- attributes --------
	
	/** The OAV panel. */
	protected OAVPanel	oavpanel;
	
	/** The Rete panel. */
	protected RetePanel	retepanel;
	
	/** The Rule base panel. */
	protected RulebasePanel	rulebasepanel;
	
	//-------- constructors -------
	
	/**
	 *  Create a rule engine panel.
	 */
	public RuleEnginePanel(final RuleSystem rulesystem, final ISteppable steppable)
	{
		this.oavpanel	= new OAVPanel(rulesystem.getState());
		this.rulebasepanel = new RulebasePanel(rulesystem.getRulebase(), steppable);
		this.retepanel = new RetePanel(rulesystem, steppable, rulebasepanel);
		
		JSplitPane sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp1.setOneTouchExpandable(true);
		sp1.add(rulebasepanel);
		sp1.add(retepanel);
		sp1.setDividerLocation(150);
		
		this.addTab("Working Memory", icons.getIcon("show_state"), oavpanel);
		this.addTab("Rule Engine", icons.getIcon("show_rete"), sp1);
		this.setSelectedIndex(0);
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel
	 *  and remove all rule engine / state listeners.
	 */
	public void	dispose()
	{
		oavpanel.dispose();
		retepanel.dispose();
		rulebasepanel.dispose();
	}
	
	//-------- static methods --------
	
	/**
	 *  Create and open a rule engine tool frame.
	 */
	public static JFrame createRuleEngineFrame(RuleSystemExecutor exe, String title)
	{
		JComponent	tabs	= new RuleEnginePanel(exe.getRulesystem(), exe);
		JFrame f = new JFrame(title);
		f.getContentPane().setLayout(new BorderLayout());
		f.add("Center", tabs);
		f.setSize(800,600);
		f.setLocation(SGUI.calculateMiddlePosition(f));
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter()
        {
        	public void windowClosing(WindowEvent e)
        	{
        		System.exit(0);
        	}
        });
		return f;
	}
}
