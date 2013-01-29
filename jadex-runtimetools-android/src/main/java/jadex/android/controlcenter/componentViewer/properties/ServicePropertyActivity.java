package jadex.android.controlcenter.componentViewer.properties;

import java.io.Serializable;

import javax.swing.text.ComponentView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import jadex.android.controlcenter.MetaActivity;
import jadex.android.controlcenter.SubActivity;
import jadex.android.controlcenter.componentViewer.ComponentViewer;

public class ServicePropertyActivity extends SubActivity
{
	public ServicePropertyActivity(Intent intent, MetaActivity ctx)
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
		this.setTitle("Service Properties");
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
