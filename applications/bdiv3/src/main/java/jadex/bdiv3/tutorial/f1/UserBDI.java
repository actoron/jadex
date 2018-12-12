package jadex.bdiv3.tutorial.f1;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Description;

/**
 *  User agent that presents a gui for using the 
 *  translation service of the translation agent.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Description("User agent that presents a gui for using the translation service of the translation agent.")
public class UserBDI
{
	//-------- attributes --------

	@Agent
	protected IInternalAccess agent;
	
	/** The gui. */
	protected JFrame	f;
	
	//-------- methods ---------
	
	/**
	 *  The agent body.
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
				
				f.add(pp, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
				
				bt.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// Search a translation service
						agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(ITranslationService.class, ServiceScope.PLATFORM))
							.addResultListener(new IntermediateDefaultResultListener<ITranslationService>()
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
						});
					}
				});
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
