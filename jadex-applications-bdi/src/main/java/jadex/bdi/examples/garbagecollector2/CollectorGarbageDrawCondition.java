package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawCondition;

/**
 * 
 */
public class CollectorGarbageDrawCondition extends DrawCondition
{
	/**
	 * Evaluates the condition.
	 * @return True, if the drawable should be drawn.
	 */
	public boolean evaluate()
	{
		return getProperty("garbage")!=null;
	}
}
