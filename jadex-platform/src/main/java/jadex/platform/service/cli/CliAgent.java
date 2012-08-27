package jadex.platform.service.cli;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;


/**
 * 
 */
@Agent
@Service
public class CliAgent implements ICliService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The command line. */
	protected CliPlatform clip; 
	
	@AgentBody
	public void body()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JTextField tf = new JTextField(20);
				final JTextArea ta = new JTextArea();
				tf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						agent.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ICliService clis = (ICliService)ia.getServiceContainer().getProvidedServices(ICliService.class)[0];
								String txt = tf.getText();
								ta.append(txt+SUtil.LF);
								tf.setText("");
								clis.executeCommand(txt, ia.getExternalAccess()).addResultListener(new IResultListener<String>()
								{
									public void resultAvailable(String result)
									{
										ta.append(result+SUtil.LF);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										ta.append(exception.getMessage()+SUtil.LF);
									}
								});
								return IFuture.DONE;
							}
						});
					}
				});
				JPanel p = new JPanel(new BorderLayout());
				p.add(ta, BorderLayout.CENTER);
				p.add(tf, BorderLayout.SOUTH);
				JFrame f = new JFrame();
				f.add(p, BorderLayout.CENTER);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
			}
		});
	}
	
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(String line, Object context)
	{
		return clip.executeCommand(line, context);
	}

}
