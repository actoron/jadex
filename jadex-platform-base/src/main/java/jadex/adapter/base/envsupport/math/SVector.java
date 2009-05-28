package jadex.adapter.base.envsupport.math;

/**
 * 
 */
public class SVector
{
	/**
	 * 
	 */
	public static IVector2 createVector2(IVector1 a, IVector1 b)
	{
		IVector2 ret; 
			
		if(a instanceof Vector1Double)
			ret = new Vector2Double(a.getAsDouble(), b.getAsDouble());
		else if(a instanceof Vector1Int)
			ret = new Vector2Int(a.getAsInteger(), b.getAsInteger());
		else
			throw new RuntimeException("Unknown vector2 type: "+a);
		
		return ret;
	}
}
