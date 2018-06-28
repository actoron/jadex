package jadex.android.controlcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public abstract class SubActivity extends Activity implements IActivity
{
	private Intent intent;
	private MetaActivity context;

	public SubActivity(Intent intent, MetaActivity ctx)
	{
		this.intent = intent;
		this.context = ctx;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	}
	
	@Override
	public void onResume()
	{
	}

	@Override
	public void onPause()
	{
	}
	
	@Override
	public void onDestroy()
	{
	}
	
	@Override
	public void setTitle(CharSequence title)
	{
		context.setTitle(title);
	}

	public Intent getIntent() {
		return intent;
	}
	
	public void setContentView(int layoutResID)
	{
		context.setContentView(layoutResID);
	}
	
	public void setContentView(View view)
	{
		context.setContentView(view);
	}
	
	public void setContentView(View view, LayoutParams params)
	{
		context.setContentView(view, params);
	}
	
	protected Context getContext() {
		return context;
	}
	
	public Context getApplicationContext()
	{
		return context;
	}
	
	public void startActivity(Intent intent)
	{
		context.startActivity(intent);
	}
	
}
