package monads;

import java.util.function.Function;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Monadic futures for Java.
 *  http://zeroturnaround.com/rebellabs/monadic-futures-in-java8/
 */
public class Test
{
	public static void main(String[] args)
	{
		IFuture<String> f = $(getHello(), x -> getWorld(x));
//		IFuture<String> f = $(getHello(), Test::getWorld);
//		IFuture<String> f = getHello().$(x -> getWorld(x));
		
		System.out.println("result is: "+$(f));
	}

	public static IFuture<String> getHelloWorld()
	{
		return $(getHello(), x->getWorld(x)); 
	}
	
	public static IFuture<String> getHello()
	{
		return new Future<String>("hello");
	}
	
	public static IFuture<String> getWorld(String hello)
	{
		return new Future<String>(hello+" world");
	}
	
	public static <V, R> IFuture<R> $(IFuture<V> orig, final Function<V, IFuture<R>> function)
	{
		Future<R> ret = new Future<>();

		orig.addResultListener(new IResultListener<V>()
		{
			public void resultAvailable(V result)
			{
				IFuture<R> res = function.apply(result);
				res.addResultListener(new DelegationResultListener<R>(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	public static <T> T $(IFuture<T> fut)
	{
		return fut.get();
	}

}
