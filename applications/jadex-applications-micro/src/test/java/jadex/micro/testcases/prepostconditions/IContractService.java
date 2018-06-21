package jadex.micro.testcases.prepostconditions;

import java.util.List;

import jadex.bridge.service.annotation.CheckIndex;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.CheckState;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Example for contracts in services.
 *  
 *  As precondition it can be used: @CheckNotNull @CheckState @CheckIndex()
 *  As postcondition it can be used: @CheckNotNull @CheckState
 */
public interface IContractService
{
	/**
	 *  Test method for @CheckNotNull and @CheckState.
	 */
	public @CheckNotNull @CheckState("$res>0 && $res<100") IFuture<Integer> doSomething(
		@CheckNotNull String a, 
		@CheckState("$arg>0 && $arg<100") int x,
		@CheckState("$arg>0") int y);
	
	/**
	 *  Test method for @CheckIndex.
	 */
	public IFuture<String> getName(@CheckIndex(1) int idx, @CheckNotNull List<String> names);

	/**
	 *  Test method for @CheckState with intermediate results.
	 *  
	 *  Will automatically try to determine the number of intermediate results to keep.
	 */
	public @CheckState(value="$res[-1] < $res", intermediate=true) 
		IIntermediateFuture<Integer> getIncreasingValue2();
	
	/**
	 *  Test method for @CheckState with intermediate results.
	 */
	public @CheckState(value="$res[-1] < $res", intermediate=true, keep=1) 
		IIntermediateFuture<Integer> getIncreasingValue();
	

}
