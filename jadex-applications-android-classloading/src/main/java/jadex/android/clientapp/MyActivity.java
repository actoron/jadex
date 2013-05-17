package jadex.android.clientapp;

import jadex.android.clientapp.CalcService.CalcBinder;
import jadex.android.clientapp.CalcService.CalcResult;
import jadex.android.clientapp.MyService.MyBinder;
import jadex.android.standalone.clientapp.JadexClientAppService;
import jadex.android.standalone.clientapp.PlatformProvidingClientAppFragment;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.StreamCopy;
import jadex.commons.future.DefaultResultListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity extends PlatformProvidingClientAppFragment
{
	private TextView statusTextView;
	
	private MyBinder service1;

	private Button callServicesButton;

	protected CalcBinder service2;

	private ServiceConnection sc1;

	private ServiceConnection sc2;

	public MyActivity()
	{
		setPlatformAutostart(true);
		setPlatformName("classloadingtest");
		setPlatformKernels(KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BDI);
	}

	@Override
	public void onPrepare(Activity act)
	{
		super.onPrepare(act);
		System.out.println("MyActivity onPrepare");
	}

	@Override
	public void onStart()
	{
		super.onStart();
		System.out.println("MyActivity onStart");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		System.out.println("MyActivity onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("MyActivity onCreateView");
		int userlayout = R.layout.mylayout2;
		View view = inflater.inflate(userlayout, container, false);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		callServicesButton = (Button) view.findViewById(R.id.callServicesButton);
		callServicesButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (service1 != null){
					final String result = service1.getResultObject().result;
					runOnUiThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							System.out.println("Service 1 says: " + result);
						}
					});
				}
				if (service2 != null){
					final CalcResult add = service2.add(3, 4);
					runOnUiThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							System.out.println("Service 2 says: " + add.result);
						}
					});
					
					unbindService(sc1);
				}
			}
		});
		return view;
	}


	@Override
	public void onResume()
	{
		super.onResume();
		System.out.println("MyActivity onResume");
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
//		unbindService(this);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		System.out.println("MyActivity onAttach");
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		System.out.println("MyActivity onPlatformStarted");
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				statusTextView.setText(R.string.status_started);
				// Toast.makeText(getActivity(), "Jadex Platform started!",
				// Toast.LENGTH_LONG).show();
			}
		});

		bindServices();
		
//		ClassLoader classLoader = this.getClass().getClassLoader();
//		URL resource = classLoader.getResource("jadex/android/classloading/bditest/HelloWorld.agent.xml");
//		System.out.println(resource);
		
		startBDIAgent("myAgent", "jadex/android/clientapp/bditest/HelloWorld.agent.xml").addResultListener(new DefaultResultListener<IComponentIdentifier>()
		{

			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				System.out.println("Agent Started");
			}
		});
	}

	private void bindServices()
	{
		System.out.println("MyActivity binding service 1...");

		Intent intent = new Intent(getActivity(), MyService.class);
		intent.putExtra("myExtra", "myValue");
		
		sc1 = new ServiceConnection()
		{

			@Override
			public void onServiceDisconnected(final ComponentName name)
			{
				System.out.println("MyActivity.onServiceDisconnected() 1");
			}

			@Override
			public void onServiceConnected(final ComponentName name, final IBinder binder)
			{
				System.out.println("MyActivity.onServiceConnected() 1");
				service1 = (MyBinder) binder;
			}
		};
		boolean bindService = bindService(intent, sc1, BIND_AUTO_CREATE);		
		
		System.out.println("MyActivity binding service 2...");

		Intent intent2 = new Intent(getActivity(), CalcService.class);
		intent2.putExtra("myExtra", "myValue");
		
		sc2 = new ServiceConnection()
		{

			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				System.out.println("MyActivity.onServiceDisconnected() 2");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder binder)
			{
				System.out.println("MyActivity.onServiceConnected() 2");
				service2 = (CalcBinder) binder;
			}
		};
		boolean bindService2 = bindService(intent2, sc2, BIND_AUTO_CREATE);		
	}

	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				statusTextView.setText(R.string.status_loading);
			}
		});
	}
	
}
