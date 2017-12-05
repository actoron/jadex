package jadex.micro.testcases.recfutures;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Example service with a recursive future.
 */
@Service
public interface IAService
{
	/**
	 *  Test method with future in future.
	 */
	public IFuture<IFuture<String>> methodA();
	
	/**
	 *  Test method with intermediate future in future.
	 */
	public IFuture<IIntermediateFuture<String>> methodB();
	
	/**
	 *  Test method with intermediate future in future.
	 */
//	public ITuple2Future<IComponentIdentifier, Tuple2<String, Object> ... > methodC();
	
	/**
	 *  Alternatives for implementing the createComponent method.
	 */
//	public Tuple2<IFuture<IComponentIdentifier>, IIntermediateFuture<Tuple2<String, Object>>> methodC();
	
//	public ITuple2Future<IComponentIdentifier, IIntermediateFuture<Tuple2<String, Object>>> methodD();
	
//	public IIntermediateFuture<StatusEvent> methodD();

}
