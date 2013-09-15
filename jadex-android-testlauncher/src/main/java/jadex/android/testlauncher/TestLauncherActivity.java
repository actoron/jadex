package jadex.android.testlauncher;

import jadex.android.standalone.clientapp.ClientAppFragment;
import junit.framework.TestResult;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestLauncherActivity extends ClientAppFragment
{
	private TextView statusTextView;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		setTitle("TestLauncher");
		int userlayout = R.layout.mainapp;
		View view = inflater.inflate(userlayout, container, false);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		FragmentActivity activity = getActivity();
		
		statusTextView.setText("started");
//		MicroCreationTest microCreationTest = new MicroCreationTest();
//		TestResult run = microCreationTest.run();
		
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
	}

}