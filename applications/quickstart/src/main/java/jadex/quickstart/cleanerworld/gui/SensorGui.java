package jadex.quickstart.cleanerworld.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSTerminatedEvent;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.gui.SGUI;
import jadex.quickstart.cleanerworld.environment.SensorActuator;


/**
 *  The GUI for the cleaner world example.
 *  Shows the world from the viewpoint of a single agent.
 */
public class SensorGui
{
	//-------- attributes --------

	// The window
	private JFrame	frame;
	
	// The repaint timer
	private Timer	timer;

	//-------- constructors --------

	/**
	 *  Creates a GUI that updates itself when beliefs change.
	 */
	public SensorGui(SensorActuator sensor)
	{
		String	id	= sensor.getSelf().getId();
		final IComponentIdentifier	cid	= sensor.getSelf().getAgentIdentifier();
		IInternalAccess	agent	= ExecutionComponentFeature.LOCAL.get();
		
		// Open window on swing thread
		SwingUtilities.invokeLater(()->
		{
			this.frame	= new JFrame(id);
			final JPanel map = new SensorPanel(sensor);

			frame.getContentPane().add(BorderLayout.CENTER, map);
			frame.setSize(300, 300);
			frame.setLocation(SGUI.calculateMiddlePosition(frame));
			frame.setVisible(true);
			
			// Repaint every 50 ms.
			timer	= new Timer(50, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					map.invalidate();
					map.repaint();
				}
			});
			timer.start();
			
			// Kill agent on window close.
			frame.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					agent.getExternalAccess().killComponent();
				}
			});
		});
		
		// Close window on agent kill.
		SComponentManagementService.listenToComponent(cid, agent)
			.addIntermediateResultListener(cse ->
		{
			if(cse instanceof CMSTerminatedEvent)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						timer.stop();
						frame.dispose();
					}
				});
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Show/hide the GUI.
	 */
	public void	setVisible(boolean visible)
	{
		SwingUtilities.invokeLater(()->frame.setVisible(visible));
	}		
}