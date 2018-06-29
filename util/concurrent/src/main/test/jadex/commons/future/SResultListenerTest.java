package jadex.commons.future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class SResultListenerTest {

    private Future<String> successTarget;
    private Future<String> exceptionTarget;

    @Before
    public void setUp() {
        successTarget = new Future<String>();
        exceptionTarget = new Future<String>();
    }

    // delegations:

    @Test
    public void delegateAll_successCase() {
        Future<String> fut = new Future<String>();
        fut.setResult("myRes");
        fut.addResultListener(SResultListener.delegate(successTarget));

        String result = successTarget.get();
        assertEquals("myRes", result);
    }

    @Test
    public void delegateAll_exceptionCase() {
        Future<String> fut = new Future<String>();
        fut.setException(new Exception("myEx"));
        fut.addResultListener(SResultListener.delegate(successTarget));

        Exception ex = successTarget.getException();
        assertEquals("myEx", ex.getMessage());
    }

    @Test
    public void delegateFunctionalException_exceptionCase() {
        Future<String> fut = new Future<String>();
        fut.setException(new Exception("myEx"));

        fut.addResultListener(new IFunctionalResultListener<String>() {
            public void resultAvailable(String result) {
                successTarget.setResult(result);
            }
        }, SResultListener.delegate(exceptionTarget));

        Exception ex = exceptionTarget.getException();
        assertEquals("myEx", ex.getMessage());
        assertFalse(successTarget.resultavailable);
    }

    @Test
    public void delegateFunctionalException_successCase() {
        Future<String> fut = new Future<String>();
        fut.setResult("myRes");

        fut.addResultListener(new IFunctionalResultListener<String>() {
            public void resultAvailable(String result) {
                successTarget.setResult(result);
            }
        }, SResultListener.delegate(exceptionTarget));

        Exception ex = exceptionTarget.getException();
        assertNull(ex);
        assertEquals("myRes", successTarget.get());
    }

    @Test
    public void delegateFunctionalSuccess_exceptionCase() {
        Future<String> fut = new Future<String>();
        fut.setException(new Exception("myEx"));

        fut.addResultListener(SResultListener.delegate(successTarget), new IFunctionalExceptionListener() {
            public void exceptionOccurred(Exception exception) {
                exceptionTarget.setException(exception);
            }
        });

        Exception ex = exceptionTarget.getException();
        assertEquals("myEx", ex.getMessage());
    }

    @Test
    public void delegateFunctionalSuccess_successCase() {
        Future<String> fut = new Future<String>();
        fut.setResult("myRes");

        fut.addResultListener(SResultListener.delegate(successTarget), new IFunctionalExceptionListener() {
            public void exceptionOccurred(Exception exception) {
                exceptionTarget.setException(exception);
            }
        });

        Exception ex = exceptionTarget.getException();
        assertNull(ex);

        assertEquals("myRes", successTarget.get());
    }

    // delegate with IntermediateFuts

    @Test
    public void delegateWithIntermediateFutures() {
        IntermediateFuture<String> iFut = new IntermediateFuture<String>();
        IntermediateFuture<String> delegationTarget = new IntermediateFuture<String>();

        SResultListener.delegateFromTo(iFut, delegationTarget);

        iFut.addIntermediateResultListener(new IntermediateDelegationResultListener<String>(delegationTarget));
        iFut.addIntermediateResult("test1");

        String nextRes = delegationTarget.getNextIntermediateResult();

        assertEquals("test1", nextRes);
    }

    @Test
    public void delegateWithTupleFutures_success() {
        Tuple2Future<String, String> tfut = new Tuple2Future<String, String>();
        Tuple2Future<String, String> delegationTarget = new Tuple2Future<String, String>();

//        SResultListener.delegateFromTo(iFut, delegationTarget);

        tfut.addResultListener(SResultListener.delegate(delegationTarget));

        tfut.setFirstResult("first");
        String first = delegationTarget.getFirstResult();

        tfut.setSecondResult("second");
        String second = delegationTarget.getSecondResult();

        assertEquals("first", first);
        assertEquals("second", second);
    }


    @Test
    public void delegateWithTupleFutures_exception() {
        Tuple2Future<String, String> tfut = new Tuple2Future<String, String>();
        Tuple2Future<String, String> delegationTarget = new Tuple2Future<String, String>();

        tfut.addResultListener(SResultListener.delegate(delegationTarget));

        tfut.setException(new Exception("error"));


        Exception ex = delegationTarget.getException();
        assertEquals("error", ex.getMessage());

        try {
            delegationTarget.getFirstResult();
            fail("should have thrown");
        } catch (Exception e) {

        }
    }

    @Test
    public void delegateWithTupleFutures_fromTo() {
        Tuple2Future<String, String> tfut = new Tuple2Future<String, String>();
        Tuple2Future<String, String> delegationTarget = new Tuple2Future<String, String>();

        SResultListener.delegateFromTo(tfut, delegationTarget);

        tfut.setFirstResult("first");
        String first = delegationTarget.getFirstResult();

        tfut.setSecondResult("second");
        String second = delegationTarget.getSecondResult();

        assertEquals("first", first);
        assertEquals("second", second);
    }



    // counter:

    @Test
    public void countResults() {
        Future<String> fut1 = new Future<String>("res1");
        Future<String> fut2 = new Future<String>("res2");
        Future<String> fut3 = new Future<String>("res3");

        CounterResultListener<String> res = SResultListener.countResults(3, new IFunctionalResultListener<Void>() {
            @Override
            public void resultAvailable(Void result) {
                successTarget.setResult("completed");
            }
        });

        fut1.addResultListener(res);
        fut2.addResultListener(res);
        fut3.addResultListener(res);

        assertEquals("completed", successTarget.get());
    }
}
