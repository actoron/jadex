package jadex.micro.tutorial;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

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
				agent.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IFuture<Collection<IExtendedChatService>>	chatservices	= ia.getComponentFeature(IRequiredServicesFeature.class).getServices("chatservices");
						chatservices.addResultListener(new DefaultResultListener<Collection<IExtendedChatService>>()
						{
							public void resultAvailable(Collection<IExtendedChatService> result)
							{
								for(Iterator<IExtendedChatService> it=result.iterator(); it.hasNext(); )
								{
									IExtendedChatService cs = it.next();
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
						return IFuture.DONE;
					}
				});
			}
		});
	}
}
