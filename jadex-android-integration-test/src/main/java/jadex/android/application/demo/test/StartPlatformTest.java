package jadex.android.application.demo.test;

import jadex.android.application.demo.JadexAndroidHelloWorldActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;

public class StartPlatformTest extends ActivityInstrumentationTestCase2<JadexAndroidHelloWorldActivity>
{

	private JadexAndroidHelloWorldActivity activity;

	public StartPlatformTest()
	{
		super("jadex.android.application.demo", JadexAndroidHelloWorldActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		activity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	@UiThreadTest
	public void testStartPlatform() throws Exception
	{
		final Button startPlatformButton = (Button) activity.findViewById(jadex.android.application.demo.R.id.startPlatformButton);
		assertTrue(startPlatformButton.isEnabled());
		startPlatformButton.requestFocus();
		startPlatformButton.performClick();
		assertFalse(startPlatformButton.isEnabled());
		Thread.sleep(5000);
	}

}
