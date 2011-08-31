package jadex.android.application.demo;

import jadex.android.application.demo.AndroidAgent;
import jadex.android.application.demo.R;
import jadex.base.Starter;
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

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JadexAndroidHelloWorldActivity extends Activity {
	
	private Button startAgentButton;
	
	private IExternalAccess extAcc;
	
	private int num;
	
	private Button startPlatformButton;

	private TextView textView;
	
	private static Handler handler;
	
	public static Handler getHandler() {
		return handler;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		startPlatformButton = (Button) findViewById(R.id.startPlatformButton);
		startPlatformButton.setOnClickListener(buttonListener);
		
		startAgentButton = (Button) findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(buttonListener);
		startAgentButton.setEnabled(false);
		
		textView = (TextView) findViewById(R.id.infoTextView);
		
		handler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				runOnUiThread(new Runnable() {
					
					public void run() {
						Toast makeText = Toast.makeText(JadexAndroidHelloWorldActivity.this, msg.getData().getString("text"), Toast.LENGTH_SHORT);
						makeText.show();
					}
				});
			}
		};
	}

	private OnClickListener buttonListener = new OnClickListener() {

		public void onClick(View view) {
			if (view == startPlatformButton) {
				IFuture future = Starter
						.createPlatform(new String[] {
								"-conf",
								"jadex/android/application/demo/Platform.component.xml",
								"-configname", "android_fixed",
								"-platformname", "testcases", "-saveonexit",
								"false", "-gui", "false" });

				future.addResultListener(platformResultListener);
			} else if (view == startAgentButton) {
				startAgentButton.setEnabled(false);
				
				IFuture scheduleStep = extAcc
						.scheduleStep(new IComponentStep() {
							@XMLClassname("create-component")
							public Object execute(IInternalAccess ia) {
								Future ret = new Future();
								SServiceProvider.getService(
										ia.getServiceContainer(),
										IComponentManagementService.class,
										RequiredServiceInfo.SCOPE_PLATFORM)
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

						cms.createComponent(
								"HelloWorldAgent " + num,
								AndroidAgent.class.getName().replaceAll("\\.",
										"/")
										+ ".class", new CreationInfo(args),
								null).addResultListener(
								agentCreatedResultListener);
					}
				});
			}
		}
	};

	private IResultListener platformResultListener = new DefaultResultListener() {

		public void resultAvailable(Object result) {
			extAcc = (IExternalAccess) result;
			runOnUiThread(new Runnable() {

				public void run() {
					startPlatformButton.setEnabled(false);
					startAgentButton.setEnabled(true);
					textView.setText("Platform started");
				}
			});
		}
	};

	private IResultListener agentCreatedResultListener = new DefaultResultListener() {

		public void resultAvailable(Object arg0) {
			runOnUiThread(new Runnable() {

				public void run() {
					textView.setText("Agents started: " + num);
					num++;
					startAgentButton.setEnabled(true);
				}
			});
		}
	};
}