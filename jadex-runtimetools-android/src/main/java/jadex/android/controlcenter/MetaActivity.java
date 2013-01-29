package jadex.android.controlcenter;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MetaActivity extends android.app.Activity implements IActivity
{

	public final String EXTRA_ACTIVITYNAME = "EXTRA_ACTIVITYNAME";

	private IActivity subActivity;

	private Stack<IActivity> subActivityStack;
	private Map<IActivity,View> contentViews;

	public MetaActivity()
	{
		super();
		this.subActivityStack = new Stack<IActivity>();
		contentViews = new HashMap<IActivity, View>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		String stringExtra = getIntent().getStringExtra(EXTRA_ACTIVITYNAME);

		if (stringExtra != null)
		{
			showSubActivity(stringExtra, getIntent(), savedInstanceState);
		} else
		{
			subActivity = this;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (subActivity != this)
			subActivity.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (subActivity != this)
			subActivity.onPause();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (subActivity != this) {
			subActivity.onDestroy();
			contentViews.put(subActivity, null);
		}
	}

	private void showSubActivity(String className, Intent intent, Bundle savedInstanceState)
	{
		ClassLoader cl = this.getClass().getClassLoader();
		try
		{
			@SuppressWarnings("unchecked")
			Class<SubActivity> loadClass = ((Class<SubActivity>) cl.loadClass(className));
			SubActivity newInstance = loadClass.getConstructor(Intent.class, MetaActivity.class).newInstance(intent, this);
			subActivity = newInstance;
			newInstance.onCreate(savedInstanceState);
			showSubActivity(newInstance, savedInstanceState);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void showSubActivity(IActivity subAct, Bundle savedInstanceState)
	{
		subActivity = subAct;
		subActivity.onResume();
		View view = contentViews.get(subActivity);
		if (view != null) {
			setContentView(view);
		}
	}

	@Override
	public void onBackPressed()
	{
		if (!subActivityStack.isEmpty())
		{
			IActivity sub = subActivityStack.pop();
			showSubActivity(sub, null);
		} else
		{
			super.onBackPressed();
		}

	}

	@Override
	public void startActivity(Intent intent)
	{
		subActivityStack.add(subActivity);
		String className = intent.getComponent().getClassName();
		subActivity.onPause();
		showSubActivity(className, intent, null);
	}
	
	@Override
	public void setContentView(View view)
	{
		super.setContentView(view);
		contentViews.put(subActivity,view);
	}

	protected boolean isSubActivity()
	{
		return (subActivity != this);
	}
}
