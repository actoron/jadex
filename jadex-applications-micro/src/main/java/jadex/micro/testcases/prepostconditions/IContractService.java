package jadex.micro.testcases.prepostconditions;

import java.util.List;

import jadex.bridge.service.annotation.CheckIndex;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.CheckState;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
public interface IContractService
{
	/**
	 * 
	 */
//	@PreConditions(
//	{
//		@PreCondition(type=PreCondition.Type.NOTNULL, argno=0),
//		@PreCondition(type=PreCondition.Type.EXPRESSION, expression="$arg1>0 && $arg2>0"),
//		@PreCondition(type=PreCondition.Type.EXPRESSION, expression="$arg1>0 && $arg2>0"),
//	})
//	@PostConditions(
//	{
//		@PostCondition(value=PostCondition.Type.NOTNULL),
//		@PostCondition(value=PostCondition.Type.EXPRESSION, expression="$res>0 && $res<100"),
//	})
	public @CheckNotNull @CheckState("$res>0 && $res<100") IFuture<Integer> doSomething(
		@CheckNotNull String a, 
		@CheckState("$arg>0 && $arg<100") int x,
		@CheckState("$arg>0") int y);
	
	/**
	 * 
	 */
	public IFuture<String> getName(@CheckIndex(1) int idx, @CheckNotNull List<String> names);
	
	/**
	 * 
	 */
	public @CheckState(value="$res[-1] < $res", intermediate=true, keep=2) 
		IIntermediateFuture<Integer> getIncreasingValue();

}
