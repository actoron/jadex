package jadex.tools.debugger.bdi;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.IBreakpointPanel;
import jadex.commons.SGUI;
import jadex.rules.tools.stateviewer.OAVPanel;
import jadex.tools.debugger.IDebuggerPanel;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  Show the state of a BDI agent.
 */
public class BDIAgentInspectorDebuggerPanel	implements IDebuggerPanel
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"contents", SGUI.makeIcon(BDIAgentInspectorDebuggerPanel.class, "/jadex/tools/common/images/bug_small.png")
	});

	//-------- IDebuggerPanel methods --------
	
	/** The gui component. */
	protected JComponent	oavpanel;

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
		this.oavpanel	= new JPanel(new BorderLayout());
		
		// Hack!!!
		final BDIInterpreter bdii = ((ElementFlyweight)access).getInterpreter();
		// Open tool on introspected agent thread as required for copy state constructor (hack!!!)
		bdii.getAgentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				final	JPanel	tmp	= new OAVPanel(bdii.getRuleSystem().getState());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						oavpanel.add(BorderLayout.CENTER, tmp);
						oavpanel.invalidate();
						oavpanel.doLayout();
						oavpanel.repaint();
					}
				});
			}
		});
	}

	/**
	 *  The title of the panel (name of the tab).
	 *  @return	The tab title.
	 */
	public String getTitle()
	{
		return "Agent Inspector";
	}

	/**
	 *  The icon of the panel.
	 *  @return The icon (or null, if none).
	 */
	public Icon getIcon()
	{
		return icons.getIcon("contents");
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return oavpanel;
	}
	
	/**
	 *  The tooltip text of the panel, if any.
	 *  @return The tooltip text, or null.
	 */
	public String getTooltipText()
	{
		return "Show the agent state.";
	}

}
