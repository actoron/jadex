package de.unihamburg.vsis.jadexAndroid_test;

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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AgentActivity extends BaseActivity {


	private Button createAgentButton;

	private TextView textView;

	private IExternalAccess extAcc;
	
	private int num;
	
	public static Handler getHandler() {
		return handler;
	}

	private static Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agent_activity);
		
		handler = new Handler() {

			@Override
			public void handleMessage(final Message msg) {
				runOnUiThread(new Runnable() {
					
					public void run() {
						Toast makeText = Toast.makeText(AgentActivity.this, msg.getData().getString("text"), Toast.LENGTH_SHORT);
						makeText.show();
					}
				});
			}
		};
		// future.addResultListener(listener)

		textView = findTextViewById(R.id.agent_activity_textView1);
		textView.setText("starting Platform...");
		createAgentButton = findButtonById(R.id.agent_activity_button1);
		createAgentButton.setEnabled(false);
		createAgentButton.setOnClickListener(onClick);

		new Thread(new Runnable() {
			public void run() {
				IFuture<IExternalAccess> future = Startup.startEmptyPlatform();
				future.addResultListener(platformResultListener);
			}
		}).start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private OnClickListener onClick = new OnClickListener() {
		public void onClick(View v) {
			if (v == createAgentButton) {
				System.out.println(extAcc.toString());
				IFuture scheduleStep = extAcc
						.scheduleStep(new IComponentStep() {
							@XMLClassname("create-component")
							public IFuture execute(IInternalAccess ia) {
								Future ret = new Future();
								SServiceProvider.getService(
										ia.getServiceContainer(),
										IComponentManagementService.class,
										RequiredServiceInfo.SCOPE_PLATFORM)
										// ia.getRequiredService("cms")
										.addResultListener(
												ia.createResultListener(new DelegationResultListener(
														ret)));

								return ret;
							}
						});
				scheduleStep.addResultListener(new DefaultResultListener() {

				

					public void resultAvailable(Object arg0) {
						IComponentManagementService cms = (IComponentManagementService) arg0;
						HashMap<String, Object> args = new HashMap<String, Object>();
//						args.put("num", new Integer(1));
//						args.put("max", new Integer(2));
						
						args.put("context", AgentActivity.this);

						runOnUiThread(new Runnable() {
							
							public void run() {
								createAgentButton.setEnabled(false);
							}
						});
						cms.createComponent(
								"agent" + num,
								AndroidAgent.class.getName().replaceAll(
										"\\.", "/")
										+ ".class", new CreationInfo(args),
								null).addResultListener(
								agentCreatedResultListener);
					}
				});
			}
		}
	};

	private IResultListener<IExternalAccess> platformResultListener = new DefaultResultListener<IExternalAccess>() {

		public void resultAvailable(IExternalAccess result) {
			extAcc = result;
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("Platform started.");
					createAgentButton.setEnabled(true);
				}
			});
		}

		@Override
		public void exceptionOccurred(Exception exception) {
			Log.e(Helper.LOG_TAG, exception.getMessage());
		}
		
		
	};

	private IResultListener agentCreatedResultListener = new DefaultResultListener() {

		public void resultAvailable(Object arg0) {
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("Agent started: " + num);
					num++;
					createAgentButton.setEnabled(true);
				}
			});
		}
	};
}
