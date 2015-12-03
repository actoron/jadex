package monads;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SResultListener;

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

//		IIntermediateFuture<Object> res = mapAsync(abc, s -> {return new Future(s+"_");});
		IIntermediateFuture<String> res = abc.mapAsync(s -> new Future<String>(s+"_"));
		
		System.out.println(res.get());
		
//		IIntermediateFuture<String> res2 = mapAsync(coll, Test::getWorld);
		IFuture<List<String>> thenApply = coll.thenApply(c -> {
			// no async processing in stream design...
			return c.stream().map(Test::getWorld).map(x -> x.get()).collect(Collectors.toList());
		});
		
		
		System.out.println(thenApply.get());
	}

	private static IFuture<Collection<String>> doChainedWork()
	{
		IFuture<String> s1 = Test.getHello();
		IFuture<String> s2 = s1.thenCompose(x -> Test.getD(x));
		IFuture<Collection<String>> s3 = s2.thenCompose(f -> Test.getE(f));
		
		s2.thenCompose(s -> new Future(""));
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


}
