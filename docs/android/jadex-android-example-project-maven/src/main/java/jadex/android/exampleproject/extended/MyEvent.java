package jadex.android.exampleproject.extended;

import jadex.bridge.service.types.context.IJadexAndroidEvent;

public class MyEvent implements IJadexAndroidEvent
{

	public String data;
	
	@Override
	public String getType()
	{
		return "eventtype";
	}
	
	
	

}
