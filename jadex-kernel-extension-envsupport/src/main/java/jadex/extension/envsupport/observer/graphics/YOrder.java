package jadex.extension.envsupport.observer.graphics;

import java.util.Comparator;

import jadex.extension.envsupport.math.IVector2;


/**
 * Causes objects to be drawn from the highest y-coordinate to the lowest.
 */
public class YOrder implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		float y1 = ((IVector2)((Object[])o1)[0]).getYAsFloat();
		float y2 = ((IVector2)((Object[])o2)[0]).getYAsFloat();
		if(y1 == y2)
		{
			return 0;
		}
		else if(y1 < y2)
		{
			return 1;
		}
		return -1;
	}
}
