package jadex.tools.introspector;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.rules.tools.reteviewer.RetePanel;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 *  A generic tool panel for easy integration into the introspector.
 */
public class ToolPanel	extends JPanel
{
	//-------- constants --------

	/** The text when nothing is selected in the details view. */
	public static final String NOTHING = "No element selected for detailed view.\nUse double-click to view element.";

	//-------- attributes --------

	/** The agent access. */
	protected IExternalAccess	agent;
	
	/** The agent to observe. */
	protected IAgentIdentifier	observed;
	
	/** The tab panel. */
	protected JTabbedPane tabs;

	/** The tool components. */
	protected JComponent[]	tools;

	//-------- constructors --------

	/**
	 *  Create a new tool panel for a remote agent.
	 *  @param agent	The agent access.
	 *  @param active	Flags indicating which tools should be active.
	 */
	public ToolPanel(IExternalAccess agent, IAgentIdentifier observed, final boolean[] active)
	{
		this.agent	= agent;
		this.observed	= observed;
		this.tabs = new JTabbedPane();

        // Hack!?!?!
		((IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE))
			.getExternalAccess(observed, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(final Object result)
			{
				// Hack!!!
				final BDIInterpreter bdii = ((ElementFlyweight)result).getInterpreter();
//				StandaloneAgentAdapter	adapter	= (StandaloneAgentAdapter)result;
//				final BDIInterpreter bdii	= (BDIInterpreter)adapter.getJadexAgent();
				// Open tool on introspected agent thread (hack!!!)
				bdii.invokeLater(new Runnable()
				{
					public void run()
					{
						final IntrospectorAdapter	tooladapter	= (IntrospectorAdapter)bdii.getToolAdapter(IntrospectorAdapter.class);
						final JComponent	toolpanel	= RetePanel.createToolPanel(bdii.getRuleSystem(), tooladapter);
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								setLayout(new BorderLayout());
								add("Center", toolpanel);
								
								doLayout();
								repaint();
							}
						});
					}
				});
			}
		});
	}	
}
