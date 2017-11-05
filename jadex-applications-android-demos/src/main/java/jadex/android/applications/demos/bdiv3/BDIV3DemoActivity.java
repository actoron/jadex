package jadex.android.applications.demos.bdiv3;

import jadex.android.JadexAndroidActivity;
import jadex.android.applications.demos.R;
import jadex.android.commons.JadexPlatformOptions;
import jadex.base.IPlatformConfiguration;
import jadex.base.IRootComponentConfiguration;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This Activity starts the BDI V3 puzzle implementation.
 */
@Reference
public class BDIV3DemoActivity extends JadexAndroidActivity
{
	/** Constructor */
	public BDIV3DemoActivity()
	{
		super();
		setPlatformAutostart(true);

		setPlatformKernels(IRootComponentConfiguration.KERNEL_MICRO);//, JadexPlatformOptions.KERNEL_BDIV3);
		IPlatformConfiguration config = getPlatformConfiguration();
		config.setPlatformName("bdiV3DemoPlatform");
		config.getRootConfig().setKernels(IRootComponentConfiguration.KERNEL_MICRO, IRootComponentConfiguration.KERNEL_BDIV3);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(jadex.android.applications.demos.R.layout.bdiv3_demo);
		final TextView	tv	= (TextView)findViewById(R.id.bdiv3_text);
		SUtil.addSystemOutListener(new IChangeListener<String>()
		{
			public void changeOccurred(final ChangeEvent<String> event)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						tv.setText(tv.getText()+event.getValue()+"\n");
					}
				});
			}
		});
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
//		startMicroAgent("Puzzle", BenchmarkBDI.class);
	}
}
