package jadex.tools.debugger.bdi;

import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.IBreakpointPanel;
import jadex.commons.SGUI;
import jadex.rules.tools.reteviewer.RetePanel;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.debugger.IDebuggerPanel;
import jadex.tools.help.SHelp;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

/**
 *  Show the rule engine of a BDI agent.
 */
public class BDIAgentRuleEngineDebuggerPanel	implements IDebuggerPanel
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"show_rete", SGUI.makeIcon(SHelp.class, "/jadex/tools/common/images/bug_small.png")
	});

	//-------- IDebuggerPanel methods --------
	
	/** The gui component. */
	protected JComponent	retepanel;

	//-------- IDebuggerPanel methods --------

	/**
	 *  Called to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 *  @param bpp	The breakpoint panel.
	 * 	@param id	The component identifier.
	 * 	@param access	The external access of the component.
	 */
	public void init(IControlCenter jcc, IBreakpointPanel bpp, IComponentIdentifier name, IExternalAccess access)
	{
		BDIInterpreter bdii = ((ElementFlyweight)access).getInterpreter();
		this.retepanel = new RetePanel(bdii.getRuleSystem(), null, bpp);
	}

	/**
	 *  The title of the panel (name of the tab).
	 *  @return	The tab title.
	 */
	public String getTitle()
	{
		return "Rule Engine";
	}

	/**
	 *  The icon of the panel.
	 *  @return The icon (or null, if none).
	 */
	public Icon getIcon()
	{
		return icons.getIcon("show_rete");
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return retepanel;
	}
	
	/**
	 *  The tooltip text of the panel, if any.
	 *  @return The tooltip text, or null.
	 */
	public String getTooltipText()
	{
		return "Show the rule engine.";
	}

}
