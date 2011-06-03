package jadex.wfms.client.standard;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;

import javax.swing.JFrame;

public class StandardClientWindow extends JFrame
{
	protected static final String WINDOW_TITLE = "Workflow Client Application";
	
	protected StandardClientApplication app;
	
	public StandardClientWindow(final IExternalAccess access)
	{
		access.scheduleStep(new IComponentStep()
		{
			@XMLClassname("dispose") 
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						EventQueue.invokeLater(new Runnable()
						{
							
							public void run()
							{
								dispose();
							}
						});
					}
				});
				
				return null;
			}
		});
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				app.disconnect();
				access.scheduleStep(new IComponentStep()
				{
					@XMLClassname("kill") 
					public Object execute(IInternalAccess ia)
					{
						ia.killComponent();
						return null;
					}
				});
				dispose();
			}
		});
		
		app = new StandardClientApplication(access);
		getContentPane().add(app.getView());
		
		pack();
		setSize(800, 550);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
	}
}
