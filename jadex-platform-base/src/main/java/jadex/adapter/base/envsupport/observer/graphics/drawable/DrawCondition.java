package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;

public abstract class DrawCondition
{
	public Object object;
	
	/**
	 * Evaluates the condition.
	 * @return true, if the drawable should be drawn.
	 */
	public abstract boolean evaluate();
	
	/**
	 * Tests the condition. Used by the drawable. 
	 * @param obj the object used as reference
	 * @return true if the object should be drawn
	 */
	public boolean testCondition(Object obj)
	{
		object = obj;
		return evaluate();
	}
	
	/**
	 * Returns a property of the object for evaluation.
	 * @param name name of the property
	 * @retun the property
	 */
	protected Object getProperty(String name)
	{
		return SObjectInspector.getProperty(object, name);
	}
}
