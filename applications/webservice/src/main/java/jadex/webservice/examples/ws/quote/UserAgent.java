package jadex.webservice.examples.ws.quote;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that lets a user interact with the web service offering a user interface. 
 */
@Agent
@RequiredServices(@RequiredService(name="quoteservice", type=IQuoteService.class))
public class UserAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The gui. */
	protected JFrame	f;

	//-------- methods --------
	
	/**
	 *  Called when agent is born.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void>	cret	= new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				f = new JFrame();
				f.setLayout(new BorderLayout());
				JPanel p = new JPanel(new GridBagLayout());
				final JTextField	tfsymbol = new JTextField(10);
				tfsymbol.setText("Google");
				JButton b = new JButton("get");
				final JTextField	tfresult = new JTextField(10);
				p.add(tfsymbol, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, 
					GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
				p.add(b, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, 
					GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
				p.add(tfresult, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.WEST, 
					GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
				f.add(p, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);

				final IExternalAccess exta = agent.getExternalAccess();
				
				b.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final String stock = tfsymbol.getText();
//						System.out.println("stock is: "+stock);
						
						exta.scheduleStep(new IComponentStep<Object>()
						{
							public IFuture<Object> execute(IInternalAccess ia)
							{
								final Future<Object> ret = new Future<Object>();
								IFuture<IQuoteService> fut = ia.getComponentFeature(IRequiredServicesFeature.class).getService("quoteservice");
								fut.addResultListener(new ExceptionDelegationResultListener<IQuoteService, Object>(ret)
								{
									public void customResultAvailable(IQuoteService qs)
									{
										qs.getQuote(stock)
											.addResultListener(new ExceptionDelegationResultListener<String, Object>(ret)
										{
											public void customResultAvailable(String result) 
											{
												ret.setResult(result);
//												System.out.println("quote: "+result);
											};
										});
									}
								});
								return ret;
							}
						}).addResultListener(new SwingDefaultResultListener<Object>()
						{
							public void customResultAvailable(Object result)
							{
								tfresult.setText(""+result);
							}
						});
					}
				});
				cret.setResult(null);
			}
		});
		
		return cret;
	}
	
	
	/**
	 *  Called when the agent is killed.
	 */
	@AgentKilled
	public IFuture<Void>	cleanup()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				f.dispose();
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
