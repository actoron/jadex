package jadex.bdi.planlib.simsupport.common.graphics.order;

import jadex.bdi.planlib.simsupport.common.math.IVector2;

import java.util.Comparator;

/** Causes objects to be drawn from the highest y-coordinate to the lowest.
 */
public class ReverseYOrder implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		float y1 = ((IVector2) ((Object[]) o1)[0]).getYAsFloat();
		float y2 = ((IVector2) ((Object[]) o1)[0]).getYAsFloat();
		float diff = y1 - y2;
		if (diff == 0.0f)
		{
			return 0;
		}
		else if (diff > 0.0f)
		{
			return 1;
		}
		return -1;
	}
}
