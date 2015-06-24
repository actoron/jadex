package jadex.tools.debugger.bdi;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.IBreakpointPanel;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.kernelbase.ExternalAccess;
import jadex.rules.tools.stateviewer.OAVPanel;
import jadex.tools.debugger.IDebuggerPanel;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
		"contents", SGUI.makeIcon(BDIAgentInspectorDebuggerPanel.class, "/jadex/tools/debugger/bdi/images/bug_small.png")
	});

	//-------- IDebuggerPanel methods --------
	
	/** The oav panel. */
	protected OAVPanel	oavpanel;

	/** The gui component (oavpanel or empty label). */
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
	public void init(IControlCenter jcc, IBreakpointPanel bpp, IComponentIdentifier name, final IExternalAccess access)
	{
		if(access instanceof ExternalAccess)
		{
			this.component	= new JPanel(new BorderLayout());
			
			// Hack!!!
			final IInternalAccess bdii = ((ExternalAccess)access).getInternalAccess();
			// Open tool on introspected agent thread as required for copy state constructor (hack!!!)
			bdii.getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IInternalBDIAgentFeature bdii = (IInternalBDIAgentFeature)((ExternalAccess)access)
						.getInternalAccess().getComponentFeature(IBDIAgentFeature.class);
					oavpanel	= new OAVPanel(bdii.getRuleSystem().getState());
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							component.add(BorderLayout.CENTER, oavpanel);
							component.invalidate();
							component.doLayout();
							component.repaint();
						}
					});
					return IFuture.DONE;
				}
			});
		}
		else
		{
			JLabel	emptylabel	= new JLabel("BDI introspector only supported for local components.", JLabel.CENTER);
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
		return component;
	}
	
	/**
	 *  The tooltip text of the panel, if any.
	 *  @return The tooltip text, or null.
	 */
	public String getTooltipText()
	{
		return "Show the agent state.";
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
		if(oavpanel!=null)
		{
			oavpanel.dispose();
		}
	}
}
