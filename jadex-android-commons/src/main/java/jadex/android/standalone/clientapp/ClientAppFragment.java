package jadex.android.standalone.clientapp;

import android.app.Activity;
import android.content.Context;
import android.view.Window;

public class ClientAppFragment extends ActivityAdapterFragment
{
	/**
	 * This method is called upon instantiation of the Fragment. Tasks that
	 * should be run before the layout of the Activity is set should be
	 * performed here.
	 * 
	 * Note that getActivity() will return null during this method.
	 * 
	 * @param mainActivity
	 */
	public void onPrepare(Activity mainActivity)
	{
		mainActivity.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}
	
	protected Context getContext()
	{
		return getActivity().getApplicationContext();
	}
	
	
	
}
