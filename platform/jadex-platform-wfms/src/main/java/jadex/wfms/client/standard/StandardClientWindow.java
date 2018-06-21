package jadex.wfms.client.standard;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class StandardClientWindow extends JFrame
{
	protected static final String WINDOW_TITLE = "Workflow Client Application";
	
	protected StandardClientApplication app;
	
	public StandardClientWindow(final IExternalAccess access)
	{
		access.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose") 
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								dispose();
							}
						});
					}
				});
				
				return IFuture.DONE;
			}
		});
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				app.disconnect();
				access.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("kill") 
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.killComponent();
						return IFuture.DONE;
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
