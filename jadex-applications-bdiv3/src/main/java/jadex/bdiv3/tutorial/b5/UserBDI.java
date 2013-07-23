package jadex.bdiv3.tutorial.b5;

import jadex.bdiv3.BDIAgent;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

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
@Description("User agent that presents a gui for using the translation service of the translation agent.")
public class UserBDI
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
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
				JFrame f = new JFrame();
				
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
			}
		});
	}
}
