package jadex.micro.tutorial;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;

/**
 *  The chat gui with profile button.
 */
public class ChatGuiD3 extends ChatGuiD2
{
	/**
	 *  Create the user interface
	 */
	public ChatGuiD3(final IExternalAccess agent)
	{
		super(agent);
		JButton profiles = new JButton("Profiles");
		getContentPane().add(profiles, BorderLayout.NORTH);
		profiles.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						ia.getServiceContainer().getRequiredServices("chatservices")
							.addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
								{
									IExtendedChatService cs = (IExtendedChatService)it.next();
									cs.getUserProfile().addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											addMessage(result.toString());
										}
									});
								}
							}
						});
						return null;
					}
				});
			}
		});
	}
}
