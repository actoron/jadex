package jadex.bdiv3.examples.marsworld;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;

/**
 *  Helper class to deal with 2D and 3D vectors.
 */
public class SVector
{
	public static double	getDistance(Object v1, Object v2)
	{
		double	ret;
		if(v1 instanceof IVector3)
		{
			ret	= ((IVector3)v1).getDistance((IVector3)v2).getAsDouble();
		}
		else
		{
			ret	= ((IVector2)v1).getDistance((IVector2)v2).getAsDouble();
		}
		
		return ret;
	}
}
