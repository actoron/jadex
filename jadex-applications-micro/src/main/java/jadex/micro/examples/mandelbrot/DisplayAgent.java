package jadex.micro.examples.mandelbrot;

import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.SGUI;
import jadex.commons.service.RequiredServiceInfo;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 *  Agent offering a display service.
 */
public class DisplayAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The GUI. */
	protected DisplayPanel	panel;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		// Hack!!! Swing code not on swing thread!?
		DisplayAgent.this.panel	= new DisplayPanel(getExternalAccess());

		addService(new DisplayService(this));
		
		final IExternalAccess	access	= getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JFrame	frame	= new JFrame(getAgentName());
				JScrollPane	scroll	= new JScrollPane(panel);

				JTextPane helptext = new JTextPane();
				helptext.setText(DisplayPanel.HELPTEXT);
				helptext.setEditable(false);
				JPanel	right	= new JPanel(new BorderLayout());
				right.add(new ColorChooserPanel(panel), BorderLayout.CENTER);
				right.add(helptext, BorderLayout.NORTH);

				
				JSplitPane	split	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, right);
				split.setResizeWeight(1);
				split.setOneTouchExpandable(true);
				split.setDividerLocation(375);
				frame.getContentPane().add(BorderLayout.CENTER, split);
				frame.setSize(500, 400);
				frame.setLocation(SGUI.calculateMiddlePosition(frame));
				frame.setVisible(true);
				
				frame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						access.killComponent();
					}
				});
				
				access.scheduleStep(new IComponentStep()
				{
					public static final String XML_CLASSNAME = "dispose"; 
					public Object execute(IInternalAccess ia)
					{
						ia.addComponentListener(new IComponentListener()
						{
							public void componentTerminating(ChangeEvent ce)
							{
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										frame.setVisible(false);
									}
								});
							}
							
							public void componentTerminated(ChangeEvent ce)
							{
							}
						});
						
						return null;
					}
				});
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Get the display panel.
	 */
	public DisplayPanel	getPanel()
	{
		return this.panel;
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a display service.", null, null,
			null, null, null,
			new RequiredServiceInfo[]{
				new RequiredServiceInfo("generateservice", IGenerateService.class), 
				new RequiredServiceInfo("cmsservice", IComponentManagementService.class),
				new RequiredServiceInfo("progressservice", IProgressService.class), // not used
						},
			new Class[]{IDisplayService.class});
	}
}
