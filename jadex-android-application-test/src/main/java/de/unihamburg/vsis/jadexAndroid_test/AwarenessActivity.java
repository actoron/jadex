package de.unihamburg.vsis.jadexAndroid_test;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.HashMap;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AwarenessActivity extends BaseActivity {
	private TextView textView;
	protected IExternalAccess extAcc;
	public static Handler handler;
	private ArrayAdapter<RemoteComponentIdentifier> listAdapter;

	private String platformID;

	public AwarenessActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awareness_activity);
		textView = findTextViewById(R.id.awareness_activity_textView1);
		textView.setText("starting Platform...");

		platformID = createRandomPlattformID();

		ListView listView = findListViewById(R.id.awareness_activity_listView1);
		listAdapter = new ArrayAdapter<RemoteComponentIdentifier>(this,
				R.layout.componentidentifier_listitem);

		Button exitButton = findButtonById(R.id.awareness_activity_button1);
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (extAcc != null) {
					IFuture killComponent = extAcc.killComponent();
					killComponent
							.addResultListener(new DefaultResultListener() {

								@Override
								public void resultAvailable(Object result) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											AwarenessActivity.this.finish();
										}
									});
								}

								@Override
								public void exceptionOccurred(
										Exception exception) {
									Message message = handler.obtainMessage();
									Bundle data = new Bundle();
									data.putString("text",
											"Platform already stopped. (why?)");
									message.setData(data);
									message.sendToTarget();
								}
							});
				}
			}
		});

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

				IFuture future = Startup.startBluetoothPlatform("Platform-"
						+ platformID);
				future.addResultListener(platformResultListener);
			}
		}).start();

		listView.setOnItemClickListener(onItemClickListener);
	}

	private IResultListener<IExternalAccess> platformResultListener = new DefaultResultListener<IExternalAccess>() {

		@Override
		public void resultAvailable(IExternalAccess result) {
			extAcc = (IExternalAccess) result;
			runOnUiThread(new Runnable() {

				public void run() {
					IComponentIdentifier componentIdentifier = extAcc
							.getComponentIdentifier();
					textView.setText("Platform started: Platform-" + platformID);
				}
			});

			IFuture<IComponentManagementService> scheduleStep = extAcc
					.scheduleStep(new IComponentStep<IComponentManagementService>() {
						@XMLClassname("create-component")
						public IFuture<IComponentManagementService> execute(
								IInternalAccess ia) {
							Future<IComponentManagementService> ret = new Future<IComponentManagementService>();
							SServiceProvider.getService(
									ia.getServiceContainer(),
									IComponentManagementService.class,
									RequiredServiceInfo.SCOPE_PLATFORM)
									// ia.getRequiredService("cms")
									.addResultListener(
											ia.createResultListener(new DelegationResultListener<IComponentManagementService>(
													ret)));
							return ret;
						}
					});

			scheduleStep
					.addResultListener(new DefaultResultListener<IComponentManagementService>() {
						@XMLClassname("create-awarenessactivityagent")
						public void resultAvailable(
								IComponentManagementService arg0) {
							IComponentManagementService cms = (IComponentManagementService) arg0;
							HashMap<String, Object> args = new HashMap<String, Object>();
							Log.i(Helper.LOG_TAG,
									"Starting AwarenessActivityAgent...");
							cms.createComponent(
									"AwarenessActivityAgent",
									AwarenessActivityAgent.class.getName()
											.replaceAll("\\.", "/") + ".class",
									new CreationInfo(args), null)
									.addResultListener(
											new DefaultResultListener<IComponentIdentifier>() {
												@Override
												public void resultAvailable(
														IComponentIdentifier result) {
													// agent created!
													Log.i(Helper.LOG_TAG,
															"AwarenessActivityAgent created!");
												}
											});
						}
					});
		}
	};

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			RemoteComponentIdentifier item = (RemoteComponentIdentifier) parent
					.getItemAtPosition(position);
			IFuture ret = AwarenessActivityAgent.instance
					.getRemoteComponents(item);
			ret.addResultListener(new DefaultResultListener() {
				@Override
				public void resultAvailable(Object result) {
					Log.i(Helper.LOG_TAG,
							"AwarenessActivity: received result from Agent!");
				}
			});
		}
	};

}
