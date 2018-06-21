package jadex.rules.eca;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

/**
 * 
 */
public class ExpressionCondition implements ICondition
{
	/** The condition expression. */
	protected UnparsedExpression expression;
	
	/** The fetcher. */
	protected SimpleValueFetcher fetcher;
	
	/**
	 *  Create a new ExpressionCondition.
	 */
	public ExpressionCondition(UnparsedExpression expression, IValueFetcher fetcher)
	{
		this.expression = expression;
		this.fetcher = new SimpleValueFetcher(fetcher);
	}

	/**
	 *  Evaluate the condition.
	 */
	public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
	{
		IFuture<Tuple2<Boolean, Object>> ret = null;
		try
		{
			fetcher.setValue("$event", event);
			IParsedExpression exp = SJavaParser.parseExpression(expression, null, null); // todo: classloader?
			Object res = exp.getValue(fetcher);
			ret = CommandCondition.evaluateResult(res);
		}
		catch(Exception e)
		{
			ret = new Future<Tuple2<Boolean, Object>>(e);
//			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 *  Get the expression.
	 *  @return The expression.
	 */
	public UnparsedExpression getExpression()
	{
		return expression;
	}

	/**
	 *  Set the expression.
	 *  @param expression The expression to set.
	 */
	public void setExpression(UnparsedExpression expression)
	{
		this.expression = expression;
	}

	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	public SimpleValueFetcher getFetcher()
	{
		return fetcher;
	}

	/**
	 *  Set the fetcher.
	 *  @param fetcher The fetcher to set.
	 */
	public void setFetcher(SimpleValueFetcher fetcher)
	{
		this.fetcher = fetcher;
	}
}
