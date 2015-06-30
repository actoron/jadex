package monads;

import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SResultListener;

import java.util.Collection;
import java.util.function.Function;

public class Test2
{
	public static void main(String[] args)
	{
//		Test.getHello().addResultListener(x -> {
//			String string = Test.getD(x).get();
//			Collection<String> collection = Test.getE(string).get();
//			return collection;
//		});
		
		IFuture<Collection<String>> coll = doChainedWork();
		
		IIntermediateFuture<String> abc = Test.getABC();

		IIntermediateFuture<Object> res = mapAsync(abc, s -> {return new Future(s+"_");});
		System.out.println(res.get());
		
		IIntermediateFuture<String> res2 = mapAsync(coll, Test::getWorld);
		System.out.println(res2.get());
	}

	private static IFuture<Collection<String>> doChainedWork()
	{
		IFuture<String> s1 = Test.getHello();
		IFuture<String> s2 = s1.$(x -> Test.getD(x));
		IFuture<Collection<String>> s3 = s2.$(f -> Test.getE(f));
		return s3;
	}
	
//	public static <R, E> IIntermediateFuture<R> mapAsync(IIntermediateFuture<E> orig, final IResultCommand<IIntermediateFuture<R>, E> function)
//	{
//		return Test.$$$(orig, function);
//	}
	
	public static <R, E> IIntermediateFuture<R> mapAsync(IFuture<? extends Collection<E>> orig, final Function<E, IFuture<R>> function)
	{
		final IntermediateFuture<R> ret = new IntermediateFuture<R>();

		orig.addResultListener(coll -> {
			CounterResultListener<R> counter = new CounterResultListener<R>(coll.size(), res -> ret.setFinished());

			for (E item : coll) {
				IFuture<R> apply = function.apply(item);
				
				apply.addResultListener(result -> {
					ret.addIntermediateResult(result);
					counter.resultAvailable(result);
				});
			}
			
		}, SResultListener.delegate(ret));

		return ret;
	}

	public static <R, E> IIntermediateFuture<R> mapAsync(IIntermediateFuture<E> orig, final Function<E, IFuture<R>> function)
	{
		final IntermediateFuture<R> ret = new IntermediateFuture<R>();

		orig.addIntermediateResultListener(new IIntermediateResultListener<E>()
		{
			boolean	fin	= false;

			public void resultAvailable(Collection<E> result)
			{
				for(E v : result)
				{
					intermediateResultAvailable(v);
				}
				finished();
			}

			public void intermediateResultAvailable(E result)
			{
				IFuture<R> res = function.apply(result);
				res.addResultListener(new IResultListener<R>()
				{
					public void exceptionOccurred(Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}

					@Override
					public void resultAvailable(R result)
					{
						ret.addIntermediateResult(result);
					}
				});
			}

			public void finished()
			{
				ret.setFinished();
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
