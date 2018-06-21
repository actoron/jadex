package jadex.android.exampleproject;

import jadex.bridge.service.types.context.JadexAndroidEvent;

public class MyEvent extends JadexAndroidEvent
{
	private String message;

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String msg)
	{
		this.message = msg;
	}
}
