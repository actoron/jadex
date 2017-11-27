package jadex.examples.docs;

import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SResultListener;
import jadex.commons.future.TerminableFuture;

/**
 * Created by kalinowski on 01.09.16.
 */
public class Futures {

    /**
     * DELEGATION
     */

    public IFuture<String> addAndToStringJava8(int a, int b) {
        Future<String> res = new Future<String>();
        addService.add(a,b).addResultListener(sum -> {
            toStringService.toString(sum).addResultListener(SResultListener.delegate(res));
        }, SResultListener.delegate(res));

        addService.add(a,b).addResultListener(SResultListener.delegateExceptions(res, sum -> toStringService.toString(sum).addResultListener(SResultListener.delegate(res))));
        return res;
    }

    public IFuture<String> addAndToString(int a, int b) {
        Future<String> res = new Future<String>();
        addService.add(a,b).addResultListener(new ExceptionDelegationResultListener<Integer, String>(res) {
            @Override
            public void customResultAvailable(Integer result) throws Exception {
                toStringService.toString(result).addResultListener(new DelegationResultListener<String>(res));
            }
        });
        return res;
    }

//    CounterResultListener<String> res = SResultListener.countResults(3, res -> completionFuture.setResult(null));

    private static class addService {
        public static IFuture<Integer> add(int a, int b) {
            return new Future(a+b);
        }
    }

    private static class toStringService {
        public static IFuture<String> toString(Object o) {
            return new Future(o.toString());
        }
    }

//    private void delegation() {
//
//
//        final Future<String> completionFuture = new Future<String>();
//
//        Future<Integer> addFut = new Future<>();
//        addFut.addResultListener(new ExceptionDelegationResultListener<Integer, String>(completionFuture) {
//            @Override
//            public void customResultAvailable(Integer result) throws Exception {
//                toStringService.toString(result).addResultListener(new DelegationResultListener<>());
//            }
//        });
//
//    }

    public static TerminableFuture<String> doTerminableWork() {
        TerminableFuture<String> fut = new TerminableFuture<String>();
        ITerminationCommand term = new ITerminationCommand() {

            public boolean checkTermination(Exception reason) {
                return true; // termination is accepted at this point
            }

            public void terminated(Exception reason) {
//                stopWork();
            }
        };
        fut.setTerminationCommand(term);
        // do some asynchronous work that calls fut.setResult() eventually
        return fut;
    }


    /**
     * SResultListener
     */

    private void samples() {
        Future<Void> fut = null;
        Future<Void> myFut = null;
        // delegate results and exceptions:
        fut.addResultListener(SResultListener.delegate(myFut));

// use results, delegate exceptions:
        fut.addResultListener(res -> threeDots(res) , SResultListener.delegate(myFut));

// delegate results, use exceptions:
        fut.addResultListener(SResultListener.delegate(myFut), ex -> threeDots(ex));

// count results
        CounterResultListener<?> counter = SResultListener.countResults(2, reached -> System.out.println("reached"), ex -> ex.printStackTrace());

    }

    private void threeDots(Object res) {
        // replacement in samples for "..."
    }
}
