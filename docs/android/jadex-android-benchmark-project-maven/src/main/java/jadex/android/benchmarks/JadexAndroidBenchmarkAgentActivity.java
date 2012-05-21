package jadex.android.benchmarks;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bdi.puzzle.agent.Main;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
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
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Binding;
import jadex.micro.benchmarks.MessagePerformanceAgent;
import jadex.micro.benchmarks.PojoAgentCreationAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	/** The chat gui service. */
	private IChatGuiService	chatgui;
	
	/** The chat subscription. */
	private ISubscriptionIntermediateFuture<ChatEvent>	subscription;
	
	/** The text view for showing results. */
	private TextView textView;
	
	//-------- methods --------

	/**
	 *  Called when the menu is shown.
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	/**
	 *  Called when a menu item is selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean	ret;
		
		if(item.getItemId()==R.id.creation || item.getItemId()==R.id.nocodec || item.getItemId()==R.id.codec
			|| item.getItemId()==R.id.remote || item.getItemId()==R.id.remotecodec)
		{
			String	agent	= item.getItemId()==R.id.creation
				? PojoAgentCreationAgent.class.getName().replaceAll("\\.", "/")+".class"
				: MessagePerformanceAgent.class.getName().replaceAll("\\.", "/")+".class";
			Map<String, Object> args	= new HashMap<String, Object>();
			if(item.getItemId()==R.id.codec || item.getItemId()==R.id.remotecodec)
			{
				args.put("codec", Boolean.TRUE);
			}
			if(item.getItemId()==R.id.remote || item.getItemId()==R.id.remotecodec)
			{
				args.put("echo", new ComponentIdentifier("echo@echo",
					new String[]{SRelay.DEFAULT_ADDRESS}));
//					new String[]{SRelay.ADDRESS_SCHEME+"134.100.11.200:8080/jadex-platform-relay-web/"}));
//					new String[]{SRelay.ADDRESS_SCHEME+"grisougarfield.dyndns.org:52339/relay/"}));
			}
			
			runBenchmark(agent, args).addResultListener(new IResultListener<Collection<Tuple2<String, Object>>>()
			{
				public void resultAvailable(final Collection<Tuple2<String, Object>> result)
				{
					System.out.println(result);
				}
				
				public void exceptionOccurred(final Exception exception)
				{
					exception.printStackTrace();
					System.out.println("Benchmark failed: "+exception);
				}
			});
			
			ret	= true;
	    }
		else if(item.getItemId()==R.id.puzzle)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					Main.main(new String[0]);					
				}
			}).start();
			
			ret	= super.onOptionsItemSelected(item);
		}
		else
		{
			ret	= super.onOptionsItemSelected(item);
		}
		
		return ret;
	}
	
	/**
	 *  Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agntmain);

		final Button	send	= (Button)findViewById(R.id.send);
		final EditText	msg	= (EditText)findViewById(R.id.chatmsg);
		msg.setEnabled(false);
		send.setEnabled(false);
		
		send.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				send.setEnabled(false);
				msg.setEnabled(false);
				
				SServiceProvider.getService(platform.getServiceProvider(), IChatGuiService.class, Binding.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener<IChatGuiService>()
				{
					public void resultAvailable(IChatGuiService chat)
					{
						chat.message(msg.getText().toString(), new IComponentIdentifier[0])
							.addResultListener(new IResultListener<Collection<IChatService>>()
						{
							public void resultAvailable(Collection<IChatService> result)
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
		bindService(new Intent(this, JadexPlatformService.class), new ServiceConnection()
		{
			public void onServiceConnected(ComponentName comp, IBinder service)
			{
				IJadexPlatformService	jps	= (IJadexPlatformService)service;
				jps.getPlatform().addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess result)
					{
						platform = result;
						platform.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ia.getServiceContainer().searchService(IChatGuiService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(new IResultListener<IChatGuiService>()
								{
									public void resultAvailable(IChatGuiService res)
									{
										chatgui	= res;
										subscription	= chatgui.subscribeToEvents();
										subscription.addResultListener(new IntermediateDefaultResultListener<ChatEvent>()
										{
											public void intermediateResultAvailable(ChatEvent ce)
											{
												if(ChatEvent.TYPE_MESSAGE.equals(ce.getType()))
												{
													StringBuffer buf = new StringBuffer();
													buf.append("[").append(ce.getComponentIdentifier().getName()).append("]: ").append(ce.getValue());
													System.out.println(buf);
												}
											}
										});
										
										runOnUiThread(new Runnable()
										{
											public void run()
											{
												System.out.println("Connected to chat.");
												msg.setEnabled(true);
												send.setEnabled(true);
											}
										});
										
										Intent	intent	= getIntent();
										if(intent!=null && Intent.ACTION_SEND.equals(intent.getAction()))
										{
											System.out.println("Intent: "+intent);
											System.out.println("Type: "+intent.getType());
											Bundle	extras	= intent.getExtras();
											if(extras!=null)
											{
												String	text	= (String) extras.get(Intent.EXTRA_TEXT);
												Uri	uri	= (Uri)extras.get(Intent.EXTRA_STREAM);
												System.out.println("Text: "+text);	
												System.out.println("Uri: "+uri);
														 
												// Convert the image URI to the direct file system path of the image file
												String[]	proj	= new String[]{MediaStore.Images.Media.DATA};
												Cursor cursor = managedQuery(uri,
													proj,	// Which columns to return
													null,	// WHERE clause; which rows to return (all rows)
													null,	// WHERE clause selection arguments (none)
													null);	// Order-by clause (ascending by name)
												int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
												cursor.moveToFirst();
												final String	path	= cursor.getString(column_index);
												System.out.println("Path: "+path);
												
												chatgui.findUsers().addResultListener(new IResultListener<Collection<IChatService>>()
												{
													public void exceptionOccurred(Exception exception) {}
													
													public void resultAvailable(final Collection<IChatService> results)
													{
														runOnUiThread(new Runnable()
														{
															public void run()
															{
																AlertDialog.Builder	builder	= new AlertDialog.Builder(JadexAndroidBenchmarkAgentActivity.this);
																builder.setTitle("Choose send target");
																List<CharSequence>	items	= new ArrayList<CharSequence>();
																for(IChatService chat: results)
																{
																	items.add(((IService)chat).getServiceIdentifier().getProviderId().getName());
																}
																builder.setSingleChoiceItems(items.toArray(new CharSequence[items.size()]), -1, new DialogInterface.OnClickListener()
																{
																	public void onClick(DialogInterface dialog, int which)
																	{
																		dialog.dismiss();
																		if(which!=-1)
																		{
																			IChatService	chat	= new ArrayList<IChatService>(results).get(which);
																			System.out.println("Sending to: "+chat);
																			chatgui.sendFile(path, ((IService)chat).getServiceIdentifier().getProviderId())
																				.addResultListener(new IResultListener<Void>()
																			{
																				public void resultAvailable(Void result)
																				{
																					System.out.println("Transfer started.");
																				}
																				public void exceptionOccurred(Exception exception)
																				{
																					System.out.println("Transfer failed to initialize: "+exception);
																				}
																			});
																		}
																	}
																});
																AlertDialog alert = builder.create();
																alert.show();
															}
														});
													}
												});
											}
										}
									}
									public void exceptionOccurred(Exception exception)
									{
										// No chat service.
										System.out.println("Not connected to chat: "+exception);
									}
								});
								
								return IFuture.DONE;
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
			
			public void onServiceDisconnected(ComponentName name)
			{
			}
		}, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 *  Cleanup on exit.
	 */
	protected void onDestroy()
	{
		subscription.terminate();
	}
		
	//-------- helper methods --------

	/**
	 *  Run a benchmark and return the results.
	 */
	protected IFuture<Collection<Tuple2<String, Object>>>	runBenchmark(final String agent, final Map<String, Object> args)
	{
		return platform.scheduleStep(new IComponentStep<Collection<Tuple2<String, Object>>>()
		{
			@Classname("create-component")
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