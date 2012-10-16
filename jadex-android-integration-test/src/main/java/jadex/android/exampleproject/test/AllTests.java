package jadex.android.exampleproject.test;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;



/**
 * A test suite containing all tests for my application.
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}