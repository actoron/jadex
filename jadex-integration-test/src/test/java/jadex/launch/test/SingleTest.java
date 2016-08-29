package jadex.launch.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SUtil;
import junit.framework.Test;
import junit.framework.TestResult;


/**
 *  Test suite for BDI tests.
 */
public class SingleTest extends	ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 * @param tests
	 */
	public SingleTest(String... tests) throws Exception
	{
		super(findOutputDirs("jadex-applications-bdi", "jadex-applications-bdiv3", "jadex-applications-micro", "jadex-applications-bpmn"), tests, new String[0]);
	}

	private static File[] findOutputDirs(String... projects) {
		List<File> list = new ArrayList<File>();
		for (String project : projects) {
			list.addAll(Arrays.asList(SUtil.findOutputDirs(project)));
		}
		return list.toArray(new File[list.size()]);
	}


	/**
	 * Implement this to adjust this SingleTest to your needs.
	 * @return
     */
	public static SingleTest getSingleTest() throws Exception {
		SingleTest test = null;
//		test = new SingleTest("jadex.bdiv3.examples.puzzle.BenchmarkBDI");
//		test = new SingleTest("jadex.bdi.testcases.semiautomatic.Wakeup",
//				"jadex.micro.testcases.servicequeries.User",
//				"jadex.micro.testcases.threading.Initiator",
//				"jadex.micro.servicecall.ServiceCall");

//		test = new SingleTest(
//				"jadex.micro.testcases.threading.Initiator"
//		);

		return test;
	}

	/**
	 * Static method called by eclipse (and gradle?) JUnit runner.
	 */
	public static Test suite() throws Exception {
		SingleTest test = getSingleTest();
		if (test == null) {
			test = new SingleTest() {
				@Override
				public void run(TestResult result) {
					result.startTest(this);
					result.endTest(this);
				}

				@Override
				protected List<String> getAllFiles(File root) {
					return Collections.emptyList();
				}
			};
		}
		return test;
	}
}
