package org.activecomponents.webservice;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Test service.
 */
@Service
public interface IWebsocketTestService
{		
	/**
	 *  Say hello to somebody.
	 */
	public IFuture<String> sayHelloTo(String name);
	
	/**
	 *  Say hello to somebody.
	 */
	public IFuture<String> sayHelloTo(String name, String name2);
	
	/**
	 *  Add two numbers.
	 */
	public IFuture<Integer> add(int a, int b);
	
	/**
	 *  Add two numbers.
	 */
	public IFuture<Double> add(double a, double b);
	
	/**
	 *  Produce some exception.
	 */
	public IFuture<Void> produceException();
	
	/**
	 *  Method with intermediate results.
	 */
	public ISubscriptionIntermediateFuture<Integer> count(int num, long delay);
	
	/**
	 *  Method with pullable results.
	 */
	public IPullIntermediateFuture<String> pull(int num);
}
