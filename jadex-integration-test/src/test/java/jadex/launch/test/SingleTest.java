package jadex.launch.test;

import junit.framework.Test;
import junit.framework.TestResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import jadex.commons.SUtil;


/**
 *  Test suite for BDI tests.
 */
public class SingleTest extends	ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";


	/**
	 *  Constructor called by Maven JUnit runner.
	 * @param tests
	 */
	public SingleTest(String... tests) throws Exception
	{
		super(findOutputDirs("jadex-applications-bdi", "jadex-applications-micro"), tests, new String[0]);
	}

	private static File[] findOutputDirs(String... projects) {
		List<File> list = new ArrayList<File>();
		for (String project : projects) {
			list.addAll(Arrays.asList(SUtil.findOutputDirs(project)));
		}
		return list.toArray(new File[list.size()]);
	}

	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
//		jadex.base.test.impl.ComponentTest.jadex.micro.servicecall.ServiceCall
//		jadex.base.test.impl.ComponentTest.jadex.micro.testcases.threading.Initiator
//		jadex.base.test.impl.ComponentTest.jadex.micro.testcases.servicequeries.User
//		jadex.bdi.testcases.semiautomatic.Wakeup

//		return new SingleTest("jadex.bdiv3.examples.puzzle.BenchmarkBDI");
//		return new SingleTest("jadex.bdi.testcases.semiautomatic.Wakeup",
//				"jadex.micro.testcases.servicequeries.User",
//				"jadex.micro.testcases.threading.Initiator",
//				"jadex.micro.servicecall.ServiceCall");

//		return new SingleTest(
//				"jadex.micro.testcases.threading.Initiator"
//		);
		return new Test() {
			@Override
			public int countTestCases() {
				return 0;
			}

			@Override
			public void run(TestResult result) {

			}
		};
	}


}
