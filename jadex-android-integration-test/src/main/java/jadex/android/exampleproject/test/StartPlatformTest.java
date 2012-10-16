package jadex.android.exampleproject.test;

import com.jayway.android.robotium.solo.Solo;

import jadex.android.exampleproject.HelloWorldActivity;
import jadex.android.exampleproject.R;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartPlatformTest extends ActivityInstrumentationTestCase2<HelloWorldActivity>
{

	private Solo solo;

	public StartPlatformTest()
	{
		super("jadex.android.exampleproject", HelloWorldActivity.class);
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
		solo.clickOnView(solo.getView(jadex.android.exampleproject.R.id.startPlatformButton));
		
		solo.waitForView(solo.getView(R.id.startPlatformButton));
		
		

		solo.waitForText("Starting Jadex Platform...");
		
		solo.sleep(1500);
//		assertFalse(solo.getView(R.id.startPlatformButton).isEnabled());
		
		solo.waitForText("Platform started:",1,15000);
		
		TextView view = (TextView) solo.getView(R.id.infoTextView);
		assertTrue(view.getText().toString().contains("Platform started"));
		
		solo.clickOnView(solo.getView(R.id.startAgentButton));
		solo.waitForText("This is Agent");
		
		assertTrue(view.getText().toString().contains("Agent started: HelloWorldAgent"));
		solo.sleep(2000);
		
//		solo.clickOnView(solo.getView(R.id.pingButton));
//		solo.waitForText("HelloWorldAgent1: pong");
		
		solo.clickOnView(solo.getView(R.id.startPlatformButton));
		solo.waitForText("Platform stopped.");
		
	}

}
