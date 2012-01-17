package jadex.android.benchmarks;

import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.benchmarks.MessagePerformanceAgent;
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
					"-platformname", "and-" + createRandomPlattformID(),
					"-extensions", "null",
					"-wspublish", "false",
					"-kernels", "\"component, micro\"",
					"-tcptransport", "false",
					"-niotcptransport", "false",
					"-relaytransport", "true",
					"-relayaddress", "\"http://10.0.2.2:8080/jadex-platform-relay-web/\"",					
					"-saveonexit", "false", "-gui", "false",
					"-autoshutdown", "false"
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
				args.put("max", new Integer(2));
				args.put("codec", Boolean.TRUE);
				args.put("echo", new ComponentIdentifier("echo@echo",
//					new String[]{SRelay.DEFAULT_ADDRESS}));
					new String[]{"http://10.0.2.2:8080/jadex-platform-relay-web/"}));
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