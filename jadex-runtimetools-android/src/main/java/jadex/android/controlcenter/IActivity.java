package jadex.android.controlcenter;

import android.os.Bundle;

public interface IActivity
{

	public abstract void onCreate(Bundle savedInstanceState);

	public abstract void onResume();

	public abstract void onPause();

	public abstract void onDestroy();

}