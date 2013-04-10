package jadex.android.classloading;

import jadex.android.platformapp.JadexUserFragment;
import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends JadexUserFragment
{
	@Override
	public void onResume()
	{
		System.out.println("MyActivity onResume");
		super.onResume();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("MyActivity onAttach");
		super.onAttach(activity);
	}
}
