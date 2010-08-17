package jadex.tools.serviceviewer.awareness;

import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.serviceviewer.IComponentViewerPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 
 */
public class AwarenessAgentPanel implements IComponentViewerPanel
{
	protected IControlCenter jcc;
	
	protected IExternalAccess component;
	
	protected JPanel p;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public void init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = component;
		
		p = new JPanel(new BorderLayout());
		p.add(new JButton("test"), BorderLayout.CENTER);
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public void shutdown()
	{
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "awarenessviewer";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return p;
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}
}
