package jadex.android.applications.demos.benchmark;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.base.IPlatformConfiguration;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 *  Activity (screen) for the jadex android benchmark app.
 *  Starts the platform and allows launching the different benchmarks.
 */
@Reference
public class BenchmarkDemoActivity extends JadexAndroidActivity
{
	//-------- attributes --------
	
	/** The text view for showing results. */
	private TextView textView;
	private Button chooseButton;
	
	//-------- constructor --------
	
	public BenchmarkDemoActivity()
	{
		super();
		IPlatformConfiguration config = getPlatformConfiguration();
		config.setPlatformName("benchmarkDemoPlatform");
		setPlatformAutostart(true);
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.benchmark_demo);

		textView = (TextView) findViewById(R.id.benchmark_logTextView);
		final ScrollView scrollView = (ScrollView) findViewById(R.id.benchmark_logScrollView);
		
		chooseButton = (Button) findViewById(R.id.benchmark_openMenuButton);
		chooseButton.setOnClickListener(chooseButtonClickListener);
		chooseButton.setEnabled(false);
		
		SUtil.addSystemOutListener(new IChangeListener<String>()
		{
			public void changeOccurred(final ChangeEvent<String> event)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						textView.append(event.getValue() + "\n");
						scrollView.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
	}
	
	private OnClickListener chooseButtonClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			openOptionsMenu();
		}
	};

	/**
	 *  Called when the menu is shown.
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.benchmark_menu, menu);
	    return true;
	}
	
	/**
	 *  Called when a menu item is selected.
	 */
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean	ret;
		
		if(item.getItemId()==R.id.benchmark_creation || item.getItemId()==R.id.benchmark_nocodec || item.getItemId()==R.id.benchmark_codec
			|| item.getItemId()==R.id.benchmark_remote || item.getItemId()==R.id.benchmark_remotecodec)
		{
			String	agent	= item.getItemId()==R.id.benchmark_creation
				? PojoAgentCreationAgent.class.getName().replaceAll("\\.", "/")+".class"
				: MessagePerformanceAgent.class.getName().replaceAll("\\.", "/")+".class";
			Map<String, Object> args	= new HashMap<String, Object>();
			if(item.getItemId()==R.id.benchmark_codec || item.getItemId()==R.id.benchmark_remotecodec)
			{
				args.put("codec", Boolean.TRUE);
			}
			if(item.getItemId()==R.id.benchmark_remote || item.getItemId()==R.id.benchmark_remotecodec)
			{
				args.put("echo", new BasicComponentIdentifier("echo@echo"));
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
		else
		{
			ret	= super.onOptionsItemSelected(item);
		}
		
		return ret;
	}
	
	
	
	//-------- helper methods --------

	/**
	 *  Run a benchmark and return the results.
	 */
	protected IFuture<Collection<Tuple2<String, Object>>>	runBenchmark(final String agent, final Map<String, Object> args)
	{
		return getPlatformAccess().scheduleStep(new IComponentStep<Collection<Tuple2<String, Object>>>()
		{
			@Classname("create-component")
			public IFuture<Collection<Tuple2<String, Object>>> execute(IInternalAccess ia)
			{
				final Future<Collection<Tuple2<String, Object>>>	fut	= new Future<Collection<Tuple2<String, Object>>>();
//				ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class))
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<Tuple2<String, Object>>>(fut)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
						ia.createComponent(new CreationInfo(args).setName(agent), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
							.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<Tuple2<String, Object>>>(fut)
						{
							public void customResultAvailable(IExternalAccess result)
							{
								// ignore (wait for agent termination)
							}
						});
//					}
//				});
				
				return fut;
			}
		});
	}

	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		textView.append("Platform starting...\n");
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				chooseButton.setEnabled(true);
				textView.append("Platform started. Press Menu to launch Benchmarks.\n");
			}
		});
	}

}