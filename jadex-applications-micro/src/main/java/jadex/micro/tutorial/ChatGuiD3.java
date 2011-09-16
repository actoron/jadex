package jadex.micro.tutorial;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

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
		JPanel p = new JPanel();//new BorderLayout());
		p.add(profiles);//, BorderLayout.CENTER);
		getContentPane().add(p, BorderLayout.NORTH);
		profiles.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						IFuture<Collection<IChatService>>	chatservices	= ia.getServiceContainer().getRequiredServices("chatservices");
						chatservices.addResultListener(new DefaultResultListener<Collection<IChatService>>()
						{
							public void resultAvailable(Collection<IChatService> result)
							{
								for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
								{
									IExtendedChatService cs = (IExtendedChatService)it.next();
									cs.getUserProfile().addResultListener(new DefaultResultListener<UserProfileD3>()
									{
										public void resultAvailable(UserProfileD3 result)
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
