package jadex.bpmn.model.io;

import jadex.xml.IPostProcessor;

public abstract class FirstPassPostProcessor implements IPostProcessor
{
	public int getPass()
	{
		return 0;
	}
}
