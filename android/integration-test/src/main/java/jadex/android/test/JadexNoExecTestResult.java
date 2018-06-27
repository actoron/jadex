package jadex.android.test;

import junit.framework.TestCase;

/**
 * Skips test execution and only informs listeners about the Tests.
 * @author kalinowski
 *
 */
public class JadexNoExecTestResult extends JadexTestResult {

	@Override
	protected void run(TestCase test) {
		startTest(test);
		endTest(test);
	}
	
}
