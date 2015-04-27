package jadex.tools.debugger.bdi;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.IBreakpointPanel;
import jadex.commons.gui.SGUI;
import jadex.kernelbase.ExternalAccess;
import jadex.rules.tools.reteviewer.RetePanel;
import jadex.tools.debugger.IDebuggerPanel;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
		"show_rete", SGUI.makeIcon(BDIAgentRuleEngineDebuggerPanel.class, "/jadex/tools/debugger/bdi/images/bug_small.png"),
		"empty", SGUI.makeIcon(BDIAgentRuleEngineDebuggerPanel.class, "/jadex/tools/debugger/bdi/images/introspector_empty.png")
	});

	//-------- IDebuggerPanel methods --------
	
	/** The rete panel. */
	protected RetePanel retepanel;
	
	/** The gui component (rete panel or empty label). */
	protected JComponent	component;

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
		if(access instanceof ExternalAccess)
		{
			IInternalBDIAgentFeature bdii = (IInternalBDIAgentFeature)((ExternalAccess)access)
				.getInternalAccess().getComponentFeature(IBDIAgentFeature.class);
			this.retepanel = new RetePanel(bdii.getRuleSystem(), null, bpp);
			this.component	= retepanel;
		}
		else
		{
			JLabel	emptylabel	= new JLabel("Rule engine debugger only supported for local components.", JLabel.CENTER);
			emptylabel.setVerticalAlignment(JLabel.CENTER);
			emptylabel.setHorizontalTextPosition(JLabel.CENTER);
			emptylabel.setFont(emptylabel.getFont().deriveFont(emptylabel.getFont().getSize()*1.3f));
			this.component	= new JPanel(new BorderLayout());
			component.add(emptylabel, BorderLayout.CENTER);
		}
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
		return component;
	}
	
	/**
	 *  The tooltip text of the panel, if any.
	 *  @return The tooltip text, or null.
	 */
	public String getTooltipText()
	{
		return "Show the rule engine.";
	}
	
	/**
	 *  Get the step info. Help to decide which component step to perform next.
	 *  @return Step info for debugging.
	 */
	public String getStepInfo()
	{
		return null;
	}

	/**
	 *  Dispose the component.
	 */
	public void dispose()
	{
		if(retepanel!=null)
		{
			retepanel.dispose();
		}
	}
}
