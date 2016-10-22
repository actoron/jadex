package jadex.commons.future;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

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
