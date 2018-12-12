package jadex.rules.rulesystem.rules;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  The operator class contains the implementation of all 
 *  operators for evaluating two values.
 */
public class Operator
{
	/**
	 *  Test two objects for equality.
	 */
	public static class Equal implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 
			
			return val1 instanceof Number && val2 instanceof Number
				? compare(val1,val2)==0
				: (val1==null && val2==null) || val1!=null && (val1.getClass().isArray() && val2!=null && val2.getClass().isArray() ? SUtil.arrayEquals(val1, val2)	: val1.equals(val2));
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "==";
		}
	}
	
	/**
	 *  Test two objects for non-equality.
	 */
	public static class NotEqual implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return val1 instanceof Number && val2 instanceof Number
				? compare(val1,val2)!=0
				: (val1==null && val2!=null) || val1!=null && !val1.equals(val2);
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "!=";
		}
	}
	
	/**
	 *  Test two objects for less than.
	 */
	public static class Less implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return val1!=null && val2!=null && compare(val1, val2)<0;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "<";
		}
	}
	
	/**
	 *  Test two objects for less or equal.
	 */
	public static class LessOrEqual implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return val1!=null && val2!=null && compare(val1, val2)<=0;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "<=";
		}
	}
	
	/**
	 *  Test two objects for greater than.
	 */
	public static class Greater implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return val1!=null && val2!=null && compare(val1, val2)>0;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return ">";
		}
	}
	
	/**
	 *  Test two objects for greater or equal.
	 */
	public static class GreaterOrEqual implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return val1!=null && val2!=null && compare(val1, val2)>=0;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return ">=";
		}
	}
	
	/**
	 *  Requires strings as both parameters.
	 *  Test two strings for matches.
	 */
	public static class Matches implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			if(!(val1 instanceof String))
				throw new IllegalArgumentException("Matches operator only applies for strings: +"+val1);
			if(!(val2 instanceof String))
				throw new IllegalArgumentException("Matches operator only applies for strings: +"+val2);
			
			// val1 is the value to test
			// val2 should be the pattern
			
			return ((String)val1).matches((String)val2);
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "matches";
		}
	}
	
	/**
	 *  Requires strings as both parameters.
	 *  Test two strings for matches.
	 */
	public static class StartsWith implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			if(!(val1 instanceof String))
				throw new IllegalArgumentException("Matches operator only applies for strings: +"+val1);
			if(!(val2 instanceof String))
				throw new IllegalArgumentException("Matches operator only applies for strings: +"+val2);
			
			// val1 is the value to test
			// val2 should be the pattern
			
			return ((String)val1).startsWith((String)val2);
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "startsWith";
		}
	}
	
	/**
	 *  Test if an object is contained in a collection.
	 */
	public static class Contains implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return contains(val1, val2);
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "contains";
		}
	}
	
	/**
	 *  Test if an object is excluded from a collection.
	 */
	public static class Excludes implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			return !contains(val1, val2);
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "excludes";
		}
	}
	
	/**
	 *  Test if an object is instanceof a class.
	 */
	public static class InstanceOf implements IOperator
	{
		/**
		 *  Evaluate two objects with respect to the
		 *  operator semantics.
		 *  @param state The state.
		 *  @param val1 The first object.
		 *  @param val2 The second object.
		 *  @return True, if objects fit wrt. the operator semantics.
		 */
		public boolean evaluate(IOAVState state, Object val1, Object val2)
		{
			val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
			val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

			boolean ret;
			if(val2 instanceof Class)
			{
				ret = ((Class)val2).isAssignableFrom(val1 instanceof Class? (Class)val1: val1.getClass());
			}
			else //if(val2 instanceof OAVObjectType)
			{
				ret = val1 instanceof OAVObjectType? ((OAVObjectType)val1).isSubtype((OAVObjectType)val2)
					:state.getType(val1).isSubtype((OAVObjectType)val2);
			}
			return ret;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return "instanceof";
		}
	}
	
	/**
	 *  Compare two values.
	 *  @param val1	The first value.
	 *  @param val2	The second value.
	 *  @return	A negative integer, zero, or a positive integer as the
	 *    first value is less than, equal to, or greater than the second value.
	 *  @throws ClassCastException	when the values are not comparable.
	 */
	protected static int compare(Object val1, Object val2)
	{
		val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
		val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 

		// Should support some external comparators???

		// Check for null values.
		if(val1==null || val2==null)
		{	
			throw new NullPointerException("Cannot compare null "+val1+" "+val2);
		}

		// Compare numbers.
		// Number.compareTo() works only for number objects of same type, grrr...
		else if(val1 instanceof Number && val2 instanceof Number)
		{
			Number	numval1	= (Number)val1;
			Number	numval2	= (Number)val2;
			if(numval1 instanceof Double || numval2 instanceof Double
				|| numval1 instanceof Float || numval2 instanceof Float)
			{
				return numval1.doubleValue()>numval2.doubleValue() ? 1 : (numval1.doubleValue()<numval2.doubleValue() ? -1 : 0);
			}
			else
			{
				return numval1.longValue()>numval2.longValue() ? 1 : (numval1.longValue()<numval2.longValue() ? -1 : 0);
			}
		}

		// Use Comparable interface.
		else
		{
			return ((Comparable)val1).compareTo(val2);
		}
	}
	
	/**
	 *  Test if a collection contains a value.
	 *  @param val1 The collection.
	 *  @param val2 The value to test.
	 *  @return True, if contained.
	 */
	protected static boolean contains(Object val1, Object val2)
	{
		val1 = val1 instanceof ILazyValue? ((ILazyValue)val1).getValue(): val1; 
		val2 = val2 instanceof ILazyValue? ((ILazyValue)val2).getValue(): val2; 
		
		if(val1==null)
			return false;
		
		boolean ret = false;
				
		// val1 should be the collection,
		// val2 is the value to test
		if(val1.getClass().isArray())
		{
			Object[]	array = (Object[])val1;
			for(int i=0; !ret && i<array.length; i++)
			{
				Object totest = array[i];
				ret = (totest==null && val2==null) || totest!=null && totest.equals(val2);
			}
		}
		else if(val1 instanceof Collection)
		{
			ret = ((Collection)val1).contains(val2);
		}
		else if(val1 instanceof Enumeration)
		{
			for(Enumeration e =(Enumeration)val1; !ret && e.hasMoreElements();) 
			{
				Object totest = e.nextElement();
				ret = (totest==null && val2==null) || totest!=null && totest.equals(val2);
			}
		}
		else if(val1 instanceof Iterator)
		{
			for(Iterator it =(Iterator)val1; !ret && it.hasNext();) 
			{
				Object totest = it.next();
				ret = (totest==null && val2==null) || totest!=null && totest.equals(val2);
			}
		}
		else
		{
			throw new IllegalArgumentException("First value is no collection "+val1+" "+val2);
		}
		
		return ret;
	}
}
