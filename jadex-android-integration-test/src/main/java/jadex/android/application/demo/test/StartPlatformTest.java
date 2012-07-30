package jadex.android.application.demo.test;

import com.jayway.android.robotium.solo.Solo;

import jadex.android.application.demo.JadexAndroidHelloWorldActivity;
import jadex.android.application.demo.R;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.TextView;

public class StartPlatformTest extends ActivityInstrumentationTestCase2<JadexAndroidHelloWorldActivity>
{

	private Solo solo;

	public StartPlatformTest()
	{
		super("jadex.android.application.demo", JadexAndroidHelloWorldActivity.class);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testStartPlatform() throws Exception
	{
		assertTrue(solo.getView(R.id.startPlatformButton).isEnabled());
		solo.clickOnView(solo.getView(jadex.android.application.demo.R.id.startPlatformButton));
		
		solo.waitForView(solo.getView(R.id.startPlatformButton));
		
//		solo.sleep(1500);
		

		solo.waitForText("Starting Jadex platform...");
		assertFalse(solo.getView(R.id.startPlatformButton).isClickable());
		solo.waitForText("Platform started:");
		
		assertTrue(solo.getText(0).getText().toString().contains("Platform started"));
		
		solo.clickOnView(solo.getView(R.id.startAgentButton));
		solo.waitForText("This is Agent");
		
	}

}
