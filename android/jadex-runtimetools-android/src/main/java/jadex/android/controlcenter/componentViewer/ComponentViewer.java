package jadex.android.controlcenter.componentViewer;

import jadex.android.controlcenter.JadexAndroidControlCenter;
import jadex.android.controlcenter.MetaActivity;
import jadex.android.controlcenter.SubActivity;
import jadex.android.controlcenter.componentViewer.tree.ComponentTreeNode;
import jadex.android.controlcenter.componentViewer.tree.IAndroidTreeNode;
import jadex.android.service.JadexPlatformService;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.Serializable;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ComponentViewer extends MetaActivity implements ServiceConnection
{
	private IJadexPlatformBinder platformService;
	private IComponentIdentifier platformId;
	private TreeNodeAdapter treeAdapter;
	protected AsyncTreeModel model;
	private BreadCrumbView breadCrumbView;
	
	public final static String EXTRA_PROPERTIES = "EXTRA_PROPERTIES";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!isSubActivity()) {
			
			Serializable platformId = getIntent().getSerializableExtra(JadexAndroidControlCenter.EXTRA_PLATFORMID);
			if (platformId != null)
			{
				this.platformId = (IComponentIdentifier) platformId;
			}
			Intent intent = new Intent(this, JadexPlatformService.class);
			bindService(intent, this, BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (!isSubActivity()) {
			setTitle("ComponentViewer");
		}
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
		if (!isSubActivity()) {
			unbindService(this);
		}
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
						
						breadCrumbView.setCurrentTreeNode(treeAdapter.getCurrentNode());
					}
				});
			}
		});

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);


		breadCrumbView = new BreadCrumbView(this);
		// rootLabel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// rootLabel.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.addView(breadCrumbView);

		ListView lv = new ListView(this);
		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				boolean result = false;
				
				ITreeNode item = treeAdapter.getItem(position);
				if (item.hasProperties()) {
					result = true;
					openPropertiesPanel(item);
				}
				
				return result;
			}
		});
		
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
					if (item.hasProperties()) {
						openPropertiesPanel(item);
					}
				}
			}
		});
		lv.setAdapter(treeAdapter);

		ll.addView(lv);
		setContentView(ll);
	}

	protected void openPropertiesPanel(ITreeNode item)
	{
		if (item instanceof IAndroidTreeNode) {
			IAndroidTreeNode androidNode = (IAndroidTreeNode) item;
			Class<? extends SubActivity> activityClass = androidNode.getPropertiesActivityClass();
			// show properties
			Intent intent = new Intent(ComponentViewer.this, activityClass);
			intent.putExtra(EXTRA_PROPERTIES, androidNode.getProperties());
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed()
	{
		if (isSubActivity()) {
			super.onBackPressed();
		} else {
			ITreeNode parent = treeAdapter.getCurrentNode().getParent();
			if (parent == null)
			{
				super.onBackPressed();
			} else
			{
				treeAdapter.setCurrentNode(parent);
			}
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
