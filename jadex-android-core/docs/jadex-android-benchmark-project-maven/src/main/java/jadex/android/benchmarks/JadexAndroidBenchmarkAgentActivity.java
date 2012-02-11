package jadex.android.benchmarks;

import jadex.base.Starter;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Binding;
import jadex.micro.benchmarks.MessagePerformanceAgent;
import jadex.micro.examples.chat.IChatService;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 *  Activity (screen) for the jadex android benchmark app.
 *  Starts the platform and allows launching the different benchmarks.
 */
public class JadexAndroidBenchmarkAgentActivity extends Activity
{
	//-------- attributes --------
	
	/** The jadex platform access. */
	private IExternalAccess platform;
	
	/** Button to start the first message performance test. */
	private Button startMB1;
	
	/** Button to start the second message performance test. */
	private Button startMB2;
	
	/** Button to start the third message performance test. */
	private Button startMB3;
	
	/** The text view for showing results. */
	private TextView textView;
	
	/** The chat agent (if any). */
	private IComponentIdentifier	chatcid;

	
	//-------- methods --------

	/**
	 *  Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agntmain);

		startMB1 = (Button)findViewById(R.id.startAB1);
		startMB2 = (Button)findViewById(R.id.startAB2);
		startMB3 = (Button)findViewById(R.id.startAB3);
		startMB1.setOnClickListener(buttonListener);
		startMB2.setOnClickListener(buttonListener);
		startMB3.setOnClickListener(buttonListener);
		startMB1.setEnabled(false);
		startMB2.setEnabled(false);
		startMB3.setEnabled(false);

		final Button	chat	= (Button)findViewById(R.id.chat);
		final Button	send	= (Button)findViewById(R.id.send);
		final EditText	name	= (EditText)findViewById(R.id.chatname);
		final EditText	msg	= (EditText)findViewById(R.id.chatmsg);
		chat.setEnabled(false);
		send.setEnabled(false);
		name.setEnabled(false);
		msg.setEnabled(false);
		
		chat.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				chat.setEnabled(false);
				name.setEnabled(false);
				send.setEnabled(false);
				msg.setEnabled(false);
				platform.scheduleStep(new IComponentStep<IComponentIdentifier>()
				{
					public IFuture<IComponentIdentifier> execute(IInternalAccess ia)
					{
						final Future<IComponentIdentifier>	fut	= new Future<IComponentIdentifier>();
						ia.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(fut)
						{
							public void customResultAvailable(IComponentManagementService cms)
							{
								if(chatcid==null)
								{
									cms.createComponent(name.getText().toString(), "jadex.android.benchmarks.ChatAgent.class", null, null)
										.addResultListener(new DelegationResultListener<IComponentIdentifier>(fut));
								}
								else
								{
									cms.destroyComponent(chatcid).addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, IComponentIdentifier>(fut)
									{
										public void customResultAvailable(Map<String, Object> result)
										{
											fut.setResult(null);
										}
									});
								}
							}
						});
						
						return fut;
					}
				}).addResultListener(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(final IComponentIdentifier result)
					{
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								chatcid	= result;
								chat.setEnabled(true);
								if(result!=null)
								{
									chat.setText("Exit Chat");
									send.setEnabled(true);
									msg.setEnabled(true);
									System.out.println("Connected to chat.");
								}
								else
								{
									chat.setText("Enter Chat");
									name.setEnabled(true);
									System.out.println("Disconnected from chat.");									
								}
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Chat connection problem: "+exception);
						exception.printStackTrace();
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								chat.setEnabled(true);
							}
						});
					}
				});
			}
		});

		send.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(chatcid==null)
					return;
				
				send.setEnabled(false);
				msg.setEnabled(false);
				
				SServiceProvider.getService(platform.getServiceProvider(), IComponentManagementService.class, Binding.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener<IComponentManagementService>()
				{
					public void resultAvailable(IComponentManagementService cms)
					{
						cms.getExternalAccess(chatcid).addResultListener(new DefaultResultListener<IExternalAccess>()
						{
							public void resultAvailable(IExternalAccess exta)
							{
								exta.scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										IIntermediateFuture<IChatService>	chats	= ia.getServiceContainer().getRequiredServices("chats");
										chats.addResultListener(new IIntermediateResultListener<IChatService>()
										{
											public void intermediateResultAvailable(IChatService result)
											{
												result.message(msg.getText().toString());
											}
											
											public void finished()
											{
												runOnUiThread(new Runnable()
												{
													public void run()
													{
														msg.setText("");
														send.setEnabled(true);
														msg.setEnabled(true);								
													}
												});
											}
											
											public void exceptionOccurred(Exception exception)
											{
												System.out.println("Chat message problem: "+exception);
												exception.printStackTrace();
												runOnUiThread(new Runnable()
												{
													public void run()
													{
														send.setEnabled(true);
														msg.setEnabled(true);								
													}
												});
											}
											
											public void resultAvailable(Collection<IChatService> result)
											{
											}
										});
										return IFuture.DONE;
									}
								});
							}
						});
					}
				});
			}
		});
		
		textView = (TextView) findViewById(R.id.agntTextView);
		SUtil.addSystemOutListener(new IChangeListener<String>()
		{
			public void changeOccurred(final ChangeEvent<String> event)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						textView.append(event.getValue() + "\n");
					}
				});
			}
		});
		
		// Start the platform
		System.out.println("Starting Jadex Platform...");
		new Thread(new Runnable()
		{
			public void run()
			{
				Starter.createPlatform(new String[]
				{
					"-logging_level", "java.util.logging.Level.INFO",
					"-platformname", "and_" + createRandomPlattformID(),
					"-extensions", "null",
					"-wspublish", "false",
					"-rspublish", "false",
					"-kernels", "\"component, micro\"",
//					"-tcptransport", "false",
					"-niotcptransport", "false",
					"-relaytransport", "true",
//					"-relayaddress", "\""+SRelay.DEFAULT_ADDRESS+"\"",
//					"-relayaddress", "\""+SRelay.ADDRESS_SCHEME+"134.100.11.200:8080/jadex-platform-relay-web/\"",					
					"-saveonexit", "false",
					"-gui", "false",
					"-autoshutdown", "false",
//					"-awamechanisms", "new String[]{\"Relay\"}",
//					"-awareness", "false",
//					"-usepass", "false"
				}).addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess result)
					{
						platform = result;
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								startMB1.setEnabled(true);
								startMB2.setEnabled(true);
								startMB3.setEnabled(true);
								chat.setEnabled(true);
								name.setEnabled(true);
							}
						});
					}
					
					public void exceptionOccurred(final Exception exception)
					{
						exception.printStackTrace();
						System.out.println("Start of platform failed: "+exception);
					}
				});
			}
		}).start();
	}
	
	//-------- helper methods --------

	/**
	 *  The button listener starts a benchmarks and re-enables buttons
	 *  when the benchmark has finished.
	 */
	private OnClickListener buttonListener = new OnClickListener()
	{
		public void onClick(View view)
		{
			String	agent	= MessagePerformanceAgent.class.getName().replaceAll("\\.", "/")+".class";
			Map<String, Object> args	= null;
			if(view == startMB2)
			{
				args	= new HashMap<String, Object>();
				args.put("codec", Boolean.TRUE);
			}
			else if(view == startMB3)
			{
				args	= new HashMap<String, Object>();
//				args.put("max", new Integer(2));
//				args.put("codec", Boolean.TRUE);
				args.put("echo", new ComponentIdentifier("echo@echo",
					new String[]{SRelay.DEFAULT_ADDRESS}));
//					new String[]{SRelay.ADDRESS_SCHEME+"134.100.11.200:8080/jadex-platform-relay-web/"}));
			}
			
			startMB1.setEnabled(false);
			startMB2.setEnabled(false);
			startMB3.setEnabled(false);
			
			runBenchmark(agent, args).addResultListener(new IResultListener<Collection<Tuple2<String, Object>>>()
			{
				public void resultAvailable(final Collection<Tuple2<String, Object>> result)
				{
					System.out.println(result);
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							startMB1.setEnabled(true);
							startMB2.setEnabled(true);
							startMB3.setEnabled(true);
						}
					});
				}
				
				public void exceptionOccurred(final Exception exception)
				{
					exception.printStackTrace();
					System.out.println("Benchmark failed: "+exception);
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							startMB1.setEnabled(true);
							startMB2.setEnabled(true);
							startMB3.setEnabled(true);
						}
					});
				}
			});
		}
	};

	/**
	 *  Run a benchmark and return the results.
	 */
	protected IFuture<Collection<Tuple2<String, Object>>>	runBenchmark(final String agent, final Map<String, Object> args)
	{
		return platform.scheduleStep(new IComponentStep<Collection<Tuple2<String, Object>>>()
		{
			@XMLClassname("create-component")
			public IFuture<Collection<Tuple2<String, Object>>> execute(IInternalAccess ia)
			{
				final Future<Collection<Tuple2<String, Object>>>	fut	= new Future<Collection<Tuple2<String, Object>>>();
				ia.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<Tuple2<String, Object>>>(fut)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
						cms.createComponent(null, agent, new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
							.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<Tuple2<String, Object>>>(fut)
						{
							public void customResultAvailable(IComponentIdentifier result)
							{
								// ignore (wait for agent termination)
							}
						});
					}
				});
				
				return fut;
			}
		});
	}
	
	/**
	 *  Generate a unique platform name.
	 */
	protected String createRandomPlattformID()
	{
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString().substring(0, 5);
	}

}