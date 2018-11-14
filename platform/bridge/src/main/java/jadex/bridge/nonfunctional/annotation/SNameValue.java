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
				ret[i] = convertNameValue(values[i]);
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
				ret.add(convertNameValue(values[i]));
			}
		}
		return ret;
	}
	
	/**
	 *  Convert a name value annotation to an unparsed expression.
	 *  @param nval The name value annotation.
	 *  @return The expression.
	 */
	public static UnparsedExpression convertNameValue(NameValue nval)
	{
		UnparsedExpression ret = null;
		
		String val = nval.value();
		String[] vals = nval.values();
		String clname = nval.clazz().getName();
		if(vals.length==0)
		{
			ret = new UnparsedExpression(nval.name(), clname, (val==null || val.length()==0) && clname!=null? clname+".class": val, null);
		}
		else
		{
			StringBuffer buf = new StringBuffer();
			buf.append("java.util.Arrays.asList(");
			boolean first = true;
			for(String v: vals)
			{
				if(!first)
					buf.append(",");
				first = false;
				buf.append(v);
			}
			buf.append(")");
			ret = new UnparsedExpression(nval.name(), clname, buf.toString(), null);
		}
		
		return ret;
	}
}
