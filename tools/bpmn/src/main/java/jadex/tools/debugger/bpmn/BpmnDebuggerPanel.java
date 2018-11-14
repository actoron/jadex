package jadex.tools.debugger.bpmn;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bpmn.tools.ui.VisualProcessViewPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.IBreakpointPanel;
import jadex.commons.gui.SGUI;
import jadex.tools.debugger.IDebuggerPanel;

/**
 *  A bpmndebugger panel that can display bpmn processes.
 */
public class BpmnDebuggerPanel implements IDebuggerPanel
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"contents", SGUI.makeIcon(BpmnDebuggerPanel.class, "/jadex/tools/common/images/bug_small.png")
	});

	//-------- IDebuggerPanel methods --------
	
	/** The gui component. */
//	protected ProcessViewPanel processpanel;
	protected VisualProcessViewPanel processpanel;

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
//		this.processpanel = new ProcessViewPanel(access, bpp);
		this.processpanel = new VisualProcessViewPanel(access, bpp, jcc.getCMSHandler());
	}

	/**
	 *  The title of the panel (name of the tab).
	 *  @return	The tab title.
	 */
	public String getTitle()
	{
		return "Process Inspector";
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
		return processpanel;
	}
	
	/**
	 *  The tooltip text of the panel, if any.
	 *  @return The tooltip text, or null.
	 */
	public String getTooltipText()
	{
		return "Show the process state and history.";
	}
	
	/**
	 *  Get the step info. Help to decide which component step to perform next.
	 *  @return Step info for debugging.
	 */
	public String getStepInfo()
	{
		return processpanel.getStepInfo();
	}
	
	/**
	 *  Dispose the component.
	 */
	public void dispose()
	{
		if(processpanel!=null)
		{
			processpanel.dispose();
		}
	}

}
