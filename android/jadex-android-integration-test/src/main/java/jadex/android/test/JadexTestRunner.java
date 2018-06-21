package jadex.android.test;

import jadex.android.commons.Logger;
import junit.framework.TestResult;
import android.test.AndroidTestRunner;

/**
 * This custom TestRunner uses custom TestResults to work around an error in
 * Android 2.2 Test Execution.
 * @author kalinowski
 *
 */
public class JadexTestRunner extends AndroidTestRunner
{
	private boolean logOnly;

	/**
	 * Constructor
	 * @param logOnly if true, no tests are executed.
	 */
	public JadexTestRunner(boolean logOnly) {
		super();
		this.logOnly = logOnly;
		if (logOnly) {
			Logger.i("logOnly is set, not executing any test in this run.");
		} else {
			Logger.i("logOnly is not set, executing tests...");
		}
		Logger.i("continue...");
	}

	@Override
	protected TestResult createTestResult() {
		if (logOnly) {
			return new JadexNoExecTestResult();
		} else {
			return new JadexTestResult();
		}
	}

}
