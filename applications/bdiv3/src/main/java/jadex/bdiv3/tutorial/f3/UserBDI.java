package jadex.bdiv3.tutorial.f3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.ServicePlan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that presents a gui and sends translation requests
 *  via goal delegation to the translation agent.
 */
@Agent(type=BDIAgentFactory.TYPE)
@RequiredServices(@RequiredService(name="transser", type=ITranslationService.class, scope=RequiredServiceInfo.SCOPE_PLATFORM))
@Goals(@Goal(clazz=TranslationGoal.class))
@Plans(@Plan(trigger=@Trigger(goals=TranslationGoal.class), body=@Body(service=@ServicePlan(name="transser"))))
public class UserBDI
{
	//-------- attributes --------

	@Agent
	protected IInternalAccess agent;

	@AgentFeature
	protected IExecutionFeature execFeature;

	@AgentFeature
	protected IBDIAgentFeature bdiFeature;
	
	/** The gui. */
	protected JFrame	f;
	
	//-------- methods ---------

	/**
	 *  The plan body.
	 */
	@AgentBody
	public void body()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				f = new JFrame();
				
				PropertiesPanel pp = new PropertiesPanel();
				final JTextField tfe = pp.createTextField("English Word", "dog", true);
				final JTextField tfg = pp.createTextField("German Word");
				JButton bt = pp.createButton("Initiate", "Translate");
				
				bt.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						execFeature.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								try
								{
									final String gword = (String)bdiFeature.dispatchTopLevelGoal(new TranslationGoal(tfe.getText())).get();
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											tfg.setText(gword);
										}
									});
								}
								catch(final Exception e)
								{
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											tfg.setText(e.getMessage());
										}
									});
								}
								
								return IFuture.DONE;
							}
						});
						
//						IFuture<String> fut = agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new TranslationGoal(tfe.getText()));
//						fut.addResultListener(new IResultListener<String>()
//						{
//							public void resultAvailable(String res) 
//							{
//								tfg.setText(res);
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								exception.printStackTrace();
//								tfg.setText(exception.getMessage());
//							}
//						});
					}
				});
				
				f.add(pp, BorderLayout.CENTER);
				
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
			}
		});
	}
	
	/**
	 *  Cleanup when agent is killed.
	 */
	@AgentKilled
	public void	cleanup()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(f!=null)
				{
					f.dispose();
				}
			}
		});
	}
}
