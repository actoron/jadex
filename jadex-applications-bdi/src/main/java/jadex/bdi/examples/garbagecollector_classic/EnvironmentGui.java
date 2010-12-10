package jadex.bdi.examples.garbagecollector_classic;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.SGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The gui plan.
 */
public class EnvironmentGui	extends JFrame
{
	//-------- constructors --------

	/**
	 *  Create a new gui.
	 */
	public EnvironmentGui(final IBDIExternalAccess agent)
	{
		super("Garbage Collector Environment");
		
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				final Environment env = (Environment)bia.getBeliefbase().getBelief("env").getFact();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						MapPanel map = new MapPanel(env);
						getContentPane().add("Center", map);
						
						setSize(400, 400);
						setLocation(SGUI.calculateMiddlePosition(EnvironmentGui.this));
						setVisible(true);
					}
				});
				return null;
			}
		});
//		agent.getBeliefbase().getBeliefFact("env").addResultListener(new SwingDefaultResultListener(this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				MapPanel map = new MapPanel((Environment)result);
//				getContentPane().add("Center", map);
//				
//				setSize(400, 400);
//				setLocation(SGUI.calculateMiddlePosition(EnvironmentGui.this));
//				setVisible(true);
//			}
//		});
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				// Shutdown environment agent to close application (due to master flag).
				agent.killComponent();
			}
		});
		
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new IComponentListener()
				{
					public void componentTerminating(ChangeEvent ae)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								EnvironmentGui.this.dispose();
							}
						});
					}
					
					public void componentTerminated(ChangeEvent ae)
					{
					}
				});		
				return null;
			}
		});
	}
}

