package jadex.micro.examples.ws;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="quoteservice", type=IQuoteService.class))
public class UserAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The text field. */
	protected JTextField tf;
	
	/**
	 *  Called when agent is born.
	 */
	@AgentCreated
	public void agentCreated()
	{
		JFrame f = new JFrame();
		f.setLayout(new BorderLayout());
		JPanel p = new JPanel(new FlowLayout());
		tf = new JTextField(10);
		JButton b = new JButton("get");
		p.add(tf);
		p.add(b);
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);

		final IExternalAccess exta = agent.getExternalAccess();
		
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String exchange = null;
				final String stock = tf.getText();
				final String time = null;
				
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						final Future<Void> ret = new Future<Void>();
						IFuture<IQuoteService> fut = ia.getServiceContainer().getRequiredService("quoteservice");
						fut.addResultListener(new ExceptionDelegationResultListener<IQuoteService, Void>(ret)
						{
							public void customResultAvailable(IQuoteService qs)
							{
								qs.getQuote(exchange, stock, time)
									.addResultListener(new ExceptionDelegationResultListener<BigDecimal, Void>(ret)
								{
									public void customResultAvailable(BigDecimal result) 
									{
										System.out.println("quote: "+result);
									};
								});
							}
						});
						return ret;
					}
				});
			}
		});
	}
}


