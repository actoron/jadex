package jadex.micro.testcases.prepostconditions;

import jadex.bridge.service.annotation.PostCondition;
import jadex.bridge.service.annotation.PostConditions;
import jadex.bridge.service.annotation.PreCondition;
import jadex.bridge.service.annotation.PreConditions;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IContractService
{
	/**
	 * 
	 */
	@PreConditions(
	{
		@PreCondition(value=PreCondition.Type.NOTNULL, argno=0),
		@PreCondition(value=PreCondition.Type.EXPRESSION, expression="$arg1>0 && $arg2>0"),
		@PreCondition(value=PreCondition.Type.EXPRESSION, expression="$arg1>0 && $arg2>0"),
	})
	@PostConditions(
	{
		@PostCondition(value=PostCondition.Type.NOTNULL),
		@PostCondition(value=PostCondition.Type.EXPRESSION, expression="$res>0 && $res<100"),
	})
	public IFuture<Integer> doSomething(String a, int x, int y);
}
