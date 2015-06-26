package jadex.android.controlcenter.componentViewer.properties;

import jadex.android.controlcenter.MetaActivity;
import jadex.android.controlcenter.SubActivity;
import jadex.android.controlcenter.componentViewer.ComponentViewer;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class ComponentPropertyActivity extends SubActivity
{

	public ComponentPropertyActivity(Intent intent, MetaActivity ctx)
	{
		super(intent, ctx);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		PropertyItem[] props = (PropertyItem[]) getIntent().getSerializableExtra(ComponentViewer.EXTRA_PROPERTIES);

		ListView listView = new ListView(getContext());
		PropertyItemAdapter adapter = new PropertyItemAdapter(getContext(), props);

		listView.setAdapter(adapter);
		this.setContentView(listView);
	}

	@Override
	public void onResume()
	{
		this.setTitle("Component Properties");
	}

	@Override
	public void onPause()
	{
	}

	@Override
	public void onDestroy()
	{
	}

}
