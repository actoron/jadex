package jadex.android.applications.demos.bdi;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.android.commons.JadexPlatformOptions;
import jadex.base.IPlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This Activity shows a sample BPMN Workflow beeing executed using Jadex.
 */
@Reference
public class BDIDemoActivity extends JadexAndroidActivity
{
	/** The Button to send a message to the agent **/
	private Button btnCallAgent;
	
	/** Only for testing purposes, see event demo for a better way. **/
	public static BDIDemoActivity INSTANCE;

	/** Constructor */
	public BDIDemoActivity()
	{
		super();
		INSTANCE = this;
		setPlatformAutostart(true);
		IPlatformConfiguration config = getPlatformConfiguration();
		config.setPlatformName("bdiDemoPlatform");
		config.setValue("kernel_component", "true");
		config.setValue("kernel_bdiv3", "true");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(jadex.android.applications.demos.R.layout.bdi_demo);
		btnCallAgent = (Button) findViewById(R.id.bdi_demoButtonServiceCall);

		btnCallAgent.setOnClickListener(callBtnOnClickListener);
		btnCallAgent.setEnabled(false);
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		startBDIAgent("HelloWorldAgent", "jadex/android/applications/demos/bdi/HelloWorldBDI.class").addResultListener(
				bdiCreatedResultListener);
	}

	private IResultListener<IComponentIdentifier> bdiCreatedResultListener = new DefaultResultListener<IComponentIdentifier>()
	{
		public void resultAvailable(final IComponentIdentifier bdiId)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					btnCallAgent.setEnabled(true);		
				}
			});
		}
	};

	OnClickListener callBtnOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View arg0)
		{
			// Get DisplayService
			IExternalAccess platformAccess = getPlatformAccess();
			platformAccess.searchService( new ServiceQuery<>( IDisplayTextService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(
					new DefaultResultListener<IDisplayTextService>()
					{

						@Override
						public void resultAvailable(IDisplayTextService displayTextService)
						{
							// call display service
							displayTextService.showUiMessage("New Message (same plan)!");
						}
					});

		}
	};

}
