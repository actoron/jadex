package jadex.android.classloading;

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
import android.view.ViewGroup;
import android.widget.TextView;

public class MyActivity extends PlatformProvidingClientAppFragment
{
	private TextView statusTextView;

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
		return view;
	}

	private void copyLayouts() throws IOException
	{
		URL resource = getClass().getClassLoader().getResource("res/layout/mylayout2.xml");
		InputStream input = resource.openStream();
		File internalStoragePath = new File(getActivity().getDir("layout", Context.MODE_PRIVATE), "mylayout2.xml");
		FileOutputStream fileOutputStream = new FileOutputStream(internalStoragePath);

		StreamCopy streamCopy = new StreamCopy(input, fileOutputStream);
		streamCopy.run();
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

		System.out.println("MyActivity binding service...");

		Intent intent2 = new Intent(getActivity(), JadexClientAppService.class);
		bindService(intent2, new ServiceConnection()
		{

			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				System.out.println("MyActivity.onServiceDisconnected()");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				System.out.println("MyActivity.onServiceConnected()");
			}
		}, BIND_AUTO_CREATE);
		
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL resource = classLoader.getResource("jadex/android/classloading/bditest/HelloWorld.agent.xml");
		System.out.println(resource);
		
		startBDIAgent("myAgent", "jadex/android/classloading/bditest/HelloWorld.agent.xml").addResultListener(new DefaultResultListener<IComponentIdentifier>()
		{

			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				System.out.println("Agent Started");
				
			}
			
		});
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
