package jadex.android.clientappdemo;

import jadex.android.standalone.clientapp.PlatformProvidingClientAppFragment;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DefaultResultListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is a Sample Fragment which inherits from
 * {@link PlatformProvidingClientAppFragment} to make use of the Jadex Platform.
 * 
 * It starts a jadex platform on creation and provides a button to start a sample BDI agent.
 */
public class DefaultFragment extends PlatformProvidingClientAppFragment
{
	private TextView statusTextView;
	private Button startAgentButton;

	public DefaultFragment()
	{
		setPlatformAutostart(true);
		setPlatformName("ClientAppDemo");
		setPlatformKernels(KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BDI);
	}

	/**
	 * This method is called upon instantiation of the Fragment and before the
	 * default Fragment lifecycle comes into play. 
	 * Tasks that should be run before the layout of the Activity is set must be
	 * performed here, such as requesting Window Features.
	 **/
	@Override
	public void onPrepare(Activity act)
	{
		super.onPrepare(act);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		int userlayout = R.layout.defaultactivity;
		// inflate must be called with attachToRoot: false!
		View view = inflater.inflate(userlayout, container, false);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		startAgentButton = (Button) view.findViewById(R.id.startAgentButton);
		startAgentButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (isJadexPlatformRunning())
				{
					startComponent("myAgent", "jadex/android/clientapp/bditest/HelloWorld.agent.xml").addResultListener(
							new DefaultResultListener<IComponentIdentifier>()
							{

								@Override
								public void resultAvailable(IComponentIdentifier result)
								{
									System.out.println("Agent Started");
								}
							});
				}
				else
				{
					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							Toast.makeText(getContext(), "Platform not running yet", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		});
		return view;
	}

	@Override
	protected void onPlatformStarted(IExternalAccess result)
	{
		super.onPlatformStarted(result);
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				statusTextView.setText(R.string.status_started);
			}
		});

	}

	@Override
	protected void onPlatformStarting()
	{
		super.onPlatformStarting();
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				statusTextView.setText(R.string.status_loading);
			}
		});
	}

}
