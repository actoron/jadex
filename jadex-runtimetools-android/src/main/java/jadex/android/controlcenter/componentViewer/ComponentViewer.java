package jadex.android.controlcenter.componentViewer;

import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.android.controlcenter.componentViewer.tree.AsyncTreeModel;
import jadex.android.controlcenter.componentViewer.tree.ComponentTreeNode;
import jadex.android.service.IJadexPlatformBinder;
import jadex.android.service.JadexPlatformService;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.Serializable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ComponentViewer extends Activity implements ServiceConnection
{
	private IJadexPlatformBinder platformService;
	private IComponentIdentifier platformId;
	private TreeNodeAdapter treeAdapter;
	protected AsyncTreeModel model;
	private TextView rootLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle("ComponentViewer");
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Serializable platformId = getIntent().getSerializableExtra(JadexAndroidControlCenter.EXTRA_PLATFORMID);
		if (platformId != null)
		{
			this.platformId = (IComponentIdentifier) platformId;
		}
		Intent intent = new Intent(this, JadexPlatformService.class);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unbindService(this);
	}

	public void onServiceConnected(ComponentName name, IBinder service)
	{
		this.platformService = (IJadexPlatformBinder) service;

		createTreeModel().addResultListener(new DefaultResultListener<AsyncTreeModel>()
		{

			@Override
			public void resultAvailable(AsyncTreeModel result)
			{
				model = result;
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						createView();
					}
				});
			}
		});

	}

	private void createView()
	{
		treeAdapter = new TreeNodeAdapter(this, model);
		treeAdapter.registerDataSetObserver(new DataSetObserver()
		{

			@Override
			public void onChanged()
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						rootLabel.setText(treeAdapter.getCurrentNode().toString());
					}
				});
			}
		});

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView rootLabel = new TextView(this);
		this.rootLabel = rootLabel;
		rootLabel.setText(treeAdapter.getCurrentNode().toString());
		rootLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		rootLabel.setTextColor(ColorStateList.valueOf(Color.GRAY));
		// rootLabel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// rootLabel.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.addView(rootLabel);

		ListView lv = new ListView(this);
		lv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				ITreeNode item = treeAdapter.getItem(position);
				if (!item.isLeaf())
				{
					treeAdapter.setCurrentNode(item);
				} else
				{
					// show properties
				}
			}
		});
		lv.setAdapter(treeAdapter);

		ll.addView(lv);
		setContentView(ll);
	}

	@Override
	public void onBackPressed()
	{
		ITreeNode parent = treeAdapter.getCurrentNode().getParent();
		if (parent == null)
		{
			super.onBackPressed();
		} else
		{
			treeAdapter.setCurrentNode(parent);
		}
	}

	private IFuture<AsyncTreeModel> createTreeModel()
	{
		final Future<AsyncTreeModel> result = new Future<AsyncTreeModel>();

		IFuture<IComponentManagementService> fut = platformService.getCMS(platformId);
		fut.addResultListener(new DefaultResultListener<IComponentManagementService>()
		{
			@Override
			public void resultAvailable(final IComponentManagementService cms)
			{
				IFuture<IComponentDescription[]> fut = cms.getComponentDescriptions();
				fut.addResultListener(new DefaultResultListener<IComponentDescription[]>()
				{

					@Override
					public void resultAvailable(IComponentDescription[] descriptions)
					{
						IComponentDescription root = null;
						for (int i = 0; root == null && i < descriptions.length; i++)
						{
							if (descriptions[i].getName().getParent() == null)
							{
								root = descriptions[i];
							}
						}
						AsyncTreeModel model = new AsyncTreeModel();
						model.setRoot(new ComponentTreeNode(null, model, root, cms, platformService.getExternalPlatformAccess(platformId)));
						result.setResult(model);
					}

				});
			}
		});

		return result;
	}

	public void onServiceDisconnected(ComponentName name)
	{
		this.platformService = null;
		onDestroy(); // ?
	}

}
