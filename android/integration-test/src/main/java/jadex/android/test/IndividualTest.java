package jadex.android.test;

import jadex.base.test.ComponentTestSuite;
import jadex.bridge.service.BasicService;

import java.io.File;

public class IndividualTest extends ComponentTestSuite {

	public IndividualTest(String packagePath, String cpRoot) throws Exception {
		super(new File(packagePath), new File(cpRoot), new String[0]);
	}
}
