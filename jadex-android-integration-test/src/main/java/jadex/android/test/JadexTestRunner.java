package jadex.android.test;

import jadex.android.commons.Logger;
import junit.framework.TestResult;
import android.test.AndroidTestRunner;

public class JadexTestRunner extends AndroidTestRunner
{

	private boolean logOnly;

	public JadexTestRunner(boolean logOnly) {
		this.logOnly = logOnly;
		if (logOnly) {
			Logger.i("logOnly is set, not executing any test in this run.");
		} else {
			Logger.i("logOnly is not set, executing tests...");
		}
	}

	@Override
	public void runTest(TestResult testResult) {
		if (logOnly) {
			// logonly is handled in TestResult.run
		}
		super.runTest(testResult);
	}

}
