package jadex.android.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;

/**
 * Wraps the original Junit TestResult class to catch errors during Listener calls.
 * @author kalinowski
 *
 */
public class JadexTestResult extends TestResult {

	private List<ErrorCatchingTestListenerWrapper> listeners = new ArrayList<ErrorCatchingTestListenerWrapper>();
	
	@Override
	public synchronized void addListener(TestListener listener) {
		ErrorCatchingTestListenerWrapper wrapped = new ErrorCatchingTestListenerWrapper(listener);
		super.addListener(wrapped);
		this.listeners.add(wrapped);
	}
	
	@Override
	public synchronized void removeListener(TestListener listener) {
		for (ErrorCatchingTestListenerWrapper l: listeners) {
			if (l.originalListener == listener) {
				super.removeListener(l);
				break;
			}
		}
	}

	private static class ErrorCatchingTestListenerWrapper implements TestListener {
		private TestListener originalListener;

		public ErrorCatchingTestListenerWrapper(TestListener originalListener) {
			this.originalListener = originalListener;
		}

		@Override
		public void addError(Test test, Throwable t) {
			originalListener.addError(test, t);
		}

		@Override
		public void addFailure(Test test, AssertionFailedError t) {
			originalListener.addFailure(test, t);
		}

		@Override
		public void endTest(Test test) {
			originalListener.endTest(test);
		}

		@Override
		public void startTest(Test test) {
			try {
				originalListener.startTest(test);
			} catch (IllegalStateException e) {
				// one of the default test listeners tries to invoke
				// getClass().getMethod(), which throws a NoSuchMethodException on android 2.2. 
				// see: http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.2.1_r1/android/test/InstrumentationTestRunner.java#767
				if (e.getCause() instanceof NoSuchMethodException) {
					// ignore
				} else {
					throw e;
				}
			}
		}
		
	}
}
