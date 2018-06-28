package monads;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

/**
 *  Monadic futures for Java.
 *  http://zeroturnaround.com/rebellabs/monadic-futures-in-java8/
 */
public class Test
{
    public static void main(String[] args)
    {
//		IFuture<String> f = $(getHello(), Test::getWorld);
//		IFuture<String> f = getHello().$(x -> getWorld(x));
//        IFuture<String> f = $(getHello(), x -> getWorld(x));
//        IIntermediateFuture<String> inf = $$(getABC(), x -> getD(x));

        IIntermediateFuture<String> inf2 = $$$(getABC(), x -> getE(x));
        
//        test(Test::abc);
        
//        System.out.println("result is: "+$(f));
//        System.out.println("result is: "+$(inf));
        System.out.println("result is: "+$(inf2));
    }

    public static void abc(String a)
    {
        System.out.println("abc: "+a);
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

    public static IIntermediateFuture<String> getABC()
    {
        return new IntermediateFuture<>(Arrays.asList("a", "b", "c"));
    }

    public static IFuture<String> getD(String arg)
    {
        Future<String> ret = new Future<>();
        ret.setResult(arg+"_1");
        return ret;
    }
    
    public static IIntermediateFuture<String> getE(String arg)
    {
        IntermediateFuture<String> ret = new IntermediateFuture<>();
        ret.addIntermediateResult(arg+"_1");
        ret.addIntermediateResult(arg+"_2");
        ret.addIntermediateResult(arg+"_3");
        ret.setFinished();
        return ret;
    }

    public static <V, R> void test(final IFunctionalResultListener<R> function)
    {
    	function.resultAvailable((R)"hsa");
    }
    
//    public static <V, R> IFuture<R> $(IFuture<V> orig, final Function<V, IFuture<R>> function)
    public static <V, R> IFuture<R> $(IFuture<V> orig, final IResultCommand<IFuture<R>, V> function)
    {
        Future<R> ret = new Future<>();

        orig.addResultListener(new IResultListener<V>()
        {
            public void resultAvailable(V result)
            {
                IFuture<R> res = function.execute(result);
                res.addResultListener(new DelegationResultListener<R>(ret));
            }

            public void exceptionOccurred(Exception exception)
            {
                ret.setException(exception);
            }
        });

        return ret;
    }

    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<V, IFuture<R>> function)
    {
        IntermediateFuture<R> ret = new IntermediateFuture<>();

        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
        {
            public void resultAvailable(Collection<V> result)
            {
                for(V v: result)
                {
                    intermediateResultAvailable(v);
                }
                finished();
            }

            public void intermediateResultAvailable(V result)
            {
                IFuture<R> res = function.apply(result);
                res.addResultListener(new IResultListener<R>()
                {
                    public void resultAvailable(R result)
                    {
                        ret.addIntermediateResult(result);
                    }

                    public void exceptionOccurred(Exception exception)
                    {
                        ret.setExceptionIfUndone(exception);
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
    
//    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<V, IFuture<R>> function)
//    {
//        IntermediateFuture<R> ret = new IntermediateFuture<>();
//
//        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
//        {
//            public void resultAvailable(Collection<V> result)
//            {
//                for(V v: result)
//                {
//                    intermediateResultAvailable(v);
//                }
//                finished();
//            }
//
//            public void intermediateResultAvailable(V result)
//            {
//                IFuture<R> res = function.apply(result);
//                res.addResultListener(new IResultListener<R>()
//                {
//                    public void resultAvailable(R result)
//                    {
//                        ret.addIntermediateResult(result);
//                    }
//
//                    public void exceptionOccurred(Exception exception)
//                    {
//                        ret.setExceptionIfUndone(exception);
//                    }
//                });
//            }
//
//            public void finished()
//            {
//                ret.setFinished();
//            }
//
//            public void exceptionOccurred(Exception exception)
//            {
//                ret.setException(exception);
//            }
//        });
//
//        return ret;
//    }

//    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<V, R> function)
//    {
//        IntermediateFuture<R> ret = new IntermediateFuture<>();
//
//        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
//        {
//            public void resultAvailable(Collection<V> result)
//            {
//                for(V v: result)
//                {
//                    intermediateResultAvailable(v);
//                }
//                finished();
//            }
//
//            public void intermediateResultAvailable(V result)
//            {
//                R res = function.apply(result);
//                ret.addIntermediateResult(res);
//            }
//
//            public void finished()
//            {
//                ret.setFinished();
//            }
//
//            public void exceptionOccurred(Exception exception)
//            {
//                ret.setException(exception);
//            }
//        });
//
//        return ret;
//    }

//    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<Collection<V>, IIntermediateFuture<R>> function)
//    {
//        IntermediateFuture<R> ret = new IntermediateFuture<>();
//
//        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
//        {
//            public void resultAvailable(Collection<V> result)
//            {
//                IIntermediateFuture<R> res = function.apply(result);
//
////                for(V v: result)
////                {
////                    intermediateResultAvailable(v);
////                }
//                finished();
//            }
//
//            public void intermediateResultAvailable(V result)
//            {
////                IIntermediateFuture<R> res = function.apply(result);
////                res.addResultListener(new IntermediateDelegationResultListener<R>(ret));
//            }
//
//            public void finished()
//            {
//                ret.setFinished();
//            }
//
//            public void exceptionOccurred(Exception exception)
//            {
//                ret.setException(exception);
//            }
//        });
//
//        return ret;
//    }

    /**
	 *  Implements async loop and applies a an async multi-function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public static <R, E> IIntermediateFuture<R> $$$(IIntermediateFuture<E> orig, final IResultCommand<IIntermediateFuture<R>, E> function)
    {
        final IntermediateFuture<R> ret = new IntermediateFuture<R>();

        orig.addIntermediateResultListener(new IIntermediateResultListener<E>()
        {
        	boolean fin = false;
        	int cnt = 0;
        	int num = 0;
        	
            public void resultAvailable(Collection<E> result)
            {
                for(E v: result)
                {
                    intermediateResultAvailable(v);
                }
                finished();
            }

            public void intermediateResultAvailable(E result)
            {
            	cnt++;
                IIntermediateFuture<R> res = function.execute(result);
                res.addResultListener(new IIntermediateResultListener<R>()
                {
                    public void intermediateResultAvailable(R result)
                    {
                    	ret.addIntermediateResult(result);
                    }
                    
                    public void finished()
                    {
                    	if(++num==cnt && fin)
                    	{
                    		ret.setFinished();
                    	}
                    }
                    
                    public void resultAvailable(Collection<R> result)
                    {
                    	for(R r: result)
                        {
                            intermediateResultAvailable(r);
                        }
                        finished();
                    }
                    
                    public void exceptionOccurred(Exception exception)
                    {
                    	ret.setExceptionIfUndone(exception);
                    }
                });
            }

            public void finished()
            {
            	fin = true;
            	if(num==cnt)
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
