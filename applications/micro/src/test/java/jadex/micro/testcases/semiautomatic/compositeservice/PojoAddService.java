package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service(IAddService.class)
public class PojoAddService implements IAddService
{
	/** The service identifier. */
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The service provider. */
	@ServiceComponent
	protected IInternalAccess comp;
	
	//-------- methods --------

	/**
	 *  Add two numbers.
	 *  @param a Number one.
	 *  @param b Number two.
	 *  @return The sum of a and b.
	 */
	public IFuture add(double a, double b)
	{
		System.out.println("add service called on: "+sid+", comp="+(comp!=null?comp.getId():null));
		return new Future(Double.valueOf(a+b));
	}
	
	/**
	 * 
	 */
	//@ServiceStart
	@OnStart
	public IFuture start()
	{
//		System.out.println("start");
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	//@ServiceShutdown
	@OnEnd
	public IFuture shutdown()
	{
//		System.out.println("shutdown");
		return IFuture.DONE;
	}
	
//	/**
//	 * 
//	 */
//	@ServiceProperties
//	public Map getPropertyMap()
//	{
//		System.out.println("properties");
//		return IFuture.DONE;
//	}
}
