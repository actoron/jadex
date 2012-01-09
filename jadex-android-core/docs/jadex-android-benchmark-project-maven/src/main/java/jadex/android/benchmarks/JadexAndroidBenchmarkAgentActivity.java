package jadex.android.benchmarks;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.benchmarks.MessagePerformanceAgent;

import java.util.Collections;
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
		setContentView(R.layout.main);

		startMB1 = (Button)findViewById(R.id.startMB1);
		startMB2 = (Button)findViewById(R.id.startMB2);
//		startMB3 = (Button)findViewById(R.id.startMB3);
		startMB1.setOnClickListener(buttonListener);
		startMB2.setOnClickListener(buttonListener);
		startMB3.setOnClickListener(buttonListener);
//		startMB1.setEnabled(false);
		startMB2.setEnabled(false);
		startMB3.setEnabled(false);
		
		textView = (TextView) findViewById(R.id.infoTextView);
		
//		// Start the platform
//		textView.setText("Starting Jadex Platform...");
//		new Thread(new Runnable()
//		{
//			public void run()
//			{
//				Starter.createPlatform(new String[]
//				{
//					"-conf",
//					"jadex/android/benchmarks/Platform.component.xml",
//					"-configname", "android_fixed",
//					"-logging_level", "java.util.logging.Level.INFO",
//					"-platformname", "and-" + createRandomPlattformID(),
//					"-saveonexit", "false", "-gui", "false",
//					"-autoshutdown", "false"
//				}).addResultListener(new IResultListener<IExternalAccess>()
//				{
//					public void resultAvailable(IExternalAccess result)
//					{
//						platform = result;
//						runOnUiThread(new Runnable()
//						{
//							public void run()
//							{
//								startMB1.setEnabled(true);
//								startMB2.setEnabled(true);
//								startMB3.setEnabled(true);
//								textView.setText("Platform started.");
//							}
//						});
//					}
//					
//					public void exceptionOccurred(final Exception exception)
//					{
//						exception.printStackTrace();
//						runOnUiThread(new Runnable()
//						{
//							public void run()
//							{
//								textView.setText("Start of platform failed: "+exception);
//							}
//						});
//					}
//				});
//			}
//		}).start();
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
				args.put("codec", Boolean.TRUE);
				args.put("echo", new ComponentIdentifier("echo@echo", new String[]{SRelay.DEFAULT_ADDRESS}));
			}
			
			startMB1.setEnabled(false);
			startMB2.setEnabled(false);
			startMB3.setEnabled(false);
			
			runBenchmark(agent, args).addResultListener(new IResultListener<Map<String,Object>>()
			{
				public void resultAvailable(final Map<String, Object> result)
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							textView.setText(result.toString());
							startMB1.setEnabled(true);
							startMB2.setEnabled(true);
							startMB3.setEnabled(true);
						}
					});
				}
				
				public void exceptionOccurred(final Exception exception)
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							textView.setText("Benchmark failed: "+exception);
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
	protected IFuture<Map<String, Object>>	runBenchmark(final String agent, final Map<String, Object> args)
	{
		try
		{
//			ReceivingBenchmark.main(null);
//			SendingBenchmark.main(null);
			Map<String, Object>	ret	= Collections.emptyMap();
			return new Future<Map<String, Object>>(ret);
		}
		catch(Exception e)
		{
			return new Future<Map<String, Object>>(e);
		}
//		return platform.scheduleStep(new IComponentStep<Map<String, Object>>()
//		{
//			@XMLClassname("create-component")
//			public IFuture<Map<String, Object>> execute(IInternalAccess ia)
//			{
//				final Future<Map<String, Object>>	fut	= new Future<Map<String, Object>>();
//				ia.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(fut)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						cms.createComponent(null, agent, new CreationInfo(args), new DelegationResultListener<Map<String,Object>>(fut))
//							.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Map<String, Object>>(fut)
//						{
//							public void customResultAvailable(IComponentIdentifier result)
//							{
//								// ignore (wait for agent termination)
//							}
//						});
//					}
//				});
//				
//				return fut;
//			}
//		});
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