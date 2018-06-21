package jadex.extension.rs.publish;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 *  Simple method comparator that can be used to
 *  order methods alphabetically.
 */
public class MethodComparator implements Comparator<Method>
{
	public int compare(Method m1, Method m2)
	{
		int ret = m1.getName().compareTo(m2.getName());
		if(ret==0)
		{
			ret = m1.getParameterTypes().length - m2.getParameterTypes().length;
			if(ret==0)
			{
				ret = m1.toString().compareTo(m2.toString());
			}
		}
		return ret;
	}
}
