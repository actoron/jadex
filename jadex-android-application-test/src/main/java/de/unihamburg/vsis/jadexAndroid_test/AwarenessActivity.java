package de.unihamburg.vsis.jadexAndroid_test;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.xml.annotation.XMLClassname;

import java.io.Serializable;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AwarenessActivity extends BaseActivity {
	private TextView textView;
	protected IExternalAccess extAcc;
	public static Handler handler;
	private ArrayAdapter<RemoteComponentIdentifier> listAdapter;

	public AwarenessActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awareness_activity);
		textView = findTextViewById(R.id.awareness_activity_textView1);
		textView.setText("starting Platform...");

		ListView listView = findListViewById(R.id.awareness_activity_listView1);
		listAdapter = new ArrayAdapter<RemoteComponentIdentifier>(this,
				R.layout.componentidentifier_listitem);

		listView.setAdapter(listAdapter);

		handler = new Handler() {

			@Override
			public void handleMessage(final Message msg) {
				runOnUiThread(new Runnable() {

					public void run() {
						Toast makeText = Toast.makeText(AwarenessActivity.this,
								msg.getData().getString("text"),
								Toast.LENGTH_SHORT);
						makeText.show();
						RemoteComponentIdentifier id = (RemoteComponentIdentifier) msg
								.getData().getSerializable("identifier");
						if (id != null) {
							String method = msg.getData().getString("method");
							if ("add".equals(method)) {
								listAdapter.add(id);
							} else {
								listAdapter.remove(id);
							}
						}
					}
				});
			}
		};

		new Thread(new Runnable() {
			public void run() {
				IFuture future = Startup.startNotifyingPlatform();
				future.addResultListener(platformResultListener);
			}
		}).start();
	}

	private IResultListener platformResultListener = new DefaultResultListener() {

		@Override
		public void resultAvailable(Object result) {
			extAcc = (IExternalAccess) result;
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("Platform started.");
				}
			});

			// IFuture scheduleStep = extAcc.scheduleStep(new IComponentStep() {
			// @XMLClassname("create-component")
			// public Object execute(IInternalAccess ia) {
			// Future ret = new Future();
			// SServiceProvider.getService(ia.getServiceContainer(),
			// IComponentManagementService.class,
			// RequiredServiceInfo.SCOPE_PLATFORM)
			// .addResultListener(
			// ia.createResultListener(new DelegationResultListener(
			// ret)));
			//
			// return ret;
			// }
			// });
			// scheduleStep.addResultListener(new DefaultResultListener() {
			//
			// public void resultAvailable(Object arg0) {
			// IComponentManagementService cms = (IComponentManagementService)
			// arg0;
			// HashMap<String, Object> args = new HashMap<String, Object>();
			// // args.put("num", new Integer(1));
			// // args.put("max", new Integer(2));
			//
			// args.put("context", AwarenessActivity.this);
			//
			// runOnUiThread(new Runnable() {
			//
			// public void run() {
			// }
			// });
			// cms.createComponent(
			// "notifierAgent",
			// AwarenessNotifierAgent.class.getName().replaceAll(
			// "\\.", "/")
			// + ".class", new CreationInfo(args), null);
			// }
			// });
		}
	};
}
