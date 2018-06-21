package jadex.rules.eca;

import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Interface for a condition part of a rule.
 */
public interface ICondition
{
	public static Tuple2<Boolean, Object> TRUE = new Tuple2<Boolean, Object>(Boolean.TRUE, null);
	
	public static Tuple2<Boolean, Object> FALSE = new Tuple2<Boolean, Object>(Boolean.FALSE, null);
	
	public static ICondition TRUE_CONDITION = new ICondition()
	{
		public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
		{
			return new Future<Tuple2<Boolean, Object>>(TRUE);
		}
	};
	
//	/**
//	 *  Evaluation the condition.
//	 *  @param event The event.
//	 *  @return True, if condition is met (plus additional user data).
//	 */
//	public Tuple2<Boolean, Object> evaluate(IEvent event);
	
	/**
	 *  Evaluation the condition.
	 *  @param event The event.
	 *  @return True, if condition is met (plus additional user data).
	 */
	public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event);
}
