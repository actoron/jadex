package jadex.bdi.benchmarks;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Gui for displaying messages.
 */
public class MessageGui extends JFrame
{
	/**
	 *  Create a new message gui.
	 */
	public MessageGui(IExternalAccess agent)
	{
		final JLabel sent = new JLabel("Sent: [0]");
		final JLabel rec = new JLabel("Received: [0]");
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("addListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bdif = ia.getFeature(IBDIXAgentFeature.class);
//				bia.getBeliefbase().getBelief("sent").addBeliefListener(new IBeliefListener()
//				((BDIAgentFeature)bdif).getCapability().getBeliefbase().getBelief("sent").addBeliefListener(new IBeliefListener()
				bdif.getBeliefbase().getBelief("sent").addBeliefListener(new BeliefAdapter<Object>()
				{
					public void beliefChanged(jadex.rules.eca.ChangeInfo<Object> info) 
					{
						sent.setText("Sent: ["+info.getValue()+"]");
					}
				});
				
//				bia.getBeliefbase().getBelief("received").addBeliefListener(new IBeliefListener()
				bdif.getBeliefbase().getBelief("received").addBeliefListener(new BeliefAdapter<Object>()
				{
					public void beliefChanged(jadex.rules.eca.ChangeInfo<Object> info) 
					{
						rec.setText("Received: ["+info.getValue()+"]");
					}
				});
//				bia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{						
//								MessageGui.this.dispose();	
//							}
//						});
//					}
//				});
				
				ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						MessageGui.this.dispose();	
					}
				}));
				return IFuture.DONE;
			}
		});
		
		JPanel infos = new JPanel(new GridLayout(2,1));
		infos.add(sent);
		infos.add(rec);
		getContentPane().add(infos);
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
	}
}
