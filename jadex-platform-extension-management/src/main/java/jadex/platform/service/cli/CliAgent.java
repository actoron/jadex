package jadex.platform.service.cli;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.micro.IntervalBehavior;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  The client agent allows for executing command line commands.
 *  
 *  It offers the executeCommand() method via the ICliService.
 */
@Agent
@Service
@Arguments(
{
	@Argument(name="console", clazz=boolean.class, description="Flag if a console reader should be opened.", defaultvalue="true"),
	@Argument(name="gui", clazz=boolean.class, description="Flag if a gui for console in and out should be opened.", defaultvalue="false"),
	@Argument(name="shelltimeout", clazz=boolean.class, description="The timeout after whichs shells become garbage collected (default = 5 mins).", defaultvalue="5*60*1000")
})
@ProvidedServices(
{
	@ProvidedService(name="cliser", type=ICliService.class, implementation=@Implementation(expression="$pojoagent")),
	@ProvidedService(type=IInternalCliService.class, implementation=@Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"cliser\")"))
})
@RequiredServices(
	@RequiredService(name="dtp", type=IDaemonThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
)
@Properties(@NameValue(name="system", value="true"))
public class CliAgent implements ICliService, IInternalCliService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The gui flag. */
	@AgentArgument
	protected boolean gui;

	/** The console flag. */
	@AgentArgument
	protected boolean console;
	
	/** The shell timeout. */
	@AgentArgument
	protected long shelltimeout;;
	
	/** The shells per session. */
	protected Map<Tuple2<String, Integer>, Tuple2<ACliShell, Long>> shells;
	
	/** Flag if the agent is killed. */
	protected boolean	aborted;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		shells = new HashMap<Tuple2<String, Integer>, Tuple2<ACliShell, Long>>();
		
		if(gui)
			createGui();
		
		if(console)
			createConsole();
		
		IntervalBehavior<Void> b = new IntervalBehavior<Void>(agent, 30000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				long ct = System.currentTimeMillis();
				Tuple2<String, Integer>[] keys = (Tuple2<String, Integer>[])shells.keySet().toArray(new Tuple2[0]);
				for(Tuple2<String, Integer> key: keys)
				{
					Tuple2<ACliShell, Long> val = shells.get(key);
					
					if(ct-shelltimeout>val.getSecondEntity().longValue())
					{
						removeShell(key);
					}
				}
				return IFuture.DONE;
			}
		}, true);
		b.startBehavior();
	}
	
	/**
	 *  Called when the agent is killed.
	 */
	@AgentKilled
	public void	killed()
	{
		aborted	= true;
	}
	
	/**
	 *  Create a gui frame for console in and out.
	 */
	protected void createGui()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final JTextField tf = new JTextField(20);
				final JTextArea ta = new JTextArea(40, 20);
				ta.setEditable(false);

				final Tuple2<String, Integer> guisess = new Tuple2<String, Integer>(SUtil.createUniqueId("guisess"), Integer.valueOf(0));
				tf.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ICliService clis = (ICliService)ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedServices(ICliService.class)[0];
								String txt = tf.getText();
								ta.append(txt+SUtil.LF);
								tf.setText("");
								clis.executeCommand(txt, guisess).addResultListener(new IResultListener<String>()
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
	 *  Create a console reader.
	 */
	protected void createConsole()
	{
		IFuture<IDaemonThreadPoolService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("dtp");
		fut.addResultListener(new IResultListener<IDaemonThreadPoolService>()
		{
			public void resultAvailable(IDaemonThreadPoolService tp)
			{
				proceed(tp);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				proceed(null);
			}
			
			protected void proceed(final IThreadPool tp)
			{
				Runnable reader = new Runnable()
				{
					public void run()
					{
//						ThreadSuspendable sus = new ThreadSuspendable();
						final Tuple2<String, Integer> consess = new Tuple2<String, Integer>(SUtil.createUniqueId("consess"), Integer.valueOf(0));
						System.out.println("Jadex shell (type 'h' for help)");
						System.out.println(getShell(consess).getShellPrompt().get());
						// redirect System.in
						try{SUtil.getOutForSystemIn();}catch(Exception e){}
//						System.out.println("sysin: "+System.in+" "+System.in.getClass());
						BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//						Scanner sc = new Scanner(System.in);
						
						try
						{
							while(!aborted)
							{
//								String tmp = sc.nextLine();
//								System.out.println(tmp);
								if(br.ready())
								{
									final String tmp = br.readLine();
									if(tmp==null)	// null means end of stream.
									{
										break;
									}
									
									final String cmd = tmp.endsWith(";")? tmp.substring(0, tmp.length()-1): tmp;
									if("exit".equals(cmd) || "quit".equals(cmd))
									{
										break;
									}
									
									agent.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
									{
										public jadex.commons.future.IFuture<Void> execute(IInternalAccess ia) 
										{
											final Future<Void> ret = new Future<Void>();
											
											if(cmd.length()>0)
											{
												executeCommand(cmd, consess).addResultListener(new IResultListener<String>()
												{
													public void resultAvailable(String result)
													{
														if(result!=null)
															System.out.println(result);
														printPrompt();
													}
													
													public void exceptionOccurred(Exception exception)
													{
														System.out.println("Invocation error: "+exception.getMessage());
														printPrompt();
													}
													
													protected void printPrompt()
													{
														getShell(consess).getShellPrompt().addResultListener(new ExceptionDelegationResultListener<String, Void>(ret)
														{
															public void customResultAvailable(String result)
															{
																System.out.println(result);
																ret.setResult(null);
															}
														});
													}
												});
											}
											else
											{
												getShell(consess).getShellPrompt().addResultListener(new ExceptionDelegationResultListener<String, Void>(ret)
												{
													public void customResultAvailable(String result)
													{
														System.out.println(result);
														ret.setResult(null);
													}
												});
											}
											
											return ret;
										}
									}).get();
								}
								else
								{
									Thread.sleep(500);
								}
							}
						}
						catch(Exception e)
						{
							agent.getLogger().warning("Console closed due to "+e);
						}
					}
				};
				if(tp!=null)
				{
					tp.execute(reader);
				}
				else
				{
					Thread t = new Thread(reader);
					t.setDaemon(true);
					t.start();
				}
			}
		});
	}
	
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(String line, Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).executeCommand(line);
	}
	
	/**
	 *  Get the shell prompt.
	 *  @param sessionid The session id.
	 *  @return The prompt.
	 */
	public IFuture<String> internalGetShellPrompt(Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).internalGetShellPrompt();
	}
	
	/**
	 *  Remove a subshell.
	 *  @param sessionid The session id.
	 *  @return True, if could be removed.
	 */
	public IFuture<Boolean> removeSubshell(Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).removeSubshell();
	}
	
	/**
	 *  Add all commands from classpath.
	 *  @param sessionid The session id.
	 */
	public IFuture<Void> addAllCommandsFromClassPath(Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).addAllCommandsFromClassPath();
	}
	
	/**
	 *  Add a specific command.
	 *  @param sessionid The session id.
	 */
	public IFuture<Void> addCommand(ICliCommand cmd, Tuple2<String, Integer> sessionid)
	{
		return getShell(sessionid).addCommand(cmd);
	}
	
	/**
	 *  Get the shell.
	 *  @param session The session.
	 *  @return The shell.
	 */
	public ACliShell getShell(Tuple2<String, Integer> sessionid)
	{
		if(sessionid==null)
			throw new IllegalArgumentException("Must not null");
		
		// todo: remove obsolete shells
		
		Tuple2<ACliShell, Long> tup = shells.get(sessionid);
		ACliShell shell;
		if(tup==null)
		{
//			System.out.println("created new shell for session: "+sessionid);
			shell = new CliShell(agent.getExternalAccess(), agent.getExternalAccess().getComponentIdentifier().getRoot().getName(), sessionid, agent.getClassLoader());
			shell.addAllCommandsFromClassPath(); // agent.getClassLoader()
			shells.put(sessionid, new Tuple2<ACliShell, Long>(shell, Long.valueOf(System.currentTimeMillis())));
		}
		else
		{
			shell = tup.getFirstEntity();
			shells.put(sessionid, new Tuple2<ACliShell, Long>(shell, Long.valueOf(System.currentTimeMillis())));
		}
		return shell;
	}
	
	/**
	 *  Remove a shell.
	 *  @param session The session.
	 */
	public void removeShell(Tuple2<String, Integer> sessionid)
	{
		shells.remove(sessionid);
	}
}
