package jadex.platform.service.servicepool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;

@Agent
public class ManualUserAgent extends UserAgent
{
	/**
	 *  Use the services.
	 */
	protected IFuture<Void> useServices(final IAService aser, final IBService bser)
	{
		final Future<Void> ret = new Future<Void>();
	
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame f = new JFrame();
				JButton ba = new JButton("Service A");
				JButton bb = new JButton("Service B");
				JPanel p = new JPanel(new BorderLayout());
				p.add(ba, BorderLayout.WEST);
				p.add(bb, BorderLayout.EAST);
				f.add(p, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
				
				ba.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final String callid = SUtil.createPlainRandomId("ma1", 3);
						System.out.println("service called: "+callid);
						aser.ma2().addResultListener(new IResultListener<Collection<Integer>>()
						{
							public void resultAvailable(Collection<Integer> result)
							{
								System.out.println("service finished: "+callid);
							}
							
							public void exceptionOccurred(Exception exception)
							{
							}
						});
					}
				});
				bb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						bser.mb1("def");
					}
				});
			}
		});
	
		return ret;
	}

}
