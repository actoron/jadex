package jadex.bdiv3.tutorial;

import jadex.bdiv3.BDIAgent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *  User agent that presents a gui for using the 
 *  translation service of the translation agent.
 */
@Agent
public class UserB1BDI
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
	//-------- methods ---------
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JFrame f = new JFrame();
				
				PropertiesPanel pp = new PropertiesPanel();
				final JTextField tfe = pp.createTextField("English Word", "dog", true);
				final JTextField tfg = pp.createTextField("German Word");
				JButton bt = pp.createButton("Initiate", "Translate");
				
				bt.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// Search a translation service
						SServiceProvider.getServices(agent.getServiceProvider(), ITranslationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new IIntermediateResultListener<ITranslationService>()
						{
							public void intermediateResultAvailable(ITranslationService ts)
							{
								// Invoke translate on the service.
								ts.translateEnglishGerman(tfe.getText())
									.addResultListener(new SwingResultListener<String>(new IResultListener<String>()
								{
									public void resultAvailable(String gword) 
									{
										tfg.setText(gword);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										exception.printStackTrace();
										tfg.setText(exception.getMessage());
									}
								}));
							}

							public void exceptionOccurred(Exception exception)
							{
							}
							
							public void finished()
							{
							}
							
							public void resultAvailable(Collection<ITranslationService> result)
							{
							}
						});
					}
				});
				
				f.add(pp, BorderLayout.CENTER);
				
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
				
				// Dispose frame on exception.
				IResultListener<Void>	dislis	= new IResultListener<Void>()
				{
					public void exceptionOccurred(Exception exception)
					{
						f.dispose();
					}
					public void resultAvailable(Void result)
					{
					}
				};
				
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("dispose")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false)
							.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
						{
							public void intermediateResultAvailable(IMonitoringEvent result)
							{
								f.dispose();
							}
						}));
						
						return IFuture.DONE;
					}
				}).addResultListener(dislis);
			}
		});
	}
}
