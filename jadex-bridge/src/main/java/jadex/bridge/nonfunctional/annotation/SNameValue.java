package jadex.bridge.nonfunctional.annotation;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  NameValue converter helper.
 */
public class SNameValue
{
	/**
	 *  Create unparsed expressions.
	 */
	public static UnparsedExpression[] createUnparsedExpressions(NameValue[] values)
	{
		UnparsedExpression[] ret = null;
		if(values.length>0)
		{
			ret = new UnparsedExpression[values.length];
			for(int i=0; i<values.length; i++)
			{
				String val = values[i].value();
				String clname = values[i].clazz().getName();
				ret[i] = new UnparsedExpression(values[i].name(), clname, (val==null || val.length()==0) && clname!=null? clname+".class": val, null);
			}
		}
		return ret;
	}
	
	/**
	 *  Create unparsed expressions.
	 */
	public static List<UnparsedExpression> createUnparsedExpressionsList(NameValue[] values)
	{
		List<UnparsedExpression>  ret = null;
		if(values.length>0)
		{
			ret = new ArrayList<UnparsedExpression>();
			for(int i=0; i<values.length; i++)
			{
				String val = values[i].value();
				String clname = values[i].clazz().getName();
				String v = (val==null || val.length()==0) && clname!=null? clname+".class": val;
				ret.add(new UnparsedExpression(values[i].name(), (String)null, v, null));
			}
		}
		return ret;
	}
}
